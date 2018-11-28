import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Crawls URLs
 * @author Andrew
 *
 */
public class WebCrawler {

	/**
	 * Fetches the headers and content for the specified URL. The content is placed
	 * as a list of all the lines fetched under the "Content" key.
	 *
	 * @param url the url to fetch
	 * @return a map with the headers and content
	 * @throws IOException if unable to fetch headers and content
	 */
	public static String fetchURL(URL url, InvertedIndex index) throws IOException {
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
			List<String> lines = stream.collect(Collectors.toList());
			String html = String.join("\n", lines);
			
			//TODO Build index using html here
			Stemmer stem = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			int pos = 0;
			for (String word : TextParser.parse(HTMLCleaner.stripHTML(html))) {
				index.add(stem.stem(word).toString(), url.toString(), ++pos);
			}
			return html;
		} catch (IOException e) {
			return null;
		}
	}
}
