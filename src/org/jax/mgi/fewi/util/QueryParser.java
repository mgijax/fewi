package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Provides static methods to help parse user input on query forms
 */
public class QueryParser 
{
	public static List<String> tokeniseOnWhitespace(String query)
	{
		List<String> tokens = new ArrayList<String>(Arrays.asList(query.trim().split("\\s+")));
		tokens.remove("");
		return tokens;
	}
	
	public static List<String> tokeniseOnWhitespaceAndComma(String query)
	{
		List<String> tokens = new ArrayList<String>(Arrays.asList(query.trim().split("[\\s,]+")));
		tokens.remove("");
		return tokens;
	}
	
	public static List<String> tokeniseOnSpecialCharacters(String query)
	{
		List<String> tokens = new ArrayList<String>(Arrays.asList(query.trim().split("[^a-zA-Z0-9]+")));
		tokens.remove("");
		return tokens;
	}
	
	/*
	 * split on whitespace and special characters.
	 * wildcards are ignored.
	 * lowercase everything.
	 */
	public static List<String> parseAutoCompleteSearch(String query)
	{
		List<String> tokens = tokeniseOnSpecialCharacters(query);
		List<String> parsedTokens = new ArrayList<String>();
		for(String token : tokens)
		{
			parsedTokens.add(token.toLowerCase());
		}
		return parsedTokens;
	}
	
	/*
	 * rules are as follows:
	 * split on whitespace first.
	 * remove outer *s to save for each token
	 * split tokens further on special characters (including others *s)
	 * lowercase all tokens.
	 */
	public static List<String> parseNomenclatureSearch(String query)
	{
		List<String> tokens = tokeniseOnWhitespaceAndComma(query);

		// Keep the final list of tokens unique
		Set<String> parsedTokens = new HashSet<String>();
		for (String token : tokens)
		{
			token = token.toLowerCase();
			List<String> sTokens = tokeniseOnSpecialCharacters(token);
			if (sTokens.size()>0)
			{
				// re-add the outer wildcards
				if(token.startsWith("*"))
				{
					sTokens.set(0, "*"+sTokens.get(0));
				}
				if(token.endsWith("*"))
				{
					int lastIndex = sTokens.size()-1;
					sTokens.set(lastIndex, sTokens.get(lastIndex)+"*");
				}
				parsedTokens.addAll(sTokens);
			}
		}
		
		return new ArrayList<String>(parsedTokens);
	}
	
}
