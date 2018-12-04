import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface of Query Map
 * @author Andrew
 *
 */
public interface QueryMapInterface {
	
	/**
	 * Builds Query Map by using search queries from path Search. 
	 * Can be switched from partial or exact search
	 * @param search Path to use search queries
	 * @param exact (false) Partial or (true) Exact search
	 * @throws IOException
	 */
	public void builder(Path search, boolean exact) throws IOException;
	
	/**
	 * Turns Query Map into JSON File in index path
	 * @param index Where to write to JSON
	 * @throws IOException
	 */
	public void toJSON(Path index) throws IOException;
}
