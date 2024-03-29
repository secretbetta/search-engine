import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Multhreaded version of QueryMap, implements QueryMapInterface
 * @author Andrew
 *
 */
public class MultithreadQueryMap implements QueryMapInterface {
	private final TreeMap<String, ArrayList<Result>> query;
	private final ThreadSafeInvertedIndex index;
	private final int threads;
	
	/**
	 * Initializes query and ThreadSafeInvertedIndex
	 * @param index
	 */
	public MultithreadQueryMap(ThreadSafeInvertedIndex index, int threads) {
		query = new TreeMap<>();
		this.index = index;
		this.threads = threads;
	}
	
	@Override
	public void builder(Path search, boolean exact) throws IOException {
		WorkQueue queue = new WorkQueue(this.threads);
		
		try (BufferedReader reader = Files.newBufferedReader(search, StandardCharsets.UTF_8);) {
			String line;
			while ((line = reader.readLine()) != null) {
				queue.execute(new Builder(line, exact));
			}
		} finally {
			queue.finish();
			queue.shutdown();
		}
	}
	
	@Override
	public void toJSON(Path index) throws IOException {
		NestedJSON.queryObject(this.query, index);
	}
	
	/**
	 * Runnable builder class for QueryMap
	 * @author Andrew
	 *
	 */
	private class Builder implements Runnable {
		String queryLine;
		boolean exact;
		
		public Builder(String line, boolean exact) {
			this.queryLine = line;
			this.exact = exact;
		}
		
		@Override
		public void run() {
			TreeSet<String> queries = new TreeSet<String>();
			
			queries.addAll(TextFileStemmer.stemLine(this.queryLine));
			queryLine = String.join(" ", queries);
			
			synchronized(query) {
				if (this.queryLine.isEmpty() || query.containsKey(queryLine)) {
					return;
				}
			}
			
			ArrayList<Result> results;
			
			if (exact) {
				results = index.exactSearch(queries);
			} else {
				results = index.partialSearch(queries);
			}
			
			synchronized(query) {
				query.put(queryLine, results);
			}
		}	
	}
}
