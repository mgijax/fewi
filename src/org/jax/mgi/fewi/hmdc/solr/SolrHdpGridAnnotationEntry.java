package org.jax.mgi.fewi.hmdc.solr;


public class SolrHdpGridAnnotationEntry implements SolrHdpEntityInterface {

	private String uniqueKey;
	private Integer gridKey;
	private String term;
	private String termId;
	private String termHeader;
	private String termType;
	private String qualifier;
	
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
}
