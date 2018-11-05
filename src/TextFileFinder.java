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
	
	public static void traverse(Path path, InvertedIndex index) throws IOException {
		String filename;
		
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				for (Path file : listing) {
					traverse(file, index);
				}
			}
		} else {
			filename = path.getFileName().toString().toLowerCase();
			
			if (filename.endsWith(".txt") || filename.endsWith(".text")) {
				IndexBuilder.buildWords(path, index);
			}
		}
	}
	
	public static void traverse(Path path, InvertedIndex index, int threads) throws IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				for (Path file : listing) {
					traverse(file, index);
				}
			}
		} else {
			String filename = path.getFileName().toString().toLowerCase();
			
			if (filename.endsWith(".txt") || filename.endsWith(".text")) {
				IndexBuilder.buildWords(path, index);
			}
		}
	}
	
	/**
	 * Runnable Traverser class
	 * @author Andrew
	 *
	 */
	public static class Traverser extends Thread {

		InvertedIndex index;
		Path path;
		
		public Traverser(Path path, InvertedIndex index) {
			this.index = index;
			this.path = path;
		}
		
		@Override
		public void run() {
			if (Files.isDirectory(path)) {
				try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
					for (Path file : listing) {
						Thread worker = new Traverser(file, index);
						worker.start();
					}
				} catch (IOException e) {
					System.err.println("Path is not a valid path: " + path);
				}
			} else {
				String filename = path.getFileName().toString().toLowerCase();
				
				if (filename.endsWith(".txt") || filename.endsWith(".text")) {
					try {
						IndexBuilder.buildWords(path, index);
					} catch (IOException e) {
						System.err.println("Cannot build index from " + path.toString());
					}
				}
			}
			
			synchronized(index) {
				
			}
		}	
	}
}
