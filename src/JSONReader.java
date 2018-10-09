import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONReader {
	public static TreeMap<String, TreeMap<String, TreeMap<String, Number>>> invertedindex = new TreeMap<String, TreeMap<String, TreeMap<String, Number>>>();
	
	public static TreeMap<String, TreeMap<String, TreeMap<String, Number>>> searchNested(Path index, String query) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(index, StandardCharsets.UTF_8);) {
			String filename = index.normalize().toString();
			String line;
			double wordcount = 0;
			
			TreeSet<String> querywords = QueryParsing.cleaner(query);
			
			invertedindex.put(query, new TreeMap<String, TreeMap<String, Number>>());
			invertedindex.get(query).put(filename, new TreeMap<String, Number>());
			invertedindex.get(query).get(filename).put("count", 0.0);
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
								invertedindex.get(query).get(filename).put("count", (double)(invertedindex.get(query).get(filename).get("count")) + 1);
								wordcount++;
							}
						}
					}
				}
			}
			invertedindex.get(query).get(filename).put("score", (double)invertedindex.get(query).get(filename).get("count")/wordcount);
		}
		return invertedindex;
	}

	public static void main(String[] args) throws IOException {
		String line = "loris\r\n";
		String temp = "1";
		
		Path index = Paths.get("..", "project-tests", "expected", "index-text", "index-text-simple-animals.json");
		System.out.println(searchNested(index, TextParser.clean(line)));
		
	}

}
