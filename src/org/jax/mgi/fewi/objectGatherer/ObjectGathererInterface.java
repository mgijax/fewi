package org.jax.mgi.fewi.objectGatherer;

import java.util.List;

public interface ObjectGathererInterface<T> {
	
	public T get(Integer key);
	
	public List<T> get(List<Integer> keys);
	
	public void setType(Class<T> type);

}
