package org.jax.mgi.fewi.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Can make cells for a DAG based matrix (where the DagTerms are the rows of the
 * matrix)
 */
public class DagMatrixMapper {

	protected Map<String, Set<String>> edgeMap;
	// track columns with data
	protected Set<String> columnsWithData = new HashSet<String>();

	public DagMatrixMapper() {

	}

	public DagMatrixMapper(List<? extends DagEdge> edges) {
		setEdges(edges);
	}

	public void setEdges(List<? extends DagEdge> edges) {
		edgeMap = new HashMap<String, Set<String>>();
		for (DagEdge edge : edges) {
			if (!edgeMap.containsKey(edge.getParentId())) {
				edgeMap.put(edge.getParentId(), new HashSet<String>());
			}
			edgeMap.get(edge.getParentId()).add(edge.getChildId());
		}
	}

	public List<? extends GridDataCell> mapCells(
			List<? extends MatrixRow> parentRows,
			List<? extends MatrixResult> results) {
		Map<String, Map<String, GridDataCell>> rowColumnMap = new HashMap<String, Map<String, GridDataCell>>();

		// init parent terms
		for (MatrixRow parentRow : parentRows) {
			Map<String, GridDataCell> columnMap = new HashMap<String, GridDataCell>();
			rowColumnMap.put(parentRow.getRowId(), columnMap);

			for (MatrixResult result : results) {
				// do we add this cell? Answer: only if it is a child of the
				// parent or is the parent itself
				boolean isParent = isResultAParent(parentRow, result);
				boolean isChild = isResultAChild(parentRow, result);
				if (isChild || isParent) {
					String columnId = result.getColumnId();
					columnsWithData.add(columnId);
					insertNewCell(columnMap, parentRow.getRowId(), columnId,
							result, isParent, isChild);
				}
			}
		}

		postProcess(rowColumnMap, parentRows, results);

		// make list of cells to return
		List<GridDataCell> cells = new ArrayList<GridDataCell>();
		for (String rowId : rowColumnMap.keySet()) {
			Map<String, GridDataCell> columnMap = rowColumnMap.get(rowId);
			cells.addAll(columnMap.values());
		}
		return cells;
	}

	protected void insertNewCell(Map<String, GridDataCell> columnMap,
			String rowId, String columnId, MatrixResult result,
			boolean isParent, boolean isChild) {
		columnMap.put(columnId, new GridDataCell(result.getValue(), rowId,
				columnId));
	}

	// post process the data cell map
	protected void postProcess(
			Map<String, Map<String, GridDataCell>> rowColumnMap,
			List<? extends MatrixRow> parentRows,
			List<? extends MatrixResult> results) {
		// to be overridden if needed
	}

	/*
	 * helper logic functions
	 */
	protected boolean isResultAParent(MatrixRow parentRow, MatrixResult result) {
		return parentRow.getRowId().equals(result.getRowId());
	}

	protected boolean isResultAChild(MatrixRow parentRow, MatrixResult result) {
		return edgeMap.containsKey(parentRow.getRowId())
				&& edgeMap.get(parentRow.getRowId())
						.contains(result.getRowId());
	}
}