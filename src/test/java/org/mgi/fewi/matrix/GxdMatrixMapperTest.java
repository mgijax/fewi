package org.mgi.fewi.matrix;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jax.mgi.fewi.matrix.GxdMatrixCell;
import org.jax.mgi.fewi.matrix.GxdMatrixMapper;
import org.jax.mgi.fewi.matrix.GxdMatrixRow;
import org.jax.mgi.fewi.searchUtil.entities.SolrDagEdge;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdStageMatrixResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/*
 * Test functionality specific to the GxdMatrixMapper
 * 	1) Dag relationships need to account for stage ranges
 */
public class GxdMatrixMapperTest {

	GxdMatrixMapper mapper;

	@Before
	public void init() {
		mapper = new GxdMatrixMapper();
	}

	/*
	 * Test TS range roll ups
	 */
	@Test
	public void testOneChildNotInStageRangeAbove() {
		List<SolrDagEdge> edges = mockEdges(edge("p1", "c1"), edge("p2", "c1"));
		mapper.setEdges(edges);

		GxdMatrixRow mockParent = term("parent1", "p1");
		mockParent.setStartStage(1);
		mockParent.setEndStage(20);
		List<GxdMatrixRow> parentTerms = mockTerms(mockParent);

		List<SolrGxdStageMatrixResult> results = mockResults(result("c1", 21, "Yes"));
		List<GxdMatrixCell> cells = mapper.mapCells(parentTerms, results);

		Set<String> cellRowIds = cellRowIds(cells);
		Assert.assertTrue("p1 was mapped to a cell below its stage range",
				!cellRowIds.contains("p1"));
	}

	@Test
	public void testOneChildNotInStageRangeBelow() {
		List<SolrDagEdge> edges = mockEdges(edge("p1", "c1"), edge("p2", "c1"));
		mapper.setEdges(edges);

		GxdMatrixRow mockParent = term("parent1", "p1");
		mockParent.setStartStage(10);
		mockParent.setEndStage(20);
		List<GxdMatrixRow> parentTerms = mockTerms(mockParent);

		List<SolrGxdStageMatrixResult> results = mockResults(result("c1", 9, "Yes"));
		List<GxdMatrixCell> cells = mapper.mapCells(parentTerms, results);

		Set<String> cellRowIds = cellRowIds(cells);
		Assert.assertTrue("p1 was mapped to a cell below its stage range",
				!cellRowIds.contains("p1"));
	}

	/*
	 * helper methods
	 */
	private Set<String> cellRowIds(List<GxdMatrixCell> cells) {
		Set<String> cellRowIds = new HashSet<String>();
		for (GxdMatrixCell cell : cells) {
			cellRowIds.add(cell.getRid());
		}
		return cellRowIds;
	}

	private SolrDagEdge edge(String parentId, String childId) {
		SolrDagEdge child = new SolrDagEdge();
		child.setChildId(childId);
		child.setParentId(parentId);
		return child;
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
