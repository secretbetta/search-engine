import java.io.IOException;
import java.io.StringWriter;

/**
 * Stores basic movie information.
 */
public class Query {

	private final String word;
	private final Result result;
	
	/**
	 * 
	 * @param word
	 * @param file
	 * @param score
	 * @param count
	 */
	public Query(String word, Result result) {
		this.word = word;
		this.result = result;
	}

	public String word() {
		return this.word;
	}

	public Result results() {
		return this.result;
	}

	public String toString(int level) throws IOException {
		StringWriter writer = new StringWriter();
		
		
		writer.write("[");
		writer.write(System.lineSeparator());
		NestedJSON.indent(level + 1, writer);
		
		writer.write("{");
		writer.write(System.lineSeparator());
		NestedJSON.indent(level + 2, writer);
		
		writer.write("\"queries\": \"");
		writer.write(this.word);
		writer.write("\",");
		writer.write(System.lineSeparator());
		NestedJSON.indent(level + 2, writer);
		
		writer.write("\"results\": [");
		writer.write(System.lineSeparator());
		NestedJSON.indent(level + 3, writer);
		
//		for (int i = 0; i < results.length; i++) {
//			writer.write(results[i].toString(0));
//			if (i != results.length - 1) {
//				writer.write(",");
//				writer.write(System.lineSeparator());
//				NestedJSON.indent(level + 3, writer);
//			} else {
//				writer.write(System.lineSeparator());
//				NestedJSON.indent(level + 2, writer);
//			}
//		}
		
		writer.write("]");
		writer.write(System.lineSeparator());
		NestedJSON.indent(level + 1, writer);
		
		return writer.toString();
	}
	
	public static void main(String[] args) throws IOException {
//		Result[] result = new Result[1];
		Result result = new Result("file.txt", 11, 0.27272727);
		Query querymap = new Query("word", result);
//		Path index = Paths.get("..", "testing.json");
		
		System.out.println(querymap.toString(0));
		
	}
}
