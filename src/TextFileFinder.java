import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class TextFileFinder {
	
	/**
	 * Finds text files by retrieving sub-directories until hitting files.
	 * If file has .txt or .text extensions, return a list of the files.
	 *
	 * <p>The recursive version of this method is private. Users of this class will
	 * have to use the public version (see below).</p>
	 *
	 * @param path   to retrieve the listing, assumes a directory and not a file
	 *               is passed
	 * @return textlist a list of text file names
	 * @throws IOException
	 */
	private ArrayList<String> findText(Path path) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			var  textlist = new ArrayList<String>();
			String filename;
			
			for (Path file : listing) {
				filename = file.getFileName().toString().toLowerCase();
				if (Files.isDirectory(file)) {
					textlist.addAll(findText(file));
				} else if (filename.endsWith(".txt") || filename.endsWith(".text")) {
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
	public ArrayList<String> traverse(Path directory) throws IOException {
		var single = new ArrayList<String>();
		String filename;
		
		if (Files.isDirectory(directory)) {
			return findText(directory);
		} else {
			filename = directory.getFileName().toString().toLowerCase();
			
			if (filename.endsWith("txt") || filename.endsWith("text")) {
				single.add(directory.getFileName().toString());
				return single;
			} else {
				return null;
			}
		}
	}
}
