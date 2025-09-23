package org.jax.mgi.fewi.hunter;

public class ESLookupIndex {
	private String indexName;
	private String joinFieldName;

	public ESLookupIndex(String indexName, String joinFieldName) {
		this.indexName = indexName;
		this.joinFieldName = joinFieldName;
	}
	
	public String toString() {
		return this.indexName + " " + this.joinFieldName;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getJoinFieldName() {
		return joinFieldName;
	}

	public void setJoinFieldName(String joinFieldName) {
		this.joinFieldName = joinFieldName;
	}
}
