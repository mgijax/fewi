package org.jax.mgi.fewi.searchUtil;

import java.util.List;

import org.slf4j.Logger;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;

public class ESSearchOption {
	private boolean useCountEndpoint = false;
	private String groupField;
	private boolean getTotalCount = false;
	private boolean getGroupInfo = false;
	private boolean getGroupFirstDoc = false;
	private boolean countNumOfBuckts = false; 
	private boolean tractTopHit = false; 
	private boolean getAllBuckets = false; 
	private boolean useSearchAfter = false; 	
	private boolean useScroll = false; 
	private boolean useESQL = false; 	
	private int precisionThreshold;
	private List<String> returnFields;
	private String extraJoinClause;
	private ESQuery esQuery;
	private Class<?> clazz;
	private List<Query> extraQueries;

	public ESSearchOption() {
	}
	
	public ESSearchOption(String groupField, boolean isGetTotalCount) {
		this.groupField = groupField;
		this.getTotalCount = isGetTotalCount;
	}
	
	public static void logRunTime(Logger logger, long start, String message) {
		long end = System.currentTimeMillis();
		logger.warn("RUN_TIME " + message + ": " + (end - start) + " ms");
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

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public boolean isCountNumOfBuckts() {
		return countNumOfBuckts;
	}

	public void setCountNumOfBuckts(boolean countNumOfBuckts) {
		this.countNumOfBuckts = countNumOfBuckts;
	}

	public boolean isUseCountEndpoint() {
		return useCountEndpoint;
	}

	public void setUseCountEndpoint(boolean useCountEndpoint) {
		this.useCountEndpoint = useCountEndpoint;
	}

	public boolean isTractTopHit() {
		return tractTopHit;
	}

	public void setTractTopHit(boolean tractTopHit) {
		this.tractTopHit = tractTopHit;
	}

	public int getPrecisionThreshold() {		
		return precisionThreshold;
	}

	public void setPrecisionThreshold(int precisionThreshold) {
		this.precisionThreshold = precisionThreshold;
	}

	public boolean isGetAllBuckets() {
		return getAllBuckets;
	}

	public void setGetAllBuckets(boolean getAllBuckets) {
		this.getAllBuckets = getAllBuckets;
	}

	public List<Query> getExtraQueries() {
		return extraQueries;
	}

	public void setExtraQueries(List<Query> extraQueries) {
		this.extraQueries = extraQueries;
	}

	public boolean isUseSearchAfter() {
		return useSearchAfter;
	}

	public void setUseSearchAfter(boolean useSearchAfter) {
		this.useSearchAfter = useSearchAfter;
	}

	public boolean isUseScroll() {
		return useScroll;
	}

	public void setUseScroll(boolean useScroll) {
		this.useScroll = useScroll;
	}

	public boolean isUseESQL() {
		return useESQL;
	}

	public void setUseESQL(boolean useESQL) {
		this.useESQL = useESQL;
	}
}
