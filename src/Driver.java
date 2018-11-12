import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
		var invertedIndex = new InvertedIndex();
		
		if (argmap.hasFlag("-path")) {
			Path path = argmap.getPath("-path");
			List<Path> files = null; // TODO See IndexBuilder
			
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
			// TODO Combine into one line
			Path index = null;
			index = argmap.getPath("-index", Paths.get("index.json"));
			
			try {
				invertedIndex.toJSON(index);
			} catch (IOException e) {
				System.err.println("Cannot write to index " + index);
			}
		}
		
		Path locIndex = null; // TODO Declare within the if block below
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
		
		var query = new QueryMap(); // TODO Move up with the other declarations that are used in multiple blocks
		if (argmap.hasFlag("-search")) {
			
			boolean exact;
			if (argmap.hasFlag("-exact")) {
				exact = true;
			} else {
				exact = false;
			}
			
			// TODO boolean exact = argmap.hasFlag("-exact");

			Path search = argmap.getPath("-search");
			Path path = argmap.getPath("-path");
			
			try {
				query.builder(search, path, exact, invertedIndex);
			} catch (IOException e) {
				System.err.println("Cannot build query map");
			}
		}
		
		if (argmap.hasFlag("-results")) {
			Path index;
			index = argmap.getPath("-results", Paths.get("results.json"));
			
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