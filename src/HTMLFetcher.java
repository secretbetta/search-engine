import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class HTMLFetcher {
	/**
	 * Fetches the headers and content for the specified URL. The content is
	 * placed as a list of all the lines fetched under the "Content" key.
	 *
	 * @param url the url to fetch
	 * @return a map with the headers and content
	 * @throws IOException if unable to fetch headers and content
	 */
	public static Map<String, List<String>> fetchURL(URL url) throws IOException {
		Map<String, List<String>> results = new HashMap<>();

		String protocol = url.getProtocol();
		String host = url.getHost();
		String resource = url.getFile().isEmpty() ? "/" : url.getFile();

		boolean https = (protocol != null) && protocol.equalsIgnoreCase("https");
		int defaultPort = https ? 443 : 80;
		int port = url.getPort() < 0 ? defaultPort : url.getPort();

		try (
				Socket socket = https ?
						SSLSocketFactory.getDefault().createSocket(host, port) :
						SocketFactory.getDefault().createSocket(host, port);

				PrintWriter request = new PrintWriter(socket.getOutputStream());

				InputStreamReader input = new InputStreamReader(socket.getInputStream());
				BufferedReader response = new BufferedReader(input);
		) {
			request.printf("GET %s HTTP/1.1\r\n", resource);
			request.printf("Host: %s\r\n", host);
			request.printf("Connection: close\r\n");
			request.printf("\r\n");
			request.flush();

			String line = response.readLine();

			results.put(null, Arrays.asList(line));

			while ((line = response.readLine()) != null) {
				if (line.trim().isEmpty()) {
					break;
				}

				String[] split = line.split(":\\s+", 2);
//				assert split.length == 2;

				results.putIfAbsent(split[0], new ArrayList<>());
				results.get(split[0]).add(split[1]);
			}
			
			List<String> lines = new ArrayList<>();
			while ((line = response.readLine()) != null) {
				lines.add(line);
			}

			results.put("Content", lines);
		}

		return results;
	}

	/**
	 * Given a map of headers (as returned either by {@link URLConnection#getHeaderFields()}
	 * or by {@link HttpsFetcher#fetchURL(URL)}, determines if the content type of the
	 * response is HTML.
	 *
	 * @param headers map of HTTP headers
	 * @return true if the content type is html
	 *
	 * @see URLConnection#getHeaderFields()
	 * @see HttpsFetcher#fetchURL(URL)
	 */
	public static boolean isHTML(Map<String, List<String>> headers) {
		String regex = ".*?/html;.*?";
		
		if (headers.get("Content-Type") != null) {
			return Pattern.matches(regex, headers.get("Content-Type").get(0));
		} else {
			return false;
		}
	}

	/**
	 * Given a map of headers (as returned either by {@link URLConnection#getHeaderFields()}
	 * or by {@link HttpsFetcher#fetchURL(URL)}, returns the status code as an int value.
	 * Returns -1 if any issues encountered.
	 *
	 * @param headers map of HTTP headers
	 * @return status code or -1 if unable to determine
	 *
	 * @see URLConnection#getHeaderFields()
	 * @see HttpsFetcher#fetchURL(URL)
	 */
	public static int getStatusCode(Map<String, List<String>> headers) {
		return Integer.parseInt(headers.get(null).get(0).substring(9, 12));
	}

	/**
	 * Given a map of headers (as returned either by {@link URLConnection#getHeaderFields()}
	 * or by {@link HttpsFetcher#fetchURL(URL)}, returns whether the status code
	 * represents a redirect response *and* the location header is properly included.
	 *
	 * @param headers map of HTTP headers
	 * @return true if the HTTP status code is a redirect and the location header is non-empty
	 *
	 * @see URLConnection#getHeaderFields()
	 * @see HttpsFetcher#fetchURL(URL)
	 */
	public static boolean isRedirect(Map<String, List<String>> headers) {
		return headers.containsKey("Location");
	}

	/**
	 * Uses {@link HttpsFetcher#fetchURL(URL)} to fetch the headers and content of the
	 * specified url. If the response was HTML, returns the HTML as a single {@link String}.
	 * If the response was a redirect and the value of redirects is greater than 0, will
	 * return the result of the redirect (decrementing the number of allowed redirects).
	 * Otherwise, will return {@code null}.
	 *
	 * @param url the url to fetch and return as html
	 * @param redirects the number of times to follow a redirect response
	 * @return the html as a single String if the response code was ok, otherwise null
	 * @throws IOException
	 *
	 * @see #isHTML(Map)
	 * @see #getStatusCode(Map)
	 * @see #isRedirect(Map)
	 */
	public static String fetchHTML(URL url, int redirects) throws IOException {
		Map<String, List<String>> headers = fetchURL(url);
		
		if (isRedirect(headers) && redirects > 0) {
			return fetchHTML(headers.get("Location").get(0), --redirects);
		}
		
		if (!isHTML(headers) || getStatusCode(headers) != 200) {
			return null;
		} else {
			return String.join(System.lineSeparator(), headers.get("Content"));
		}
	}

	/**
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(String url) throws IOException {
		return fetchHTML(new URL(url), 0);
	}

	/**
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(String url, int redirects) throws IOException {
		return fetchHTML(new URL(url), redirects);
	}

	/**
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(URL url) throws IOException {
		return fetchHTML(url, 0);
	}
//	
//	/**
//	 * Removes the fragment component of a URL (if present), and properly encodes
//	 * the query string (if necessary).
//	 *
//	 * @param url url to clean
//	 * @return cleaned url (or original url if any issues occurred)
//	 */
//	public static URL clean(URL url) {
//		try {
//			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
//					url.getQuery(), null).toURL();
//		}
//		catch (MalformedURLException | URISyntaxException e) {
//			return url;
//		}
//	}
//	
//	/**
//	 * Returns a list of all the HTTP(S) links found in the href attribute of the
//	 * anchor tags in the provided HTML. The links will be converted to absolute
//	 * using the base URL and cleaned (removing fragments and encoding special
//	 * characters as necessary).
//	 *
//	 * @param base base url used to convert relative links to absolute3
//	 * @param html raw html associated with the base url
//	 * @return cleaned list of all http(s) links in the order they were found
//	 * @throws MalformedURLException 
//	 */
//	public static ArrayList<URL> listLinks(URL base, String html) throws MalformedURLException {
//		ArrayList<URL> links = new ArrayList<URL>();
//		
//		String regex = "(?is)<a.*?href.*?=.*?\"([^@&]*?)\"[^<]*?>";
//		Pattern pattern = Pattern.compile(regex);
//		Matcher matcher = pattern.matcher(html);
//		
//		while (matcher.find()) {
//			links.add(clean(new URL(base, matcher.group(1))));
//		}
//
//		return links;
//	}
}
