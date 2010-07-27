package mgi.frontend.finder;

import java.util.List;

public interface GenericGatherer<T> {
	
	public T get(Integer key);
	
	public List<T> get(List<Integer> keys);

}
