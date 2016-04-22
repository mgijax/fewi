package org.jax.mgi.fewi.entities.hmdc.models;

import java.util.List;

import org.jax.mgi.fewi.entities.hmdc.visitors.GridVisitorInterface;

public class GridCluster extends AbstractGridModel {

	private List<String> humanSymbols;
	private List<String> mouseSymbols;
	
	public List<String> getHumanSymbols() {
		return humanSymbols;
	}
	public void setHumanSymbols(List<String> humanSymbols) {
		this.humanSymbols = humanSymbols;
	}
	public List<String> getMouseSymbols() {
		return mouseSymbols;
	}
	public void setMouseSymbols(List<String> mouseSymbols) {
		this.mouseSymbols = mouseSymbols;
	}

	@Override
	public void Accept(GridVisitorInterface pi) {
		pi.Visit(this);
	}
}
