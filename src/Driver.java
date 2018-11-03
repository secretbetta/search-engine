import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * Driver of Project Codes
 * @author Andrew
 *
 */
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
		
		int threads = 1;
		if (argmap.hasFlag("-threads")) {
			threads = argmap.getInt("-threads", 5);
		}
		
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
		}
		
		TreeMap<String, Integer> locationIndex = LocationIndex.indexLocation(invertedIndex);
		try {
			if (locIndex != null) {
				NestedJSON.asObject(locationIndex, locIndex);
			}
		} catch (IOException e) {
			System.err.println("Cannot write to file " + locIndex);
		}
		
		TreeMap<String, ArrayList<Result>> queries = null;
		if (argmap.hasFlag("-search")) {
			boolean exact;
			Path search;
			
			if (argmap.hasFlag("-exact")) {
				exact = true;
			} else {
				exact = false;
			}

			search = argmap.getPath("-search");
			String line;
			Path path = argmap.getPath("-path");
			
			queries = new TreeMap<String, ArrayList<Result>>();
			
			try (BufferedReader reader = Files.newBufferedReader(search, StandardCharsets.UTF_8);) {
				
				ArrayList<Path> files = null;
				
				if (path != null) {
					files = TextFileFinder.traverse(path);
					
					List<String> que;
					
					while ((line = reader.readLine()) != null) {
						que = TextFileStemmer.stemLine(line);
						for (Path file : files) {
							if (!(line = TextParser.clean(line).trim()).isEmpty()) {
								IndexReader.searcher(locationIndex, queries, file, invertedIndex.getIndex(), que, exact);
							}
						}
					}
				}
				
				for (String que : queries.keySet()) {
					Collections.sort(queries.get(que));
				}
			} catch (IOException e) {
				System.err.println("Cannot read from path " + search);
			}
		}
		
		if (argmap.hasFlag("-results")) {
			Path index;
			index = argmap.getPath("-results", Paths.get("results.json"));
			
			try {
				NestedJSON.queryObject(queries, index);
			} catch (NullPointerException e) {
				System.err.println("Cannot write query from empty index");
			} catch (IOException e) {
				System.err.println("Cannot write query at path " + index);
			}
		}
	}
}
