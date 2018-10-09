import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;


public class QueryParsing {
	
	/**
	 * Turns string line to cleaned and parsed hashset of words.
	 * Removes duplicates and sorts using HashSet data structure.
	 * 
	 * @param line String of words to parse
	 * @return words HashSet of words parsed
	 */
	public static TreeSet<String> cleaner(String line) {
		var words = new TreeSet<String>();
		for (String word: TextFileStemmer.stemLine(line)) {
			words.add(word);
		}
		return words;
	}
	
	public static int wordScore(Path file, String word) {
		int totalWords = 0;
		int totalMatches = 0;
		int score;
		
		score = totalMatches/totalWords;
		return score;
	}
	
	/**
	 * Searches for words by query. Can look for exact or partial searches. 
	 * @param text Textfile to look at
	 * @param query What words to look for
	 * @param index2 Place to write the results
	 * @param exact Search for exact keywords or not
	 * @throws IOException
	 */
	public static void search(Path text, Path query, Path indexfile, Boolean exact) {
		var queries = new TreeSet<String>();
		var index = new TreeMap<String, TreeMap<String, TreeMap<String, Number>>>();
		String filename = text.normalize().toString();
		
		int wordcount = 0;
		
		String line;
		try (BufferedReader reader = Files.newBufferedReader(query, StandardCharsets.UTF_8);) {
			while ((line = reader.readLine()) != null) {
				queries.addAll(QueryParsing.cleaner(line));
			}
		} catch (IOException e) {
			System.err.println("Cannot read file");
		}
		ArrayList<String> textFiles;
		
		if (Files.isDirectory(text)) {
			try {
				textFiles = TextFileFinder.traverse(text);
				for (String file : textFiles) {
					
				}
			} catch (IOException e1) {
				System.err.println("Cannot traverse path");
			}
		} else {
			try (BufferedReader reader = Files.newBufferedReader(text, StandardCharsets.UTF_8);) {
			
				while ((line = reader.readLine()) != null) {
					for (String textword : QueryParsing.cleaner(line)) {
						if (queries.contains(textword)) {
							index.putIfAbsent(textword, new TreeMap<String, TreeMap<String, Number>>());
							index.get(textword).putIfAbsent(filename, new TreeMap<String, Number>());
							index.get(textword).get(filename).putIfAbsent("count", 0);
							index.get(textword).get(filename).put("count", (int)(index.get(textword).get(filename).get("count")) + 1);
						}
						wordcount++;
					}
				}
				double score;
				for (String word : index.keySet()) {
					score = (int)(index.get(word).get(filename).get("count"))/(double)(wordcount);
					index.get(word).get(filename).put("score", score);
				}
			} catch (IOException e) {
				System.err.println("Cannot find text file");
			}
		}
		
		try (BufferedWriter writer = Files.newBufferedWriter(indexfile, StandardCharsets.UTF_8);) {
			NestedJSON.queryObject(index, writer, 0);
		} catch (IOException e) {
			System.err.println("Cannot find index file");
		}
	}
	
	public static void partialSearch(Path text, Path index, Path query, boolean exact) {
		
	}
	
	public static void main(String[] args) throws IOException {
		Path text = Paths.get("..", "project-tests", "text", "simple", "words.text");
		Path query = Paths.get("..", "project-tests", "query", "words.txt");
		Path index = Paths.get("..", "project-tests", "out", "index-text-words.json");
		search(text, query, index, true);
		
		TreeMap<String, TreeMap<String, TreeSet<Integer>>> map = new JSONReader().read(index);
	}
}
