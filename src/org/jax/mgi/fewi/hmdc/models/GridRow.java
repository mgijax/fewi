package org.jax.mgi.fewi.hmdc.models;

import java.util.HashMap;

import org.jax.mgi.fewi.hmdc.visitors.GridVisitorInterface;

public class GridRow extends AbstractGridModel {

	private GridCluster gridCluster;
	private HashMap<String, GridTermHeaderAnnotation> diseaseCells;
	private HashMap<String, GridTermHeaderAnnotation> mpHeaderCells;
	
	public GridCluster getGridCluster() {
		return gridCluster;
	}
	public void setGridCluster(GridCluster gridCluster) {
		this.gridCluster = gridCluster;
	}
	public HashMap<String, GridTermHeaderAnnotation> getDiseaseCells() {
		return diseaseCells;
	}
	public void setDiseaseCells(HashMap<String, GridTermHeaderAnnotation> diseaseCells) {
		this.diseaseCells = diseaseCells;
	}
	public HashMap<String, GridTermHeaderAnnotation> getMpHeaderCells() {
		return mpHeaderCells;
	}
	public void setMpHeaderCells(HashMap<String, GridTermHeaderAnnotation> mpHeaderCells) {
		this.mpHeaderCells = mpHeaderCells;
	}
	
	@Override
	public void Accept(GridVisitorInterface pi) {
		pi.Visit(this);
	}
}
