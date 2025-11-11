package org.jax.mgi.fewi.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fewi.searchUtil.entities.ESDagEdge;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdPhenoMatrixResult;

/**
 * Adding logic specific to the Correlation Matrix view (tissue by gene [expression]
 * and genocluster [phenotype])
 * 
 * Notably: 1) Returns GxdPhenoMatrixCells 2) performs roll up logic by calling
 * cell.aggregate() on each duplicate cell 3) Incorporates Theiler stage range
 * into isResultAChild() function for mapping results to parentRows
 */
public class GxdPhenoMatrixMapper extends DagMatrixMapper {
	public GxdPhenoMatrixMapper() {
		super();
	}

	public GxdPhenoMatrixMapper(List<ESDagEdge> edges) {
		super(edges);
	}

	/*
	 * Change return type from GridDataCell to GxdPhenoMatrixCell
	 */
/*	@SuppressWarnings("unchecked")
	public List<GxdPhenoMatrixCell> mapPhenoGridCells(List<GxdMatrixRow> parentRows,
			List<SolrGxdPhenoMatrixResult> results) {
		return (List<GxdPhenoMatrixCell>) this.mapCells(parentRows, results);
	}
*/
	public List<GxdPhenoMatrixCell> mapPhenoGridCells(
			List<GxdMatrixRow> parentRows,
			List<ESGxdPhenoMatrixResult> results) {
		Map<String, Map<String, GxdPhenoMatrixCell>> rowColumnMap = new HashMap<String, Map<String, GxdPhenoMatrixCell>>();

		// init parent terms
		for (MatrixRow parentRow : parentRows) {
			Map<String, GxdPhenoMatrixCell> columnMap = new HashMap<String, GxdPhenoMatrixCell>();
			rowColumnMap.put(parentRow.getRowId(), columnMap);

			for (ESGxdPhenoMatrixResult result : (List<ESGxdPhenoMatrixResult>) results) {
				// do we add this cell? Answer: only if it is a child of the
				// parent or is the parent itself
				boolean isParent = isResultAParent(parentRow, result);
				boolean isChild = isResultAChild(parentRow, result);
				if (isChild || isParent) {
					String columnId = result.getColumnId();
					columnsWithData.add(columnId);
					insertNewGxdPhenoCell(columnMap, parentRow.getRowId(), "0",
							result, isParent, isChild, columnId);
				}
			}
		}

// No post-processing needed.
//		postProcess(rowColumnMap, parentRows, results);

		// make list of cells to return
		List<GxdPhenoMatrixCell> cells = new ArrayList<GxdPhenoMatrixCell>();
		for (String rowId : rowColumnMap.keySet()) {
			Map<String, GxdPhenoMatrixCell> columnMap = rowColumnMap.get(rowId);
			cells.addAll(columnMap.values());
		}
		return cells;
	}
	/*
	 * Insert GxdPhenoMatrixCells instead of GridDataCells Also aggregate the data
	 * based on roll up rules
	 */
	protected void insertNewGxdPhenoCell(Map<String, GxdPhenoMatrixCell> columnMap,
			String rowId, String columnId, MatrixResult result,
			boolean isParent, boolean isChild, String symbol) {

		ESGxdPhenoMatrixResult gxdResult = (ESGxdPhenoMatrixResult) result;
		GxdPhenoMatrixCell cell;			// the cell we're inserting (or aggregating)
		
		// need to check cell type here and handle GXD and Pheno differently
		
		if (ESGxdPhenoMatrixResult.GXD.equals(gxdResult.getCellType())) {
			if (!columnMap.containsKey(columnId)) {
				cell = new GxdPhenoMatrixCell(ESGxdPhenoMatrixResult.GXD, rowId, columnId, !isParent);
				cell.initializeGxd(gxdResult.getDetectionLevel(), gxdResult.getCount());
				cell.setSymbol(symbol);
				columnMap.put(columnId, cell);
			} else {
				cell = (GxdPhenoMatrixCell) columnMap.get(columnId);
				cell.aggregateGxd(gxdResult.getDetectionLevel(), gxdResult.getCount(), !isParent);
			}
		} else {	// is a Pheno result
			
		}
	}

	/*
	 * helper logic functions
	 */
	@Override
	protected boolean isResultAChild(MatrixRow parentTerm, MatrixResult result) {
		return edgeMap.containsKey(parentTerm.getRowId())
				&& edgeMap.get(parentTerm.getRowId()).contains( ((ESGxdPhenoMatrixResult) result).getRowId())
				&& isResultInStageRange((GxdMatrixRow) parentTerm,
						(ESGxdPhenoMatrixResult) result);
	}

	private boolean isResultInStageRange(GxdMatrixRow parentTerm,
			ESGxdPhenoMatrixResult result) {
		return result.getTheilerStage() >= parentTerm.getStartStage()
				&& result.getTheilerStage() <= parentTerm.getEndStage();
	}
}
