import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
		InvertedIndex invertedIndex = new InvertedIndex();
		
		if (argmap.hasFlag("-path")) {
			Path path = argmap.getPath("-path");
			List<Path> files = null;
			
			try {
				files = TextFileFinder.traverse(path);
				
				for (Path file : files) {
					IndexBuilder.getWords(file, invertedIndex);
				}
			} catch (NullPointerException e) {
				System.err.println("Unable to create inverted index. Has no elements");
			} catch (IOException e) {
				System.err.println("Unable to get path from " + path);
			}
		}
		
		if (argmap.hasFlag("-index")) {
			Path index = null;
			index = argmap.getPath("-index", Paths.get("index.json"));
			
			try {
				invertedIndex.toJSON(index);
			} catch (IOException e) {
				System.err.println("Cannot write to index " + index);
			}
		}
		
		Path locIndex = null;
		if (argmap.hasFlag("-locations")) {
			if (argmap.getPath("-locations") != null) {
				locIndex = argmap.getPath("-locations");
			} else {
				locIndex = Paths.get("out", "index-text-locations.json");
			}
			
			try {
				invertedIndex.locationtoJSON(locIndex);
			} catch (IOException e) {
				System.err.println("Cannot write to file " + locIndex);
			}
		}
		
		TreeMap<String, ArrayList<Result>> queries = null; // TODO Move this into IndexReader class (might rename that class)
		if (argmap.hasFlag("-search")) {
			boolean exact;
			Path search;
			
			if (argmap.hasFlag("-exact")) {
				exact = true;
			} else {
				exact = false;
			}

			search = argmap.getPath("-search");
			
			Path path = argmap.getPath("-path");
			
			queries = new TreeMap<String, ArrayList<Result>>();
			
			try {
				QueryBuilder.builder(search, path, exact, invertedIndex, queries);
			} catch (IOException e) {
				System.err.println("Cannot build query");
			}
		}
		
		if (argmap.hasFlag("-results")) {
			Path index;
			index = argmap.getPath("-results", Paths.get("results.json"));
			
			try {
				NestedJSON.queryObject(queries, index);
			} catch (NullPointerException e) {
				System.err.println("Cannot write query from empty index");
			} catch (IOException e) {
				System.err.println("Cannot write query at path " + index);
			}
		}
	}
}
