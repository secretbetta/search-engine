import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.TreeSet;

public class GetWords {
	/**
	 * Gets words from a given text file. Adds word and position into TreeMap.
	 * Words may have more than one position in a text file.
	 * <p>key = word(s) from text files</p>
	 * <p>value = position(s) of word from text files</p>
	 * @param input path to the input file
	 * @return list list of words in text file
	 * @throws IOException
	 */
	public static TreeMap<String, TreeSet<Integer>> getWords(Path input) // TODO Put this not in driver. (Put it in a "buidler" class.)
			throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);) {
			
			int position = 0;
			
			var index = new TreeMap<String, TreeSet<Integer>>();
			var temp = new TreeSet<Integer>();
			String line = null;
			
			// TODO Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH)
			// TODO Use one of these per file
			
			while ((line = reader.readLine()) != null) {
				/*
				 * TODO
				 * Using temporary storage, want to avoid... efficiency is a case
				 * where you can copy/paste!
				 * 
				 * Where stemLine was adding to a list, instead add to an index.
				 */
				for (String word: TextFileStemmer.stemLine(line)) {
					temp = new TreeSet<Integer>();
					position++;
					if (!index.containsKey(word)) {
						temp.add(position);
						index.put(word, temp);
					} else {
						temp.addAll(index.get(word));
						temp.add(position);
						index.put(word, temp);
					}
				}
			}
			return index;
		}
	}
	
	public static Path[] args(String[] args) {
		Path path = null;
		Path index = null;
		
		var argmap = new ArgumentMap(args);
		if (argmap.hasFlag("-path")) {
			if (!(argmap.getPath("-path") == null)) {
				path = Paths.get(argmap.getPath("-path").toString());
			}
		}
		
		if (argmap.hasFlag("-index") && !(argmap.getPath("-index") == null)) {
			index = Paths.get(argmap.getPath("-index").toString());
		} else if (argmap.hasFlag("-index") && argmap.getPath("-index") == null) {
			index = Paths.get("index.json");
		} else {
			index = Paths.get("out", "index.json");
		}
		
		Path[] paths = {path, index};
		
		return paths;
	}
}