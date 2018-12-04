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
public class QueryMap implements QueryMapInterface {
	private final TreeMap<String, ArrayList<Result>> query;
	private final InvertedIndex index;
	
	/**
	 * Initializes querymap and invertindex
	 * @param index InvertedIndex to search
	 */
	public QueryMap(InvertedIndex index) {
		this.query = new TreeMap<>();
		this.index = index;
	}
	
	@Override
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

	@Override
	public void toJSON(Path index) throws IOException {
		NestedJSON.queryObject(this.query, index);
	}
}
