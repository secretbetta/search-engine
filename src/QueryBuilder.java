import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class QueryBuilder {
	
	public static void builder(Path search, 
			Path path, 
			boolean exact, 
			InvertedIndex index, 
			TreeMap<String, ArrayList<Result>> query) throws IOException {
		
		BufferedReader reader = Files.newBufferedReader(search, StandardCharsets.UTF_8);
		ArrayList<Path> files = null;
		
		if (path != null) {
			files = TextFileFinder.traverse(path);
			
			TreeSet<String> que;
			String line;
			
			while ((line = reader.readLine()) != null) {
				String word = "";
				que = new TreeSet<String>();
				que.addAll(TextFileStemmer.stemLine(line));
				
				if (!que.isEmpty()) {
					for (String w : que.headSet(que.last())) {
						word += w + " ";
					}
					word += que.last();
				}
				
				if (!(word.isEmpty())) {
					if (exact) {
						query.put(word, index.exactSearch(que));
					}
				}
			}
		}
	}
	
	/**
	 * Searches through the inverted index for all the query matches. 
	 * Outputs results in a TreeMap
	 * @param locationIndex 
	 * 
	 * @param queries TreeMap data structure of Queries
	 * @param path What files to look for
	 * @param index The inverted index used to search for queries
	 * @param query Word/Query to search for
	 * @param exact Whether it's an exact or partial search
	 * @throws IOException
	 */
//	public static void searcher(TreeMap<String, Integer> locationIndex, 
//			TreeMap<String, ArrayList<Result>> queries, 
//			Path path, 
//			InvertedIndex index, 
//			List<String> query,
//			boolean exact) throws IOException {
//		
//		Result result = null;
//		String filename = path.toString();
//		int wordtotal = 0;
//		int wordcount = 0;
//		
//		ArrayList<Result> results = new ArrayList<Result>();
//		
//		TreeSet<String> temp = new TreeSet<String>();
//		temp.addAll(query);
//		
//		String line = "";
//		for (String w : temp.headSet(temp.last())) {
//			line += w + " ";
//		}
//		line += temp.last();
//		
//		if (exact) {
//			// TODO Move this into an exactSearch(...) method in index.
////			for (String q : temp) {
////				wordtotal = 0;
////				if (index.containsKey(q) && index.get(q).containsKey(filename)) {
////					wordtotal = locationIndex.get(filename);
////					
////					wordcount = index.get(q).get(filename).size() + wordcount;
////					result = new Result(filename, wordcount, wordtotal);
////				}
////			}
//			
//		} else {
//			for (String q : temp) {
//				wordtotal = 0;
//				
//				wordtotal = locationIndex.containsKey(filename) ? locationIndex.get(filename) : 1;
//				
////				for (String word : index) {
////					if (word.startsWith(q)) {
////						if (index.contains(word, filename)) {
////							wordcount = index.get(word).get(filename).size() + wordcount;
////							result = new Result(filename, wordcount, wordtotal);
////						}
////					}
////				}
//			}
//		}
//		
//		results.add(result);
//		
//		boolean containFlag = false;
//		if (queries.containsKey(line)) {
//			for (Result r : queries.get(line)) {
//				if (result != null && r != null && r.getFile().equals(result.getFile())) {
//					containFlag = true;
//				}
//			}
//			if (!containFlag && result != null) {
//				queries.get(line).add(result);
//			}
//		} else {
//			queries.put(line, results);
//		}
//	}
}
