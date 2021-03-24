package org.jax.mgi.fewi.summary;

public abstract class QSResult {
	public abstract String getSearchTermExact();			// methods to get info about the searchable data
	public abstract String getSearchTermInexact();
	public abstract String getSearchTermStemmed();
	public abstract String getSearchTermDisplay();
	public abstract String getSearchTermType();
	public abstract Integer getSearchTermWeight();

	public abstract void setStars(String stars);		// methods to set/get star rating info
	public abstract int getStarCount();
	public abstract String getStars();

	public abstract String getPrimaryID();			// unique identifier of the match
	public abstract Long getSequenceNum();

	public abstract void addBoost(int boost);		// add a boost to the weight (to allow preferences in choosing Best Match)
}
