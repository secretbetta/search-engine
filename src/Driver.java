import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Driver of Project Codes
 * @author Andrew
 *
 */
public class Driver {
	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		var argmap = new ArgumentMap(args);
		var invertedIndex = new ThreadSafeInvertedIndex();
		var query = new QueryMap(invertedIndex);
		
		int threads = 1;
		if (argmap.hasFlag("-threads")) {
			threads = argmap.getInt("-threads", 5);
		}
		
		int limit = 50;
		if (argmap.hasFlag("-limit")) {
			try {
				if (argmap.getInt("-limit") >= 0) {
					limit = argmap.getInt("-limit", 50);
				}
			} catch (NumberFormatException e) {
				System.err.println(argmap.getString("-limit") + " is not a number");
			}
		}
		
		if (argmap.hasFlag("-url")) {
			String url = argmap.getString("-url");
			
			try {
				IndexBuilder.traverse(url, invertedIndex, limit);
			} catch (IOException e) {
				System.err.println("Cannot access URL " + url);
			}
		}
		
		if (argmap.hasFlag("-path")) {
			Path path = argmap.getPath("-path");
			
			try {
				IndexBuilder.traverse(path, invertedIndex, threads);
			} catch (NullPointerException e) {
				System.err.println("Unable to create inverted index. Has no elements");
			} catch (IOException e) {
				System.err.println("Unable to get path from " + path);
			}
		}
		
		if (argmap.hasFlag("-index")) {
			Path index = argmap.getPath("-index", Paths.get("index.json"));
			
			try {
				invertedIndex.toJSON(index);
			} catch (IOException e) {
				System.err.println("Cannot write to index " + index);
			}
		}
		
		if (argmap.hasFlag("-locations")) {
			Path locIndex = argmap.getPath("-locations", Paths.get("index-text-locations.json"));
			
			try {
				invertedIndex.locationtoJSON(locIndex);
			} catch (IOException e) {
				System.err.println("Cannot write to file " + locIndex);
			}
		}
		
		if (argmap.hasFlag("-search")) {
			Path search = argmap.getPath("-search");
			
			try {
				query.builder(search, argmap.hasFlag("-exact"), threads);
			} catch (IOException e) {
				System.err.println("Cannot build query map");
			}
		}
		
		if (argmap.hasFlag("-results")) {
			Path index = argmap.getPath("-results", Paths.get("results.json"));
			
			try {
				query.toJSON(index);
			} catch (NullPointerException e) {
				System.err.println("Cannot write query from empty index");
			} catch (IOException e) {
				System.err.println("Cannot write query at path " + index);
			}
		}
	}
}