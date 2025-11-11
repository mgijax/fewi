package org.jax.mgi.fewi.matrix;

import java.util.List;
import java.util.Map;

import org.jax.mgi.fewi.searchUtil.entities.ESDagEdge;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdMatrixResult;

/**
 * Adding logic specific to the Gxd Matrix views (both tissue by stage and
 * tissue by gene
 * 
 * Notably: 1) Returns GxdMatrixCells 2) performs roll up logic by calling
 * cell.aggregate() on each duplicate cell 3) Incorporates theiler stage range
 * into isResultAChild() function for mapping results to parentRows
 */
public class GxdMatrixMapper extends DagMatrixMapper {
	public GxdMatrixMapper() {
		super();
	}

	public GxdMatrixMapper(List<ESDagEdge> edges) {
		super(edges);
	}

	/*
	 * Change return type from GridDataCell to GxdMatrixCell
	 */
	@SuppressWarnings("unchecked")
	public List<GxdMatrixCell> mapCells(List<? extends MatrixRow> parentRows,
			List<? extends MatrixResult> results) {
		return (List<GxdMatrixCell>) super.mapCells(parentRows, results);
	}

	/*
	 * Insert GxdMatrixCells instead of GridDataCells Also aggregate the data
	 * based on roll up rules
	 */
	@Override
	protected void insertNewCell(Map<String, GridDataCell> columnMap,
			String rowId, String columnId, MatrixResult result,
			boolean isParent, boolean isChild) {
		ESGxdMatrixResult gxdResult = (ESGxdMatrixResult) result;
		if (!columnMap.containsKey(columnId)) {
			columnMap.put(columnId, new GxdMatrixCell(rowId, columnId,
					gxdResult.getDetectionLevel(), gxdResult.getCount(),
					!isParent));
		} else {
			((GxdMatrixCell) columnMap.get(columnId)).aggregate(
					gxdResult.getDetectionLevel(), gxdResult.getCount(),
					!isParent);
		}
	}

	/*
	 * helper logic functions
	 */
	@Override
	protected boolean isResultAChild(MatrixRow parentTerm, MatrixResult result) {
		return edgeMap.containsKey(parentTerm.getRowId())
				&& edgeMap.get(parentTerm.getRowId()).contains(
						result.getRowId())
				&& isResultInStageRange((GxdMatrixRow) parentTerm,
						(ESGxdMatrixResult) result);
	}

	private boolean isResultInStageRange(GxdMatrixRow parentTerm,
			ESGxdMatrixResult result) {
		return result.getTheilerStage() >= parentTerm.getStartStage()
				&& result.getTheilerStage() <= parentTerm.getEndStage();
	}
}
