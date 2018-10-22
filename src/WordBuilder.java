import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
	 * @param invertedIndex The index to edit
	 * @return list list of words in text file
	 * @throws IOException
	 */
	public static void getWords(Path file, InvertedIndex invertedIndex) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			int pos = 0;
			String line = null;
			String filename = file.toString();
			
			List<String> tempLine;
			
			while ((line = reader.readLine()) != null) {
				tempLine = TextFileStemmer.stemLine(line);
				for (String word: tempLine) {
					if (!word.isEmpty()) {
						invertedIndex.add(word, filename, ++pos);
					}
				}
			}
		}
	}
}
