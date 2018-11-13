import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

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
	 * Writes to index file the query map in JSON format
	 * 
	 * @param index File to write to
	 * @throws IOException
	 */
	public void toJSON(Path index) throws IOException {
		NestedJSON.queryObject(this.query, index);
	}
}
