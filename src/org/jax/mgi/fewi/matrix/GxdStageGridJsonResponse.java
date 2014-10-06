package org.jax.mgi.fewi.matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON Response Object; the purpose of this object is to be serialized into a
 * JSON response for GXD's stage grid
 */
public class GxdStageGridJsonResponse {

	// ////////////////////////////////////////////////////////////////////////
	// INTERNAL FIELDS
	// ////////////////////////////////////////////////////////////////////////

	// parent terms
	protected List<GxdMatrixRow> parentTerms = new ArrayList<GxdMatrixRow>();

	// cells of the matrix
	protected List<GxdMatrixCell> gxdMatrixCells = new ArrayList<GxdMatrixCell>();

	// ////////////////////////////////////////////////////////////////////////
	// BASIC ACCESSORS
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Get the parent terms
	 */
	public List<GxdMatrixRow> getRows() {
		return parentTerms;
	}

	/**
	 * Set the parent terms
	 */
	public void setParentTerms(List<GxdMatrixRow> parentTerms) {
		this.parentTerms = parentTerms;
	}

	/**
	 * Get the parent terms
	 */
	public List<GxdMatrixCell> getData() {
		return gxdMatrixCells;
	}

	/**
	 * Set the parent terms
	 */
	public void setGxdMatrixCells(List<GxdMatrixCell> gxdMatrixCells) {
		this.gxdMatrixCells = gxdMatrixCells;
	}

}
