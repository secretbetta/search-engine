import java.io.IOException;
import java.nio.file.Path;
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
	public TreeSet<String> cleaner(String line) {
		var words = new TreeSet<String>();
		for (String word: TextParser.parse(line)) {
			words.add(word);
		}
		return words;
	}
	
	public int wordScore(Path file, String word) {
		int totalWords = 0;
		int totalMatches = 0;
		int score;
		
		score = totalMatches/totalWords;
		return score;
	}
	
	public TreeMap<String, Integer> wordScoreAll(Path file) {
		var scores = new TreeMap<String, Integer>();
		
		
		
		return scores;
	}
	
	public static void main(String[] args) throws IOException {

	}
}
