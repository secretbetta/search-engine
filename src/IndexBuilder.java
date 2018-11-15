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
	 * Traverses through path and builds index from each file
	 * @param path Path to traverse
	 * @param index InvertedIndex to build
	 * @throws IOException
	 * 
	 * @see {@link #getWords(Path, InvertedIndex)}
	 */
	public static void traverse(Path path, InvertedIndex index) throws IOException {
		for (Path file : TextFileFinder.traverse(path)) {
			getWords(file, index);
		}
	}
	
	/**
	 * Parses a text file into stemmed words, and adds those words to an inverted index
	 * 
	 * @param file path to the input file
	 * @param invertedIndex The index to edit
	 * @throws IOException For BufferedReader
	 */
	public static void getWords(Path file, InvertedIndex index) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			int pos = 0;
			String line = null;
			String filename = file.toString();
			
			Stemmer stem = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			
			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					index.add(stem.stem(word).toString(), filename, ++pos);
				}
			}
		}
	}
	
	/**
	 * Thread version of traverse
	 * 
	 * @see #buildWords(Path, InvertedIndex)
	 * 
	 * @throws IOException
	 */
	public static void traverse(Path path, InvertedIndex index, int threads) throws IOException {
		WorkQueue queue = new WorkQueue(threads);
		
		for (Path file : TextFileFinder.traverse(path)) {
			queue.execute(new Builder(file, index));
		}
	}
	
	/**
	 * Runnable builder class
	 * @author Andrew
	 *
	 */
	public static class Builder implements Runnable {

		InvertedIndex index;
		Path file;
		
		public Builder(Path file, InvertedIndex index) {
			this.index = index;
			this.file = file;
		}
		
		@Override
		public void run() {
			try {
				synchronized(index) {
					IndexBuilder.getWords(file, index);
				}
			} catch (IOException e) {
				System.err.println("Cannot make index from file " + file);
			}
		}	
	}
}
