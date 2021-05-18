package org.jax.mgi.fewi.summary;

// Is: a wrapper over a QSOtherResult object, limiting the number of fields that are exposed for
// conversion to JSON (minimizes transfer time)
public class QSOtherResultWrapper {
	private QSOtherResult result;
	
	public QSOtherResultWrapper(QSOtherResult result) {
		this.result = result;
	}
	
	public String getStars() { return result.getStars(); }
	public String getName() { return result.getName(); }
	public String getDetailUri() { return result.getDetailUri(); }
	public String getObjectType() { return result.getObjectType(); }
	public String getObjectSubtype() { return result.getObjectSubtype(); }
	public String getBestMatchType() { return result.getSearchTermType(); }
	public String getBestMatchText() { return result.getSearchTermDisplay(); }
}
