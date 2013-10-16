package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jax.mgi.fewi.searchUtil.entities.VocabACResult;
import org.jax.mgi.fewi.util.QueryParser;

/**
 * The formatted results for vocab autocomplete are implemented in a summary row object for performance.
 * It was too slow to do this in javascript.
 * 
 * @author kstone
 */
public class VocabACSummaryRow 
{
	/*
	 * Possible types of Vocab AC rows
	 * 	each can have different formattedTerm text
	 */
	public enum ACType 
	{
		DEFAULT,
		GXD,
		DISEASE_PORTAL
	}
	
	private VocabACResult vr;
	List<Pattern> queryPatterns;
	
	//private VocabACResult result;
	private String vocab;
	private String inputTerm;
	private String termId;
	private int markerCount;
	private boolean hasExpression;
	private String formattedTerm;
	
	/*
	 * empty constructor used for deserializing JSON
	 */
	public VocabACSummaryRow(){}
	
	/*
	 * Constructor takes in a VocabACSolrHunter Result and regex patterns for matching the query tokens
	 */
	public VocabACSummaryRow(VocabACResult vr,List<Pattern> queryPatterns, ACType formatType)
	{
		init(vr,queryPatterns, formatType);
	}
	
	/*
	 * Constructor takes in a VocabACSolrHunter Result and the original query used.
	 * It is a bit more efficient to compile the regex Patterns ahead of time  and pass into the above constructor,
	 * due to the performance cost of each compilation.
	 */
	public VocabACSummaryRow(VocabACResult vr,String query, ACType formatType)
	{
		List<Pattern> ps = new ArrayList<Pattern>();
		List<String> queryTokens = QueryParser.parseAutoCompleteSearch(query);
		for(String token : queryTokens)
		{
			if(!token.equals(""))
			{
				ps.add(Pattern.compile(token,Pattern.CASE_INSENSITIVE));
			}
		}
		init(vr,ps,formatType);
	}
	
	public void init(VocabACResult vr, List<Pattern> queryPatterns, ACType formatType)
	{
		this.vr = vr;
		this.queryPatterns = queryPatterns;
		this.markerCount = vr.getMarkerCount();
		this.hasExpression = vr.getHasExpressionResults();
		this.termId = vr.getTermId();
		
		//create the text that appears in input box
		this.vocab = vr.getRootVocab();
		// our rule is to display 'Function' for all GO terms
		if(vocab.equals("GO")) vocab = "Function";
		this.inputTerm = vr.getTerm();
		if(!vr.getOriginalTerm().equals("") && vr.getIsSynonym()) this.inputTerm += " ["+vr.getOriginalTerm()+"]";
		this.inputTerm += " - "+vocab;
		
		
		// create the formatted term based on ACType
		switch(formatType)
		{
			case DISEASE_PORTAL:
				this.formattedTerm = this.makeDiseasePortalFormattedTerm();
				break;
			case GXD:
				this.formattedTerm = this.makeGxdFormattedTerm();
				break;
			default:
				this.formattedTerm = vr.getTerm();
		}
	}
	
	private String makeDiseasePortalFormattedTerm()
	{
		String formattedTerm = vr.getTerm();
		if(vr.getIsSynonym()) formattedTerm += " ["+vr.getOriginalTerm()+"]";
		
		String termSection = "<span style=\"font-size:1em; font-style:normal;\">"+formattedTerm+"</span>";
		String otherSection = "<span style=\"color:#ccc; font-size:80%; font-style:italic;\">"+vocab+"</span>";
		formattedTerm = termSection + " " + otherSection;
		
		return formattedTerm;
	}
	
	private String makeGxdFormattedTerm()
	{
		String formattedTerm = vr.getTerm();
		if(vr.getIsSynonym()) formattedTerm += " ["+vr.getOriginalTerm()+"]";
		
		// "highlight" the user's query inside the term(s)
		Set<String> replacements = new HashSet<String>();
		for(Pattern p : queryPatterns)
		{
			Matcher m = p.matcher(formattedTerm);
			while(m.find())
			{
				replacements.add(m.group());
			}
		}
		for (String replacement : replacements)
		{
			formattedTerm = formattedTerm.replace(replacement,"***"+replacement+"%%%");
		}
		// HACK: maybe there is a better way to do this
		formattedTerm = formattedTerm.replace("***","<b>");
		formattedTerm = formattedTerm.replace("%%%","</b>");

		String termSection = "<span style=\"font-size:1em; font-style:normal;\">"+formattedTerm+"</span>";
		// we need to avoid a potential data inconsistency where there might be markers with expression that we excluded from the count
		if (!this.hasExpression || this.markerCount == 0)
		{   
			termSection = "<span style=\"color:#ccc; font-size:1em; font-style:normal;\">"+formattedTerm+"</span>";
		}
		String otherSection = "<span style=\"color:#ccc; font-size:80%; font-style:italic;\">"
				+this.markerCount+" genes, "+vocab+"</span>";
		formattedTerm = termSection + " " + otherSection;
		
		return formattedTerm;
	}
	
	
	public String getFormattedTerm()
	{
		return this.formattedTerm;
	}
	
	public String getInputTerm()
	{
		return this.inputTerm;
	}
	
	public int getMarkerCount()
	{
		return this.markerCount;
	}
	
	public boolean getHasExpression()
	{
		return this.hasExpression;
	}
	
	public String getTermId()
	{
		return this.termId;
	}

	public void setFormattedTerm(String formattedTerm) {
		this.formattedTerm = formattedTerm;
	}

	public void setInputTerm(String inputTerm) {
		this.inputTerm = inputTerm;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public void setMarkerCount(int markerCount) {
		this.markerCount = markerCount;
	}

	public void setHasExpression(boolean hasExpression) {
		this.hasExpression = hasExpression;
	}

}
