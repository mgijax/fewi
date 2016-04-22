package org.jax.mgi.fewi.entities.hmdc.solr;

import java.util.List;

import org.jax.org.mgi.shr.fe.util.GridMarker;

public class SolrHdpGridEntry implements SolrHdpEntityInterface {
	
	private Integer gridKey;
	private Integer genoClusterKey;
	private Integer gridClusterKey;
	private Integer homologyClusterKey;
	
	private String allelePairs;
	private List<GridMarker> gridHumanSymbols;
	private List<GridMarker> gridMouseSymbols;
	
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
	public List<GridMarker> getGridHumanSymbols() {
		return gridHumanSymbols;
	}
	public void setGridHumanSymbols(List<GridMarker> gridHumanSymbols) {
		this.gridHumanSymbols = gridHumanSymbols;
	}
	public List<GridMarker> getGridMouseSymbols() {
		return gridMouseSymbols;
	}
	public void setGridMouseSymbols(List<GridMarker> gridMouseSymbols) {
		this.gridMouseSymbols = gridMouseSymbols;
	}
}
