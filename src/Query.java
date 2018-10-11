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
	
	public void sort() throws IOException {
		ArrayList<Result> temp = new ArrayList<Result>();
		Result tempr = null;
//		for (int x = 1; x < this.results.size(); x++) {
//			for (int y = x - 1; y < this.results.size(); y++) {
//				if (this.results.get(x).compareTo(this.results.get(y)) == 0) {
//					temp.add(this.results.get(x));
//				} 
//				else {
//					temp.add(this.results.get(y));
//				}
//			}
//		}
		
		for (int x = 0; x < this.results.size() - 1; x++) {
			for (int y = x + 1; y < this.results.size(); y++) {
				if (this.results.get(y).compareTo(this.results.get(x)) == 1) {
					System.out.println("test");
					tempr = this.results.get(x);
				}
			}
			temp.add(tempr);
		}
		
		this.results.clear();
		for (Result result : temp) {
			this.results.add(result);
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
		
//		for (int i = 0; i < results.size(); i++) {
//			writer.write(results.get(i).toString(0));
//			if (i != results.size() - 1) {
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
		ArrayList<Result> results = new ArrayList<Result>();
		Result result = new Result("file.txt", 11, 0.27272727);
		Result result1 = new Result("secondfile.txt", 13, 0.33333333);
		Result result2 = new Result("thirdfile.txt", 13, 0.33333333);
		results.add(result);
		results.add(result1);
		results.add(result2);
		
		Query querymap = new Query("word", results);
		
		querymap.toString(0);
		
//		System.out.println(querymap.toString(0));
		
	}
}
