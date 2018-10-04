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

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/*
 * TODO 
 * For production code, do not output any stack traces. Instead, output the user
 * friendly error messages.
 */

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
	public static TreeMap<String, TreeSet<Integer>> getWords(Path input) // TODO Put this not in driver. (Put it in a "buidler" class.)
			throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);) {
			
			int position = 0;
			
			var stem = new TextFileStemmer(); // TODO Access methods staticly 
			var wordIndex = new WordIndex(); // TODO Try not to use
			
			String line = null;
			List<String> list = null;
			
			// TODO Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH)
			// TODO Use one of these per file
			
			while ((line = reader.readLine()) != null) {
				/*
				 * TODO
				 * Using temporary storage, want to avoid... efficiency is a case
				 * where you can copy/paste!
				 * 
				 * Where stemLine was adding to a list, instead add to an index.
				 */
				list = stem.stemLine(line);
				for (String word: list) {
					position++;
					wordIndex.add(word, position);
				}
			}
			
			return wordIndex.getAll();
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
		
		var textFiles = new ArrayList<String>();
		TreeMap<String, TreeSet<Integer>> words;
		var argmap = new ArgumentMap(args);
		var finder = new TextFileFinder();
		var invertedIndex = new InvertedIndex();
		
		/*
		TODO
		var index = new InvertedIndex();
		var argmap = new ArgumentMap(args);

		if (argmap.hasFlag("-path")) {
			Path path = argumap.getPath("-path");
			
			try {
				List<Path> files = finder.traverse(path);
				for (each file) {
					getWords(file, index);
				}
			}
			catch ( ) {
			
			}
		}

		if (argmap.hasFlag(-index)) {
			try (BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);) {
				writer.write(NestedJSON.tripleNested(invertedIndex.getIndex()));
			}
			catch () {
			
			}
		
		}
		
		For next code review, can try to add -locations flag to your project 1 code.

		*/

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
			
			if (argmap.hasFlag("-search")) {
				
			}

			// TODO try-with-resources
			BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);
			
			if (path != null) {
				if (Files.isDirectory(path)) {
					textFiles = finder.traverse(path);
					
					//Test
					System.out.println("Found all text files");
					int i = 0;
					//Test
					
					for (String file : textFiles) {
						words = getWords(Paths.get(file));
						
						System.out.println("Got words " + i++);
						
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
			} else {
				System.err.println("Did not input a path");
			}
		} catch (NoSuchFileException e) {
			System.err.println("Cannot find path " + path);
		} catch (IOException e) {
			System.err.println("Command arguments are invalid");
			System.err.println("Valid arguments:\n-path\n-index");
			e.printStackTrace(); // TODO No stack traces!
		}
	}
}
