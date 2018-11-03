import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Writes a triple nested JSON format
 */
public class NestedJSON {

	/**
	 * Writes several tab <code>\t</code> symbols using the provided
	 * {@link Writer}.
	 *
	 * @param times  the number of times to write the tab symbol
	 * @param writer the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void indent(int times, Writer writer) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Writes the element surrounded by quotes using the provided {@link Writer}.
	 *
	 * @param element the element to quote
	 * @param writer  the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Returns the set of elements formatted as a pretty JSON array of numbers.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static String asArray(TreeSet<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asArray(TreeSet<Integer> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers
	 * using the provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer,
			int level) throws IOException {
		
		if (!elements.isEmpty()) {
			writer.write('[');
			writer.write(System.lineSeparator());
			for (Integer element : elements.headSet(elements.last())) {
				indent(level + 1, writer);
				writer.write(element.toString());
				writer.write(",");
				writer.write(System.lineSeparator());
			} 
			indent(level + 1, writer);
			writer.write(elements.last().toString());
			writer.write(System.lineSeparator());
			indent(level, writer);
			writer.write(']');
		} else {
			writer.write('[');
			writer.write(System.lineSeparator());
			indent(level, writer);
			writer.write(']');
		}
	}

	/**
	 * Returns the map of elements formatted as a pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the map of elements as a pretty JSON object using the provided
	 * {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Writer writer,
			int level) throws IOException {
		writer.write('{');
		writer.write(System.lineSeparator());
		
		if (!elements.isEmpty()) {
			for (String element : elements.headMap(elements.lastKey()).keySet()) {
				indent(level + 2, writer);
				quote(element.toString(), writer);
				writer.write(": ");
				writer.write(elements.get(element).toString());
				
				writer.write(",");
				
				writer.write(System.lineSeparator());
			}
			indent(level + 2, writer);
			quote(elements.lastKey().toString(), writer);
			writer.write(": ");
			writer.write(elements.get(elements.lastKey()).toString());
			writer.write(System.lineSeparator());
    }
      
		indent(level, writer);
		writer.write('}');
	}

	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedObject(TreeMap<String, TreeSet<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object
	 * to the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements,
			Writer writer, int level) throws IOException {
		writer.write('{');
		writer.write(System.lineSeparator());

		if (!elements.isEmpty()) {
			for (String element : elements.headMap(elements.lastKey()).keySet()) {
				indent(level + 1, writer);
				quote(element.toString(), writer);
	
				writer.write(": ");
				
				asArray(elements.get(element), writer, level + 1);
				
				writer.write(",");
				writer.write(System.lineSeparator());
			}
			
			indent(level + 1, writer);
			quote(elements.lastKey().toString(), writer);
			writer.write(": ");
			asArray(elements.get(elements.lastKey()), writer, level + 1);
			writer.write(System.lineSeparator());
		}
		
		
		indent(level, writer);
		writer.write('}');
	}
	
	/**
	 * Creates an inverted index version of elements in JSON format
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #tripleNested(TreeMap, Writer, int)
	 */
	public static String tripleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			tripleNested(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Creates tripleNested inverted index in JSON format
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #tripleNested(TreeMap, Writer, int)
	 */
	public static void tripledNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			tripleNested(elements, writer, 0);
		}
	}
	
	/**
	 * Creates a triple nested reverse index in JSON format
	 * 
	 * @param elements The data taken in to format into JSON format
	 * @param index Where to write the data
	 * @return writer.toString() Whatever is written in JSON format
	 * @throws IOException
	 * 
	 * @see {@link #asNestedObject(TreeMap, Writer, int)}
	 */
	public static void tripleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements,
			Writer writer,
			int level) throws IOException {
		if (!elements.isEmpty()) {
			indent(level, writer);
			writer.write('{');
			writer.write(System.lineSeparator());
			
			for (String element : elements.headMap(elements.lastKey()).keySet()) {
				indent(level + 1, writer);
				quote(element.toString(), writer);
				writer.write(": ");
				
				asNestedObject(elements.get(element), writer, level + 1);
				
				writer.write(",");
				writer.write(System.lineSeparator());
			}
			
			indent(level + 1, writer);
			quote(elements.lastKey().toString(), writer);
			writer.write(": ");
			asNestedObject(elements.get(elements.lastKey()), writer, level + 1);
			writer.write(System.lineSeparator());
			
			indent(level, writer);
			writer.write('}');
		}
	}
	
	/**
	 * Writes the query inverted index
	 * @param queries The querymap
	 * @param index Where to write it
	 * 
	 * @throws IOException
	 */
	public static void queryObject(TreeMap<String, ArrayList<Result>> queries, Path index) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8)) {
			writer.write("[");
			writer.write(System.lineSeparator());
			
			if (!queries.isEmpty()) {
				for (String query : queries.headMap(queries.lastKey()).keySet()) {
					NestedJSON.indent(1, writer);
					
					writer.write("{");
					writer.write(System.lineSeparator());
					NestedJSON.indent(2, writer);
					
					writer.write("\"queries\": \"");
					writer.write(query);
					writer.write("\",");
					writer.write(System.lineSeparator());
					NestedJSON.indent(2, writer);
					
					writer.write("\"results\": [");
					writer.write(System.lineSeparator());
					for (int i = 0; i < queries.get(query).size() - 1; i++) {
						if (queries.get(query).get(i) != null) { 
							writer.write(queries.get(query).get(i).toString());
							writer.write(",");
							writer.write(System.lineSeparator());
						}
						
					}
					
					if (queries.get(query).get(queries.get(query).size() - 1) != null) {
						writer.write(queries.get(query).get(queries.get(query).size()-1).toString());
						writer.write(System.lineSeparator());
					}
					
					NestedJSON.indent(2, writer);
					writer.write("]");
					writer.write(System.lineSeparator());
					NestedJSON.indent(1, writer);
					
					writer.write("}");
					writer.write(",");
					writer.write(System.lineSeparator());
				}
				NestedJSON.indent(1, writer);
				
				writer.write("{");
				writer.write(System.lineSeparator());
				NestedJSON.indent(2, writer);
				
				writer.write("\"queries\": \"");
				writer.write(queries.lastKey());
				writer.write("\",");
				writer.write(System.lineSeparator());
				NestedJSON.indent(2, writer);
				
				writer.write("\"results\": [");
				writer.write(System.lineSeparator());
				
				for (int i = 0; i < queries.get(queries.lastKey()).size() - 1; i++) {
					if (queries.get(queries.lastKey()).get(i) != null) { 
						writer.write(queries.get(queries.lastKey()).get(i).toString());
						writer.write(",");
						writer.write(System.lineSeparator());
					}
				}
				
				if (queries.get(queries.lastKey()).get(queries.get(queries.lastKey()).size() - 1) != null) {
					writer.write(queries.get(queries.lastKey()).get(queries.get(queries.lastKey()).size()-1).toString());
					writer.write(System.lineSeparator());
				}
				
				NestedJSON.indent(2, writer);
				writer.write("]");
				writer.write(System.lineSeparator());
				NestedJSON.indent(1, writer);
				
				writer.write("}");
				writer.write(System.lineSeparator());
			}
			
			writer.write("]");
		} catch (IOException e) {
			System.err.println("Cannot write to file " + index);
		}
	}
}
