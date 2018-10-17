import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeMap;

/**
 * Driver of the project codes
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
		// TODO Create index and locIndex in the scope they are used
		Path index = null;
		Path locIndex = null;
		
		var invertedIndex = new InvertedIndex();
		var argmap = new ArgumentMap(args);
		
		/* TODO Don't separate the logic checking for flags and using them
		if (argmap.hasFlag("-path")) {
			Path path = argmap.getPath("-path");
			try {
				building code
			}
			catch (IOException e) {
				System.err.println("Unable to build index from path: " + path);
			}
		}
		*/
		
		if (argmap.hasFlag("-index")) {
			if (argmap.getPath("-index") != null) {
				index = argmap.getPath("-index");
			} else {
				index = Paths.get("index.json");
			}
		}
		
		if (argmap.hasFlag("-locations")) {
			if (argmap.getPath("-locations") != null) {
				locIndex = argmap.getPath("-locations");
			} else {
				locIndex = Paths.get("out", "index-text-locations.json");
			}
		}
		
		if (argmap.hasFlag("-path")) {
			Path path = argmap.getPath("-path");
			List<Path> files = null;
			try {
				if (path != null) {
					files = TextFileFinder.traverse(path);
				}
				if (files != null) {
					for (Path file : files) {
						WordBuilder.getWords(file, invertedIndex);
					}
				}
			}
			catch (IOException e) { // TODO Never do this!
			}
		}
		
		try {
			// TODO Remove for project 1 for now since you already have it in project 2
			if (argmap.hasFlag("-locations")) {
				TreeMap<String, Integer> locationIndex = LocationIndex.indexLocation(invertedIndex);
				NestedJSON.asObject(locationIndex, locIndex);
			}
			
			NestedJSON.tripleNested(invertedIndex.getIndex(), index);
		} catch (NullPointerException e) {  // TODO Never do this!
		} catch (IOException e) {  // TODO Never do this!
		}
	}
}
