import java.io.IOException;
import java.nio.file.Path;
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
		for (String word: TextParser.parse(line)) {
			words.add(word);
		}
		return words;
	}
	
	public static int wordScore(Path file, String word) {
		int totalWords = 0;
		int totalMatches = 0;
		int score;
		
		score = totalMatches/totalWords;
		return score;
	}
	
	public static void main(String[] args) throws IOException {
//		var results = new TreeMap<String, Number>();
//		var file = new TreeMap<String, TreeMap<String, Number>>();
//		var queries = new TreeMap<String, TreeMap<String, TreeMap<String, Number>>>();
//		Path path = Paths.get("testing-index.json");
//		results.put("score", 0.458333);
//		results.put("count", 11.0);
//		file.put("testfile.txt", results);
//		results = new TreeMap<String, Number>();
//		results.put("score", 0.4293);
//		results.put("count", 23.0);
//		file.put("otherfile", results);
//		
//		queries.put("word", file);
//		
//		results.put("score", 0.42343);
//		results.put("count", 32.0);
//		file.put("boom.txt", results);
//		results = new TreeMap<String, Number>();
//		results.put("score", 0.1234);
//		results.put("count", 42.0);
//		file.put("This.ksjskd", results);
//		
//		queries.put("word2", file);
//		
//		BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
//		NestedJSON.queryObject(queries, writer, 0);
		
		System.out.println(cleaner("ant \r\n" + 
				"seven 88eight88 nine\r\n" + 
				"hello world\r\n" + 
				"WORLD  +   HELLO\r\n" + 
				"cardinal\r\n" + 
				"elephant aar* alpaca\r\n" + 
				"     hidden       capybara      \r\n" + 
				"ob-SERVE!! antelöpé"));
	}
}
