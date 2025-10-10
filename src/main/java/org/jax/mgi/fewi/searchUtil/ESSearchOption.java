package org.jax.mgi.fewi.searchUtil;

import java.util.List;

public class ESSearchOption {
	private String groupField;
	private boolean getTotalCount = false;
	private boolean getGroupInfo = false;
	private boolean getGroupFirstDoc = false;
	private List<String> returnFields;
	private String extraJoinClause;
	private ESQuery esQuery;
	
	public ESSearchOption() {
	}
	
	public ESSearchOption(String groupField, boolean isGetTotalCount) {
		this.groupField = groupField;
		this.getTotalCount = isGetTotalCount;
	}
	
	public String getGroupField() {
		return groupField;
	}
	public void setGroupField(String groupField) {
		this.groupField = groupField;
	}
	public boolean isGetGroupInfo() {
		return getGroupInfo;
	}
	public void setGetGroupInfo(boolean getGroupInfo) {
		this.getGroupInfo = getGroupInfo;
	}
	public List<String> getReturnFields() {
		return returnFields;
	}
	public void setReturnFields(List<String> returnFields) {
		this.returnFields = returnFields;
	}

	public String getExtraJoinClause() {
		return extraJoinClause;
	}

	public void setExtraJoinClause(String extraJoinClause) {
		this.extraJoinClause = extraJoinClause;
	}

	public boolean isGetGroupFirstDoc() {
		return getGroupFirstDoc;
	}

	public void setGetGroupFirstDoc(boolean getGroupFirstDoc) {
		this.getGroupFirstDoc = getGroupFirstDoc;
	}

	public boolean isGetTotalCount() {
		return getTotalCount;
	}

	public void setGetTotalCount(boolean getTotalCount) {
		this.getTotalCount = getTotalCount;
	}

	public ESQuery getEsQuery() {
		return esQuery;
	}

	public void setEsQuery(ESQuery esQuery) {
		this.esQuery = esQuery;
	}

}
