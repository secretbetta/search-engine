import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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
	public static TreeMap<String, TreeMap<String, TreeMap<String, Double>>> searchNested(Path path, TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, String query, boolean exact) throws IOException {
		var invertedindex = new TreeMap<String, TreeMap<String, TreeMap<String, Double>>>();
		String temp = "";
		String filename = path.normalize().toString();
		
		double totalwords = 0;
		double score = 0;
		
		TreeSet<String> querywords = QueryParsing.cleaner(query);
		
		for (String x : querywords.headSet(querywords.last())) {
			temp += x + " ";
		}
		
		temp += querywords.last();
		invertedindex.put(temp, new TreeMap<String, TreeMap<String, Double>>());
		invertedindex.get(temp).put(filename, new TreeMap<String, Double>());
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
			if (index.get(word).containsKey(filename)) {
				totalwords += index.get(word).get(filename).size();
			}
		}
		
		score = (double)invertedindex.get(temp).get(filename).get("count")/(double)totalwords;
		
		if (score != 0 && totalwords != 0) {
			invertedindex.get(temp).get(filename).put("score", score);
		} else {
			invertedindex.get(temp).remove(filename);
		}
		
		return invertedindex;
	}
	
	public static void searcher(TreeSet<Query> queries, Path path, TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, String query, boolean exact) throws IOException {
		Result result = null;
		double wordtotal = 0;
		int wordcount = 0;
		Query tempQuery;
		ArrayList<Result> results = new ArrayList<Result>();
		
		for (String q : new TreeSet<String>(TextFileStemmer.stemLine(query))) {
			wordtotal = 0;
			
			for (String word : index.keySet()) {
				if (word.equals(q)) { 
					for (String file : index.get(word).keySet()) {
						if (file.equals(path.toString())) {
							wordtotal += index.get(word).get(file).size();
						}
					}
					wordcount = index.get(q).get(path.toString()).size();
					results.add(new Result(path.toString(), wordcount, ((double)wordcount)/wordtotal));
				}
			}
//			if (index.containsKey(q)) {
//				if (index.get(q).containsKey(path.toString())) {
//					for (String word : index.keySet()) {
//						for (String file : index.get(word).keySet()) {
//							if (file.equals(path.toString())) {
//								wordtotal += index.get(word).get(file).size();
//							}
//						}
//					}
//					wordcount = index.get(q).get(path.toString()).size();
//					results.add(new Result(query, wordcount, ((double)wordcount)/wordtotal));
//				}
//			}
			
			System.out.println(query + " " + results);
			
//			if (index.containsKey(q) && index.get(q).containsKey(path.toString())) {
//				for (String word : index.keySet()) {
//					for (String file : index.get(word).keySet()) {
//						if (file.equals(path.toString())) {
//							wordtotal += index.get(word).get(file).size();
//						}
//					}
//				}
//				wordcount = index.get(q).get(path.toString()).size();
//				result = new Result(path.toString(), wordcount, ((double)wordcount)/((double)wordtotal));
//			}
		}
		
		if (result != null) {
			for (Query que : queries) {
				if (que.word().equals(query)) {
					que.add(result);
					break;
				} else {
					results.add(result);
					tempQuery = new Query(query, results);
					queries.add(tempQuery);
					break;
				}
			}
			
			tempQuery = new Query(query, results);
			queries.add(tempQuery);
		}
	}
}
