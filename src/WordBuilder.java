import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/*
TODO
Consider renaming this class and method. You aren't "building" words, you are building an inverted index.
And you aren't "getting" words, you are adding them to the index.
*/

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
			/*
			TODO Two efficiency issues in your while loop.
			
			1) stemLine(...) creates 1 stemmer object per line. This is a lot of objects
			that get created and are only used for a short period of time. This forces the
			Java garbage collector to run in the background cleaning up memory, and slowing
			down your code. Create 1 stemmer object here before the while loop, and reuse it
			inside the while loop.
			
			2) stemLine(...) loops through the parse words and adds them to a list. Then,
			you loop through the list and add everything to the index. This is more loops
			than necessary, and since it happens for every line, you are essentially looping
			through the entire file twice. 
			
			Efficiency is one reason why you can make a more-specific version of generalized
			code. In this case, that means leaving stemLine(...) as it is but copy/pasting 
			the relevant parts of that code into your while loop below. Except, instead of 
			adding to a list, immediately add to the index without using any kind of temporary
			storage in between the parsed words and the inverted index.
			*/
			TextFileStemmer stem = new TextFileStemmer();
			
			while ((line = reader.readLine()) != null) {
				//TODO Ask what sophie means when she says immediately add it to the index without using temp storage
//				tempLine = stem.stemLine(line);
				for (String word: stem.stemLine(line)) {
					if (!word.isEmpty()) {
						invertedIndex.add(word, filename, ++pos);
					}
				}
			}
		}
	}
}
