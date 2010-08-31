package org.jax.mgi.fewi.objectGatherer;

import java.util.List;

public interface ObjectGathererInterface<T> {

	public T get(String key);

	public List<T> get(List<String> keys);

	public void setType(Class<T> type);

}
