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
}
