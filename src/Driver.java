import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Driver of the project codes
 * @author Andrew
 *
 */
public class Driver {
	
	/*
	TODO
	Driver should not need to test for flags within nested if blocks. Simplify.
	
	var argmap = new ArgumentMap(args);
	InvertedIndex invertedIndex = new InvertedIndex();
	
	(check -path first so index is built)
	if (-path) {
		build index
	}
	
	(output stuff goes after building stuff)
	if (-index) {
		output index
	}
	
	if (-locations) {
		output locations
	}
	*/
	
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
					WordBuilder.getWords(file, invertedIndex);
				}
			} catch (NullPointerException e) {
				System.err.println("Unable to create inverted Index. Has no elements");
			} catch (IOException e) {
				System.err.println("Unable to get path from " + path);
			}
		}
		
		if (argmap.hasFlag("-index")) {
			Path index = null;
			
			if (argmap.getPath("-index") != null) {
				index = argmap.getPath("-index");
			} else {
				index = Paths.get("index.json");
			}
			
			try {
				invertedIndex.toJSON(index);
			} catch (IOException e) {
				System.err.println("Cannot write to index " + index);
			}
		}
		
		if (argmap.hasFlag("-locations")) {
			Path locIndex;
			if (argmap.getPath("-locations") != null) {
				locIndex = argmap.getPath("-locations");
			} else {
				locIndex = Paths.get("out", "index-text-locations.json");
			}
		}
	}
}
