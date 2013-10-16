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
		return parseNomenclatureSearch(query,true,"");
	}
	public static List<String> parseNomenclatureSearch(String query,boolean doSpecialCharacters,String phraseDelimiter)
	{
		List<String> finalTokens = new ArrayList<String>();
		String term = query;
		
		// if phraseDelimiter specified, pull out the tokens surrounded by the delimiter
		// E.g. The following text "is a phrase token" delimited by double quote
		//	would become [The,following,text,is a phrase token,delimited,by,double,quote]
		if (phraseDelimiter!=null && !phraseDelimiter.equals(""))
		{
			// we will likely be ORing tokens and need to turn phrases into their own tokens
	        String[] pieces = term.split(phraseDelimiter,-1);
	        if(pieces.length>2)
	        {
	        	String leftOver = pieces[0];
	        	for(int i=1;i<pieces.length;i++)
	        	{
	        		if(i%2==1) finalTokens.add(pieces[i]);
	        		else leftOver += pieces[i];
	        	}
	        	term=leftOver;
	        }
		}
		List<String> tokens = (tokeniseOnWhitespaceAndComma(term));

		// Keep the final list of tokens unique
		Set<String> parsedTokens = new HashSet<String>();
		
		if(doSpecialCharacters)
		{
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
		}
		else
		{
			parsedTokens = new HashSet<String>(tokens);
		}
		finalTokens.addAll(parsedTokens);
		
		return new ArrayList<String>(finalTokens);
	}
	
}
