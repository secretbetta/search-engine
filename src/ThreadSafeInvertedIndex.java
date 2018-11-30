import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Thread safe version of inverted index
 * @author Andrew
 *
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
	
	private final ReadWriteLock lock;
	
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}
	
	@Override
	public void add(String word, String file, int pos) {
		lock.lockReadWrite();
		
		try {
			super.add(word, file, pos);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public void addAll(InvertedIndex other) {
		lock.lockReadWrite();
		
		try {
			super.addAll(other);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public boolean contains(String word) {
		lock.lockReadOnly();
		
		try {
			return super.contains(word);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public boolean contains(String word, String location) {
		lock.lockReadOnly();
		
		try {
			return super.contains(word, location);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public boolean contains(String word, String location, int position) {
		lock.lockReadOnly();
		
		try {
			return super.contains(word, location, position);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public int words() {
		lock.lockReadOnly();
		
		try {
			return super.words();
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public int locations(String word) {
		lock.lockReadOnly();
		
		try {
			return super.locations(word);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public int positions(String word, String file) {
		lock.lockReadOnly();
		
		try {
			return super.positions(word, file);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public void toJSON(Path path) throws IOException {
		lock.lockReadOnly();
		
		try {
			super.toJSON(path);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public void locationtoJSON(Path locIndex) throws IOException {
		lock.lockReadOnly();
		
		try {
			super.locationtoJSON(locIndex);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public ArrayList<Result> exactSearch(Collection<String> queries) {
		lock.lockReadWrite();
		
		try {
			return super.exactSearch(queries);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public ArrayList<Result> partialSearch(Collection<String> queries) {
		lock.lockReadWrite();
		
		try {
			return super.partialSearch(queries);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public String toString() {
		lock.lockReadOnly();
		
		try {
			return super.toString();
		} finally {
			lock.unlockReadOnly();
		}
	}
}
