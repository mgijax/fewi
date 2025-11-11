package org.jax.mgi.fewi.matrix;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jax.mgi.fewi.searchUtil.entities.ESDagEdge;

/**
 * Adding logic specific to the Gxd Tissue by Stage Matrix
 * 
 * Notably: 1) Adds dummy cells for theiler stage indicators
 */

public class GxdStageMatrixMapper extends GxdMatrixMapper {
	Set<String> stagesInMatrix;

	public GxdStageMatrixMapper() {
		super();
	}

	public GxdStageMatrixMapper(List<ESDagEdge> edges) {
		super(edges);
	}

	public void setStagesInMatrix(Set<String> stages) {
		this.stagesInMatrix = stages;
		this.columnsWithData.addAll(stages);
	}

	/*
	 * We add a postprocess step to add the dummy theiler stage indicators
	 */
	@Override
	protected void postProcess(
			Map<String, Map<String, GridDataCell>> rowColumnMap,
			List<? extends MatrixRow> parentRows,
			List<? extends MatrixResult> results) {
		// add indication of valid stage/structure combination for empty cells
		@SuppressWarnings("unchecked")
		List<GxdMatrixRow> gxdParentRows = (List<GxdMatrixRow>) parentRows;
		for (GxdMatrixRow gxdParentRow : gxdParentRows) {
			Map<String, GridDataCell> stageMap = rowColumnMap.get(gxdParentRow
					.getRowId());
			// add any fake data cells, but only if we have at least one result
			// thus far.
			if (stageMap.keySet().size() > 0) {
				for (Integer stage = gxdParentRow.getStartStage(); stage <= gxdParentRow
						.getEndStage(); stage++) {
					String stageKey = stage.toString();
					if (columnsWithData.contains(stageKey)
							&& !stageMap.containsKey(stageKey)) {
						GxdMatrixCell dummyCell = new GxdDummyMatrixCell(
								gxdParentRow.getRowId(), stageKey,
								GxdDummyMatrixCell.DUMMY_TYPE.STAGE);
						stageMap.put(stageKey, dummyCell);
					}
				}
			}
		}
	}
}
