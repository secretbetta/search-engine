import java.util.TreeMap;

/**
 * Creates a Map of File -> wordcount
 * @author Andrew
 *
 */
public class LocationIndex {
	
	/**
	 * Creates the map of how many words per file
	 * @param index InvertedIndex to count 
	 * @return Map of wordcount in each file
	 */
	public static TreeMap<String, Integer> indexLocation(InvertedIndex index) {
		int wordcount = 0;
		var locationIndex = new TreeMap<String, Integer>();
		
		for (String word : index.index.keySet()) {
			for (String file : index.index.get(word).keySet()) {
				if (locationIndex.containsKey(file)) {
					wordcount = index.index.get(word).get(file).size() + locationIndex.get(file);
				} else {
					wordcount = index.index.get(word).get(file).size();
				}
				locationIndex.put(file, wordcount);
			}
		}
		
		return locationIndex;
	}
}
