import java.io.IOException;
import java.io.StringWriter;
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
	
	/**
	 * Adds result to arraylist of results
	 * @param result Result datatype
	 */
	public void add(Result result) {
		this.results.add(result);
	}
	
	/**
	 * Sorts the results by score, wordcount, and then filename
	 */
	public void sort() {
		Collections.sort(results);
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
		
		ArrayList<Result> results = new ArrayList<Result>();

		Result result = new Result("firstfile.txt", 15, 0.33333333);
		Result result1 = new Result("secondfile.txt", 13, 0.99333333);
		Result result2 = new Result("thirdfile.txt", 10, 0.33333333);
		TreeSet<Query> queries = new TreeSet<Query>();
		
		results.add(result);
		results.add(result1);
		results.add(result2);
		
		System.out.println(results);
		
		Collections.sort(results);
		
		System.out.println(results);
		
		Query query = new Query("word", new ArrayList<Result>());
		Query query2 = new Query("bird", new ArrayList<Result>());
		query.add(result2);
		query2.add(result1);
		query.add(result1);
		query2.add(result);
		
		queries.add(query);
		queries.add(query2);
		
		
		NestedJSON.queryObject(queries, index);
	}
	
	/**
	 * Compares query words and sorts them
	 */
	@Override
	public int compareTo(Query query) {
		return this.word.compareTo(query.word);
	}
}
