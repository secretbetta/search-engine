import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {
	
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> indexL3;
	public TreeMap<String, TreeSet<Integer>> indexL2;
	public TreeSet<Integer> indexL1;
	
	/**
	 * Initializes Inverted Index 
	 */
	public InvertedIndex() {
		this.indexL3 = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		this.indexL2 = new TreeMap<String, TreeSet<Integer>>();
		this.indexL1 = new TreeSet<Integer>();
	}
	
	/**
	 * Adds number into Layer 1 (L1) of inverted index
	 * 
	 * @param pos
	 * @return true if successful in adding number, false otherwise
	 */
	public boolean addL1(int pos) {
		if (indexL1.contains(pos)) {
			return false;
		} else {
			indexL1.add(pos);
			return true;
		}
	}
	
//	public boolean addL2(String file) {
//		return addL2(file, null);
//	}
//	
//	public boolean addL2(String file, TreeSet<Integer> set) {
//		if (indexL2.containsKey(file)) {
//			return false;
//		} else {
//			if (indexL2.) {
//				
//			}
//			indexL2.put(file, indexL1);
//			return true;
//		}
//	}
	
	/**
	 * Returns inverted index
	 * 
	 * @return index The class's index variable
	 */
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndex() {
		return this.index;
	}
}
