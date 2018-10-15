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
				index = Paths.get("results.json");
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
					String line;
					Path path = argmap.getPath("-path");
					
					TreeMap<String, ArrayList<Result>> queries = new TreeMap<String, ArrayList<Result>>();
					
					ArrayList<Path> files = null;
					
					if (path != null) {
						files = TextFileFinder.traverse(path);
						while ((line = reader.readLine()) != null) {
							for (Path file : files) {
								if (!(line = TextParser.clean(line).trim()).isEmpty()) {
									JSONReader.searcher(queries, file, invertedIndex.getIndex(), line, exact);
								}
							}
						}
					}
					
					for (String que : queries.keySet()) {
						Collections.sort(queries.get(que));
					}
					
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
}
