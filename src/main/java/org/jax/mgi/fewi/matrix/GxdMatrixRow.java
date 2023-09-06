package org.jax.mgi.fewi.matrix;

import org.jax.mgi.fe.datamodel.VocabTerm;



/*
 * meta data for rendering the GXD Matrix Rows
 */
public class GxdMatrixRow extends AbstractDagMatrixRow<GxdMatrixRow> {

	private int startStage;
	private int endStage;

	
	public int getStartStage() {
		return startStage;
	}

	public void setStartStage(int startStage) {
		this.startStage = startStage;
	}

	public int getEndStage() {
		return endStage;
	}

	public void setEndStage(int endStage) {
		this.endStage = endStage;
	}
	
	public GxdMatrixRow makeMatrixRow(VocabTerm term)
	{
		GxdMatrixRow mr = new GxdMatrixRow();
		mr.setRid(term.getPrimaryId());
		mr.setTerm(term.getTerm());
		mr.setStartStage(term.getEmapInfo().getStartStage());
		mr.setEndStage(term.getEmapInfo().getEndStage());
		return mr;
	}
	
	@Override
	public String toString() {
		return "GxdMatrixRow [rid=" + rid + ", term=" + term + ", startStage="
				+ startStage + ", endStage=" + endStage + "]";
	}
}
