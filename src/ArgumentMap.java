import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ArgumentMap {

	private final Map<String, String> map;

	/**
	 * Initializes this argument map.
	 */
	public ArgumentMap() {
		this.map = new HashMap<String, String>();
	}

	/**
	 * Initializes this argument map and then parsers the arguments into
	 * flag/value pairs where possible. Some flags may not have associated values.
	 * If a flag is repeated, its value is overwritten.
	 *
	 * @param args
	 */
	public ArgumentMap(String[] args) {
		this();
		parse(args);
	}

	/**
	 * Parses the arguments into flag/value pairs where possible. Some flags may
	 * not have associated values. If a flag is repeated, its value is
	 * overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public void parse(String[] args) {
		if (args.length > 0) {
			if (args.length > 1 && isFlag(args[0]) && isValue(args[1])) {
				map.put(args[0], args[1]);
			} else if (isFlag(args[0])) {
				map.put(args[0], null);
			}
		}
		for (int i = 1; i < args.length; i++) {
			if (isFlag(args[i - 1]) && isValue(args[i])) {
//				if (args.length == 2) {
//					System.out.println(args[0] + args[1]);
//				}
				map.put(args[i - 1], args[i]);
//				if (args.length == 2) {
//					System.out.println(map.keySet());
//				}
			}
			else if (isFlag(args[i])) {
				map.put(args[i], null);
			}
		}
	}

	/**
	 * Determines whether the argument is a flag. Flags start with a dash "-"
	 * character, followed by at least one other non-whitespace character.
	 *
	 * @param arg the argument to test if its a flag
	 * @return {@code true} if the argument is a flag
	 *
	 * @see String#startsWith(String)
	 * @see String#trim()
	 * @see String#isEmpty()
	 * @see String#length()
	 */
	public boolean isFlag(String arg) {
		if (arg == null) {
			return false;
		}
		if (arg.length() >= 1) {
			if (arg.startsWith("-")) {
				arg = arg.substring(1);
				arg = arg.trim();
				if (arg.isEmpty()) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determines whether the argument is a value. Values do not start with a dash
	 * "-" character, and must consist of at least one non-whitespace character.
	 *
	 * @param arg the argument to test if its a value
	 * @return {@code true} if the argument is a value
	 *
	 * @see String#startsWith(String)
	 * @see String#trim()
	 * @see String#isEmpty()
	 * @see String#length()
	 */
	public boolean isValue(String arg) {
		if (arg == null) {
			return false;
		}
		if (arg.length() >= 1) {
			if (!arg.startsWith("-") && !arg.trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {
		return map.size();
	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag to search for
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {
		return map.containsKey(flag);
	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to search for
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) {
//		return hasFlag(flag) && map.get(flag) != null;
		return map.containsValue(flag);
	}

	/**
	 * Returns the value to which the specified flag is mapped as a
	 * {@link String}, or null if there is no mapping for the flag.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         there is no mapping for the flag
	 */
	public String getString(String flag) {
		return map.get(flag);
	}

	/**
	 * Returns the value to which the specified flag is mapped as a
	 * {@link String}, or the default value if there is no mapping for the flag.
	 *
	 * @param flag         the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping for
	 *                     the flag
	 * @return the value to which the specified flag is mapped, or the default
	 *         value if there is no mapping for the flag
	 */
	public String getString(String flag, String defaultValue) {
		if (map.get(flag) != null) {
			return map.get(flag);
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path},
	 * or {@code null} if unable to retrieve this mapping for any reason
	 * (including being unable to convert the value to a {@link Path} or no value
	 * existing for this flag).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         unable to retrieve this mapping for any reason
	 *
	 * @see Paths#get(String, String...)
	 */
	public Path getPath(String flag) {
//		System.out.println(flag + " " + map.values());
//		System.out.println(map.values().contains(flag));
//		System.out.println(map.get(flag));
		for (String key: map.keySet()) {
			if (flag.equals(map.get(key))) {
				return Paths.get(key);
			} else if (flag.equals(key) && map.get(flag) != null) {
				return Paths.get(map.get(flag));
			}
		}
		return null;
//		if (map.containsKey(flag) && map.get(flag) != null) {
//			return Paths.get(map.get(flag));
//		} else if (map.containsKey(flag)){
//			return Paths.get(flag);
//		}
//		return null;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path},
	 * or the default value if unable to retrieve this mapping for any reason
	 * (including being unable to convert the value to a {@link Path} or no value
	 * existing for this flag).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag         the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping for
	 *                     the flag
	 * @return the value to which the specified flag is mapped as a {@link Path},
	 *         or the default value if there is no mapping for the flag
	 */
	public Path getPath(String flag, Path defaultValue) {
		for (String key: map.keySet()) {
			if (flag.equals(map.get(key))) {
				return Paths.get(flag);
			}
		}
		return defaultValue;
	}

	@Override
	public String toString() {
		return this.map.toString();
	}
}
