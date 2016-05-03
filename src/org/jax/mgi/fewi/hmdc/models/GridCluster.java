package org.jax.mgi.fewi.hmdc.models;

import java.util.List;

import org.jax.mgi.fewi.hmdc.visitors.GridVisitorInterface;
import org.jax.org.mgi.shr.fe.util.GridMarker;

public class GridCluster extends AbstractGridModel {

	private List<GridMarker> humanSymbols;
	private List<GridMarker> mouseSymbols;
	private Integer homologyClusterKey;

	public List<GridMarker> getHumanSymbols() {
		return humanSymbols;
	}
	public void setHumanSymbols(List<GridMarker> humanSymbols) {
		this.humanSymbols = humanSymbols;
	}
	public List<GridMarker> getMouseSymbols() {
		return mouseSymbols;
	}
	public void setMouseSymbols(List<GridMarker> mouseSymbols) {
		this.mouseSymbols = mouseSymbols;
	}
	public Integer getHomologyClusterKey() {
		return homologyClusterKey;
	}
	public void setHomologyClusterKey(Integer homologyClusterKey) {
		this.homologyClusterKey = homologyClusterKey;
	}
	
	@Override
	public void Accept(GridVisitorInterface pi) {
		pi.Visit(this);
	}

}
