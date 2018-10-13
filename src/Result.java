import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;

/**
 * Data structure that stores results of a query
 * @author Andrew
 *
 */
public class Result implements Comparable<Result> {
	public final String file;
	public final int count;
	public final double score;
	
	/**
	 * Initializes file, count, and score
	 * @param file filename
	 * @param count wordcount
	 * @param score score of words
	 */
	public Result(String file, int count, double score) {
		this.file = file;
		this.count = count;
		this.score = score;
	}
	
	/**
	 * Gets filename
	 * @return file
	 */
	public String file() {
		return this.file;
	}
	
	/**
	 * Gets count
	 * @return count
	 */
	public double count() {
		return this.count;
	}
	
	/**
	 * Get the score
	 * @return score
	 */
	public double score() {
		return this.score;
	}
	
	/**
	 * Default output of Results in JSON format
	 */
	public String toString() {
		DecimalFormat FORMATTER = new DecimalFormat("0.000000");
		DecimalFormat INT = new DecimalFormat("0");
		StringWriter writer = new StringWriter();
		
//		System.out.println("File: " + this.file + "\nCount: " + this.count + "\nScore: " + this.score);
		try {
			NestedJSON.indent(3, writer);
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
			return writer.toString();
		} catch (IOException e) {
			
		}
		
		return writer.toString();
	}

	/**
	 * Overriden compareTo method from Comparable for
	 * comparing results by score, then count, then file name
	 * 
	 * return -1 if the same or this values are great than, 1 if less than
	 */
	@Override
	public int compareTo(Result result) {
		int val = Double.compare(result.score, this.score);
		
		if (val == 0) {
			val = Integer.compare(result.count, this.count);
			if (val == 0) {
				return this.file.compareTo(result.file);
			}
		}
		return val;
	}
}
