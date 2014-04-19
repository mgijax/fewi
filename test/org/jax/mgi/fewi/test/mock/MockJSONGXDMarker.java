package org.jax.mgi.fewi.test.mock;



/**
 * Represents a JSON summary object that was returned from a controller's response. 
 * 
 * @author kstone
 *
 */
public class MockJSONGXDMarker
{
	private String score;
	private String symbol;
	private String name;
	private String chr;
	private String location;
	private String cM;
	private String strand;
	private String type;
	private String primaryID;
	
	
	// -------------------------------------------------------------------
	// public instance methods; JSON serializer will call all public methods
	// -------------------------------------------------------------------
	
	
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getChr() {
		return chr;
	}
	public void setChr(String chr) {
		this.chr = chr;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCM() {
		return cM;
	}
	public void setCM(String cM) {
		this.cM = cM;
	}
	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPrimaryID() {
		return primaryID;
	}
	public void setPrimaryID(String primaryID) {
		this.primaryID = primaryID;
	}

}