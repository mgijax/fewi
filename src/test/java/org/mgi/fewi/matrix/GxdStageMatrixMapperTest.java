package org.mgi.fewi.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.matrix.GxdDummyMatrixCell;
import org.jax.mgi.fewi.matrix.GxdMatrixCell;
import org.jax.mgi.fewi.matrix.GxdMatrixRow;
import org.jax.mgi.fewi.matrix.GxdStageMatrixMapper;
import org.jax.mgi.fewi.searchUtil.entities.SolrDagEdge;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdStageMatrixResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests specific to the Gxd Stage Matrix
 */
public class GxdStageMatrixMapperTest {

	GxdStageMatrixMapper mapper;

	@Before
	public void init() {
		mapper = new GxdStageMatrixMapper();
	}

	/*
	 * Test dummy stage cell indicators
	 */
	@Test
	public void testDummyStageIndicatorCell() {
		/*
		 * Create a situation where only one cell will have the dummy stage
		 * indicator.
		 */
		List<SolrDagEdge> edges = mockEdges();
		mapper.setEdges(edges);

		GxdMatrixRow mockParent = term("parent1", "p1");
		mockParent.setStartStage(1);
		mockParent.setEndStage(2);
		GxdMatrixRow mockParent2 = term("parent2", "p2");
		mockParent2.setStartStage(1);
		mockParent2.setEndStage(2);
		List<GxdMatrixRow> parentTerms = mockTerms(mockParent, mockParent2);

		List<SolrGxdStageMatrixResult> results = mockResults(result("p1", 1, "Yes"),
				result("p2", 1, "Yes"), result("p2", 2, "Yes"));
		List<GxdMatrixCell> cells = mapper.mapCells(parentTerms, results);

		cells = filterOnlyDummyCells(cells);

		Assert.assertEquals(1, cells.size());
	}

	@Test
	public void testDummyStageIndicatorCellNoValidResults() {
		List<SolrDagEdge> edges = mockEdges();
		mapper.setEdges(edges);

		GxdMatrixRow mockParent = term("parent1", "p1");
		mockParent.setStartStage(1);
		mockParent.setEndStage(1);
		List<GxdMatrixRow> parentTerms = mockTerms(mockParent);

		List<SolrGxdStageMatrixResult> results = mockResults();
		List<GxdMatrixCell> cells = mapper.mapCells(parentTerms, results);

		Assert.assertEquals(0, cells.size());
	}

	@Test
	public void testDummyStageIndicatorCellEmptyStage() {
		/*
		 * create a situation where a stage column does not appear, because
		 * there is no data, but a dummy stage indicator would be there if it
		 * did.
		 * 
		 * Basically... stage 28 column shouldn't exist. We make sure dummy cell
		 * doesn't get created for it.
		 */
		List<SolrDagEdge> edges = mockEdges();
		mapper.setEdges(edges);

		GxdMatrixRow mockParent = term("parent1", "p1");
		mockParent.setStartStage(27);
		mockParent.setEndStage(28);
		List<GxdMatrixRow> parentTerms = mockTerms(mockParent);

		List<SolrGxdStageMatrixResult> results = mockResults(result("p1", 27, "Yes"));
		List<GxdMatrixCell> cells = mapper.mapCells(parentTerms, results);

		cells = filterOnlyDummyCells(cells);

		Assert.assertEquals(0, cells.size());
	}

	/*
	 * helper methods
	 */
	private List<GxdMatrixCell> filterOnlyDummyCells(List<GxdMatrixCell> cells) {
		List<GxdMatrixCell> filteredCells = new ArrayList<GxdMatrixCell>();
		for (GxdMatrixCell cell : cells) {
			if (cell instanceof GxdDummyMatrixCell)
				filteredCells.add(cell);
		}
		return filteredCells;
	}

	private List<SolrDagEdge> mockEdges(SolrDagEdge... edges) {
		return Arrays.asList(edges);
	}

	private GxdMatrixRow term(String term, String id) {
		GxdMatrixRow vt = new GxdMatrixRow();
		vt.setTerm(term);
		vt.setRid(id);
		vt.setStartStage(1);
		vt.setEndStage(99);
		return vt;
	}

	private List<GxdMatrixRow> mockTerms(GxdMatrixRow... terms) {
		return Arrays.asList(terms);
	}

	private SolrGxdStageMatrixResult result(String structureId, int stage,
			String detectionLevel) {
		SolrGxdStageMatrixResult mr = new SolrGxdStageMatrixResult();
		mr.setStructureId(structureId);
		mr.setTheilerStage(stage);
		mr.setDetectionLevel(detectionLevel);
		return mr;
	}

	private List<SolrGxdStageMatrixResult> mockResults(
			SolrGxdStageMatrixResult... results) {
		return Arrays.asList(results);
	}
}
