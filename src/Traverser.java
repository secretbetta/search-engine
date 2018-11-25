import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds all textfiles
 */
public class Traverser {
	
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
	
	/**
	 * Removes the fragment component of a URL (if present), and properly encodes
	 * the query string (if necessary).
	 *
	 * @param url url to clean
	 * @return cleaned url (or original url if any issues occurred)
	 */
	public static URL clean(URL url) {
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), null).toURL();
		}
		catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}
	
	/**
	 * Traverses through all links up to a limit
	 * @param url URL of web
	 * @param limit Limit of how many links
	 * @return urls ArrayList of urls
	 * @throws IOException
	 */
	public static ArrayList<URL> traverse(URL url, int limit) throws IOException {
		String html = HTMLFetcher.fetchHTML(url);
		ArrayList<URL> urls = new ArrayList<URL>();
		limit--;
		urls.add(clean(url));
		
		String regex = "(?is)<a.*?href.*?=.*?\"([^@&]*?)\"[^<]*?>";
		Pattern pattern = Pattern.compile(regex);
		
		//TODO Make this faster
		System.out.println(limit);
		if (limit > 0 && html != null) {
			Matcher matcher = pattern.matcher(html);
			while (matcher.find()) {
				System.out.println(url);
				System.out.println(urls);
				if (!urls.contains(clean(new URL(url, matcher.group(1))))) {
					urls.addAll(traverse((clean(new URL(url, matcher.group(1)))), limit));
				}
			}
		}
		System.out.println(url);
		return urls;
	}
}
