import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure to store word to file(s) to position(s) in an inverted index format
 */
public class InvertedIndex {
	protected final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	private final TreeMap<String, Integer> locationIndex;
	
	/**
	 * Initializes Inverted Index and its layers
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
		this.locationIndex = new TreeMap<>();
	}
	
	/**
	 * Adds word if does not exist
	 * Adds file if does not exist
	 * Adds position if does not exist
	 * 
	 * @param word The inputted word
	 * @param file The inputted file
	 * @param pos The inputted position
	 */
	public void add(String word, String file, int pos) {
		index.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		index.get(word).putIfAbsent(file, new TreeSet<Integer>());

		if (index.get(word).get(file).add(pos)) {
			locationIndex.put(file, locationIndex.getOrDefault(file, 0) + 1);
		}
	}
	
	/**
	 * Adds everything from other Inverted Index to this {@link #index}.
	 * Also updates {@link #locationIndex}
	 * @param other The other Inverted Index
	 */
	public void addAll(InvertedIndex other) {
		for (String word : other.index.keySet()) {
			if (!this.index.containsKey(word)) {
				this.index.put(word, other.index.get(word));
			} else {
				for (String file : other.index.get(word).keySet()) {
					if (!this.index.get(word).containsKey(file)) {
						this.index.get(word).put(file, other.index.get(word).get(file));
					}
				}
			}
		}
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
	
	/**
	 * Creates a file index in JSON format
	 * @param path
	 * @throws IOException
	 */
	public void toJSON(Path path) throws IOException {
		NestedJSON.tripledNested(index, path);
	}
	
	/**
	 * Creates a file location index in JSON format
	 * @param locIndex
	 * @throws IOException 
	 */
	public void locationtoJSON(Path locIndex) throws IOException {
		NestedJSON.asObject(this.locationIndex, locIndex);
	}
	
	/**
	 * Exact index search, returns arraylist of Results that
	 * matches words from query words with index words
	 * @param queries Query words to match
	 * @return resultList ArrayList of Result
	 * 
	 * @see #searchHelper(String, ArrayList, TreeMap)
	 */
	public ArrayList<Result> exactSearch(Collection<String> queries) {
		ArrayList<Result> resultList = new ArrayList<Result>();
		TreeMap<String, Result> lookup = new TreeMap<String, Result>();
		
		for (String query : queries) {
			if (this.index.containsKey(query)) {
				//Runs up until here
				searchHelper(query, resultList, lookup);
			}
		}
		
		Collections.sort(resultList);
		return resultList;
	}
	
	/**
	 * Partial index search, returns arraylist of Results that
	 * matches all words starting with query words
	 * @param queries Query words
	 * @return resultList ArrayList of Results
	 * 
	 * @see #searchHelper(String, ArrayList, TreeMap)
	 */
	public ArrayList<Result> partialSearch(Collection<String> queries) {
		ArrayList<Result> resultList = new ArrayList<Result>();
		TreeMap<String, Result> lookup = new TreeMap<String, Result>();
		
		for (String query : queries) {
			for (String word : this.index.tailMap(query).keySet()) {
				if (word.startsWith(query)) {
					searchHelper(word, resultList, lookup);
				} else {
					break;
				}
			}
		}
		
		Collections.sort(resultList);
		return resultList;
	}
	
	/**
	 * The extension of searching for both partial and exact searches
	 * @param query Query word 
	 * @param resultList List of results to add
	 * @param lookup To keep track of location in Results
	 */
	public void searchHelper(String query, ArrayList<Result> resultList, TreeMap<String, Result> lookup) {
		System.out.println("Start searchHelper"); //This never runs? :/
		int wordcount;
		
		for (String path : this.index.get(query).keySet()) {
			wordcount = this.index.get(query).get(path).size();
			if (lookup.containsKey(path)) {
				lookup.get(path).add(wordcount);
			} else {
				Result current = new Result(path, wordcount, this.locationIndex.get(path));
				resultList.add(current);
				lookup.put(path, current);
			}
		}
	}

	@Override
	public String toString() {
		return this.index.toString();
	}
}
