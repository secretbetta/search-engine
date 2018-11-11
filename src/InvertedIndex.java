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
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
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
		index.get(word).get(file).add(pos);
		
		locationIndex.putIfAbsent(file, 0);
		locationIndex.put(file, locationIndex.get(file) + 1);
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
	
	public ArrayList<Result> exactSearch(Collection<String> query) {
		ArrayList<Result> resultList = new ArrayList<Result>();
		int wordcount = 0;
		boolean exists;
		
		
		for (String que : query) {
			if (this.contains(que)) {
				for (String loc : this.index.get(que).keySet()) {
					exists = false;
					wordcount = this.index.get(que).get(loc).size();
					
					for (Result r : resultList) {
						if (r.getFile().equals(loc)) {
							r.add(wordcount);
							exists = true;
						}
					}
					
					if (!exists) {
						resultList.add(new Result(loc, wordcount, this.locationIndex.get(loc)));
					}
				}
			}
		}
		
		Collections.sort(resultList);
		return resultList;
	}
	
	public ArrayList<Result> partialSearch(Collection<String> query) {
		ArrayList<Result> resultList = new ArrayList<Result>();
		int wordcount = 0;
		boolean exists;
		
		for (String que : query) {
			//TODO Instead of contains, it's startswith
//			if (this.contains(que)) {
				for (String loc : this.index.get(que).keySet()) {
					exists = false;
					wordcount = this.index.get(que).get(loc).size();
					
					for (Result r : resultList) {
						if (r.getFile().equals(loc)) {
							r.add(wordcount);
							exists = true;
						}
					}
					
					if (!exists) {
						resultList.add(new Result(loc, wordcount, this.locationIndex.get(loc)));
					}
				}
//			}
		}
		
		return resultList;
	}
}
