package org.jax.mgi.fewi.objectGatherer;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class SolrObjectGatherer<T> implements ObjectGathererInterface<T> {
	
	@Override
	public T get(Class<T> modelObj, String key) {
		return null;
	}

	@Override
	public List<T> get(Class<T> modelObj, List<String> keys) {
		return null;
	}

}
