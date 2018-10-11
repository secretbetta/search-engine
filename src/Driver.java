import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Driver {
	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		Path index = null;
		
		var invertedIndex = new InvertedIndex();
		var argmap = new ArgumentMap(args);
		
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
			catch (IOException e) {
			}
		}
		
		if (argmap.hasFlag("-index")) {
			if (argmap.getPath("-index") != null) {
				index = argmap.getPath("-index");
			} else {
				index = Paths.get("index.json");
			}
		} else {
			index = Paths.get("out", "index.json");
		}
		
		try {
			NestedJSON.tripleNested(invertedIndex.getIndex(), index);
		} catch (NullPointerException e) { 
		}
		
//		TODO For next code review, can try to add -locations flag to your project 1 code.

	}
}
