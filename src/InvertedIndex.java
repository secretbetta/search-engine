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
	
	/**
	 * Adds word if does not exist
	 * Adds file if does not exist
	 * Adds position if does not exist
	 * @param word The inputted word
	 * @param file The inputted file
	 * @param pos The inputted position
	 */
	public void add(String word, String file, int pos) {
		index.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		index.get(word).putIfAbsent(file, new TreeSet<Integer>());
		index.get(word).get(file).add(pos);
	}
	
	public boolean contains(String word) {
		return index.containsKey(word);
	}
	
	public boolean contains(String word, String location) {
		return this.contains(word) && 
				index
				.get(word).containsKey(location);
	}
	
	public boolean contains(String word, String location, int position) {
		return this.contains(word, location) && 
				index.get(word)
				.get(location)
				.contains(position);
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
