import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Driver of Project Codes
 * @author Andrew
 *
 */
public class Driver {
	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		var argmap = new ArgumentMap(args);
		InvertedIndex invertedIndex = null;
		QueryMapInterface query = null;
		WebCrawler crawler = null;
		
		if (argmap.hasFlag("-limit")) {
			try {
				if (argmap.getInt("-limit") >= 0) {
					crawler = new WebCrawler(argmap.getInt("-limit", 50));
				} else  {
					crawler = new WebCrawler(0);
				}
				
			} catch (NumberFormatException e) {
				crawler = new WebCrawler(0);
				System.err.println(argmap.getString("-limit") + " is not a number");
			}
		}
		
		if (argmap.hasFlag("-threads")) {
			int threads = argmap.getInt("-threads", 5);
			ThreadSafeInvertedIndex threadIndex = new ThreadSafeInvertedIndex();
			query = new MultithreadQueryMap(threadIndex, threads);
			
			if (argmap.hasFlag("-path")) {
				Path path = argmap.getPath("-path");
				
				try {
					IndexBuilder.traverse(path, threadIndex, threads);
				} catch (NullPointerException e) {
					System.err.println("Unable to create inverted index. Has no elements");
				} catch (IOException e) {
					System.err.println("Unable to get path from " + path);
				}
			}
			
			if (argmap.hasFlag("-url")) {
				String url = argmap.getString("-url");
				
				try {
					crawler.crawler(new URL(url), threadIndex, threads);
					invertedIndex = threadIndex;
				} catch (IllegalArgumentException e) {
					System.err.println("URI Can't be " + url);
				} catch (IOException e) {
					System.err.println("Cannot get URL " + url);
				}
			}
		} else {
			invertedIndex = new InvertedIndex();
			query = new QueryMap(invertedIndex);
			
			if (argmap.hasFlag("-path")) {
				Path path = argmap.getPath("-path");
				
				try {
					IndexBuilder.traverse(path, invertedIndex);
				} catch (NullPointerException e) {
					System.err.println("Unable to create inverted index. Has no elements");
				} catch (IOException e) {
					System.err.println("Unable to get path from " + path);
				}
			}
			
			if (argmap.hasFlag("-url")) {
				String url = argmap.getString("-url");
				
				try {
					crawler.crawler(new URL(url), invertedIndex);
				} catch (IllegalArgumentException e) {
					System.err.println("URI Can't be " + url);
				} catch (IOException e) {
					System.err.println("Cannot get URL " + url);
				}
			}
		}
		
		if (argmap.hasFlag("-search")) {
			Path search = argmap.getPath("-search");
			
			try {
				query.builder(search, argmap.hasFlag("-exact"));
			} catch (IOException e) {
				System.err.println("Cannot build query map");
			}
		}
		
		if (argmap.hasFlag("-index")) {
			Path index = argmap.getPath("-index", Paths.get("index.json"));
			
			try {
				invertedIndex.toJSON(index);
			} catch (IOException e) {
				System.err.println("Cannot write to index " + index);
			}
		}
		
		if (argmap.hasFlag("-locations")) {
			Path locIndex = argmap.getPath("-locations", Paths.get("index-text-locations.json"));
			
			try {
				invertedIndex.locationtoJSON(locIndex);
			} catch (IOException e) {
				System.err.println("Cannot write to file " + locIndex);
			}
		}
		
		if (argmap.hasFlag("-results")) {
			Path index = argmap.getPath("-results", Paths.get("results.json"));
			
			try {
				query.toJSON(index);
			} catch (NullPointerException e) {
				System.err.println("Cannot write query from empty index");
			} catch (IOException e) {
				System.err.println("Cannot write query at path " + index);
			}
		}
	}
}