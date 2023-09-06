package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;

/**
 *
 */
public interface Hunter<T> {
	public void hunt(SearchParams searchParams, SearchResults<T> searchResults);
}
