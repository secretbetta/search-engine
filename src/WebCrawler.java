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
			
			//TODO Build index using html here
//			Stemmer stem = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
//			int pos = 0;
//			for (String word : TextParser.parse(HTMLCleaner.stripHTML(html))) {
//				index.add(stem.stem(word).toString(), url.toString(), ++pos);
//			}
			
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
	public int listLinks(URL base, String html, int limit) throws IOException {
		String regex = "(?is)<a.*?href.*?=.*?\"([^@&]*?)\"[^<]*?>";
		Pattern pattern = Pattern.compile(regex);
		
		if (html != null) {
			Matcher matcher = pattern.matcher(html);
			
			while (matcher.find() && limit > 0) {
				if (!links.contains(clean(new URL(base, matcher.group(1)))) 
						&& (clean(new URL(base, matcher.group(1))).toString().toLowerCase().endsWith(".html")
						|| clean(new URL(base, matcher.group(1))).toString().toLowerCase().endsWith(".htm"))
						) {
					this.links.add(clean(new URL(base, matcher.group(1))));
				}
				
				limit--;
				limit = listLinks(clean(new URL(base, matcher.group(1))), WebCrawler.fetchURL(clean(new URL(base, matcher.group(1)))), limit);
			}
		}
		return limit;
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
	
	
	public void crawler(URL url, InvertedIndex index) throws IOException {
		String html = WebCrawler.fetchURL(url);
		IndexBuilder.getWords(url, index, html);
		System.out.println(url);
		
		if (html != null) {
			listLinks(url, html, limit);
			
			for (URL site : this.links) {
				html = WebCrawler.fetchURL(site);
				System.out.println(site);
				IndexBuilder.getWords(site, index, html);
			}
		}
	}
}
