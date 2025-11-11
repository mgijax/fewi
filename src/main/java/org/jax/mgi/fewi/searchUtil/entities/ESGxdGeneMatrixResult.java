package org.jax.mgi.fewi.searchUtil.entities;

import org.jax.mgi.fewi.matrix.MatrixResult;

public class ESGxdGeneMatrixResult extends ESGxdMatrixResult implements MatrixResult {
	/*
	 * define the basic MatrixResult interface methods
	 */
	public String getRowId() {
		return structureId;
	}

	public String getColumnId() {
		return geneSymbol;
	}

	public String getValue() {
		return detectionLevel;
	}
}
