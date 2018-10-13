import java.io.BufferedReader;
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

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		
		Path index = null;
		Path locIndex = null;
		
		var invertedIndex = new InvertedIndex();
		var argmap = new ArgumentMap(args);
		
		if (argmap.hasFlag("-index")) {
			if (argmap.getPath("-index") != null) {
				index = argmap.getPath("-index");
			} else {
				index = Paths.get("index.json");
			}
		}
		
		if (argmap.hasFlag("-locations")) {
			if (argmap.getPath("-locations") != null) {
				locIndex = argmap.getPath("-locations");
			} else {
				locIndex = Paths.get("out", "index-text-locations.json");
			}
		}
		
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
		
		try {
			if (argmap.hasFlag("-locations")) {
				TreeMap<String, Integer> locationIndex = LocationIndex.indexLocation(invertedIndex);
				NestedJSON.asObject(locationIndex, locIndex);
			}
			
			NestedJSON.tripleNested(invertedIndex.getIndex(), index);
		} catch (NullPointerException e) { 
		} catch (IOException e) {
		}
		
		if (argmap.hasFlag("-results")) {
			if (argmap.getPath("-results") != null) {
				index = Paths.get(argmap.getPath("-results").toString());
			} else {
				index = Paths.get("out", "results.json");
			}
		}
		
		if (argmap.hasFlag("-search")) {
			boolean exact;
			Path search;
			
			if (argmap.hasFlag("-exact")) {
				exact = true;
			} else {
				exact = false;
			}

			if (argmap.getPath("-search") != null) {
				Path stemmed = Paths.get("stemmedfile.txt");
				search = argmap.getPath("-search");
				try {
					TextFileStemmer.stemFile(search, stemmed);
				} catch (IOException e1) {
				}
				
				try (BufferedReader reader = Files.newBufferedReader(stemmed, StandardCharsets.UTF_8);) {
					var query = new TreeSet<String>();
					String line;
					Path path = argmap.getPath("-path");
					TreeSet<Query> queries = new TreeSet<Query>();
					
					ArrayList<Path> files = null;
					
					if (path != null) {
						files = TextFileFinder.traverse(path);
						for (Path file : files) {
							while ((line = reader.readLine()) != null) {
								query.addAll(QueryParsing.cleaner(line));
								if (!(line = TextParser.clean(line).trim()).isEmpty()) {
									JSONReader.searcher(queries, file, invertedIndex.getIndex(), line, exact);
								}
							}
						}
					}
					if (index != null) {
						NestedJSON.queryObject(queries, index);
					}
				} catch (IOException e) {
				}
			}
		}
	}
}
