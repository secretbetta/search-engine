import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONReader {
	
	public static void searcher(TreeSet<Query> queries, Path path, TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, String query, boolean exact) throws IOException {
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
		
		Query tempQuery = new Query(query, new ArrayList<Result>());
		
//		if (!queries.isEmpty()) {
//			for (Query q : queries) {
//				for (String que : temp) {
//					if (q.word.equals(que)) {
//						q.add(result);
//						break;
//					} else {
//						tempQuery.add(result);
//						queries.add(tempQuery);
//						break;
//					}
//				}
//			}
//		} else {
//			tempQuery.add(result);
//			queries.add(tempQuery);
//		}
		
		tempQuery.add(result);
		queries.add(tempQuery);
	}
	
	public static void searcher2(TreeMap<String, ArrayList<Result>> queries) {
		
	}
}
