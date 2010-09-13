package org.jax.mgi.fewi.searchUtil;

public class MySortPaginator {
	
	private String sort = "";
	private String dir = "asc";
	private Integer startIndex = 0;
	private Integer results = 25;
	
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public Integer getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}
	public Integer getResults() {
		return results;
	}
	public void setResults(Integer results) {
		this.results = results;
	}
	@Override
	public String toString() {
		return "MySortPaginator [dir=" + dir + ", results=" + results
				+ ", sort=" + sort + ", startIndex=" + startIndex + "]";
	}
	

}
