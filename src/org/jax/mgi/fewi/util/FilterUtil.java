package org.jax.mgi.fewi.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jax.mgi.fewi.forms.AlleleQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some filter logic is used in many places.
 * We simply place that logic here.
 */
public class FilterUtil 
{
	private static Logger logger = LoggerFactory.getLogger(FilterUtil.class);

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
			Filter nFilter = new Filter(property, nomenToken,Filter.Operator.OP_HAS_WORD);
			nomenFilters.add(nFilter);
		}

		if(nomenFilters.size() > 0) {
			// add the nomenclature search filter
			return Filter.and(nomenFilters);
		}
		// We don't want to return an empty filter object, because it screws up Solr.
		return null;
	}

	/*
	 * For any place making a standard coordinate search
	 * 	Builds a query between SearchConstants.START_COORD and SearchConstants.END_COORD
	 * 
	 * 	returns null if there is a problem
	 *
	 */

	public static Filter genCoordFilter(String coord, String coordUnit) {
		return genCoordFilter(coord, coordUnit, false);
	}

	public static Filter genCoordFilter(String coord, String coordUnit, boolean range)
	{
		coord = coord.trim();
		BigDecimal unitMultiplier = new BigDecimal(1);
		if(AlleleQueryForm.COORD_UNIT_MBP.equalsIgnoreCase(coordUnit))
		{
			// convert to Mbp
			unitMultiplier = new BigDecimal(1000000);
		}
		// split on either -, periods, or whitespaces
		String[] coordTokens = coord.split("\\s*(-|\\.\\.|\\s+)\\s*");
		Long start=null,end=null;
		try {
			BigDecimal startDec = QueryParser.parseDoubleInput(coordTokens[0]);
			startDec = startDec.multiply(unitMultiplier);
			start = bdToLong(startDec);
			// support single coordinate by setting end to be same as start
			if(coordTokens.length<2) end=start;
			else
			{
				BigDecimal endDec = QueryParser.parseDoubleInput(coordTokens[1]);
				endDec = endDec.multiply(unitMultiplier);
				end = bdToLong(endDec);
			}
		} catch(Exception e) {
			// ignore any errors, we just won't do a coord query
			logger.debug("failed to parse coordinates");
		}
		
		if(start!=null && end!=null) {
			if(range) {
				if(start < end) {
					return Filter.range(SearchConstants.STARTCOORDINATE, start.toString(), end.toString());
				} else {
					return Filter.range(SearchConstants.STARTCOORDINATE, end.toString(), start.toString());
				}
			} else {
				Filter endF = new Filter(SearchConstants.START_COORD,end.toString(),Filter.Operator.OP_LESS_OR_EQUAL);
				Filter startF = new Filter(SearchConstants.END_COORD,start.toString(),Filter.Operator.OP_GREATER_OR_EQUAL);
				return Filter.and(Arrays.asList(endF,startF));
			}
		} else {
			return null;
		}
	}

	/*
	 * For any place making a standard cmOffset search
	 * 	Builds a query for SearchConstants.CM_OFFSET
	 * 
	 * 	returns null if there is a problem
	 *
	 */
	public static Filter genCmFilter(String cm)
	{
		cm = cm.trim();
		// split on either -, periods, or whitespaces
		String[] cmTokens = cm.split("\\s*(-|\\.\\.|\\s+)\\s*");
		Double start=null,end=null;
		try
		{
			BigDecimal startDec = QueryParser.parseDoubleInput(cmTokens[0]);
			start=startDec.doubleValue();
			// support single coordinate by setting end to be same as start
			if(cmTokens.length<2) end=start;
			else
			{
				BigDecimal endDec = QueryParser.parseDoubleInput(cmTokens[1]);
				end = endDec.doubleValue();
			}
		}catch(Exception e)
		{
			// ignore any errors, we just won't do a cm query
			logger.debug("failed to parse cm",e);
		}
		if(start!=null && end!=null)
		{
			Filter endF = new Filter(SearchConstants.CM_OFFSET,end.toString(),Filter.Operator.OP_LESS_OR_EQUAL);
			Filter startF = new Filter(SearchConstants.CM_OFFSET,start.toString(),Filter.Operator.OP_GREATER_OR_EQUAL);
			return Filter.and(Arrays.asList(endF,startF));
		}
		else
		{
			return null;
		}
	}

	private static long bdToLong(BigDecimal bd)
	{
		if(bd.compareTo(new BigDecimal(Integer.MAX_VALUE)) > 0) return Integer.MAX_VALUE;
		return bd.longValue();
	}
}
