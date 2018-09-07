import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.runners.AllTests;

/**
 * TODO Fill in your own comments!
 */
public class Driver {
	/**
	 * Finds text files by retrieving subdirectories until hitting files.
	 * If file has .txt or .text meaning that it is a text file, return a
	 * list of the files.
	 *
	 * The recursive version of this method is private. Users of this class will
	 * have to use the public version (see below).
	 *
	 * @param path   to retrieve the listing, assumes a directory and not a file
	 *               is passed
	 * @return textlist a list of text file names
	 * @throws IOException
	 */
	private static ArrayList<String> findText(Path path) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			ArrayList<String> textlist = new ArrayList<String>();
			for (Path file : listing) {
				if (Files.isDirectory(file)) {
					findText(file);
				} else if (file.getFileName().toString().toLowerCase().contains("txt") 
						|| file.getFileName().toString().toLowerCase().contains("text")) {
					textlist.add(file.getFileName().toString());
				}
			}
			return textlist;
		}
	}

	/**
	 * Safely starts the recursive traversal with the proper padding. Users of
	 * this class can access this method, so some validation is required.
	 *
	 * @param directory to traverse
	 * @return findText a list of text names, null if none.
	 * @throws IOException
	 */
	public static ArrayList<String> traverse(Path directory) throws IOException {
		if (Files.isDirectory(directory)) {
			return findText(directory);
		} else {
			return null;
		}
	}
	
	

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String[] validArguments = {"-path", "-index"};
		if (args.length == 0 
				|| !args[0].equals(validArguments[0]) 
				&& !args[0].equals(validArguments[1])) {
			System.err.println("Command line argument not valid.\nValid arguments: \n-path\n-index");
		}
		
		Path path = Paths.get("..", "Project 1", "project-tests", "text").toAbsolutePath().normalize();
		traverse(path);
	}

}
