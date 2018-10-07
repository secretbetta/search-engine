import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class Driver {
	
	/**
	 * Gets words from a given text file. Adds word and position into TreeMap.
	 * Words may have more than one position in a text file.
	 * <p>key = word(s) from text files</p>
	 * <p>value = position(s) of word from text files</p>
	 * @param input path to the input file
	 * @return list list of words in text file
	 * @throws IOException
	 */
	public static TreeMap<String, TreeSet<Integer>> getWords(Path input) 
			throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);) {
			int position = 0;
			
			var stem = new TextFileStemmer();
			var index = new TreeMap<String, TreeSet<Integer>>();
			
			String line;
			List<String> list;
			
			while ((line = reader.readLine()) != null) {
				list = stem.stemLine(line);
				for (String word: list) {
					position++;
					if (index.containsKey(word)) {
						index.get(word).add(position);
					} else {
						index.put(word, new TreeSet<Integer>());
						index.get(word).add(position);
					}
				}
			}
			
			return index;
		}
	}
	
	public static Path[] args(String[] args) {
		Path path = null;
		Path index = null;
		
		var argmap = new ArgumentMap(args);
		if (argmap.hasFlag("-path")) {
			if (!(argmap.getPath("-path") == null)) {
				path = Paths.get(argmap.getPath("-path").toString());
			}
		}
		
		if (argmap.hasFlag("-index") && !(argmap.getPath("-index") == null)) {
			index = Paths.get(argmap.getPath("-index").toString());
		} else if (argmap.hasFlag("-index") && argmap.getPath("-index") == null) {
			index = Paths.get("index.json");
		} else {
			index = Paths.get("out", "index.json");
		}
		
		Path[] paths = {path, index};
		
		return paths;
	}

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		Path path = null;
		Path index = null;
		Path search = null;
		
		var textFiles = new ArrayList<String>();
		TreeMap<String, TreeSet<Integer>> words;
		var argmap = new ArgumentMap(args);
		var finder = new TextFileFinder();
		var invertedIndex = new InvertedIndex();
		
		if (argmap.hasFlag("-path")) {
			if (!(argmap.getPath("-path") == null)) {
				path = Paths.get(argmap.getPath("-path").toString());
			}
		}
		
		if (argmap.hasFlag("-index") && !(argmap.getPath("-index") == null)) {
			index = Paths.get(argmap.getPath("-index").toString());
		} else if (argmap.hasFlag("-index") && argmap.getPath("-index") == null) {
			index = Paths.get("index.json");
		} else {
			index = Paths.get("out", "index.json");
		}
		
		if (argmap.hasFlag("-search") && !(argmap.getPath("-search") == null)) {
			search = Paths.get(argmap.getPath("-search").toString());
		}
		
		if (argmap.hasFlag("-results") && !(argmap.getPath("-results") == null)) {
			index = Paths.get(argmap.getPath("-results").toString());
		} else if (argmap.hasFlag("-results")) {
			index = Paths.get("results.json");
		}
		
		try (BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);) {

			if (path != null) {
				if (Files.isDirectory(path)) {
					textFiles = finder.traverse(path);
					for (String file : textFiles) {
						words = getWords(Paths.get(file));
						
						for (String word : words.keySet()) {
							invertedIndex.addAllWordFile(word, file, words.get(word));
						}
					}
				} else {
					words = getWords(path);
					
					for (String word : words.keySet()) {
						invertedIndex.addAllWordFile(word, argmap.getPath("-path").toString(), words.get(word));
					}
					
				}
				
				writer.write(NestedJSON.tripleNested(invertedIndex.getIndex()));
				writer.close();
			}
		} catch (NoSuchFileException e) {
			System.err.println("Cannot find path " + path);
		} catch (IOException e) {
			System.err.println("Command arguments are invalid");
			System.err.println("Valid arguments:\n-path\n-index");
		}
	}
}
