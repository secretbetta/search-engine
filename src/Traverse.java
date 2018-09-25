import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Traverse {
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
					textlist.addAll(findText(file));
				} else if (file.getFileName().toString().toLowerCase().endsWith(".txt")
						|| file.getFileName().toString().toLowerCase().endsWith(".text")) {
					textlist.add(file.toString());
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
}
