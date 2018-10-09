import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONReader {
	
	/**
	 * Does the searches for queries
	 * @param path
	 * @param index
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public static TreeMap<String, TreeMap<String, TreeMap<String, Number>>> searchNested(Path path, TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, String query) throws IOException {
		var invertedindex = new TreeMap<String, TreeMap<String, TreeMap<String, Number>>>();
		String temp = "";
		String filename = path.normalize().toString();
		
		double totalwords = 0;
		double score = 0;
		
		TreeSet<String> querywords = QueryParsing.cleaner(query);

		//TODO Now make the partial (replace .contains to .starts with)
		
		for (String x : querywords.headSet(querywords.last())) {
			temp += x + " ";
		}
		temp += querywords.last();
		invertedindex.put(temp, new TreeMap<String, TreeMap<String, Number>>());
		invertedindex.get(temp).put(filename, new TreeMap<String, Number>());
		invertedindex.get(temp).get(filename).put("count", 0.0);
		
		for (String word: querywords) {
			if (index.containsKey(word)) {
				System.out.println(index.get(word));
				System.out.println(filename);
				invertedindex.get(temp).get(filename).put("count", (double)(invertedindex.get(temp).get(filename).get("count")) + (double)index.get(word).get(filename).size());
			}
		}
		
		for (String word: index.keySet()) {
			for (String file : index.get(word).keySet()) {
				totalwords += index.get(word).get(file).size();
			}
		}
		score = (double)invertedindex.get(temp).get(filename).get("count")/(double)totalwords;
		
		if (score != 0) {
			invertedindex.get(temp).get(filename).put("score", score);
		} else {
			invertedindex.get(temp).remove(filename);
		}
		
		return invertedindex;
	}

	public static void main(String[] args) throws IOException {
		Path index = Paths.get("..", "project-tests", "expected", "index-text", "index-text-simple-animals.json");
		Path path = Paths.get("..", "test.json");
		
		BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
		
	}

}
