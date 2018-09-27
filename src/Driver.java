import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
		try (BufferedReader reader = 
				Files.newBufferedReader(input, StandardCharsets.UTF_8);) {
			
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

	/*
	 * TODO No throwing exceptions, catch them here
	 * Driver.main() is the only class that shouldn't throw an exception.
	 * Catch those exceptions here, output user-friendly error messages
	 * (Try to provide context.)
	 * "Unable to build index from path: " + path
	 */

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		boolean flag = false;
		
		Path index = Paths.get("index.json");
		Path path = Paths.get(".");
		
		var textFiles = new ArrayList<String>();

		// TODO Try to move this nested data structure into its own class. 
		// This could be a data-structure like class that just stores stuff 
		// and doesn't parse stuff.
		TreeMap<String, TreeSet<Integer>> words;
		var allwords = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();;
		var wordIndex = new TreeMap<String, TreeSet<Integer>>();
		var argmap = new ArgumentMap(args);
		var finder = new TextFileFinder();
		
		/*
		 * @TODO Modify the logic slightly... (Not sure if I did this correctly)
		 * 
		 * if (-path flag) {
		 * 		trigger building the index in this block of code
		 * }
		 * 
		 * if (-index flag) {
		 * 		trigger the writing of the index in this block of code
		 * }
		 * 
		 */
		
		if (argmap.hasFlag("-path")) {
			if (argmap.hasFlag("-index") && !(argmap.getPath("-index") == null)) {
				index = Paths.get(argmap.getPath("-index").toString());
			} else if (argmap.hasFlag("-index")) {
				index = Paths.get("index.json");
			} else {
				index = Paths.get("out", "index.json");
			}
			
			if (!(argmap.getPath("-path") == null)) {
				path = Paths.get(argmap.getPath("-path").toString());
				flag = true;
			}
		}
		
//		if (argmap.hasFlag("-path") && !(argmap.getPath("-path") == null)) {
//			path = Paths.get(argmap.getPath("-path").toString());
//		} else {
//			flag = false;
//		}
//
//		if (argmap.hasFlag("-index") && argmap.getPath("-index") == null) {
//			index = Paths.get("index.json");
//		} else if (argmap.hasFlag("-index") && !(argmap.getPath("-index") == null)) {
//			index = Paths.get(argmap.getPath("-index").toString());
//		} else {
//			index = Paths.get("out", "index.json");
//		}
		
		try (BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8)) {
			
			// TODO This is great for this project, but is going to quickly become unmanageable. 
			// Simplify and remove this check.
			if (!flag) {
						System.err.println("Command line argument not valid."
								+ "\nValid arguments: "
								+ "\n-path path where the flag -path indicates the next argument "
								+ "is a path to either a single text file or a directory of text "
								+ "files that must be processed and added to the inverted index"
								+ "\n-index where the flag -index is an optional flag that indicates "
								+ "the next argument is the path to use for the inverted index output "
								+ "file. If the path argument is not provided, use index.json as the "
								+ "default output path. If the -index flag is not provided, do not "
								+ "produce an output file.");
			} else {
				if (!Files.exists(path)) {
				    System.err.println("Path does not exist");
				} else if (Files.isDirectory(path)) {
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
					writer.write(NestedJSON.tripleNested(allwords));
				} else {
					
					words = getWords(path);
					
					for (String word : words.keySet()) {
						wordIndex.put(argmap.getPath("-path").toString(), words.get(word));
						allwords.put(word, wordIndex);
						wordIndex = new TreeMap<String, TreeSet<Integer>>();
					}
					
					writer.write(NestedJSON.tripleNested(allwords));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
