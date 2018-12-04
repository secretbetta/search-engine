import java.io.IOException;
import java.nio.file.Path;

// TODO Javadoc here and then don't have to when you @Override the methods

/**
 * Interface of Query Map
 * @author Andrew
 *
 */
public interface QueryMapInterface {
	
	// TODO public void builder(Path search, boolean exact) throws IOException;
	
	public abstract void toJSON(Path index) throws IOException;
}
