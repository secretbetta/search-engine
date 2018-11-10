import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;

/*
 * TODO
 * Make the members private and create getters and setters.
 * Want the ability to change the number of matches (trigger updates to the score calculation).
 * Want to simplify the score calculation.
 */

/**
 * Data structure that stores results of a query
 * @author Andrew
 *
 */
public class Result implements Comparable<Result> {
	public final String file;
	private int count;
	private final int wordtotal;
	private double score;
	
	/**
	 * Initializes file, count, and score
	 * @param file filename
	 * @param count wordcount
	 * @param score score of words
	 */
	public Result(String file, int count, int wordtotal) {
		this.file = file;
		this.count = count;
		this.wordtotal = wordtotal;
//		this.score = score; // TODO calculate this from the (double) count / total
	}
	
	//TODO an update method
	public void add(int count) {
		this.count += count;
		score = (double)(this.count / this.wordtotal);
	}
	
	public String getFile() {
		return this.file;
	}
	
	/**
	 * Default output of Results in JSON format
	 */
	public String toString() {
		DecimalFormat FORMATTER = new DecimalFormat("0.000000");
		DecimalFormat INT = new DecimalFormat("0");
		StringWriter writer = new StringWriter();
		
		this.score = (double)this.count / this.wordtotal;
		
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
		if (this != null && result != null) {
			int val = Double.compare(result.score, this.score);
			
			if (val == 0) {
				val = Integer.compare(result.count, this.count);
				if (val == 0) {
					return this.file.compareTo(result.file);
				}
			}
			return val;
		}
		return 0;
	}
}
