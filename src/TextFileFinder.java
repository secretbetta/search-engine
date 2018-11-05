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
		String filename;
		ArrayList<Path> files = new ArrayList<Path>();
		
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
	
	public static void builder(InvertedIndex index, ArrayList<Path> files, int threads) throws IOException {
		WorkQueue queue = new WorkQueue(threads);
		
		for (Path file : files) {
			queue.execute(new Traverser(file, index));
		}
		
		queue.finish();
		queue.shutdown();
	}
	
	/**
	 * Runnable Traverser class
	 * @author Andrew
	 *
	 */
	public static class Traverser implements Runnable {

		InvertedIndex index;
		Path path;
		
		public Traverser(Path path, InvertedIndex index) {
			this.index = index;
			this.path = path;
		}
		
		@Override
		public void run() {
			try {
				IndexBuilder.buildWords(path, index);
			} catch (IOException e) {
				System.err.println("Cannot build index from path " + path);
			}
		}
	}
}
