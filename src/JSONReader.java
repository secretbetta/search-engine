import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class JSONReader {
	
	public static void readNested(Path path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.matches("^[{}[]:,\"]")) {
					
				}
			}
		}
	}

	public static void main(String[] args) {
		
	}

}
