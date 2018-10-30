import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class Driver {
	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		var argmap = new ArgumentMap(args);
		InvertedIndex invertedIndex = new InvertedIndex();
		
		if (argmap.hasFlag("-path")) {
			Path path = argmap.getPath("-path");
			List<Path> files = null;
			
			try {
				files = TextFileFinder.traverse(path);
				
				for (Path file : files) {
					IndexBuilder.getWords(file, invertedIndex);
				}
			} catch (NullPointerException e) {
				System.err.println("Unable to create inverted index. Has no elements");
			} catch (IOException e) {
				System.err.println("Unable to get path from " + path);
			}
		}
		
		if (argmap.hasFlag("-index")) {
			Path index = null;
			index = argmap.getPath("-index", Paths.get("index.json"));
			
			try {
				invertedIndex.toJSON(index);
			} catch (IOException e) {
				System.err.println("Cannot write to index " + index);
			}
		}
		
		Path locIndex = null;
		if (argmap.hasFlag("-locations")) {
			if (argmap.getPath("-locations") != null) {
				locIndex = argmap.getPath("-locations");
			} else {
				locIndex = Paths.get("out", "index-text-locations.json");
			}
//			TreeMap<String, Integer> locationIndex = LocationIndex.indexLocation(invertedIndex);
//			try {
//				NestedJSON.asObject(locationIndex, locIndex);
//			} catch (IOException e) {
//			}
		}
		TreeMap<String, Integer> locationIndex = LocationIndex.indexLocation(invertedIndex);
		try {
			if (locIndex != null) {
				NestedJSON.asObject(locationIndex, locIndex);
			}
		} catch (IOException e) {
		}
		
//		Path index = null;
		if (argmap.hasFlag("-results")) {
			Path index;
			if (argmap.getPath("-results") != null) {
				index = Paths.get(argmap.getPath("-results").toString());
			} else {
				index = Paths.get("results.json");
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
					search = argmap.getPath("-search");
					
					//TODO Try to clean this up
					try (BufferedReader reader = Files.newBufferedReader(search, StandardCharsets.UTF_8);) {
						String line;
						Path path = argmap.getPath("-path");
						
						TreeMap<String, ArrayList<Result>> queries = new TreeMap<String, ArrayList<Result>>();
						
						ArrayList<Path> files = null;
						
						if (path != null) {
							files = TextFileFinder.traverse(path);
							
							List<String> que;
							while ((line = reader.readLine()) != null) {
								que = TextFileStemmer.stemLine(line); //TODO Copy code from TextFileStemmer instead of using it like this
								//The lag starts here hmmmm
								//TODO STOP THE LAG
								for (Path file : files) {
									if (!(line = TextParser.clean(line).trim()).isEmpty()) {
										JSONReader.searcher(locationIndex, queries, file, invertedIndex.getIndex(), que, exact);
									}
								}
								//TODO Change que to string and use cleaner on it to sort queries here
							}
							System.out.println("Completed JSONReader");
						}
						
						for (String que : queries.keySet()) {
							Collections.sort(queries.get(que));
						}
						System.out.println("Sorted");
						
						if (index != null) {
							NestedJSON.queryObject(queries, index);
						}
					} catch (IOException e) {
					}
				}
			} else {
				//Had to do this weird thing to complete EmptyQuery
				if (argmap.hasFlag("-results")) {
					try {
						if (argmap.getPath("-results") != null) {
							index = Paths.get(argmap.getPath("-results").toString());
						} else {
							index = Paths.get("results.json");
						}
						BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);
						writer.write("[\n]");
						writer.close();
					} catch (IOException e) {
					}
				}
			}
		}
		
//		if (argmap.hasFlag("-search")) {
//			boolean exact;
//			Path search;
//			
//			if (argmap.hasFlag("-exact")) {
//				exact = true;
//			} else {
//				exact = false;
//			}
//
//			if (argmap.getPath("-search") != null) {
//				search = argmap.getPath("-search");
//				
//				//TODO Try to clean this up
//				try (BufferedReader reader = Files.newBufferedReader(search, StandardCharsets.UTF_8);) {
//					String line;
//					Path path = argmap.getPath("-path");
//					
//					TreeMap<String, ArrayList<Result>> queries = new TreeMap<String, ArrayList<Result>>();
//					
//					ArrayList<Path> files = null;
//					
//					if (path != null) {
//						files = TextFileFinder.traverse(path);
//						
//						List<String> que;
//						while ((line = reader.readLine()) != null) {
//							que = TextFileStemmer.stemLine(line); //TODO Copy code from TextFileStemmer instead of using it like this
//							//The lag starts here hmmmm
//							//TODO STOP THE LAG
//							for (Path file : files) {
//								if (!(line = TextParser.clean(line).trim()).isEmpty()) {
//									JSONReader.searcher(locationIndex, queries, file, invertedIndex.getIndex(), que, exact);
//								}
//							}
//							//TODO Change que to string and use cleaner on it to sort queries here
//						}
//						System.out.println("Completed JSONReader");
//					}
//					
//					for (String que : queries.keySet()) {
//						Collections.sort(queries.get(que));
//					}
//					System.out.println("Sorted");
//					
//					if (index != null) {
//						NestedJSON.queryObject(queries, index);
//					}
//				} catch (IOException e) {
//				}
//			}
//		} else {
//			//Had to do this weird thing to complete EmptyQuery
//			if (argmap.hasFlag("-results")) {
//				try {
//					if (argmap.getPath("-results") != null) {
//						index = Paths.get(argmap.getPath("-results").toString());
//					} else {
//						index = Paths.get("results.json");
//					}
//					BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);
//					writer.write("[\n]");
//					writer.close();
//				} catch (IOException e) {
//				}
//			}
//		}
	}
}
