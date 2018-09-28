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
	public static TreeMap<String, TreeSet <Integer>> getWords(Path input) 
			throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);) {
			
			int position = 0;
			
			var stem = new TextFileStemmer();
			var wordindex = new WordIndex();
		
			
			String line = null;
			List<String> list = null;

			while ((line = reader.readLine()) != null) {
				list = stem.stemLine(line);
				for (String word: list) {
					position++;
					wordindex.add(word, position);
				}
			}
			return wordindex.getAll();
		}
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
//		Path path = Paths.get(".");
//		Path index = Paths.get("out", "index.json");
		
		var textFiles = new ArrayList<String>();

		// TODO Try to move this nested data structure into its own class. 
		// This could be a data-structure like class that just stores stuff 
		// and doesn't parse stuff.
		TreeMap<String, TreeSet<Integer>> words;
		var allwords = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();;
		var wordIndex = new TreeMap<String, TreeSet<Integer>>();
		var argmap = new ArgumentMap(args);
		var finder = new TextFileFinder();

		try {
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

			BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);
			
			if (path != null) {
				if (Files.isDirectory(path)) {
					textFiles = finder.traverse(path);
					
					for (String file : textFiles) {
						words = getWords(Paths.get(file));
						
						for (String word : words.keySet()) {
							wordIndex.put(file, words.get(word));
							
							if (allwords.containsKey(word)) {
								allwords.get(word).put(file, words.get(word));
							} else if (!allwords.containsKey(word)){
								allwords.put(word, wordIndex);
							}
							
							wordIndex = new TreeMap<String, TreeSet<Integer>>();
						}
						
					}
					
				} else {
					words = getWords(path);
					
					for (String word : words.keySet()) {
						wordIndex.put(argmap.getPath("-path").toString(), words.get(word));
						allwords.put(word, wordIndex);
						wordIndex = new TreeMap<String, TreeSet<Integer>>();
					}
					
				}
				writer.write(NestedJSON.tripleNested(allwords));
				writer.close();
			}
		} catch (NoSuchFileException e) {
			System.err.println("Cannot find path " + path);
		} catch (IOException e) {
			System.err.println("Command arguments are invalid");
			System.err.println("Valid arguments:\n-path\n-index");
			e.printStackTrace();
		}
	}
}
