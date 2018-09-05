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
	 */
	public static void main(String[] args) {
		// TODO Fill in
		System.out.println(Arrays.toString(args));
		String[] validArguments = {"-path", "-index"};
		if (args.length == 0 || !args[0].equals(validArguments[0]) && !args[0].equals(validArguments[1])) {
			System.err.println("Command line argument not valid.\nValid arguments: \n-path\n-index");
		}
		
		
		
		Path path = Paths.get("..", "project-tests", "text");
	}

}
