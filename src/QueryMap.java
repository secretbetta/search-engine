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
	private TreeMap<String, ArrayList<Result>> query;
	
	public QueryMap() {
		this.query = new TreeMap<>();
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
	public void builder(Path search, 
			Path path, 
			boolean exact, 
			InvertedIndex index) throws IOException {
		BufferedReader reader = Files.newBufferedReader(search, StandardCharsets.UTF_8);
		
		if (path != null) {
			TreeSet<String> que;
			String line;
			
			while ((line = reader.readLine()) != null) {
				String word = "";
				que = new TreeSet<String>();
				que.addAll(TextFileStemmer.stemLine(line));
				
				if (!que.isEmpty()) {
					for (String w : que.headSet(que.last())) {
						word += w + " ";
					}
					word += que.last();
				}
				
				if (!(word.isEmpty())) {
					if (exact) {
						this.query.put(word, index.exactSearch(que));
					} else {
						this.query.put(word, index.partialSearch(que));
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
