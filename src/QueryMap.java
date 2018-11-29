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
	private final InvertedIndex index;
	
	/**
	 * Initializes querymap and invertindex
	 * @param index
	 */
	public QueryMap(InvertedIndex index) {
		this.query = new TreeMap<>();
		this.index = index;
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
	 * TODO
	 * @param search
	 * @param exact
	 * @param threads
	 * @throws IOException
	 */
	public void builder(Path search, boolean exact, int threads) throws IOException {
		WorkQueue queue = new WorkQueue(threads);
		
		try (BufferedReader reader = Files.newBufferedReader(search, StandardCharsets.UTF_8);) {
			String line;
			
			while ((line = reader.readLine()) != null) {
				// TODO Only pass 2 parameters: line, exact
				queue.execute(new Builder(line, this.query, index, exact));
			}
			
			queue.finish();
			queue.shutdown(); // TODO In a finally block
		}
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
	
	// TODO private non-static inner class
	/**
	 * Runnable builder class for QueryMap
	 * @author Andrew
	 *
	 */
	public static class Builder implements Runnable {
		// TODO Clean up, Javadoc, keywords, etc.
		String queryLine;
		TreeMap<String, ArrayList<Result>> query;
		InvertedIndex index;
		TreeSet<String> que;
		boolean exact;
		
		public Builder(String line, TreeMap<String, ArrayList<Result>> query, InvertedIndex index, boolean exact) {
			this.queryLine = line;
			this.query = query;
			this.index = index;
			this.que = new TreeSet<String>();
			this.exact = exact;
			
			// TODO Make what you can local variables inside of the run() method
		}
		
		@Override
		public void run() {
			que.addAll(TextFileStemmer.stemLine(this.queryLine));
			queryLine = String.join(" ", que);
			
			synchronized(query) {
				// TODO Only 1 thread can do this at a time.
				if (!(this.queryLine.isEmpty() && !this.query.containsKey(queryLine))) {
					if (this.exact) {
						this.query.put(queryLine, this.index.exactSearch(que));
					} else {
						this.query.put(queryLine, this.index.partialSearch(que));
					}
				}
			}
			
			/* TODO
			synchronized(query) {
				if (this.queryLine.isEmpty() || this.query.containsKey(queryLine)) {
					return;
				}
			}
			
			List<Result> results = this.index.exactSearch(que);
			
			synchronized(query) {
				this.query.put(queryLine, results);
			}
			*/
		}	
	}
}
