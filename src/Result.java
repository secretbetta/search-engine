import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;

public class Result implements Comparable<Result> {
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
	
	public String toString() {
		DecimalFormat FORMATTER = new DecimalFormat("0.000000");
		DecimalFormat INT = new DecimalFormat("0");
		StringWriter writer = new StringWriter();
		
//		System.out.println("File: " + this.file + "\nCount: " + this.count + "\nScore: " + this.score);
		try {
			writer.write("{");
			writer.write(System.lineSeparator());
			NestedJSON.indent(4, writer);
			
			writer.write("\"where\": \"");
			writer.write(this.file);
			writer.write("\",");
			writer.write(System.lineSeparator());
			NestedJSON.indent(4, writer);
			
			writer.write("\"count\": ");
			writer.write(INT.format(this.count));
			writer.write(",");
			writer.write(System.lineSeparator());
			NestedJSON.indent(4, writer);
			
			writer.write("\"score\": ");
			writer.write(FORMATTER.format(this.score));
			writer.write(System.lineSeparator());
			NestedJSON.indent(3, writer);
			
			writer.write("}");
		} catch (IOException e) {
			
		}
		
		return writer.toString();
	}

	@Override
	public int compareTo(Result result) {
		if (this.score() < result.score()) {
			return 1;
		} else if (this.score() == result.score()) {
			if (this.count() < result.count()) {
				return 1;
			} else if (this.count() == result.count()) {
				return this.file().compareTo(result.file());
			}
		}
		return 0;
	}
}
