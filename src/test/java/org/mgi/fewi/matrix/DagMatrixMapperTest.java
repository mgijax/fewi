package org.mgi.fewi.matrix;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jax.mgi.fewi.matrix.DagMatrixMapper;
import org.jax.mgi.fewi.matrix.GridDataCell;
import org.jax.mgi.fewi.matrix.MatrixResult;
import org.jax.mgi.fewi.matrix.MatrixRow;
import org.jax.mgi.fewi.searchUtil.entities.SolrDagEdge;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/*
 * Test the basics of the DagMatrixMapper
 * 
 * 	Note: the basic logic used here is leveraged by other mappers,
 * 		like the GxdMatrixMapper and GxdStageMatrixMapper
 */
public class DagMatrixMapperTest {

	DagMatrixMapper mapper;

	@Before
	public void init() {
		mapper = new DagMatrixMapper();
	}

	/*
	 * Test parent / child edge mapping
	 */
	@Test
	public void testNoResults() {
		List<SolrDagEdge> edges = mockEdges();
		mapper.setEdges(edges);

		List<TestMatrixRow> parentTerms = mockTerms();
		List<TestMatrixResult> results = mockResults();

		List<? extends GridDataCell> cells = mapper.mapCells(parentTerms,
				results);
		Assert.assertEquals(0, cells.size());
	}

	@Test
	public void testOneParentResult() {
		List<SolrDagEdge> edges = mockEdges();
		mapper.setEdges(edges);

		List<TestMatrixRow> parentTerms = mockTerms(term("parent1", "p1"));
		List<TestMatrixResult> results = mockResults(result("p1", "col1", "Yes"));

		List<? extends GridDataCell> cells = mapper.mapCells(parentTerms,
				results);
		Assert.assertEquals("p1", cells.get(0).getRid());
	}

	@Test
	public void testOneChildResult() {
		List<SolrDagEdge> edges = mockEdges(edge("p1", "c1"));
		mapper.setEdges(edges);

		List<TestMatrixRow> parentTerms = mockTerms(term("parent1", "p1"));
		List<TestMatrixResult> results = mockResults(result("c1", "col1", "Yes"));

		List<? extends GridDataCell> cells = mapper.mapCells(parentTerms,
				results);
		Assert.assertEquals("p1", cells.get(0).getRid());
	}

	@Test
	public void testOneChildResultManyEdges() {
		List<SolrDagEdge> edges = mockEdges(edge("p1", "c1"), edge("p1", "c2"),
				edge("p2", "c3"), edge("p3", "c4"));
		mapper.setEdges(edges);

		List<TestMatrixRow> parentTerms = mockTerms(term("parent1", "p1"),
				term("parent2", "p2"), term("parent3", "p3"));
		List<TestMatrixResult> results = mockResults(result("c4", "col1", "Yes"));

		List<? extends GridDataCell> cells = mapper.mapCells(parentTerms,
				results);
		Assert.assertEquals("p3", cells.get(0).getRid());
	}

	@Test
	public void testOneChildResultManyParents() {
		List<SolrDagEdge> edges = mockEdges(edge("p1", "c1"), edge("p2", "c1"));
		mapper.setEdges(edges);

		List<TestMatrixRow> parentTerms = mockTerms(term("parent1", "p1"),
				term("parent2", "p2"));
		List<TestMatrixResult> results = mockResults(result("c1", "col1", "Yes"));

		List<? extends GridDataCell> cells = mapper.mapCells(parentTerms,
				results);

		Set<String> cellRowIds = cellRowIds(cells);
		Assert.assertTrue("p1 was not mapped to a cell",
				cellRowIds.contains("p1"));
		Assert.assertTrue("p2 was not mapped to a cell",
				cellRowIds.contains("p2"));
	}

	/*
	 * helper methods
	 */
	private Set<String> cellRowIds(List<? extends GridDataCell> cells) {
		Set<String> cellRowIds = new HashSet<String>();
		for (GridDataCell cell : cells) {
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

	private TestMatrixRow term(String term, String id) {
		TestMatrixRow vt = new TestMatrixRow();
		vt.setRowId(id);
		return vt;
	}

	private List<TestMatrixRow> mockTerms(TestMatrixRow... terms) {
		return Arrays.asList(terms);
	}

	private TestMatrixResult result(String rowId, String colId, String value) {
		TestMatrixResult mr = new TestMatrixResult();
		mr.setRowId(rowId);
		mr.setColumnId(colId);
		mr.setValue(value);
		return mr;
	}

	private List<TestMatrixResult> mockResults(TestMatrixResult... results) {
		return Arrays.asList(results);
	}

	/*
	 * Use the interfaces to create mock test objects
	 */
	private class TestMatrixRow implements MatrixRow {
		String id;

		public String getRowId() {
			return id;
		}

		public void setRowId(String id) {
			this.id = id;
		}
	}

	private class TestMatrixResult implements MatrixResult {
		String rowId;
		String columnId;
		String value;

		public String getRowId() {
			return rowId;
		}

		public String getColumnId() {
			return columnId;
		}

		public String getValue() {
			return value;
		}

		public void setRowId(String rowId) {
			this.rowId = rowId;
		}

		public void setColumnId(String columnId) {
			this.columnId = columnId;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
