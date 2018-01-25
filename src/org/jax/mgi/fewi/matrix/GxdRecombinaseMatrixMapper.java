package org.jax.mgi.fewi.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fewi.searchUtil.entities.SolrDagEdge;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRecombinaseMatrixResult;

/**
 * Adding logic specific to the Recombinase Matrix view (tissue by gene [expression]
 * and recombinase [expression])
 * 
 * Notably: 1) Returns GxdRecombinaseMatrixCells 2) performs roll up logic by calling
 * cell.aggregate() on each duplicate cell 3) Incorporates Theiler stage range
 * into isResultAChild() function for mapping results to parentRows
 */
public class GxdRecombinaseMatrixMapper extends DagMatrixMapper {
	public GxdRecombinaseMatrixMapper() {
		super();
	}

	public GxdRecombinaseMatrixMapper(List<SolrDagEdge> edges) {
		super(edges);
	}

	/*
	 * Change return type from GridDataCell to GxdRecombinaseMatrixCell
	 */
	public List<GxdRecombinaseMatrixCell> mapRecombinaseGridCells(
			List<GxdMatrixRow> parentRows,
			List<SolrGxdRecombinaseMatrixResult> results) {
		Map<String, Map<String, GxdRecombinaseMatrixCell>> rowColumnMap = new HashMap<String, Map<String, GxdRecombinaseMatrixCell>>();

		// init parent terms
		for (MatrixRow parentRow : parentRows) {
			Map<String, GxdRecombinaseMatrixCell> columnMap = new HashMap<String, GxdRecombinaseMatrixCell>();
			rowColumnMap.put(parentRow.getRowId(), columnMap);

			for (SolrGxdRecombinaseMatrixResult result : (List<SolrGxdRecombinaseMatrixResult>) results) {
				// do we add this cell? Answer: only if it is a child of the
				// parent or is the parent itself
				boolean isParent = isResultAParent(parentRow, result);
				boolean isChild = isResultAChild(parentRow, result);
				if (isChild || isParent) {
					String columnId = result.getColumnId();
					columnsWithData.add(columnId);
					insertNewGxdRecombinaseCell(columnMap, parentRow.getRowId(), "0",
							result, isParent, isChild, columnId);
				}
			}
		}

// No post-processing needed.
//		postProcess(rowColumnMap, parentRows, results);

		// make list of cells to return
		List<GxdRecombinaseMatrixCell> cells = new ArrayList<GxdRecombinaseMatrixCell>();
		for (String rowId : rowColumnMap.keySet()) {
			Map<String, GxdRecombinaseMatrixCell> columnMap = rowColumnMap.get(rowId);
			cells.addAll(columnMap.values());
		}
		return cells;
	}
	/*
	 * Insert GxdRecombinaseMatrixCells instead of GridDataCells Also aggregate the data
	 * based on roll up rules
	 */
	protected void insertNewGxdRecombinaseCell(Map<String, GxdRecombinaseMatrixCell> columnMap,
			String rowId, String columnId, MatrixResult result,
			boolean isParent, boolean isChild, String symbol) {

		SolrGxdRecombinaseMatrixResult gxdResult = (SolrGxdRecombinaseMatrixResult) result;
		GxdRecombinaseMatrixCell cell;			// the cell we're inserting (or aggregating)
		
		// need to check cell type here and handle GXD and Recombinase differently
		
		if (SolrGxdRecombinaseMatrixResult.GXD.equals(gxdResult.getCellType())) {
			if (!columnMap.containsKey(columnId)) {
				cell = new GxdRecombinaseMatrixCell(SolrGxdRecombinaseMatrixResult.GXD, rowId, columnId, !isParent);
				cell.initializeGxd(gxdResult.getDetectionLevel(), gxdResult.getCount());
				cell.setSymbol(symbol);
				columnMap.put(columnId, cell);
			} else {
				cell = (GxdRecombinaseMatrixCell) columnMap.get(columnId);
				cell.aggregateGxd(gxdResult.getDetectionLevel(), gxdResult.getCount(), !isParent);
			}
		} else {	// is a Recombinase result
			
		}
	}

	/*
	 * helper logic functions
	 */
	@Override
	protected boolean isResultAChild(MatrixRow parentTerm, MatrixResult result) {
		return edgeMap.containsKey(parentTerm.getRowId())
				&& edgeMap.get(parentTerm.getRowId()).contains( ((SolrGxdRecombinaseMatrixResult) result).getRowId())
				&& isResultInStageRange((GxdMatrixRow) parentTerm,
						(SolrGxdRecombinaseMatrixResult) result);
	}

	private boolean isResultInStageRange(GxdMatrixRow parentTerm,
			SolrGxdRecombinaseMatrixResult result) {
		return result.getTheilerStage() >= parentTerm.getStartStage()
				&& result.getTheilerStage() <= parentTerm.getEndStage();
	}
}
