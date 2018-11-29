import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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
	private ArrayList<URL> links;
	
	public WebCrawler(int lim) {
		this.limit = lim;
		this.links = new ArrayList<URL>();
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
	 * Fetches the HTML (without any HTTP headers) for the provided URL. Will
	 * return null if the link does not point to a HTML page.
	 *
	 * @param url url to fetch HTML from
	 * @return HTML as a String or null if the link was not HTML
	 *
	 * @see <a href="https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html">Reading Directly from a URL</a>
	 */
	public static String fetchHTML(URL url) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY!

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
	public ArrayList<URL> listLinks(URL base, String html, int limit) throws MalformedURLException {
		String regex = "(?is)<a.*?href.*?=.*?\"([^@&]*?)\"[^<]*?>";
		Pattern pattern = Pattern.compile(regex);
		
		if (html != null) {
			Matcher matcher = pattern.matcher(html);
			URL url;
			
			while (matcher.find() && this.limit > 0) {
				System.out.println(base + "||" + matcher.group(1));
				System.out.println(new URL(base, matcher.group(1)));
				url = clean(new URL(base, matcher.group(1)));
				
				if (!this.links.contains(url)) {
					this.links.add(url);
					this.limit--;
					this.links = listLinks(url, fetchHTML(url), limit);
				}
			}
		}

		return this.links;
	}
	
	
	public void crawler(URL url, InvertedIndex index) throws IOException {
//		String html = WebCrawler.fetchURL(url);
//		var headers = HTMLFetcher.fetchURL(url);
		
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
}
