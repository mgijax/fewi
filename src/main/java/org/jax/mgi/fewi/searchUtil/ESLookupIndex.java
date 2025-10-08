package org.jax.mgi.fewi.searchUtil;

public class ESLookupIndex {
	private String indexName;
	private String joinFieldName;
	private boolean joinFieldMultiValues;

	public ESLookupIndex(String indexName, String joinFieldName, boolean joinFieldMultiValues) {
		this.indexName = indexName;
		this.joinFieldName = joinFieldName;
		this.joinFieldMultiValues = joinFieldMultiValues;
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

	public boolean isJoinFieldMultiValues() {
		return joinFieldMultiValues;
	}

	public void setJoinFieldMultiValues(boolean joinFieldMultiValues) {
		this.joinFieldMultiValues = joinFieldMultiValues;
	}
}
