import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		
		if (argmap.hasFlag("-search") || argmap.hasFlag("-results")) {
			boolean exact;
			Path search;
			
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

			if (argmap.getPath("-search") != null) {
				Path stemmed = Paths.get("stemmedfile.txt");
				search = argmap.getPath("-search");
				try {
					TextFileStemmer.stemFile(search, stemmed);
				} catch (IOException e1) {
				}
				
				try (BufferedReader reader = Files.newBufferedReader(stemmed, StandardCharsets.UTF_8);) {
					var searchIndex = new TreeMap<String, TreeMap<String, TreeMap<String, Double>>>();
					var query = new TreeSet<String>();
					String line;
					Path path = argmap.getPath("-path");
					TreeSet<Query> queries = new TreeSet<Query>();
					
					while ((line = reader.readLine()) != null) {
						query.addAll(QueryParsing.cleaner(line));
						if (!(line = TextParser.clean(line).trim()).isEmpty()) {
							searchIndex.putAll(JSONReader.searchNested(path, invertedIndex.getIndex(), line, exact));
							JSONReader.searcher(queries, path, invertedIndex.getIndex(), line, exact);
						}
					}
					
					NestedJSON.queryObject(queries, index);
				} catch (IOException e) {
				}
			}
		}

//		try (BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);) {
//			if (argmap.hasFlag("-search")) {
//				var searchIndex = new TreeMap<String, TreeMap<String, TreeMap<String, Double>>>();
//				var query = new TreeSet<String>();
//				String line;
//				
//				// Reads query file
//				try (BufferedReader reader = Files.newBufferedReader(search, StandardCharsets.UTF_8);) {
//					if (!Files.isDirectory(path)) {
//						while ((line = reader.readLine()) != null) {
//							query.addAll(QueryParsing.cleaner(line));
//							if (!TextParser.clean(line).trim().isEmpty()) {
//								searchIndex.putAll(JSONReader.searchNested(path, invertedIndex.getIndex(), TextParser.clean(line), exact));
//							}
//						}
//					} else { //Basically where I need to sort
//						String queryword;
//						var temp = new TreeMap<String, TreeMap<String, TreeMap<String, Double>>>();
//						textFiles = TextFileFinder.traverse(path);
//						while ((line = reader.readLine()) != null) {
//							queryword = TextParser.clean(line).trim();
//							query.addAll(QueryParsing.cleaner(line));
//							for (String file : textFiles) {
//								temp = new TreeMap<String, TreeMap<String, TreeMap<String, Double>>>();
//								if (!queryword.isEmpty()) {
//									temp.putAll(JSONReader.searchNested(Paths.get(file), invertedIndex.getIndex(), queryword, exact));
//									for (String word : temp.keySet()) {
//										searchIndex.putIfAbsent(word, temp.get(word));
//										if (temp.get(word).get(file) != null) {
//											searchIndex.get(word).putIfAbsent(file, temp.get(word).get(file));
//										}
//									}
//								}
//							}
//						}
//					}
////						var querymap = QueryParsing.copy(searchIndex);
//					var queries = new TreeSet<Query>();
//					ArrayList<Result> results;
//					Result result;
//					int wordcount = 0;
//					int filecount;
//					double test;
//					for (String word : searchIndex.keySet()) {
////							filecount = 0;
//						for (String file : searchIndex.get(word).keySet()) {
//							results = new ArrayList<Result>();
//							test = (double)(searchIndex.get(word).get(file).get("score"));
//							wordcount = (searchIndex.get(word).get(file).get("count")).intValue();
//							result = new Result(file, wordcount, test);
//							System.out.println(result);
//							queries.add(new Query(word, results));
////								results[filecount] = new Result(file, (double)searchIndex.get(word).get(file).get("count"), (double)searchIndex.get(word).get(file).get("score"));
////								filecount++;
//						}
//					}
////						
////						System.out.println(listQuery[0].toString(0));
//					
////						Query[] listQuery = new Query[querymap.size()];
//					
//					NestedJSON.queryObject(queries, writer);
////						NestedJSON.queryObject(querymap, writer, 0);
//				} catch (IOException e) {
//				}
//			}
//		} catch (NoSuchFileException e) {
//		} catch (NullPointerException e) {
//		} catch (IOException e) {
//		}
	}
}
