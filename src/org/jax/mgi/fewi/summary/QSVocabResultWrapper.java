package org.jax.mgi.fewi.summary;

// Is: a wrapper over a QSVocabResult object, limiting the number of fields that are exposed for
// conversion to JSON (minimizes transfer time)
public class QSVocabResultWrapper {
	private QSVocabResult result;
	
	public QSVocabResultWrapper(QSVocabResult result) {
		this.result = result;
	}
	
	public String getStars() { return result.getStars(); }
	public String getDetailUri() { return result.getDetailUri(); }
	public String getVocabName() { return result.getVocabName(); }
	public String getTerm() { return result.getTerm(); }
	public Long getAnnotationCount() { return result.getAnnotationCount(); }
	public String getAnnotationUri() { return result.getAnnotationUri(); }
	public String getAnnotationText() { return result.getAnnotationText(); }
	public String getBestMatchText() { return result.getSearchTermDisplay(); }
	public String getBestMatchType() { return result.getSearchTermType(); }
	public String getPrimaryID() { return result.getPrimaryID(); }
}
