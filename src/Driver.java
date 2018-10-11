import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeSet;

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
		
		var temp = new TreeSet<Integer>();
		
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
				index = Paths.get("out", "index.json");
			}
		} else {
			index = Paths.get("out", "index.json");
		}
		
		try {
			if (argmap.hasFlag("-path")) {
				NestedJSON.tripleNested(invertedIndex.getIndex(), index);
			}
		} catch (NullPointerException e) { 
		}
		
//		For next code review, can try to add -locations flag to your project 1 code.


//		try {
//			if (argmap.hasFlag("-path")) {
//				if (!(argmap.getPath("-path") == null)) {
//					path = Paths.get(argmap.getPath("-path").toString());
//				}
//			}
//			
//			if (argmap.hasFlag("-index") && !(argmap.getPath("-index") == null)) {
//				index = Paths.get(argmap.getPath("-index").toString());
//			} else if (argmap.hasFlag("-index") && argmap.getPath("-index") == null) {
//				index = Paths.get("index.json");
//			} else {
//				index = Paths.get("out", "index.json");
//			}
//			
//			if (argmap.hasFlag("-search")) {
//				
//			}
//
//			// TODO try-with-resources
//			BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);
//			
//			if (path != null) {
//				if (Files.isDirectory(path)) {
//					textFiles = finder.traverse(path);
//					
//					for (String file : textFiles) {
//						words = GetWords.getWords(Paths.get(file));
//						
//						for (String word : words.keySet()) {
//							invertedIndex.addAllWordFile(word, file, words.get(word));
//						}
//						
//					}
//					
//				} else {
//					words = GetWords.getWords(path);
//					
//					for (String word : words.keySet()) {
//						invertedIndex.addAllWordFile(word, argmap.getPath("-path").toString(), words.get(word));
//					}
//					
//				}
//				NestedJSON.tripleNested(invertedIndex.getIndex());
//			}
//		} catch (NoSuchFileException e) {
//			System.err.println("Cannot find path " + path);
//		} catch (IOException e) {
//			System.err.println("Command arguments are invalid");
//			System.err.println("Valid arguments:\n-path\n-index");
//		}
	}
}
