import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;

/**
 * Creates an InvertedIndex in words -> file -> positions format
 * @author Andrew
 *
 */
public class WordBuilder {
	
	/**
	 * Gets words from a given text file. Adds word and position into TreeMap.
	 * Words may have more than one position in a text file.
	 * <p>key = word(s) from text files</p>
	 * <p>value = position(s) of word from text files</p>
	 * @param file path to the input file
	 * @return list list of words in text file
	 * @throws IOException
	 */
	public static void getWords(Path file, InvertedIndex invertedIndex) throws IOException {
		Path stemmed = Paths.get("stemmedfile.txt");
		TextFileStemmer.stemFile(file, stemmed);
		try (BufferedReader reader = Files.newBufferedReader(stemmed, StandardCharsets.UTF_8);) {
			int position = 0;
			var temp = new TreeSet<Integer>();
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				for (String word: line.split(" ")) {
					if (!word.isEmpty()) {
						temp = new TreeSet<Integer>();
						position++;
						
						temp.add(position);
						invertedIndex.addAllWordFile(word, file.toString(), temp);
					}
				}
			}
		}
	}
	
}
