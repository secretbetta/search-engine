import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
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
		for (String element : elements.keySet()) {
			indent(level + 2, writer);
			quote(element.toString(), writer);
			writer.write(": ");
			writer.write(elements.get(element).toString());
			
			if (!element.equals(elements.lastKey()))
				writer.write(",");
			
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
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
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

		for (String element : elements.keySet()) {
			indent(level + 1, writer);
			quote(element.toString(), writer);

			writer.write(": ");
			
			asArray(elements.get(element), writer, level + 1);
			
			if (!element.equals(elements.lastKey()))
				writer.write(",");
			writer.write(System.lineSeparator());
		}
		
		indent(level, writer);
		writer.write('}');
	}
	
	/**
	 * Creates a triple nested reverse index in JSON format
	 * 
	 * @param elements The data taken in to format into JSON format
	 * @return writer.toString() Whatever is written in JSON format
	 * @throws IOException
	 * 
	 * @see {@link #asNestedObject(TreeMap, Writer, int)}
	 */
	public static void tripleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path index) {
		try (BufferedWriter writer = Files.newBufferedWriter(index, StandardCharsets.UTF_8)) {
			
			writer.write('{');
			writer.write(System.lineSeparator());
			for (String element : elements.headMap(elements.lastKey()).keySet()) {
				indent(1, writer);
				quote(element.toString(), writer);
				writer.write(": ");
				
				asNestedObject(elements.get(element), writer, 1);
				
				writer.write(",");
				writer.write(System.lineSeparator());
			}
			
			indent(1, writer);
			quote(elements.lastKey().toString(), writer);
			writer.write(": ");
			asNestedObject(elements.get(elements.lastKey()), writer, 1);
			writer.write(System.lineSeparator());
			
			writer.write('}');
		} catch (NoSuchElementException e) {
			System.err.println("No such element");
		} catch (IOException e) { 
		}
	}
	
	/**
	 * Creates a Nested JSON query index that stores Word -> File -> word count and score
	 * 
	 * @param query Contains word -> 
	 * 		all files the word appears in ->
	 * 		word count in each file and score in each file
	 * @param writer Where to write the index
	 * @param level Level of indentation
	 * @throws IOException For Buffered Writer
	 * 
	 * @see #result(TreeMap, Writer, int)
	 */
	public static void queryObject(TreeMap<String, HashMap<String, HashMap<String, Number>>> query, Writer writer, int level) throws IOException {
		try {
			indent(level, writer);
			writer.write('[');
			
			for (String word : query.headMap(query.lastKey()).keySet()) {
				writer.write(System.lineSeparator());
				indent(level + 1, writer);
				writer.write('{');
				
				writer.write(System.lineSeparator());
				indent(level + 2, writer);
				writer.write("\"queries\": \"" + word + "\",");
				
				writer.write(System.lineSeparator());
				indent(level + 2, writer);
				writer.write("\"results\": [");
				
				writer.write(System.lineSeparator());
				
				if (!query.get(word).keySet().isEmpty()) {
					result(query.get(word), writer, level + 3);
					writer.write(System.lineSeparator());
				}
				
				indent(level + 2, writer);
				writer.write(']');
				writer.write(System.lineSeparator());
				
				indent(level + 1, writer);
				writer.write("},");
				
			}
			
	
			writer.write(System.lineSeparator());
			indent(level + 1, writer);
			writer.write('{');
			
			writer.write(System.lineSeparator());
			indent(level + 2, writer);
			writer.write("\"queries\": \"" + query.lastKey() + "\",");
			
			writer.write(System.lineSeparator());
			indent(level + 2, writer);
			writer.write("\"results\": [");
			
			writer.write(System.lineSeparator());
			
			if (!query.get(query.lastKey()).keySet().isEmpty()) {
				result(query.get(query.lastKey()), writer, level + 3);
				writer.write(System.lineSeparator());
			}
			
			indent(level + 2, writer);
			writer.write(']');
			writer.write(System.lineSeparator());
			
			indent(level + 1, writer);
			writer.write("}");
			
			writer.write(System.lineSeparator());
			indent(level, writer);
			writer.write(']');
		} catch (IOException e) {
			
		}
	}
	
	/**
	 * Creates JSON format of "result" of searches
	 * 
	 * @param query The file(s) to count:int and score:double
	 * @param writer Where to write the files to
	 * @param level Indentation level
	 * @throws IOException For BufferedWriter
	 */
	public static void result(HashMap<String, HashMap<String, Number>> query, Writer writer, int level) throws IOException {
		DecimalFormat FORMATTER = new DecimalFormat("0.000000");
		DecimalFormat INT = new DecimalFormat("0");
		int count = query.size();
		
		for (String file: query.keySet()) {
			indent(level, writer);
			writer.write('{');
			writer.write(System.lineSeparator());
			
			indent(level + 1, writer);
			writer.write("\"where\": \"" + file + "\",");
			
			writer.write(System.lineSeparator());
			indent(level + 1, writer);
			writer.write("\"count\": " + INT.format((double)query.get(file).get("count")) + ",");
			
			writer.write(System.lineSeparator());
			indent(level + 1, writer);
			writer.write("\"score\": " + FORMATTER.format((double)query.get(file).get("score")));
			writer.write(System.lineSeparator());
			
			if (count > 1) {
				indent(level, writer);
				writer.write("},");
				writer.write(System.lineSeparator());
			} else {
				writer.write("}");
			}
			count--;
		}
	}
	
	public static void queryObject(TreeSet<Query> queries, Writer writer) throws IOException {
		writer.write("[");
		writer.write(System.lineSeparator());
		
		for (Query query : queries.headSet(queries.last())) {
			writer.write(query.toString());
			writer.write(",");
			writer.write(System.lineSeparator());
		}
		
		writer.write(queries.last().toString());
		writer.write(System.lineSeparator());
		
		
		writer.write("}");
	}
	
	public static void queryObject(ArrayList<Query> queries, Writer writer) throws IOException {
		writer.write("[");
		writer.write(System.lineSeparator());
		
		for (Query query : queries) {
			writer.write(query.toString());
			if (query.equals(queries.lastIndexOf(queries))) {
				writer.write(",");
			}
			writer.write(System.lineSeparator());
		}
		
//		writer.write(queries.last().toString());
//		writer.write(System.lineSeparator());
		
		
		writer.write("}");
	}
}
