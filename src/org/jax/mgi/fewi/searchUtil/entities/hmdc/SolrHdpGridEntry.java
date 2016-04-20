package org.jax.mgi.fewi.searchUtil.entities.hmdc;

import java.util.List;

public class SolrHdpGridEntry implements SolrHdpEntityInterface {
	
	private Integer gridKey;
	private Integer genoClusterKey;
	private Integer gridClusterKey;
	private Integer homologyClusterKey;
	
	private String allelePairs;
	private List<String> gridHumanSymbols;
	private List<String> gridMouseSymbols;
	
	
	public Integer getGridKey() {
		return gridKey;
	}
	public void setGridKey(Integer gridKey) {
		this.gridKey = gridKey;
	}
	public Integer getGenoClusterKey() {
		return genoClusterKey;
	}
	public void setGenoClusterKey(Integer genoClusterKey) {
		this.genoClusterKey = genoClusterKey;
	}
	public Integer getGridClusterKey() {
		return gridClusterKey;
	}
	public void setGridClusterKey(Integer gridClusterKey) {
		this.gridClusterKey = gridClusterKey;
	}
	public Integer getHomologyClusterKey() {
		return homologyClusterKey;
	}
	public void setHomologyClusterKey(Integer homologyClusterKey) {
		this.homologyClusterKey = homologyClusterKey;
	}
	public String getAllelePairs() {
		return allelePairs;
	}
	public void setAllelePairs(String allelePairs) {
		this.allelePairs = allelePairs;
	}
	public List<String> getGridHumanSymbols() {
		return gridHumanSymbols;
	}
	public void setGridHumanSymbols(List<String> gridHumanSymbols) {
		this.gridHumanSymbols = gridHumanSymbols;
	}
	public List<String> getGridMouseSymbols() {
		return gridMouseSymbols;
	}
	public void setGridMouseSymbols(List<String> gridMouseSymbols) {
		this.gridMouseSymbols = gridMouseSymbols;
	}
}
