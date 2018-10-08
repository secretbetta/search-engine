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
	 * Searches for words by query. Can look for exact or partial searches. 
	 * @param text Textfile to look at
	 * @param query What words to look for
	 * @param index2 Place to write the results
	 * @param exact Search for exact keywords or not
	 * @throws IOException
	 */
	public static void search(Path text, Path query, Path indexfile, Boolean exact) {
		var queries = new TreeSet<String>();
		var index = new TreeMap<String, TreeMap<String, TreeMap<String, Number>>>();
		String filename = text.normalize().toString();
		
		int wordcount = 0;
		
		String line;
		try (BufferedReader reader = Files.newBufferedReader(query, StandardCharsets.UTF_8);) {
			while ((line = reader.readLine()) != null) {
				queries.addAll(QueryParsing.cleaner(line));
			}
		} catch (IOException e) {
			System.err.println("Cannot read file");
		}
		
		try (BufferedReader reader = Files.newBufferedReader(text, StandardCharsets.UTF_8);) {
			while ((line = reader.readLine()) != null) {
				for (String textword : QueryParsing.cleaner(line)) {
					if (queries.contains(textword)) {
						index.putIfAbsent(textword, new TreeMap<String, TreeMap<String, Number>>());
						index.get(textword).putIfAbsent(filename, new TreeMap<String, Number>());
						index.get(textword).get(filename).putIfAbsent("count", 0);
						index.get(textword).get(filename).put("count", (int)(index.get(textword).get(filename).get("count")) + 1);
					}
					wordcount++;
				}
			}
			double score;
			for (String word : index.keySet()) {
				score = (int)(index.get(word).get(filename).get("count"))/(double)(wordcount);
				index.get(word).get(filename).put("score", score);
			}
		} catch (IOException e) {
			System.err.println("Cannot find text file");
		}
		
		try (BufferedWriter writer = Files.newBufferedWriter(indexfile, StandardCharsets.UTF_8);) {
			NestedJSON.queryObject(index, writer, 0);
		} catch (IOException e) {
			System.err.println("Cannot find index file");
		}
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
	public static TreeMap<String, TreeSet<Integer>> getWords(Path input) 
			throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);) {
			int position = 0;
			
			var index = new TreeMap<String, TreeSet<Integer>>();
			
			String line;
			List<String> list;
			
			while ((line = reader.readLine()) != null) {
				list = TextFileStemmer.stemLine(line);
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

		boolean exact = false;
		
		String driver = "index";
		
		TreeMap<String, Integer> locations = null; 
		
		var textFiles = new ArrayList<String>();
		TreeMap<String, TreeSet<Integer>> words;
		var argmap = new ArgumentMap(args);
		var finder = new TextFileFinder();
		var invertedIndex = new InvertedIndex();
		
		if (argmap.hasFlag("-path")) {
			if (argmap.getPath("-path") != null) {
				path = Paths.get(argmap.getPath("-path").toString());
			}
		}
		
		if (argmap.hasFlag("-index") && argmap.getPath("-index") != null) {
			index = Paths.get(argmap.getPath("-index").toString());
		} else if (argmap.hasFlag("-index") && argmap.getPath("-index") == null) {
			index = Paths.get("index.json");
		} else {
			index = Paths.get("out", "index.json");
		}
		
		if (argmap.hasFlag("-search") && argmap.getPath("-search") != null) {
			search = Paths.get(argmap.getPath("-search").toString());
			
			if (argmap.hasFlag("-results") && argmap.getPath("-results") != null) {
				index = Paths.get(argmap.getPath("-results").toString());
			} else if (argmap.hasFlag("-results")) {
				index = Paths.get("results.json");
			}
			
			if (argmap.hasFlag("-exact")) {
				exact = true;
			} else {
				exact = false;
			}
			driver = "search";
		}
		
		if (argmap.hasFlag("-results") && argmap.getPath("-results") != null) {
			index = Paths.get(argmap.getPath("-results").toString());
		} else if (argmap.hasFlag("-results")) {
			index = Paths.get("results.json");
		}
		
		if (argmap.hasFlag("-locations") && (argmap.getPath("-locations") != null)) {
			index = Paths.get(argmap.getPath("-locations").toString());
			locations = new TreeMap<String, Integer>();
		}
		
		if (driver.equals("search")) {
			search(path, search, index, exact);
		}
		if (driver.equals("index")) {
			try (BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);) {
				int wordCount;
				if (path != null) {
					if (Files.isDirectory(path)) {
						textFiles = finder.traverse(path);
						for (String file : textFiles) {
							wordCount = 0;
							words = getWords(Paths.get(file));
							
							for (String word : words.keySet()) {
								invertedIndex.addAllWordFile(word, file, words.get(word));
								wordCount += words.get(word).size();
							}
							
							if (locations != null && wordCount != 0) {
								locations.put(file, wordCount);
							}
						}
					} else {
						words = getWords(path);
						
						for (String word : words.keySet()) {
							invertedIndex.addAllWordFile(word, argmap.getPath("-path").toString(), words.get(word));
						}
						
					}
					if (locations != null) {
						NestedJSON.asObject(locations, writer, 0);
					}
					writer.write(NestedJSON.tripleNested(invertedIndex.getIndex()));
				}
			} catch (NoSuchFileException e) {
	//			System.err.println("Cannot find path " + path);
			} catch (IOException e) {
	//			System.err.println("Command arguments are invalid");
	//			System.err.println("Valid arguments:\n-path\n-index");
			}
		}
	}
}
