package org.mgi.fewi.matrix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fewi.finder.VocabularyFinder;
import org.jax.mgi.fewi.matrix.DagMatrixRow;
import org.jax.mgi.fewi.matrix.DagMatrixRowOpener;
import org.jax.mgi.fewi.matrix.OpenCloseState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mgi.fewi.mock.MockVocabularyFinder;

/**
 * Tests for the DagMatrixRowOpener Class
 */
public class DagMatrixRowOpenerTest {

	DagMatrixRowOpener opener;
	
	// Test terms
	final String TERMID1 = "TEST_TERM_ID:1";
	final String TERMID2 = "TEST_TERM_ID:2";
	final String CHILD_TERM1 = "TEST_CHILD_ID:1";
	final String GRANDCHILD_TERM1 = "GRANDCHILD:1";
	final String GRANDCHILD_TERM2 = "GRANDCHILD:2";
	
	final String OPEN_STATE = OpenCloseState.OPEN.getState();
		
	@Before
	public void setup()
	{
		opener = new DagMatrixRowOpener();
		// set any mock objects that are normally autowire	
		opener.setVocabFinder(getMockVocabFinder());
	}
	
	/*
	 * Test openRows()
	 */
	@Test
	public void testOpenRowsNoPathsToOpen() {
		List<DagMatrixRow> rows = Arrays.asList(row(TERMID1));
		List<String> paths = Arrays.asList(path(""));
		opener.openRows(rows,paths);
		
		Assert.assertEquals(0, rows.get(0).getChildren().size());
		Assert.assertNotEquals(OPEN_STATE,rows.get(0).getOc());
	}
	
	@Test
	public void testOpenRowsOnePathToOpen() {
		List<DagMatrixRow> rows = Arrays.asList(row(TERMID1));
		List<String> paths = Arrays.asList(path(TERMID1));
		opener.openRows(rows,paths);
		
		Assert.assertEquals(1, rows.get(0).getChildren().size());
		Assert.assertEquals(OPEN_STATE,rows.get(0).getOc());
	}

	@Test
	public void testOpenRowsMultiplePathsToOpen() {
		List<DagMatrixRow> rows = Arrays.asList(row(TERMID1));
		List<String> paths = Arrays.asList(path(TERMID1),
				path(TERMID1,CHILD_TERM1),
				path(TERMID1,CHILD_TERM1,GRANDCHILD_TERM1)
		);
		opener.openRows(rows,paths);
		
		// first term
		DagMatrixRow row = rows.get(0);
		Assert.assertEquals(1, row.getChildren().size());
		Assert.assertEquals(OPEN_STATE,row.getOc());
		
		// child
		row = row.getChildren().get(0);
		Assert.assertEquals(1, row.getChildren().size());
		Assert.assertEquals(OPEN_STATE,row.getOc());
		
		// grand child
		row = row.getChildren().get(0);
		Assert.assertEquals(1, row.getChildren().size());
		Assert.assertEquals(OPEN_STATE,row.getOc());
		Assert.assertEquals(row.getRid(),GRANDCHILD_TERM1);
	}
	
	@Test
	public void testOpenRowsMultiplePathsToOpenAnyOrder() {
		List<DagMatrixRow> rows = Arrays.asList(row(TERMID1));
		List<String> paths = Arrays.asList(
				path(TERMID1,CHILD_TERM1,GRANDCHILD_TERM1),
				path(TERMID1),
				path(TERMID1,CHILD_TERM1)
		);
		opener.openRows(rows,paths);
		
		// first term
		DagMatrixRow row = rows.get(0);
		
		// child
		row = row.getChildren().get(0);
		
		// grand child
		row = row.getChildren().get(0);
		Assert.assertEquals(1, row.getChildren().size());
		Assert.assertEquals(OPEN_STATE,row.getOc());
		Assert.assertEquals(row.getRid(),GRANDCHILD_TERM1);
	}
	
	@Test
	public void testOpenRowsPathNotVisible() {
		List<DagMatrixRow> rows = Arrays.asList(row(TERMID1));
		List<String> paths = Arrays.asList(
				path(TERMID1,CHILD_TERM1,GRANDCHILD_TERM1)
				);
		opener.openRows(rows,paths);
		
		// first term
		DagMatrixRow row = rows.get(0);

		Assert.assertEquals(0, row.getChildren().size());
		Assert.assertNotEquals(OPEN_STATE,row.getOc());
	}
	
	@Test
	public void testOpenRowsParentsMissing() {
		List<DagMatrixRow> rows = Arrays.asList(row(CHILD_TERM1));
		List<String> paths = Arrays.asList(path(TERMID1,CHILD_TERM1));
		opener.openRows(rows,paths);
		
		// first term
		DagMatrixRow row = rows.get(0);

		Assert.assertEquals(1, row.getChildren().size());
		Assert.assertEquals(OPEN_STATE,row.getOc());
	}
	
	// Helper functions
	
	private String path(String... args)
	{
		return StringUtils.join(args,"|");
	}
	
	private DagMatrixRow row(String rid)
	{
		DagMatrixRow row = new DagMatrixRow();
		row.setRid(rid);
		return row;
	}
	
	private VocabTerm term(String id, String term)
	{
		VocabTerm vt = new VocabTerm();
		vt.setPrimaryId(id);
		vt.setTerm(term);
		return vt;
	}
	private VocabularyFinder getMockVocabFinder()
	{
		Map<String,VocabTerm> lookup = new HashMap<String,VocabTerm>();
		
		// create some default terms to exist in our mock DAG
		VocabTerm testTerm1 = term(TERMID1,"test1");
		VocabTerm testTerm2 = term(TERMID2,"test2");
		
		VocabTerm testChild1 = term(CHILD_TERM1,"child1");
		testTerm1.setChildren(Arrays.asList(testChild1));
		
		// give the child term a child of its own
		VocabTerm testGrandChild = term(GRANDCHILD_TERM1,"grandchild1");
		testChild1.setChildren(Arrays.asList(testGrandChild));
		
		// give the child term a child of its own
		VocabTerm testGrandChild2 = term(GRANDCHILD_TERM2,"grandchild2");
		testGrandChild.setChildren(Arrays.asList(testGrandChild2));
		
		lookup.put(TERMID1,testTerm1);
		lookup.put(TERMID2,testTerm2);
		lookup.put(CHILD_TERM1,testChild1);
		lookup.put(GRANDCHILD_TERM1,testGrandChild);
		lookup.put(GRANDCHILD_TERM2,testGrandChild2);
		
		return new MockVocabularyFinder(lookup);
	}
}
