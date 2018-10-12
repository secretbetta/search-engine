import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

/**
 * Stores basic movie information.
 */
public class Query implements Comparable<Query> {

	private final String word;
	private final ArrayList<Result> results;
	
	/**
	 * Creates query and list of results
	 * @param word Query word
	 * @param results List of results
	 */
	public Query(String word, ArrayList<Result> results) {
		this.word = word;
		this.results = results;
	}

	/**
	 * Gets query word
	 * @return String word
	 */
	public String word() {
		return this.word;
	}
	
	public boolean add(Result result) {
		for (int x = 0; x < this.results.size(); x++) {
			if (this.results.get(x).compareTo(result) == 1); {
				this.results.add(x, result);
				return true;
			}
		}
		
		this.results.add(result);
		return false;
		
	}
	
	/**
	 * Sorts the results by score, wordcount, and then filename
	 */
	public void sort() {
//		Result tempr = null;
		
		Collections.sort(results);
		
//		for (int x = 0; x < this.results.length - 1; x++) {
//			for (int y = x + 1; y < this.results.length; y++) {
//				if (this.results[y].compareTo(this.results[x]) == 0) {
//					tempr = this.results[x];
//					this.results[x] = this.results[y];
//					this.results[y] = tempr;
//				}
//			}
//		}
	}
	
	/**
	 * Returns string of JSON formatted search query
	 * 
	 * @return writer.toString()
	 */
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		this.sort();
		try {
			NestedJSON.indent(1, writer);
			
			writer.write("{");
			writer.write(System.lineSeparator());
			NestedJSON.indent(2, writer);
			
			writer.write("\"queries\": \"");
			writer.write(this.word);
			writer.write("\",");
			writer.write(System.lineSeparator());
			NestedJSON.indent(2, writer);
			
			writer.write("\"results\": [");
			writer.write(System.lineSeparator());
			
			for (int i = 0; i < results.size(); i++) {
				writer.write(results.get(i).toString());
				if (i != results.size() - 1) {
					writer.write(",");
				} else {
				}
				writer.write(System.lineSeparator());
			}
			
			NestedJSON.indent(2, writer);
			writer.write("]");
			writer.write(System.lineSeparator());
			NestedJSON.indent(1, writer);
			
			writer.write("}");
			
			return writer.toString();
		} catch (IOException e) {
		}
		return writer.toString();
	}
	
	public static void main(String[] args) throws IOException {
		Path index = Paths.get("querytest.json");

		Result result = new Result("firstfile.txt", 15, 0.33333333);
		Result result1 = new Result("secondfile.txt", 13, 0.99333333);
		Result result2 = new Result("thirdfile.txt", 10, 0.33333333);
		TreeSet<Query> queries = new TreeSet<Query>();
		
		Query query = new Query("word", new ArrayList<Result>());
		Query query2 = new Query("bird", new ArrayList<Result>());
		query.add(result2);
		query2.add(result1);
		query.add(result1);
		query2.add(result);
		
		queries.add(query);
		queries.add(query2);
		
		
		try (BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);) { 
			NestedJSON.queryObject(queries, writer);
		}
	}
	
	/**
	 * Compares query words and sorts them
	 */
	@Override
	public int compareTo(Query query) {
		return this.word.compareTo(query.word);
	}
}
