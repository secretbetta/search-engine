import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;


/**
 * Creates an InvertedIndex in words -> file -> positions format
 * @author Andrew
 *
 */
public class IndexBuilder {
	// TODO Avoid describing your implementation in Javadoc, try to describe generally the functionality
	// TODO Parses a text file into stemmed words, and adds those to an inverted index.
	/**
	 * Gets words from a given text file. Adds word and position into TreeMap.
	 * Words may have more than one position in a text file.
	 * <p>key = word(s) from text files</p>
	 * <p>value = position(s) of word from text files</p>
	 * @param file path to the input file
	 * @param invertedIndex The index to edit
	 * @throws IOException For BufferedReader
	 */
	public static void getWords(Path file, InvertedIndex invertedIndex) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			int pos = 0;
			String line = null;
			String filename = file.toString();
			
			Stemmer stem = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			
			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					invertedIndex.add(stem.stem(word).toString(), filename, ++pos);
				}
			}
		}
	}
}
