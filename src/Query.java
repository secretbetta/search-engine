import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

/**
 * Stores basic movie information.
 */
public class Query implements Comparable<Query> {

	public final String word;
	public final ArrayList<Result> results;
	
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
	 * @param result Result of query
	 */
	public void add(Result result) {
//		boolean flag = true;
//		if (!this.results.isEmpty()) {
//			for (Result r : this.results) {
//				if (r != null && !r.file.equals(result.file)) {
//					this.results.add(result);
//					break;
//				}
//			}
//		} else {
//			this.results.add(result);
//		}
		
//		for (Result r : this.results) {
//			if (r.file.equals(result.file)) {
//				flag = false;
//			}
//		}
//		if (flag) {
//			this.results.add(result);
//		}
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
				if (results.get(i) != null && i != results.size() - 1) {
					writer.write(results.get(i).toString());
					writer.write(",");
					writer.write(System.lineSeparator());
				} else if (results.get(i) != null) {
					writer.write(results.get(i).toString());
					writer.write(System.lineSeparator());
				}
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
		TreeMap<String, ArrayList<Result>> queries = new TreeMap<String, ArrayList<Result>>();
		
		results.add(result2);
		results.add(result1);
		results.add(result);
		Collections.sort(results);
//		System.out.println(results);
		
		Query query = new Query("word", new ArrayList<Result>());
		Query query2 = new Query("bird", new ArrayList<Result>());
		queries.put("word", results);
		query.add(result);
		query.add(result1);
		query.add(result2);
		query.sort();
//		query2.add(result);
		
		
		System.out.println(query.results);
//		query.add(result1);
//		query2.add(result1);
		
//		queries.add(query);
//		queries.add(query2);
		
		
//		NestedJSON.queryObject(queries, index);
	}
	
	/**
	 * Compares query words and sorts them
	 */
	@Override
	public int compareTo(Query query) {
		return this.word.compareTo(query.word);
	}
}
