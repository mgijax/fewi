package org.jax.mgi.fewi.searchUtil.entities;


/**
 * Represents a result from the Structure autocomplete hunter.
 * This is necessary so that we can get at both the synonym and base structure of each document
 * 
 * @author kstone
 *
 */
public class StructureACResult implements UniqueableObject
{
	private String structure;
	private String synonym;
	private boolean isStrictSynonym;
	
	public StructureACResult(){}
	public StructureACResult(String structure,String synonym,boolean isStrictSynonym)
	{
		this.structure=structure;
		this.synonym=synonym;
		this.isStrictSynonym=isStrictSynonym;
	}
	
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
	public Object getUniqueKey()
	{
		return this.synonym.trim();
	}
	public void setUniqueKey(Object uniqueKey)
	{
		// just here to appease the JSON serialiser gods
	}
	
}
