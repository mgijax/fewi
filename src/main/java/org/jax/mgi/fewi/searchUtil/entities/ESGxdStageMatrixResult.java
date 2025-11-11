package org.jax.mgi.fewi.searchUtil.entities;

import org.jax.mgi.fewi.matrix.MatrixResult;

public class ESGxdStageMatrixResult extends ESGxdMatrixResult implements MatrixResult {
	/*
	 * define the basic MatrixResult interface methods
	 */
	public String getRowId() {
		return structureId;
	}

	public String getColumnId() {
		return theilerStage.toString();
	}

	public String getValue() {
		return detectionLevel;
	}
}
