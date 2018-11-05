import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class IndexReader {
	
	public static void search(Path path, 
			Path search, 
			TreeMap<String, Integer> locationIndex, 
			ThreadSafeInvertedIndex index, 
			TreeMap<String, ArrayList<Result>> queries, 
			boolean exact,
			int threads) {
		
		try (BufferedReader reader = Files.newBufferedReader(search, StandardCharsets.UTF_8);) {
			
			ArrayList<Path> files;
			
			if (path != null) {
				files = TextFileFinder.traverse(path);
				String line;
				List<String> que;
				WorkQueue queue = new WorkQueue(threads);
				while ((line = reader.readLine()) != null) {
					que = TextFileStemmer.stemLine(line);
					
					for (Path file : files) {
						queue.execute(new Searcher(file, line, locationIndex, queries, index.getIndex(), que, exact));
					}
				}
				
				queue.finish();
				queue.shutdown();
				
			}
			
			for (String que : queries.keySet()) {
				Collections.sort(queries.get(que));
			}
		} catch (IOException e) {
			System.err.println("Cannot read from path " + search);
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
	public static void searcher(TreeMap<String, Integer> locationIndex, 
			TreeMap<String, ArrayList<Result>> queries, 
			Path path, 
			TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, 
			List<String> query,
			boolean exact) throws IOException {
		
		Result result = null;
		String filename = path.toString();
		double wordtotal = 0;
		int wordcount = 0;
		
		ArrayList<Result> results = new ArrayList<Result>();
		
		TreeSet<String> temp = new TreeSet<String>();
		temp.addAll(query);
		
		String line = "";
		for (String w : temp.headSet(temp.last())) {
			line += w + " ";
		}
		line += temp.last();
		
		if (exact) {
			for (String q : temp) {
				wordtotal = 0;
				if (index.containsKey(q) && index.get(q).containsKey(filename)) {
					wordtotal = locationIndex.get(filename);
					
					wordcount = index.get(q).get(filename).size() + wordcount;
					result = new Result(filename, wordcount, ((double)wordcount)/((double)wordtotal));
				}
			}
		} else {
			for (String q : temp) {
				wordtotal = 0;
				
				wordtotal = locationIndex.containsKey(filename) ? locationIndex.get(filename) : 1;
				
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
		if (queries.containsKey(line)) {
			for (Result r : queries.get(line)) {
				if (result != null && r != null && r.file.equals(result.file)) {
					containFlag = true;
				}
			}
			if (!containFlag && result != null) {
				queries.get(line).add(result);
			}
		} else {
			queries.put(line, results);
		}
	}
	
	/**
	 * Runnable Searcher class
	 * @author Andrew
	 *
	 */
	public static class Searcher implements Runnable {
		
		Path file;
		String line;
		TreeMap<String, Integer> locationIndex;
		TreeMap<String, ArrayList<Result>> queries;
		TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
		List<String> que;
		boolean exact;
		
		public Searcher(Path file, 
				String line, 
				TreeMap<String, Integer> locationIndex,
				TreeMap<String, ArrayList<Result>> queries,
				TreeMap<String, TreeMap<String, TreeSet<Integer>>> index,
				List<String> que,
				boolean exact) {
			
			this.file = file;
			this.line = line;
			this.locationIndex = locationIndex;
			this.queries = queries;
			this.index = index;
			this.que = que;
			this.exact = exact;
		}
		
		@Override
		public void run() {
			synchronized(queries) {
				if (!(line = TextParser.clean(line).trim()).isEmpty()) {
					try {
						IndexReader.searcher(locationIndex, queries, file, index, que, exact);
					} catch (IOException e) {
						System.err.println("Cannot build query from file: " + file.toString());
					}
				}
			}
		}	
	}
}
