import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure to store word to file(s) to position(s) in an inverted index format
 */
public class InvertedIndex {
	
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	
	/**
	 * Initializes Inverted Index and its layers
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
	}
	
	/**
	 * Adds word if does not exist
	 * Adds file to word if does not exist
	 * 
	 * @param word the inputted word
	 * @param filepos Treemap of file names and positions
	 * @return true if word did not exist before
	 */
	public void addAllWordFile(String word, String file, TreeSet<Integer> pos) {
		var temp = new TreeMap<String, TreeSet<Integer>>();
		
		if (!index.containsKey(word)) {
			temp.put(file, pos);
			index.put(word, temp);
		} else if (index.get(word).containsKey(file)) {
			index.get(word).get(file).addAll(pos);
		} else if (!index.get(word).containsKey(file)) {
			index.get(word).put(file, pos);
		}
	}
	
	public TreeMap<String, TreeSet<Integer>> get(String word) {
		return this.contains(word) ? index.get(word) : null;
	}
	
	public TreeSet<Integer> get(String word, String file) {
		return this.contains(word, file) ? index.get(word).get(file) : null;
	}
	
	/**
	 * Does index contain word?
	 * @param word Word input
	 * @return true if index contains word
	 */
	public boolean contains(String word) {
		return index.containsKey(word);
	}
	
	/**
	 * Does index contain location in this word?
	 * @param word Word input
	 * @param location File input
	 * @return true if location exist in word
	 */
	public boolean contains(String word, String location) {
		return this.contains(word) && 
				index
				.get(word).containsKey(location);
	}
	
	/**
	 * Does position exist in file location in word?
	 * @param word Word input
	 * @param location Location input
	 * @param position Position input
	 * @return true if position exists in file and in word
	 */
	public boolean contains(String word, String location, int position) {
		return this.contains(word, location) && 
				index.get(word)
				.get(location)
				.contains(position);
	}
	
	/**
	 * Number of words in index
	 * @return Number of words
	 */
	public int words() {
		return index.size();
	}
	
	/**
	 * Gets number of locations in word
	 * @param word Word to get locations from
	 * @return Number of locations in word of index, -1 if word does not exist
	 */
	public int locations(String word) {
		return index.containsKey(word) ? index.get(word).size() : -1;
	}
	
	/**
	 * Gets number of positions in a file of a word
	 * @param word Word from index
	 * @param file File from word of index
	 * @return Number of positions in file of word, 
	 * 		-1 if word or file location does not exist
	 */
	public int positions(String word, String file) {
		return this.contains(word, file) ? index.get(word).get(file).size() : -1;
	}
	
	@Override
	public String toString() {
		return index.toString();
	}
	
	/**
	 * Returns inverted index
	 * 
	 * @return index The class's index variable
	 */
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndex() {
		return index;
	}
}
