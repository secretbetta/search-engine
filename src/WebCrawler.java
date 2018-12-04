import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Crawls URLs
 * @author Andrew
 *
 */
public class WebCrawler {
	
	private int limit;
	private HashSet<URL> links;
	
	/**
	 * Initializes limit and links
	 * @param lim Number of links
	 */
	public WebCrawler(int lim) {
		this.limit = lim;
		this.links = new HashSet<URL>();
	}

	/**
	 * Removes the fragment component of a URL (if present), and properly encodes
	 * the query string (if necessary).
	 *
	 * @param url url to clean
	 * @return cleaned url (or original url if any issues occurred)
	 */
	private static URL clean(URL url) {
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), null).toURL();
		}
		catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}

	/**
	 * Fetches the HTML (without any HTTP headers) for the provided URL. Will
	 * return null if the link does not point to a HTML page.
	 *
	 * @param url url to fetch HTML from
	 * @return HTML as a String or null if the link was not HTML
	 *
	 * @see <a href="https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html">Reading Directly from a URL</a>
	 */
	public static String fetchHTML(URL url) {
		String result = null;

		try {
			URLConnection connection = url.openConnection();
			String type = connection.getContentType();

			if (type != null && type.matches(".*\\bhtml\\b.*")) {
				try (
						InputStreamReader input = new InputStreamReader(connection.getInputStream());
						BufferedReader reader = new BufferedReader(input);
						Stream<String> stream = reader.lines();
				) {
					result = stream.collect(Collectors.joining("\n"));
				}
			}
		}
		catch (IOException e) {
			result = null;
		}

		return result;
	}

	/**
	 * Returns a list of all the HTTP(S) links found in the href attribute of the
	 * anchor tags in the provided HTML. The links will be converted to absolute
	 * using the base URL and cleaned (removing fragments and encoding special
	 * characters as necessary).
	 *
	 * @param base base url used to convert relative links to absolute3
	 * @param html raw html associated with the base url
	 * @return cleaned list of all http(s) links in the order they were found
	 * @throws MalformedURLException 
	 */
	public void listLinks(URL base, String html, int limit) throws MalformedURLException {
		String regex = "(?is)<a.*?href.*?=[\\s]*?\"([^@&]*?)\"[^<]*?>";
		Pattern pattern = Pattern.compile(regex);
		
		ArrayList<URL> locallinks = new ArrayList<URL>();
		
		if (html != null) {
			Matcher matcher = pattern.matcher(html);
			URL url = null;
			
			while (matcher.find() && this.limit > 0) {
				url = clean(new URL(base, matcher.group(1)));
				
				if (!this.links.contains(url) && !locallinks.contains(url) && url.getHost().equals(base.getHost())) {
					locallinks.add(url);
					this.limit--;
				}
			}	
		}
		
		for (URL link : locallinks) {
			listLinks(link, fetchHTML(link), limit);
		}
		
		this.links.addAll(locallinks);
	}
	
	/**
	 * Crawls url and builds index
	 * @param url Link to crawl
	 * @param index Index to build to
	 * @throws IOException
	 */
	public void crawler(URL url, InvertedIndex index) throws IOException {
		String html = HTMLFetcher.fetchHTML(url, 3);
		
		if (html != null) {
			this.links.add(url);
			listLinks(url, html, --limit);
			
			for (URL site : this.links) {
				html = HTMLFetcher.fetchHTML(site, 3);
				if (html != null) {
					IndexBuilder.getWords(site, index, html);
				}
			}
		}
	}
	
	/**
	 * Thread version of crawler method
	 * @param url URL to crawl
	 * @param index Index to build to
	 * @param threads Number of threads to use
	 * @throws IOException
	 * 
	 * @see {@link #crawler(URL, InvertedIndex)}
	 */
	public void crawler(URL url, ThreadSafeInvertedIndex index, int threads) throws IOException {
		WorkQueue queue = new WorkQueue(threads);
		String html = HTMLFetcher.fetchHTML(url, 3);
		
		if (html != null) {
			this.links.add(url);
			listLinks(url, html, --limit);
			
			for (URL site : this.links) {
				queue.execute(new Crawler(site, index));
			}
		}
	}
	
	/**
	 * Runnable Crawler class for WebCrawler
	 * @author Andrew
	 *
	 */
	public static class Crawler implements Runnable {
		
		URL link;
		InvertedIndex index;
		
		public Crawler(URL link, InvertedIndex index) {
			this.link = link;
			this.index = index;
		}
		
		@Override
		public void run() {
			String html;
			try {
				html = HTMLFetcher.fetchHTML(link, 3);
				if (html != null) {
					IndexBuilder.getWords(link, index, html);
				}
			} catch (IOException e) {
				System.err.println("Cannot build words from link " + link);
			}
		}	
	}
}
