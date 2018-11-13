import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Stems and cleans whole textfiles
 */
public class TextFileStemmer {

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 * Uses the English {@link SnowballStemmer.ALGORITHM} for stemming.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see SnowballStemmer.ALGORITHM#ENGLISH
	 * @see #stemLine(String, Stemmer)
	 */
	public static List<String> stemLine(String line) {
		return stemLine(line, new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH));
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return list of cleaned and stemmed words
	 * 
	 * @see #stemLine(String, Stemmer, Collection)
	 */
	public static List<String> stemLine(String line, Stemmer stemmer) {
		List<String> words = new ArrayList<String>();
		stemLine(line, stemmer, words);
		return words;
	}
	
	/**
	 * Adds all stemmed words into collection of strings
	 * @param line
	 * @param stemmer
	 * @param container
	 * 
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static void stemLine(String line, Stemmer stemmer, List<String> container) {
		String[] list = TextParser.parse(line);
		for (String word : list) {
			container.add(stemmer.stem(word).toString());
		}
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then writes that line to a new file.
	 *
	 * @param inputFile the input file to parse
	 * @param outputFile the output file to write the cleaned and stemmed words
	 * @throws IOException if unable to read or write to file
	 *
	 * @see #stemLine(String)
	 * @see TextParser#parse(String)
	 */
	public static void stemFile(Path inputFile, Path outputFile) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
			BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8);) {
			List<String> words = new ArrayList<String>();
			String line;
			
			while ((line = reader.readLine()) != null) {
				words = stemLine(line);
				for (String word: words) {
					writer.write(word + " ");
				}
				writer.write("\n");
			}
		}
	}
}
