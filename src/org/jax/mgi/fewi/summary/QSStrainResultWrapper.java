package org.jax.mgi.fewi.summary;

// Is: a wrapper over a QSStrainResult object, limiting the number of fields that are exposed for
// conversion to JSON (minimizes transfer time)
public class QSStrainResultWrapper {
	private QSStrainResult result;
	
	public QSStrainResultWrapper(QSStrainResult result) {
		this.result = result;
	}
	
	public String getStars() { return result.getStars(); }
	public String getName() { return result.getName(); }
	public String getDetailUri() { return result.getDetailUri(); }
	public Integer getReferenceCount() { return result.getReferenceCount(); }
	public String getReferenceUri() { return result.getReferenceUri(); }
	public String getBestMatchType() { return result.getSearchTermType(); }
	public String getBestMatchText() { return result.getSearchTermDisplay(); }
	public String getPrimaryID() { return result.getPrimaryID(); }
}
