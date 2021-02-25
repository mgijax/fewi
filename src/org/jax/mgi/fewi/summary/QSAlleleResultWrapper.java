package org.jax.mgi.fewi.summary;

// Is: a wrapper over a QSFeatureResult object, limiting the number of fields that are exposed for
// conversion to JSON (minimizes transfer time)
public class QSAlleleResultWrapper {
	private QSAlleleResult result;
	
	public QSAlleleResultWrapper(QSAlleleResult result) {
		this.result = result;
	}
	
	public String getStars() { return result.getStars(); }
	public String getFeatureType() { return result.getFeatureType(); }
	public String getSymbol() { return result.getSymbol(); }
	public String getName() { return result.getName(); }
	public String getBestMatchText() { return result.getSearchTermDisplay(); }
	public String getDetailUri() { return "/allele/" + result.getPrimaryID(); }
	public String getChromosome() { return result.getChromosome(); }
	public String getLocation() { return result.getLocation(); }
	public String getStrand() { return result.getStrand(); }
	public String getBestMatchType() { return result.getSearchTermType(); }
}
