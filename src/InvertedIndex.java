import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure to store word to file(s) to position(s) in an inverted index format
 */
public class InvertedIndex {
	
	// TODO private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	
	/**
	 * Initializes Inverted Index and its layers
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
	}
	
	// TODO refactor to add( )
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
	
	/*
	 * TODO 
	 * toString()
	 * 
	 * add(String word, String file, int position)
	 * 
	 * public boolean contains(String word)
	 * public boolean contains(String word, String location)
	 * public boolean contains(String word, String location, int position)
	 * 
	 * public int words() returns # of words in the index
	 * public int locations(String word)
	 * public int positions(String word, String location)
	 * 
	 */
	
	// TODO Breaks encapsulation, need to remove
	/**
	 * Returns inverted index
	 * 
	 * @return index The class's index variable
	 */
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndex() {
		return index;
	}
	
	/* TODO Do this instead
	public void toJSON(Path path) {
		NestedJSON.tripleNested(index, path);
	}
	*/
}
