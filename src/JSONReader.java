import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONReader {
//	public static TreeMap<String, TreeMap<String, TreeMap<String, Number>>> invertedindex = new TreeMap<String, TreeMap<String, TreeMap<String, Number>>>();
	
	public static TreeMap<String, TreeMap<String, TreeMap<String, Number>>> searchNested(Path path, Path index, String query) throws IOException {
		var invertedindex = new TreeMap<String, TreeMap<String, TreeMap<String, Number>>>();
		try (BufferedReader reader = Files.newBufferedReader(index, StandardCharsets.UTF_8);) {
			String filename = path.normalize().toString();
			String line;
			double wordcount = 0;
			
			TreeSet<String> querywords = QueryParsing.cleaner(query);
			System.out.println(querywords);
			
			invertedindex.put(querywords.first(), new TreeMap<String, TreeMap<String, Number>>());
			invertedindex.get(querywords.first()).put(filename, new TreeMap<String, Number>());
			invertedindex.get(querywords.first()).get(filename).put("count", 0.0);
			String regex = "[0-9, /,]+";
			while ((line = reader.readLine()) != null) {
				if (line.trim().matches(regex)) {
					wordcount++;
				}
				
				for (String word : querywords) {
					for (String textwords : QueryParsing.cleaner(line)) {
						if (textwords.equals(word)) {
							line = reader.readLine();
							while (!((line = reader.readLine()).contains("]"))) {
								invertedindex.get(querywords.first()).get(filename).put("count", (double)(invertedindex.get(querywords.first()).get(filename).get("count")) + 1);
								wordcount++;
							}
						}
					}
				}
			}
			invertedindex.get(querywords.first()).get(filename).put("score", (double)invertedindex.get(querywords.first()).get(filename).get("count")/wordcount);
		}
		return invertedindex;
	}

	public static void main(String[] args) throws IOException {
		Path index = Paths.get("..", "project-tests", "expected", "index-text", "index-text-simple-animals.json");
		Path path = Paths.get("..", "test.json");
		
		BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
		
	}

}
