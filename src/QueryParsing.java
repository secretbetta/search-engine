import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	
	public static int wordScore(Path file, String word) {
		int totalWords = 0;
		int totalMatches = 0;
		int score;
		
		score = totalMatches/totalWords;
		return score;
	}
	
	public static void main(String[] args) throws IOException {
		Path text = Paths.get("..", "project-tests", "text", "simple", "words.text");
		Path query = Paths.get("..", "project-tests", "query", "words.txt");
		Path index = Paths.get("..", "project-tests", "out", "results-text-words-words.json");
		Driver.search(text, query, index, true);
	}
}
