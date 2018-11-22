import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

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
		lock.lockReadOnly();
		
		try {
			super.add(word, file, pos);
		} finally {
			lock.unlockReadOnly();
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
	public int words() {
		lock.lockReadWrite();
		
		try {
			return super.words();
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
	public void toJSON(Path path) throws IOException {
		lock.lockReadWrite();
		
		try {
			super.toJSON(path);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public void locationtoJSON(Path locIndex) throws IOException {
		lock.lockReadWrite();
		
		try {
			super.locationtoJSON(locIndex);
		} finally {
			lock.unlockReadWrite();
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
	public void searchHelper(String query, ArrayList<Result> resultList, TreeMap<String, Result> lookup) {
		lock.lockReadWrite();
		
		try {
			super.searchHelper(query, resultList, lookup);
		} finally {
			lock.unlockReadWrite();
		}
	}
}