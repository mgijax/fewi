package org.jax.mgi.fewi.summary;

public abstract class QSResult {
	public abstract String getSearchID();			// methods to get info about the searchable data
	public abstract String getSearchTerm();
	public abstract Integer getSearchTermWeight();

	public abstract void setStars(String stars);		// methods to set/get star rating info
	public abstract int getStarCount();
	public abstract String getStars();

	public abstract String getPrimaryID();			// unique identifier of the match
	public abstract Long getSequenceNum();
}
