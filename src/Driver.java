import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * TODO Fill in your own comments!
 */
public class Driver {

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @return 0 if everything went well
	 */
	public static int main(String[] args) {
		// TODO Fill in
		System.out.println(Arrays.toString(args));
		
		Path path = Paths.get("..", "project-tests", "text");
		
		return 0;
	}

}
