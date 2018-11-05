import java.io.IOException;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Thread safe version of inverted index
 * @author Andrew
 *
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
	
	private ReadWriteLock lock;
	
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
	public TreeMap<String, TreeSet<Integer>> get(String word) {
		lock.lockReadWrite();
		
		try {
			return super.get(word);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public TreeSet<Integer> get(String word, String file) {
		lock.lockReadWrite();
		
		try {
			return super.get(word, file);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public boolean contains(String word) {
		lock.lockReadWrite();
		
		try {
			return super.contains(word);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public boolean contains(String word, String location) {
		lock.lockReadWrite();
		
		try {
			return super.contains(word, location);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public boolean contains(String word, String location, int position) {
		lock.lockReadWrite();
		
		try {
			return super.contains(word, location, position);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public int size() {
		lock.lockReadWrite();
		
		try {
			return super.size();
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public int locations(String word) {
		lock.lockReadWrite();
		
		try {
			return super.locations(word);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public int positions(String word, String file) {
		lock.lockReadWrite();
		
		try {
			return super.positions(word, file);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndex() {
		lock.lockReadWrite();
		
		try {
			return super.getIndex();
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public void toJSON(Path path) throws IOException {
		lock.lockReadWrite();
		
		try {
			super.toJSON(path);
		} finally {
			lock.unlockReadWrite();
		}
	}
}
