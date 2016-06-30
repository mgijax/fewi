package org.jax.mgi.fewi.hmdc.solr;

import java.util.List;

import org.jax.mgi.shr.jsonmodel.GridGenocluster;
import org.jax.mgi.shr.jsonmodel.GridMarker;

public class SolrHdpGridEntry implements SolrHdpEntityInterface {
	
	private Integer gridKey;
	private Integer genoClusterKey;
	private Integer gridClusterKey;
	private Integer homologyClusterKey;
	private String markerSymbol;
	private String allelePairs;
	private List<GridMarker> gridHumanSymbols;
	private List<GridMarker> gridMouseSymbols;
	private Integer byGenoCluster;
	private Integer conditional;
	private GridGenocluster gridGenocluster;
	
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
	public String getMarkerSymbol() {
		return markerSymbol;
	}
	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}
	public Integer getByGenoCluster() {
		return byGenoCluster;
	}
	public void setByGenoCluster(Integer byGenoCluster) {
		this.byGenoCluster = byGenoCluster;
	}
	public Integer getConditional() {
		return conditional;
	}
	public void setConditional(Integer conditional) {
		this.conditional = conditional;
	}
	public GridGenocluster getGridGenocluster() {
		return gridGenocluster;
	}
	public void setGridGenocluster(GridGenocluster gridGenocluster) {
		this.gridGenocluster = gridGenocluster;
	}
	
	// convenience methods
	
	public boolean isConditional() {
		return ((conditional != null) && (conditional != 0));
	}
}
