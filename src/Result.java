import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;

/**
 * Data structure that stores results of a query
 * @author Andrew
 *
 */
public class Result implements Comparable<Result> {
	private final String file;
	private int count;
	private final int wordtotal;
	private double score;
	
	/**
	 * Initializes file, count, wordtotal, and score
	 * @param file filename
	 * @param count wordcount
	 * @param wordtotal total amount of words
	 */
	public Result(String file, int count, int wordtotal) {
		this.file = file;
		this.count = count;
		this.wordtotal = wordtotal;
		this.score = (double)(this.count / (double)this.wordtotal);
	}
	
	/**
	 * Updates count and score
	 * @param count Additional word count
	 */
	public void add(int count) {
		this.count += count;
		score = (double)(this.count / (double)this.wordtotal);
	}
	
	/**
	 * Gets filename
	 * @return {@link #file}
	 */
	public String getFile() {
		return this.file;
	}
	
	/**
	 * Gets count of words
	 * @return {@link #count}
	 */
	public int getCount() {
		return this.count;
	}
	
	/**
	 * Gets total words
	 * @return {@link #wordtotal}
	 */
	public int getTotal() {
		return this.wordtotal;
	}
	
	/**
	 * Gets score
	 * @return {@link #score}
	 */
	public double getScore() {
		return this.score;
	}
	
	/**
	 * Default output of Results in JSON format
	 */
	@Override
	public String toString() {
		DecimalFormat FORMATTER = new DecimalFormat("0.000000");
		StringWriter writer = new StringWriter();
		
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
			writer.write(Integer.toString((this.count)));
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
			System.err.println("Cannot write to JSON");
		}
		
		return writer.toString();
	}

	/**
	 * Overriden compareTo method from Comparable for
	 * comparing results by score, then count, then file name
	 * 
	 * return difference of values, 0 if equal
	 */
	@Override
	public int compareTo(Result result) {
		if (this != null && result != null) { // TODO Remove the null checks, if you are getting nulls here there is a bug we need to find.
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
