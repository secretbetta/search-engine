import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Stores basic movie information.
 */
public class IndexSorter implements Comparable<IndexSorter> {

	private final String word;
	private final List<String> file;
	private final double score;
	private final int count;
	
	/**
	 * 
	 * @param word
	 * @param file
	 * @param score
	 * @param count
	 */
	public IndexSorter(String word, List<String> file, double score, int count) {
		this.word = word;
		this.file = file;
		this.score = score;
		this.count = count;
	}

	public String word() {
		return this.word;
	}

	public double score() {
		return this.score;
	}

	public int count() {
		return this.count;
	}

	public List<String> file() {
		return this.file;
	}

	@Override
	public String toString() {
		String stringFormat = "\"%s\", released %s, %d minutes long, %2.1f rating";

		Object[] args = {
				this.word,
				this.file,
				this.count,
				this.score
		};

		return String.format(stringFormat, args);
	}

	/**
	 * Creates and outputs a movie instance.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {

		Movie m1 = new Movie(
				"The Lord of the Rings: The Fellowship of the Ring",
				new GregorianCalendar(2001, Calendar.DECEMBER, 19),
				Duration.ofHours(2).plusMinutes(58),
				8.8);

		Movie m2 = new Movie(
				"The Lord of the Rings: The Two Towers",
				new GregorianCalendar(2002, Calendar.DECEMBER, 18),
				Duration.ofHours(2).plusMinutes(59),
				8.7);

		Movie m3 = new Movie(
				"The Lord of the Rings: The Return of the King",
				new GregorianCalendar(2003, Calendar.DECEMBER, 17),
				Duration.ofHours(3).plusMinutes(21),
				8.9);

		ArrayList<Movie> movies = new ArrayList<>();
		Collections.addAll(movies, m2, m1, m3);
		Collections.sort(movies);
		movies.forEach(System.out::println);
		System.out.println();
	}
}
