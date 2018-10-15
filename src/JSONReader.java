import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONReader {
	
	/**
	 * Searches through the inverted index for all the query matches. 
	 * Outputs results in a TreeMap
	 * 
	 * @param queries TreeMap data structure of Queries
	 * @param path What files to look for
	 * @param index The inverted index used to search for queries
	 * @param query Word/Query to search for
	 * @param exact Whether it's an exact or partial search
	 * @throws IOException
	 */
	public static void searcher(TreeMap<String, ArrayList<Result>> queries, Path path, TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, String query, boolean exact) throws IOException {
		Result result = null;
		String filename = path.toString();
		double wordtotal = 0;
		int wordcount = 0;
		
		ArrayList<Result> results = new ArrayList<Result>();
		
		TreeSet<String> temp;
		temp = new TreeSet<String>(TextFileStemmer.stemLine(query));
		
		query = "";
		for (String w : temp.headSet(temp.last())) {
			query += w + " ";
		}
		query += temp.last();
		
		if (exact) {
			for (String q : temp) {
				wordtotal = 0;
				if (index.containsKey(q) && index.get(q).containsKey(filename)) {
					for (String word : index.keySet()) {
						for (String file : index.get(word).keySet()) {
							if (file.equals(filename)) {
								wordtotal += index.get(word).get(file).size();
							}
						}
					}
					
					wordcount = index.get(q).get(filename).size() + wordcount;
					result = new Result(filename, wordcount, ((double)wordcount)/((double)wordtotal));
				}
			}
		} else {
			for (String q : temp) {
				wordtotal = 0;
				for (String word : index.keySet()) {
					for (String file : index.get(word).keySet()) {
						if (file.equals(filename)) {
							wordtotal += index.get(word).get(file).size();
						}
					}
				}
				
				for (String word : index.keySet()) {
					if (word.startsWith(q)) {
						if (index.get(word).containsKey(filename)) {
							wordcount = index.get(word).get(filename).size() + wordcount;
							result = new Result(filename, wordcount, ((double)wordcount)/((double)wordtotal));
						}
					}
				}
			}
		}
		
		results.add(result);
		
		boolean containFlag = false;
		if (queries.containsKey(query)) {
			for (Result r : queries.get(query)) {
				if (result != null && r != null && r.file.equals(result.file)) {
					containFlag = true;
				}
			}
			if (!containFlag && result != null) {
				queries.get(query).add(result);
			}
		} else {
			queries.put(query, results);
		}
	}
}
