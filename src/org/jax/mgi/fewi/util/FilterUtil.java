package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.Filter;

/**
 * Some filter logic is used in many places.
 * We simply place that logic here.
 */
public class FilterUtil 
{
	/*
	 * For any place we use a gene or allele nomenclature query
	 * 	Solr field schema should have the custom 'nomen' field type
	 * 		(check allele, marker, or gxd schema for example)
	 * 
	 * Returns null if filter cannot be created
	 */
	public static Filter generateNomenFilter(String property, String query)
	{
		Collection<String> nomens = QueryParser.parseNomenclatureSearch(query);
		List<Filter> nomenFilters = new ArrayList<Filter>();
		// we want to group all non-wildcarded tokens into one solr phrase search
		List<String> nomenTokens = new ArrayList<String>();
		String phraseSearch = "";

		for(String nomen : nomens) 
		{
			if(nomen.endsWith("*") || nomen.startsWith("*")) {
				nomenTokens.add(nomen);
			} else {
				phraseSearch += nomen+" ";
			}
		}

		if(!phraseSearch.trim().equals("")) {
			// surround with double quotes to make a solr phrase. added a slop of 100 (longest name is 62 chars)
			nomenTokens.add("\""+phraseSearch+"\"~100");
		}

		for(String nomenToken : nomenTokens) 
		{
			Filter nFilter = new Filter(property, nomenToken,Filter.OP_HAS_WORD);
			nomenFilters.add(nFilter);
		}

		if(nomenFilters.size() > 0) {
			// add the nomenclature search filter
			return Filter.and(nomenFilters);
		}
		// We don't want to return an empty filter object, because it screws up Solr.
		return null;
	}
}
