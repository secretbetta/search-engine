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
	
	/**
	 * Returns inverted index
	 * 
	 * @return index The class's index variable
	 */
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndex() {
		return index;
	}
}
