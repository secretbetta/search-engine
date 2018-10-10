import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;

public class Result {
	public final String file;
	public final double count;
	public final double score;
	
	public Result(String file, double count, double score) {
		this.file = file;
		this.count = count;
		this.score = score;
	}
	
	public String file() {
		return this.file;
	}
	
	public double count() {
		return this.count;
	}
	
	public double score() {
		return this.score;
	}
	
	public String toString(int level) throws IOException {
		DecimalFormat FORMATTER = new DecimalFormat("0.000000");
		DecimalFormat INT = new DecimalFormat("0");
		StringWriter writer = new StringWriter();

		writer.write("{");
		writer.write(System.lineSeparator());
		NestedJSON.indent(level + 4, writer);
		
		writer.write("\"where\": \"");
		writer.write(this.file);
		writer.write("\",");
		writer.write(System.lineSeparator());
		NestedJSON.indent(level + 4, writer);
		
		writer.write("\"count\": ");
		writer.write(INT.format(this.count));
		writer.write(",");
		writer.write(System.lineSeparator());
		NestedJSON.indent(level + 4, writer);
		
		writer.write("\"score\": ");
		writer.write(FORMATTER.format(this.score));
		writer.write(System.lineSeparator());
		NestedJSON.indent(level + 3, writer);
		
		writer.write("}");
		
		return writer.toString();
	}
}
