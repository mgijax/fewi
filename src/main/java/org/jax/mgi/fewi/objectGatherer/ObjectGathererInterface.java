package org.jax.mgi.fewi.objectGatherer;

import java.util.List;

public interface ObjectGathererInterface<T> {

	public T get(Class<T> modelObj, String key);

	public List<T> get(Class<T> modelObj, List<String> keys);

}