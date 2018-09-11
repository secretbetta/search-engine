import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
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
	 * Gets words from a given text file in the path. 
	 *
	 * @param input path to the input file
	 * @return list list of words in textfile
	 * @throws IOException
	 */
	public static ArrayList<String> getWords(Path input) throws IOException {
		try (
				BufferedReader reader = Files.newBufferedReader(
						input, StandardCharsets.UTF_8
						);
				) {
			ArrayList<String> words = new ArrayList<String>();
			String line = null;
			String[] list = null;
//			if (traverse())

			while ((line = reader.readLine()) != null) {
				list = line.split(" ");
				for (String word: list) {
					words.add(word);
				}
			}
			return words;
		}

		// note: we can still throw exceptions (do not need to catch)
	}
	
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
	 * Safely starts the recursive traversal with the proper padding. 
	 * Finds all text files in the directory.
	 *
	 * @param directory to traverse
	 * @return findText a list of text names, null if none.
	 * @throws IOException
	 */
	public static ArrayList<String> traverse(Path directory) throws IOException {
		var single = new ArrayList<String>();
		if (Files.isDirectory(directory)) {
			return findText(directory);
		} else {
			if (directory.getFileName().toString().toLowerCase().contains("txt") 
					|| directory.getFileName().toString().toLowerCase().contains("text")) {
				single.add(directory.getFileName().toString());
				return single;
			} else {
				return null;
			}
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
		ArrayList<String> textFiles;
		ArrayList<String> words;
		if (args.length == 0 
				|| !args[0].equals(validArguments[0]) 
				&& !args[0].equals(validArguments[1])) {
			System.err.println("Command line argument not valid."
					+ "\nValid arguments: "
					+ "\n-path path where the flag -path indicates the next argument "
					+ "is a path to either a single text file or a directory of text "
					+ "files that must be processed and added to the inverted index"
					+ "\n-index where the flag -index is an optional flag that indicates "
					+ "the next argument is the path to use for the inverted index output "
					+ "file. If the path argument is not provided, use index.json as the "
					+ "default output path. If the -index flag is not provided, do not "
					+ "produce an output file.");
		}
		
//		Path path = Paths.get("..", "Project 1", "project-tests", "text").toAbsolutePath().normalize();
//		textFiles = traverse(path);
		Path path = Paths.get("..", "Project 1", "project-tests", "text", "simple", "hello.txt").toAbsolutePath().normalize();
		if (!(traverse(path.getFileName()) == null)) {
			words = getWords(path);
			System.out.println(words.size());
			System.out.println(words);
		}
		System.out.println(traverse(path.getFileName()));
		
	}

}
