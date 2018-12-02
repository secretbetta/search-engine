import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface of Query Map
 * @author Andrew
 *
 */
public interface QueryMapInterface {
	public abstract void toJSON(Path index) throws IOException;
	public void builder(Path search, boolean exact, int threads) throws IOException;
}
