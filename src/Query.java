import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Stores basic movie information.
 */
public class Query {

	private final String word;
	private final Result[] results;
	
	/**
	 * 
	 * @param word
	 * @param file
	 * @param score
	 * @param count
	 */
	public Query(String word, Result[] results) {
		this.word = word;
		this.results = results;
	}

	public String word() {
		return this.word;
	}

	public Result[] results() {
		return this.results;
	}
	
	public void sort() {
		Result tempr = null;
		
		for (int x = 0; x < this.results.length - 1; x++) {
			for (int y = x + 1; y < this.results.length; y++) {
				if (this.results[y].compareTo(this.results[x]) == 0) {
					tempr = this.results[x];
					this.results[x] = this.results[y];
					this.results[y] = tempr;
				}
			}
		}
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		this.sort();
		try {
//			writer.write("[");
//			writer.write(System.lineSeparator());
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
			NestedJSON.indent(3, writer);
			
			for (int i = 0; i < results.length; i++) {
				writer.write(results[i].toString());
				if (i != results.length - 1) {
					writer.write(",");
					writer.write(System.lineSeparator());
					NestedJSON.indent(3, writer);
				} else {
					writer.write(System.lineSeparator());
					NestedJSON.indent(2, writer);
				}
			}
			
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
		Result[] results = new Result[3];
		Result result = new Result("file.txt", 11, 0.27272727);
		Result result1 = new Result("secondfile.txt", 13, 0.33333333);
		Result result2 = new Result("thirdfile.txt", 15, 0.33333333);
		TreeSet<Query> queries = new TreeSet<Query>();
		var queries2 = new ArrayList<Query>();
		
		results[0] = result;
		results[1] = result1;
		results[2] = result2;
		
		Query query = new Query("word", results);
		
		queries.add(query);
//		queries2.add(query);
		
		
		try (BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8);) { 
			NestedJSON.queryObject(queries, writer);
		}
	}
}
