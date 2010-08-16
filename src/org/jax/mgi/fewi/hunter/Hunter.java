package org.jax.mgi.fewi.hunter;

import java.util.*;

import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;

/**
 *
 */
public interface Hunter{

	public void hunt(SearchParams searchParams, SearchResults searchResults);

}
