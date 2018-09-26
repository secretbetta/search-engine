import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import opennlp.tools.stemmer.snowball.*;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

// TODO Remove old TODO comments
// TODO Address any warnings

// TODO Configure Eclipse to remove unused imports on save. (Check Piazza)
// TODO Remove commented-out debug code from "production-ready" release

//import org.junit.runners.AllTests;

public class Driver {
	
	/*
	 * TODO Move stem() and getWords() to a different class dedicated to building
	 * an inverted index from text file.
	 */
	
	/**
	 * Removes special characters from text, lower-case text, 
	 * and stems the words.
	 * 
	 * @param words String of words to stem
	 * @return list List of stemmed words
	 */
	public static String[] stem(String words) {
		SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
		String[] list;
		words = Normalizer.normalize(words, Normalizer.Form.NFD);
		words = words.replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+", "").toLowerCase();
		
		list = words.split("(?U)\\p{Space}+");

		for (int i = 0; i < list.length; i++) {
			if (!list[i].trim().isEmpty()) {
				list[i] = stemmer.stem(list[i]).toString();
			}
		}
		
		
		return list;
	}
	
	/**
	 * Gets words from a given text file. Adds word and position into TreeMap.
	 * Words may have more than one position in a text file.
	 * <p>key = word(s) from text files</p>
	 * <p>value = position(s) of word from text files</p>
	 * @param input path to the input file
	 * @return list list of words in text file
	 * @throws IOException
	 */
	public static TreeMap<String, TreeSet <Integer>> getWords(Path input) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);) {
			TextFileStemmer stem = new TextFileStemmer();
			var words = new TreeMap<String, TreeSet <Integer>>();
			var positions = new TreeSet<Integer>();
			int position = 1;
			String line = null;
			List<String> list = null;

			while ((line = reader.readLine()) != null) {
				list = stem.stemLine(line);
				for (String word: list) {
					positions.add(position);
					if (!words.containsKey(word)) {
						words.put(word, positions);
					} else {
						positions.addAll(words.get(word));
						words.put(word, positions);
					}
					position++;
					
					positions = new TreeSet<Integer>();
				}
			}
			return words;
		}
	}
	
	/*
	 * TODO 
	 * Move generally useful code outside of Driver into a different class
	 * Keep Driver project-specific (i.e. limited to just parsing and responding
	 * to command-line parameters).
	 */
	
	/**
	 * Finds text files by retrieving subdirectories until hitting files.
	 * If file has .txt or .text meaning that it is a text file, return a
	 * list of the files.
	 *
	 * The recursive version of this method is private. Users of this class will
	 * have to use the public version (see below).
	 *
	 * @param path   to retrieve the listing, assumes a directory and not a file
	 *               is passed
	 * @return textlist a list of text file names
	 * @throws IOException
	 */
	private static ArrayList<String> findText(Path path) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			ArrayList<String> textlist = new ArrayList<String>();
			for (Path file : listing) {
				// TODO Convert the filename toString and toLowercase so you only need to do it once
				if (Files.isDirectory(file)) {
					textlist.addAll(findText(file));
				} else if (file.getFileName().toString().toLowerCase().endsWith(".txt")
						|| file.getFileName().toString().toLowerCase().endsWith(".text")) {
					textlist.add(file.toString());
				}
			}
			return textlist;
		}
	}

	/**
	 * Safely starts the recursive traversal with the proper padding. 
	 * Finds all text files in the directory.
	 *
	 * @param directory to traverse
	 * @return findText a list of text names, null if none.
	 * @throws IOException
	 */
	public static ArrayList<String> traverse(Path directory) throws IOException {
		var single = new ArrayList<String>();
		if (Files.isDirectory(directory)) {
			return findText(directory);
		} else {
			if (directory.getFileName().toString().toLowerCase().contains("txt") 
					|| directory.getFileName().toString().toLowerCase().contains("text")) {
				single.add(directory.getFileName().toString());
				return single;
			} else {
				return null;
			}
		}
	}
	
	/*
	 * TODO
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
	public static void main(String[] args) throws IOException {
		boolean flag = true;
		
		Path index = Paths.get(".");
		Path path = Paths.get(".");
		
		var textFiles = new ArrayList<String>();

		// TODO Try to move this nested data structure into its own class
		// TODO This could be a data-structure like class that just stores stuff
		// TODO and doesn't parse stuff.
		TreeMap<String, TreeSet<Integer>> words;
		TreeMap<String, TreeMap<String, TreeSet<Integer>>> allwords = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();;
		
		var wordIndex = new TreeMap<String, TreeSet<Integer>>();
		
		ArgumentMap argmap = new ArgumentMap(args);
		
		/*
		 * TODO
		 * Modify the logic slightly...
		 * 
		 * if (-path flag) {
		 * 		trigger building the index in this block of code
		 * }
		 * 
		 * if (-index flag) {
		 * 		trigger the writing of the index in this block of code
		 * }
		 */
		
		if (argmap.hasFlag("-path") && !(argmap.getPath("-path") == null)) {
			path = Paths.get(argmap.getPath("-path").toString());
		} else {
			flag = false;
		}

		if (argmap.hasFlag("-index") && argmap.getPath("-index") == null) {
			index = Paths.get("index.json");
		} else if (argmap.hasFlag("-index") && !(argmap.getPath("-index") == null)) {
			index = Paths.get(argmap.getPath("-index").toString());
		} else {
			index = Paths.get("out", "index.json");
		}
		
		// TODO Never convert the path to absolute, then you won't have to worry about making it relative later. 
		
		try (BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8)) {
			// TODO This is great for this project, but is goign to quickly become unmanageable. Simplify and remove this check.
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
			} else { //The actual program
				if (!Files.exists(path)) {
				    System.err.println("Path does not exist");
				} else if (Files.isDirectory(path)) {
					textFiles = traverse(path);
					for (String file : textFiles) {
						
						words = getWords(Paths.get(file));
						for (String word : words.keySet()) {
							// TODO Shouldn't actually need to convert paths to relative here...
							wordIndex.put(file.substring(file.indexOf("text")), words.get(word));
							
							if (allwords.containsKey(word)) {
								allwords.get(word).put(file.substring(file.indexOf("text")), words.get(word));
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
		}
	}
}
