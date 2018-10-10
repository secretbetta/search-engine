import java.io.IOException;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONReader {
	/**
	 * Does the searches for queries in the inverted index
	 * @param path Filename to put into index
	 * @param index The inverted index data
	 * @param query The words to search for
	 * @param exact Whether to be exact or just do a partial search
	 * @return The TreeMap of results
	 * 
	 * @throws IOException
	 */
	public static TreeMap<String, TreeMap<String, TreeMap<String, Number>>> searchNested(Path path, TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, String query, boolean exact) throws IOException {
		var invertedindex = new TreeMap<String, TreeMap<String, TreeMap<String, Number>>>();
		String temp = "";
		String filename = path.normalize().toString();
		
		double totalwords = 0;
		double score = 0;
		
		TreeSet<String> querywords = QueryParsing.cleaner(query);
		
		for (String x : querywords.headSet(querywords.last())) {
			temp += x + " ";
		}
		
		temp += querywords.last();
		invertedindex.put(temp, new TreeMap<String, TreeMap<String, Number>>());
		invertedindex.get(temp).put(filename, new TreeMap<String, Number>());
		invertedindex.get(temp).get(filename).put("count", 0.0);
		
		for (String word: querywords) {
			if (exact) {
				if (index.containsKey(word)) {
					if (index.get(word).containsKey(filename)) {
						invertedindex.get(temp).get(filename).put("count", (double)(invertedindex.get(temp).get(filename).get("count")) + (double)index.get(word).get(filename).size());
					}
				}
			} else if (!exact) {
				for (String key : index.keySet()) {
					if (key.startsWith(word)) {
						if (index.get(key).containsKey(filename)) {
							invertedindex.get(temp).get(filename).put("count", (double)(invertedindex.get(temp).get(filename).get("count")) + (double)index.get(key).get(filename).size());
						}
					}
				}
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
}
