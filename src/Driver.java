import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileWriter;
import java.io.IOException;
//import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
//import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import opennlp.tools.stemmer.snowball.*;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

import org.junit.runners.AllTests;

public class Driver {

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
//		System.out.println(words.length());
//		System.out.println("Test " + words);
		
		list = words.split("(?U)\\p{Space}+");
//		System.out.println(list.length);
//		System.out.println("Test " + words);
		for (int i = 0; i < list.length; i++) {
			if (!list[i].trim().isEmpty()) {
				list[i] = stemmer.stem(list[i]).toString();
			}
//			System.out.println("Words " + list[i]);
		}
		
		
		return list;
	}
	
	/**
	 * Gets words from a given text file in the path and nests
	 * them into JSON format. Calls stem method to stem words 
	 * before transforming index to nested JSON.
	 *
	 * @param input path to the input file
	 * @return list list of words in text file
	 * @throws IOException
	 */
	public static TreeMap<String, TreeSet <Integer>> getWords(Path input) throws IOException {
		try (
				BufferedReader reader = Files.newBufferedReader(
						input, StandardCharsets.UTF_8
						);
				) {
			var words = new TreeMap<String, TreeSet <Integer>>();
			var positions = new TreeSet<Integer>();
			int position = 1;
			String line = null;
			String[] list = null;

			while ((line = reader.readLine()) != null) {
				list = stem(line);
				for (String word: list) {
					if (!word.trim().isEmpty()) {
//						System.out.println(word + position);
						if (!words.containsKey(word)) {
							positions.add(position);
							words.put(word, (TreeSet<Integer>) positions.clone());
						} else {
							positions.addAll(words.get(word));
							positions.add(position);
							words.put(word, (TreeSet<Integer>) positions.clone());
						}
						position++;
					}
					positions.clear();
				}
			}
			return words;
		}
	}
	
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

	/**
	 * Writes several tab <code>\t</code> symbols using the provided
	 * {@link Writer}.
	 *
	 * @param times  the number of times to write the tab symbol
	 * @param writer the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void indent(int times, Writer writer) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Writes the element surrounded by quotes using the provided {@link Writer}.
	 *
	 * @param element the element to quote
	 * @param writer  the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Returns the set of elements formatted as a pretty JSON array of numbers.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static String asArray(TreeSet<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asArray(TreeSet<Integer> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers
	 * using the provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer,
			int level) throws IOException {
		if (!elements.isEmpty()) {
			writer.write('[');
			writer.write(System.lineSeparator());
			for (Integer element : elements.headSet(elements.last())) {
				indent(level + 1, writer);
				writer.write(element.toString());
				writer.write(",");
				writer.write(System.lineSeparator());
			} 
			indent(level + 1, writer);
			writer.write(elements.last().toString());
			writer.write(System.lineSeparator());
			indent(level, writer);
			writer.write(']');
		} else {
			writer.write('[');
			writer.write(System.lineSeparator());
			indent(level, writer);
			writer.write(']');
		}
	}

	/**
	 * Returns the map of elements formatted as a pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the map of elements as a pretty JSON object using the provided
	 * {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Writer writer,
			int level) throws IOException {
		writer.write("[");
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write('{');
		writer.write(System.lineSeparator());
		for (String element : elements.keySet()) {
			indent(level + 2, writer);
			quote(element.toString(), writer);
			writer.write(": ");
			writer.write(elements.get(element).toString());
			
			if (!element.equals(elements.lastKey()))
				writer.write(",");
			
			writer.write(System.lineSeparator());
		}
		indent(level, writer);
		writer.write('}');
		writer.write(System.lineSeparator());
		writer.write("]");
	}

	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedObject(TreeMap<String, TreeSet<Integer>> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object
	 * to the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
			writer.close();
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements,
			Writer writer, int level) throws IOException {
		writer.write('{');
		writer.write(System.lineSeparator());
//		System.out.println(elements);
		for (String element : elements.keySet()) {
//			System.out.println("test");
			indent(level + 1, writer);
			quote(element.toString(), writer);
//			writer.write(": {");
//			
//			writer.write(System.lineSeparator());
//			indent(level + 2, writer);
//			quote(element, writer);
			writer.write(": ");
			
//			System.out.println(elements.get(element));
			asArray(elements.get(element), writer, level + 1);
			
//			writer.write(System.lineSeparator());
//			indent(level, writer);
//			writer.write("}");
			
			if (!element.equals(elements.lastKey()))
				writer.write(",");
			writer.write(System.lineSeparator());
		}
		
		indent(level, writer);
		writer.write('}');
		writer.close();

	}
	
	public static String tripleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) throws IOException {
		try {
			StringWriter writer = new StringWriter();
			
			writer.write('{');
			writer.write(System.lineSeparator());
			for (String element : elements.keySet()) {
				indent(1, writer);
				quote(element.toString(), writer);
				writer.write(": ");
				
//				System.out.println(elements.get(element));
				asNestedObject(elements.get(element), writer, 1);
				
//				writer.write(System.lineSeparator());
//				indent(1, writer);
//				writer.write("L");
				if (!element.equals(elements.lastKey()))
					writer.write(",");
				writer.write(System.lineSeparator());
			}
			writer.write('}');
			writer.close();
//			asNestedObject(elements, writer, 0, file);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Path index = Paths.get(".");
		Path path = Paths.get(".");
		var textFiles = new ArrayList<String>();
//		var getFile = new ArrayList<String>();
		BufferedWriter writer;
		String[] validArguments = {"-path", "-index"};
		TreeMap<String, TreeSet<Integer>> words;
		TreeMap<String, TreeMap<String, TreeSet<Integer>>> allwords = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();;
		var temp = new TreeMap<String, TreeSet<Integer>>();
		
		if (!(args.length > 2) 
				|| (!args[0].equals(validArguments[0]) && !args[2].equals(validArguments[1])
				&& !args[0].equals(validArguments[1]) && !args[2].equals(validArguments[0]))) {
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
			System.out.println(args[2]);
			if (args[0].equals(validArguments[0])) {
				path = Paths.get(args[1]);
//				System.out.println(path.toAbsolutePath());
				if (args[2] == validArguments[1] && args.length > 3) {
					index = Paths.get(index.toAbsolutePath().normalize().getParent().toString(), "project-tests", args[3]);
				} else {
					index = Paths.get(index.toAbsolutePath().normalize().getParent().toString(), "project-tests", "out", "index.json");
				}
			} else if ((args[2].equals(validArguments[0]) && args.length > 3) || args[1].equals(validArguments[0])) {
				path = Paths.get(args[3]);
				if (args[0].equals(validArguments[1])  && !args[1].equals(validArguments[0])) {
					index = Paths.get(index.toAbsolutePath().normalize().getParent().toString(), "project-tests", args[1]);
				} else {
					index = Paths.get(index.toAbsolutePath().normalize().getParent().toString(), "project-tests", "out", "index.json");
				}
			}
			writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);
			
			
			if (!Files.exists(path)) {
			    System.err.println("Path does not exist");
			} else if (Files.isDirectory(path)) {
				textFiles = traverse(path);
				for (String file : textFiles) {
					
					words = getWords(Paths.get(file));
//					System.out.println(words);
					for (String word : words.keySet()) {

						temp.put(file, words.get(word));
						if (allwords.containsKey(word) && !allwords.get(word).containsKey(path.toString() + "\\" + file)) {
							allwords.get(word).put(file, words.get(word));
						} else if (!allwords.containsKey(word)){
							allwords.put(word, (TreeMap<String, TreeSet<Integer>>) temp.clone());
						}
						temp.clear();
					}
					
//					System.out.println(tripleNested(allwords));
				}
				writer.write(tripleNested(allwords));
				writer.close();
			} else {
				File indexJSON = new File(index.toAbsolutePath().normalize().toString());
				if (index.endsWith("index.json")) {
					indexJSON.delete();
				}
				
				words = getWords(path);
				
//				System.out.println(words.keySet());
				for (String word : words.keySet()) {
					temp.put(path.toString(), words.get(word));
					allwords.put(word, (TreeMap<String, TreeSet<Integer>>) temp.clone());
//					allwords.put(word, temp);
					temp.clear();
				}
				writer.write(tripleNested(allwords));
//				System.out.println(tripleNested(allwords));
				writer.close();
//				System.out.println("Finish");
			}
		}
	}

}
