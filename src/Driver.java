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
		Path path = null;
		Path index = null;
		Path search = null;

		boolean exact = false;
		
		TreeMap<String, Integer> locations = null; 
		
		var textFiles = new ArrayList<String>();
		TreeMap<String, TreeSet<Integer>> words = null;
		var argmap = new ArgumentMap(args);
		var invertedIndex = new InvertedIndex();
		
		if (argmap.hasFlag("-index") || argmap.hasFlag("-path"))  {
			if (argmap.getPath("-path") != null) {
				path = Paths.get(argmap.getPath("-path").toString());
			}
			
			if (argmap.getPath("-index") != null) {
				index = Paths.get(argmap.getPath("-index").toString());
			} else if (argmap.getPath("-index") == null) {
				index = Paths.get("index.json");
			}
			
			if (argmap.hasFlag("-locations") && (argmap.getPath("-locations") != null)) {
				index = Paths.get(argmap.getPath("-locations").toString());
				locations = new TreeMap<String, Integer>();
				try {
					int wordCount;
					textFiles = TextFileFinder.traverse(path);
					for (String file : textFiles) {
						wordCount = 0;
						words = getWords(Paths.get(file));
						
						for (String word : words.keySet()) {
							wordCount += words.get(word).size();
						}
						
						if (locations != null && wordCount != 0) {
							locations.put(file, wordCount);
						}
					}
				} catch (IOException e) {
				}
			}
			
			try {
				if (path != null && !Files.isDirectory(path)) {
					words = getWords(path);
					for (String word : words.keySet()) {
						invertedIndex.addAllWordFile(word, argmap.getPath("-path").toString(), words.get(word));
					}
				} else if (path != null) {
					textFiles = TextFileFinder.traverse(path);
					for (String file : textFiles) {
						words = getWords(Paths.get(file));
						
						for (String word : words.keySet()) {
							invertedIndex.addAllWordFile(word, file, words.get(word));
						}
					}
				}
				
				if (locations != null) {
					NestedJSON.asObject(locations, index);
				}
				NestedJSON.tripleNested(invertedIndex.getIndex(), index, 0);
			} catch (IOException e1) {
				System.err.println("Cannot get path");
			}
		}
		
		if (argmap.hasFlag("-search") || argmap.hasFlag("-results")) {
			if (argmap.getPath("-search") != null) {
				search = Paths.get(argmap.getPath("-search").toString());
			}
			
			if (argmap.getPath("-results") != null) {
				index = Paths.get(argmap.getPath("-results").toString());
			} else {
				index = Paths.get("results.json");
			}
			
			if (argmap.hasFlag("-exact")) {
				exact = true;
			} else {
				exact = false;
			}
		}

		try (BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);) {
			if (path != null) {
				if (locations != null) {
					NestedJSON.asObject(locations, writer, 0);
				}
				
				if (argmap.hasFlag("-index")) {
					NestedJSON.tripleNested(invertedIndex.getIndex(), index, 0);
				}
				
				if (argmap.hasFlag("-search")) {
					var searchIndex = new TreeMap<String, TreeMap<String, TreeMap<String, Number>>>();
					var query = new TreeSet<String>();
					String line;
					
					// Reads query file
					try (BufferedReader reader = Files.newBufferedReader(search, StandardCharsets.UTF_8);) {
						if (!Files.isDirectory(path)) {
							while ((line = reader.readLine()) != null) {
								query.addAll(QueryParsing.cleaner(line));
								if (!TextParser.clean(line).trim().isEmpty()) {
									searchIndex.putAll(JSONReader.searchNested(path, invertedIndex.getIndex(), TextParser.clean(line), exact));
								}
							}
						} else {
							String queryword;
							var temp = new TreeMap<String, TreeMap<String, TreeMap<String, Number>>>();
							textFiles = TextFileFinder.traverse(path);
							while ((line = reader.readLine()) != null) {
								queryword = TextParser.clean(line).trim();
								query.addAll(QueryParsing.cleaner(line));
								for (String file : textFiles) {
									temp = new TreeMap<String, TreeMap<String, TreeMap<String, Number>>>();
									if (!queryword.isEmpty()) {
										temp.putAll(JSONReader.searchNested(Paths.get(file), invertedIndex.getIndex(), queryword, exact));
										for (String word : temp.keySet()) {
											searchIndex.putIfAbsent(word, temp.get(word));
											if (temp.get(word).get(file) != null) {
												searchIndex.get(word).putIfAbsent(file, temp.get(word).get(file));
											}
										}
									}
								}
							}
						}
						var querymap = QueryParsing.copy(searchIndex);
						System.out.println(querymap);
//						System.out.println(querymap);
//						querymap = QueryParsing.sortMap(querymap);
//						System.out.println("2" + querymap);
						NestedJSON.queryObject(querymap, writer, 0);
					} catch (IOException e) {
					}
				}
			}
		} catch (NoSuchFileException e) {
		} catch (NullPointerException e) {
		} catch (IOException e) {
		}
	}
}
