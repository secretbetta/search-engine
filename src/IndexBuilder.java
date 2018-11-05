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
	
	/**
	 * Parses a text file into stemmed words, and adds those words to an inverted index
	 * 
	 * @param file path to the input file
	 * @param invertedIndex The index to edit
	 * @throws IOException For BufferedReader
	 */
	public static void buildWords(Path file, InvertedIndex invertedIndex) throws IOException {
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
	
	/**
	 * Thread version of buildWords
	 * 
	 * @see #buildWords(Path, InvertedIndex)
	 * 
	 * @throws IOException
	 */
	public static void buildWords(Path file, InvertedIndex index, int threads) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			int pos = 0;
			String line = null;
			String filename = file.toString();
			
			Stemmer stem = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			
			WorkQueue queue = new WorkQueue(threads);
			
			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					queue.execute(new Builder(stem.stem(word).toString(), filename, ++pos, index));
				}
			}
			
			queue.finish();
			queue.shutdown();
		}
	}
	
	/**
	 * Runnable builder class
	 * @author Andrew
	 *
	 */
	public static class Builder implements Runnable {

		InvertedIndex index;
		String word;
		String file;
		int pos;
		
		public Builder(String word, String filename, int pos, InvertedIndex index) {
			this.index = index;
			this.word = word;
			this.file = filename;
			this.pos = pos;
		}
		
		@Override
		public void run() {
			synchronized(index) {
				index.add(this.word, this.file, this.pos);
			}
		}	
	}
}
