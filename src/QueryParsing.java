import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;


public class QueryParsing {
	
	/**
	 * Turns string line to cleaned and parsed hashset of words.
	 * Removes duplicates and sorts using HashSet data structure.
	 * 
	 * @param line String of words to parse
	 * @return words HashSet of words parsed
	 */
	public static TreeSet<String> cleaner(String line) {
		var words = new TreeSet<String>();
		for (String word: TextFileStemmer.stemLine(line)) {
			words.add(word);
		}
		return words;
	}
	
	public static TreeMap<String, HashMap<String, HashMap<String, Number>>> copy(TreeMap<String, TreeMap<String, TreeMap<String, Number>>> index) {
		var querymap = new TreeMap<String, HashMap<String, HashMap<String, Number>>>();
		
		for (String word : index.keySet()) {
			querymap.put(word, new HashMap<String, HashMap<String, Number>>());
			for (String file : index.get(word).keySet()) {
				querymap.get(word).put(file, new HashMap<String, Number>());
				for (String file2 : index.get(word).keySet()) {
					if ((double)index.get(word).get(file).get("score") > (double)index.get(word).get(file2).get("score")) {
						querymap.get(word).get(file).putIfAbsent("count", (double)index.get(word).get(file).get("count"));
						querymap.get(word).get(file).putIfAbsent("score", (double)index.get(word).get(file).get("score"));
					} else if ((double)index.get(word).get(file).get("score") < (double)index.get(word).get(file2).get("score")) {
						querymap.get(word).get(file2).putIfAbsent("count", (double)index.get(word).get(file).get("count"));
						querymap.get(word).get(file2).putIfAbsent("score", (double)index.get(word).get(file).get("score"));
					} else {
						if ((double)index.get(word).get(file).get("count") > (double)index.get(word).get(file2).get("count")) {
							querymap.get(word).get(file).putIfAbsent("count", (double)index.get(word).get(file).get("count"));
							querymap.get(word).get(file).putIfAbsent("score", (double)index.get(word).get(file).get("score"));
						} else if ((double)index.get(word).get(file).get("count") < (double)index.get(word).get(file2).get("count")) {
							querymap.get(word).get(file2).putIfAbsent("count", (double)index.get(word).get(file).get("count"));
							querymap.get(word).get(file2).putIfAbsent("score", (double)index.get(word).get(file).get("score"));
						} else {
							querymap.get(word).get(file2).putIfAbsent("count", (double)index.get(word).get(file).get("count"));
							querymap.get(word).get(file2).putIfAbsent("score", (double)index.get(word).get(file).get("score"));
						}
					}
						
				}
			}
		}
		
		return querymap;
		
	}
	
	public static TreeMap<String, HashMap<String, HashMap<String, Number>>> sortMap(TreeMap<String, HashMap<String, HashMap<String, Number>>> index) {
		var tempmap = new TreeMap<String, HashMap<String, HashMap<String, Number>>>();
		for (String word : index.keySet()) {
			for (String file : index.get(word).keySet()) {
				tempmap.get(word).put(file, new HashMap<String, Number>());
				for (String file2 : index.get(word).keySet()) {
					if ((double)index.get(word).get(file).get("score") >= (double)index.get(word).get(file2).get("score")) {
						tempmap.get(word).get(file).putIfAbsent("count", (double)index.get(word).get(file).get("count"));
						tempmap.get(word).get(file).putIfAbsent("score", (double)index.get(word).get(file).get("score"));
					} else {
						tempmap.get(word).get(file2).putIfAbsent("count", (double)index.get(word).get(file).get("count"));
						tempmap.get(word).get(file2).putIfAbsent("score", (double)index.get(word).get(file).get("score"));
					}
				}
			}
		}
		return tempmap;
	}
}
