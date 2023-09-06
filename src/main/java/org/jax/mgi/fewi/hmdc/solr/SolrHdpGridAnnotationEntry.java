package org.jax.mgi.fewi.hmdc.solr;


public class SolrHdpGridAnnotationEntry implements SolrHdpEntityInterface {

	private String uniqueKey;
	private Integer gridKey;
	private String term;
	private String termId;
	private String termHeader;
	private String termType;
	private String qualifier;
	private String sourceTerm;
	private String sourceId;
	private Integer byDagTerm;
	private String backgroundSensitive;
	
	public String getUniqueKey() {
		return uniqueKey;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
	public Integer getGridKey() {
		return gridKey;
	}
	public void setGridKey(Integer gridKey) {
		this.gridKey = gridKey;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}
	public String getTermHeader() {
		return termHeader;
	}
	public void setTermHeader(String termHeader) {
		this.termHeader = termHeader;
	}
	public String getTermType() {
		return termType;
	}
	public void setTermType(String termType) {
		this.termType = termType;
	}
	public String getQualifier() {
		return qualifier;
	}
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	public String getSourceTerm() {
		return sourceTerm;
	}
	public void setSourceTerm(String sourceTerm) {
		this.sourceTerm = sourceTerm;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public Integer getByDagTerm() {
		return byDagTerm;
	}
	public void setByDagTerm(Integer byDagTerm) {
		this.byDagTerm = byDagTerm;
	}
	public String getBackgroundSensitive() {
		return backgroundSensitive;
	}
	public void setBackgroundSensitive(String backgroundSensitive) {
		this.backgroundSensitive = backgroundSensitive;
	}

	// convenience methods

	public boolean isNormalAnnotation() {
		return (qualifier != null) && ("normal".equals(qualifier));
	}
	
	public boolean isBackgroundSensitive() {
		return (backgroundSensitive != null);
	}
}
