import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Stores basic movie information.
 */
public class Query {

	private final String word;
	private final ArrayList<Result> results;
	
	/**
	 * 
	 * @param word
	 * @param file
	 * @param score
	 * @param count
	 */
	public Query(String word, ArrayList<Result> results) {
		this.word = word;
		this.results = results;
	}

	public String word() {
		return this.word;
	}

	public ArrayList<Result> results() {
		return this.results;
	}
	
	public void sort() {
		System.out.println(results.get(0));
		Result temp;
		for (int x = 0; x < this.results.size(); x++) {
			for (int y = x + 1; y < this.results.size(); y++) {
				if (this.results.get(x).compareTo(this.results.get(y)) == 1) {
					temp = this.results.get(x);
					this.results.get(x) = this.results.get(y);
					this.results.get(y) = temp;
				}
			}
		}
	}

	public String toString(int level) throws IOException {
		StringWriter writer = new StringWriter();
		
		this.sort();
		
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
		
		for (int i = 0; i < results.size(); i++) {
			writer.write(results.get(i).toString(0));
			if (i != results.size() - 1) {
				writer.write(",");
				writer.write(System.lineSeparator());
				NestedJSON.indent(level + 3, writer);
			} else {
				writer.write(System.lineSeparator());
				NestedJSON.indent(level + 2, writer);
			}
		}
		
		writer.write("]");
		writer.write(System.lineSeparator());
		NestedJSON.indent(level + 1, writer);
		
		return writer.toString();
	}
	
	public static void main(String[] args) throws IOException {
		ArrayList<Result> results = new ArrayList<Result>();
		Result result = new Result("file.txt", 11, 0.27272727);
		Result result1 = new Result("secondfile.txt", 13, 0.33333333);
		results.add(result);
		results.add(result1);
		
//		result.toString(0);
//		System.out.println();
		Query querymap = new Query("word", results);
		
		querymap.toString(0);
		
//		System.out.println(querymap.toString(0));
		
	}
}
