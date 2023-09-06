package org.jax.mgi.fewi.searchUtil.entities;


/**
 * Represents a result from the EMAPA autocomplete hunter.
 * This is necessary so that we can get at both the synonym and base structure of each document
 *
 */
public class EmapaACResult implements UniqueableObject
{
	private String structure;
	private String synonym;
	private String queryText;
	private boolean isStrictSynonym;
	private boolean hasCre=false;
	private boolean hasGxdHT=false;
	private String startStage;
	private String endStage;
	private String accID;

	public EmapaACResult(){}
	public EmapaACResult (String structure, String synonym, boolean isStrictSynonym)
	{
		this.structure=structure;
		this.synonym=synonym;
		this.isStrictSynonym=isStrictSynonym;
	}

	public EmapaACResult(String structure, String synonym, boolean isStrictSynonym, boolean hasCre)
	{
		this.structure=structure;
		this.synonym=synonym;
		this.isStrictSynonym=isStrictSynonym;
		this.hasCre=hasCre;
	}

	public String getAccID() { return this.accID; }
	public void setAccID(String accID) { this.accID = accID; }

	public String getQueryText() { return this.queryText; }
	public void setQueryText(String queryText) { this.queryText = queryText; }

	public String getStartStage() { return this.startStage; }
	public void setStartStage(String startStage) {
	    this.startStage = startStage;
	}

	public String getEndStage() { return this.endStage; }
	public void setEndStage(String endStage) { this.endStage = endStage; }

	public String getStructure()
	{ return structure; }

	public void setStructure(String structure)
	{ this.structure = structure; }

	public String getSynonym()
	{ return synonym; }

	public void setSynonym(String synonym)
	{ this.synonym = synonym; }

	public boolean getIsStrictSynonym()
	{
		return isStrictSynonym;
	}
	public void setIsStrictSynonym(boolean isStrictSynonym)
	{
		this.isStrictSynonym=isStrictSynonym;
	}
	public boolean getHasGxdHT() {
		return hasGxdHT;
	}
	public void setHasGxdHT(boolean hasGxdHT) {
		this.hasGxdHT = hasGxdHT; 
	} 
	public boolean getHasCre()
	{
		return hasCre;
	}
	public void setHasCre(boolean hasCre)
	{
		this.hasCre=hasCre;
	}
	public Object getUniqueKey()
	{
	    if (this.accID != null) { return this.accID.trim(); }
	    return this.synonym.trim();
	}
	public void setUniqueKey(Object uniqueKey)
	{
		// just here to appease the JSON serialiser gods
	}

}
