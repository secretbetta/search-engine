import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {
	
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	
	/**
	 * Initializes Inverted Index and its layers
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
	}
	
	/**
	 * Sees if word exists
	 * @param word in index
	 * @return true if word does exists
	 */
	public boolean containsWord(String word) {
		if (index.containsKey(word)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Adds word if does not exist and file->position automatically
	 * 
	 * @param word the inputted word
	 * @param filepos Treemap of file names and positions
	 * @return true if word did not exist before
	 */
	public boolean addAllWordFile(String word, String file, TreeSet<Integer> pos) {
		var temp = new TreeMap<String, TreeSet<Integer>>();
		temp.put(file, pos);
		if (!index.containsKey(word)) {
			index.put(word, temp);
			return true;
		} else if (!index.get(word).containsKey(file)) {
			index.get(word).put(file, pos);
			return true;
		}
		return false;
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
