import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Finds all textfiles
 */
public class TextFileFinder {
	
	/**
	 * Traverses through all directories and finds all textfiles
	 * @param path Given path
	 * @return ArrayList of Paths that lead to textfiles
	 * @throws IOException
	 */
	public static ArrayList<Path> traverse(Path path) throws IOException {
		ArrayList<Path> files = new ArrayList<Path>();
		
		String filename;
		
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				for (Path file : listing) {
					files.addAll(traverse(file));
				}
			}
		} else {
			filename = path.getFileName().toString().toLowerCase();
			
			if (filename.endsWith(".txt") || filename.endsWith(".text")) {
				files.add(path);
			}
		}
	
		return files;
	}
}
