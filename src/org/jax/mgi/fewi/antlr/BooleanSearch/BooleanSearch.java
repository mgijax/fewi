package org.jax.mgi.fewi.antlr.BooleanSearch;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.util.QueryParser;

public class BooleanSearch 
{

	/**
	 * Uses ANTLR SolrBooleanSearch grammar to parse user input with boolean logic to create nested Filter objects.
	 * 	Grammar supports AND, OR, NOT, (), and "double quoted text"
	 */
	public static Filter buildSolrFilter(String searchConstant,String s) throws RecognitionException
	{
		// sanitize the user input to remove grammar abnormalities
		s = sanitizeInput(s);
		
		ANTLRStringStream input = new ANTLRStringStream(s);
		CommonTokenStream tokens = new CommonTokenStream(new SolrBooleanSearchLexer(input));
		SolrBooleanSearchParser parser = new SolrBooleanSearchParser(tokens);
		
        Filter qFilter = parser.query();
        simplifyNests(searchConstant,qFilter);
        return qFilter;
	}
	
	/*
	 * remove unmatched double quotes
	 * remove unmatched parens
	 * remove empty double quotes
	 * remove empty parens
	 */
	private static String sanitizeInput(String s)
	{
		s = QueryParser.removeUnmatched(s,'"');
		s = QueryParser.removeUnmatched(s,'(',')');
		s = s.replace("\"\""," ");
		s = s.replace("()"," ");
		return s;
	}
	
	/*
	 *  need to recursively set the specified searchConstant.
	 *  Also is a good idea to collapse nested filters that only have one item in them.
	 */
	private static void simplifyNests(String searchConstant, Filter f)
	{
		f.setProperty(searchConstant);
		if(f.hasNestedFilters() && f.getNestedFilters().size()==1)
		{
			Filter child = f.getNestedFilters().get(0);
			if(child.doNegation()) f.negate();
			f.setNestedFilters(child.getNestedFilters());
			f.setFilterJoinClause(child.getFilterJoinClause());
			if(child.hasNestedFilters())
			{
				simplifyNests(searchConstant,f);
			}
			else
			{
				f.setValue(child.getValue());
				f.setValues(child.getValues());
				f.setOperator(child.getOperator());
			}
		}
		else if(f.hasNestedFilters())
		{
			for(Filter child : f.getNestedFilters())
			{
				simplifyNests(searchConstant,child);
			}
		}
	}
}
