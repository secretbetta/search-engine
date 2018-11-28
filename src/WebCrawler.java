import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	 * Fetches the headers and content for the specified URL. The content is placed
	 * as a list of all the lines fetched under the "Content" key.
	 *
	 * @param url the url to fetch
	 * @return a map with the headers and content
	 * @throws IOException if unable to fetch headers and content
	 */
	private static String fetchURL(URL url) throws IOException {
		// used to store all headers and content
		Map<String, List<String>> results = new HashMap<>();

		// connection to url
		URLConnection urlConnection = url.openConnection();

		// by default HttpURLConnection will follow redirects (within same protocol) automatically
		// this is the connection usually underlying URLConnection
		HttpURLConnection.setFollowRedirects(false);

		// close connection instead of keep-alive
		urlConnection.setRequestProperty("Connection", "close");

		// get all of the headers
		// note the status code might be placed with a "null" key
		results.putAll(urlConnection.getHeaderFields());

		try (
				InputStreamReader input = new InputStreamReader(urlConnection.getInputStream());
				BufferedReader reader = new BufferedReader(input);
				Stream<String> stream = reader.lines();
		) {
			String html = stream.collect(Collectors.joining("\n"));
			
			
			
			return html;
		} catch (IOException e) {
			return null;
		}
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
	 * @throws IOException 
	 */
	public void listLinks(URL base, int limit) throws IOException {
		String html = HTMLFetcher.fetchHTML(base);
		listLinks(base, html, this.links);
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
	public ArrayList<URL> listLinks(URL base, String html, ArrayList<URL> links) throws MalformedURLException {
		String regex = "(?is)<a.*?href.*?=.*?\"([^@&]*?)\"[^<]*?>";
		Pattern pattern = Pattern.compile(regex);
		
		if (html!=null) {
			Matcher matcher = pattern.matcher(html);
			
			while (matcher.find() && this.limit > 0) {
				if (!links.contains(clean(new URL(base, matcher.group(1))))) {
					links.add(clean(new URL(base, matcher.group(1))));
				}
				this.limit--;
				links = listLinks(clean(new URL(base, matcher.group(1))), fetchHTML(clean(new URL(base, matcher.group(1)))), links);
			}
		}

		return links;
	}
	
	
	public void crawler(URL url, InvertedIndex index) throws IOException {
		String html = WebCrawler.fetchURL(url);
		IndexBuilder.getWords(url, index, html);
		
		if (html != null) {
			listLinks(url, --limit);
			
			for (URL site : this.links) {
				html = WebCrawler.fetchURL(site);
				IndexBuilder.getWords(site, index, html);
			}
		}
	}
}
