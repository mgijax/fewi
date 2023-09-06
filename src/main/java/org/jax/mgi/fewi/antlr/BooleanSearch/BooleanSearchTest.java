package org.jax.mgi.fewi.antlr.BooleanSearch;

import org.antlr.runtime.RecognitionException;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.util.QueryParser;
import org.junit.Test;

public class BooleanSearchTest {

	@Test
	public void test() throws RecognitionException 
	{
		// test sanitizing input
		System.out.println(sanitize("cell AND lane NOT()))(boring)"));
		System.out.println(sanitize("cell AND \"or this\" with\" lane(box))))(("));
		
		// test making a Filter
		BooleanSearch bs = new BooleanSearch();
		Filter f = bs.buildSolrFilter("hi","cell AND lane NOT(clean)))(boring)");
		System.out.println(f);
	}
	
	private static String sanitize(String s)
	{
		s = QueryParser.removeUnmatched(s,'"');
		s = QueryParser.removeUnmatched(s,'(',')');
		s = s.replace("\"\""," ");
		s = s.replace("()"," ");
		return s;
	}
}
