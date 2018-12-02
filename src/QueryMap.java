import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * TODO Make a separate multithreaded querymap class.
 */

/**
 * Query Map data structure class with builder method
 * @author Andrew
 *
 */
public class QueryMap {
	private final TreeMap<String, ArrayList<Result>> query;
	private InvertedIndex index;
	private ThreadSafeInvertedIndex threadIndex;
	
	/**
	 * Initializes querymap and invertindex
	 * @param index InvertedIndex to search
	 */
	public QueryMap(InvertedIndex index) {
		this.query = new TreeMap<>();
		this.index = index;
	}
	
	/**
	 * Initializes querymap and threadsafeinvertedindex
	 * @param index ThreadSafeInvertedIndex to search
	 */
	public QueryMap(ThreadSafeInvertedIndex index) {
		this.query = new TreeMap<>();
		this.threadIndex = index;
	}
	
	/**
	 * Builds query map from invertedindex. 
	 * 
	 * @param search Path to use as search terms
	 * @param path Path of file(s) to search in
	 * @param exact Exact or Partial search
	 * @param index Inverted index to read from
	 * 
	 * @throws IOException
	 */
	public void builder(Path search, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(search, StandardCharsets.UTF_8);) {
		
			TreeSet<String> query; 
			String line;
			
			while ((line = reader.readLine()) != null) {
				String queryLine = "";
				
				query = new TreeSet<String>();
				query.addAll(TextFileStemmer.stemLine(line));
				
				if (!query.isEmpty()) {
					queryLine = String.join(" ", query);
				}
				
				if (!(queryLine.isEmpty() && !this.query.containsKey(queryLine))) {
					if (exact) {
						this.query.put(queryLine, this.index.exactSearch(query));
					} else {
						this.query.put(queryLine, this.index.partialSearch(query));
					}
				}
			}
		}
	}
	
	/**
	 * Thread version of builder
	 * 
	 * @param search File to use as query
	 * @param exact Exact or Partial search
	 * @param threads Number of threads to use
	 * @throws IOException
	 */
	public void builder(Path search, boolean exact, int threads) throws IOException {
		WorkQueue queue = new WorkQueue(threads);
		
		try (BufferedReader reader = Files.newBufferedReader(search, StandardCharsets.UTF_8);) {
			String line;
			while ((line = reader.readLine()) != null) {
				queue.execute(new Builder(line, exact));
			}
		}
		queue.finish();
		queue.shutdown();
	}

	/**
	 * Writes to index file the query map in JSON format
	 * 
	 * @param index File to write to
	 * @throws IOException
	 */
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
			
			ArrayList<Result> results = new ArrayList<Result>();
			
			if (exact) {
				results = threadIndex.exactSearch(queries);
			} else {
				results = threadIndex.partialSearch(queries);
			}
			
			synchronized(query) {
				query.put(queryLine, results);
			}
		}	
	}
}
