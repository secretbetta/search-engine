import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONReader {
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	
	public static void readNested(Path path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line;
			String word;
			while ((line = reader.readLine()) != null) {
				if (line.contains(":")) {
					word = line.substring(line.indexOf("\"") + 1, line.indexOf(":"));
				}
			}
		}
	}

	public static void main(String[] args) {
		String line = "	\"a\": {";
		String line2 = "		\"text/gutenberg/1400-0.txt\": [";
		String temp;
		System.out.println(line2.contains(":"));
		temp = line2.substring(line2.indexOf("\"") + 1, line2.indexOf(":") - 1);
		System.out.println(temp);
	}

}
