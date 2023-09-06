package org.mgi.fewi.handler;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fe.datamodel.VocabTermEmapInfo;
import org.jax.mgi.fewi.finder.VocabularyFinder;
import org.jax.mgi.fewi.forms.GxdQueryForm;
import org.jax.mgi.fewi.handler.GxdMatrixHandler;
import org.jax.mgi.fewi.matrix.DagMatrixRowOpener;
import org.jax.mgi.fewi.matrix.GxdMatrixRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mgi.fewi.mock.MockVocabularyFinder;

/**
 * Tests decision logic in GXDhandler
 */
public class GxdMatrixHandlerTest {

	GxdMatrixHandler handler;
	
	// 
	final String TERMID1 = "TEST_TERM_ID:1";
	final String TERMID2 = "TEST_TERM_ID:2";
	final String CHILD_TERM1 = "TEST_CHILD_ID:1";
	final String GRANDCHILD_TERM1 = "GRANDCHILD:1";
	final String MOUSEID = "EMAPA:25765";
	final String MOUSE_CHILD_ID = "MOUSE_CHILD_ID";
	final String ORGAN_SYSTEM_ID = "EMAPA:16103";
	final String ORGAN_SYSTEM_CHILD_ID = "ORGAN_SYSTEM_CHILD_ID";
	final String EMAPS_TERM1 = "TEST_EMAPS_TERM_ID:1";
	final Integer EMAPS_TERM1_KEY = 1;
	
	@Before
	public void setup()
	{
		handler = new GxdMatrixHandler();
		// set any mock objects that are normally autowired
		
		VocabularyFinder mockVocabFinder = getMockVocabFinder();
		handler.setVocabFinder(mockVocabFinder);
		DagMatrixRowOpener rowOpener = new DagMatrixRowOpener();
		rowOpener.setVocabFinder(mockVocabFinder);
		handler.setRowOpener(rowOpener);
	}
	
	/*
	 * Test the different Tissue matrix views
	 */
	@Test
	public void testGetParentTermsStructureIDQuery() 
	{
		GxdQueryForm query = new GxdQueryForm();
		query.setStructureID(TERMID1);
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixRowIds(parentRows,TERMID1);
		// verify the child term comes back
		GxdMatrixRow child = parentRows.get(0).getChildren().get(0);
		Assert.assertEquals(CHILD_TERM1,child.getRid());
		// verify it is only open 1 level
		Assert.assertEquals(0,child.getChildren().size());
	}
	@Test
	public void testGetParentTermsStructureIDQueryWithOtherParams() 
	{
		GxdQueryForm query = newFleshedOutQuery();
		query.setStructureID(TERMID1);
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixRowIds(parentRows,TERMID1);
		// verify the child term comes back
		GxdMatrixRow child = parentRows.get(0).getChildren().get(0);
		Assert.assertEquals(CHILD_TERM1,child.getRid());
	}
	@Test
	public void testGetParentTermsStructureIDQueryNotUsed() 
	{
		GxdQueryForm query = newFleshedOutQuery();
		query.setStructureID("");
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixHighLevelRows(parentRows);
	}
	
	@Test
	public void testGetParentTermsDifferentialStructureQuery() 
	{
		GxdQueryForm query = new GxdQueryForm();
		query.setStructureID(TERMID1);
		query.setDifStructureID(TERMID2);
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixRowIds(parentRows,TERMID1,TERMID2);
	}
	@Test
	public void testGetParentTermsDifferentialStageQuery() 
	{
		GxdQueryForm query =  new GxdQueryForm();
		query.setTheilerStage(Arrays.asList(12));
		query.setDifTheilerStage(Arrays.asList(13));
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixHighLevelRows(parentRows);
	}
	@Test
	public void testGetParentTermsDifferentialStructureAndStageQuery() 
	{
		GxdQueryForm query = new GxdQueryForm();
		query.setStructureID(TERMID1);
		query.setDifStructureID(TERMID2);
		query.setTheilerStage(Arrays.asList(12));
		query.setDifTheilerStage(Arrays.asList(13));
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixRowIds(parentRows,TERMID1,TERMID2);
	}
	@Test
	public void testGetParentTermsDifferentialStructureAndStageQuerySameStructure() 
	{
		GxdQueryForm query = new GxdQueryForm();
		query.setStructureID(TERMID1);
		query.setDifStructureID(TERMID1);
		query.setTheilerStage(Arrays.asList(12));
		query.setDifTheilerStage(Arrays.asList(13));
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixRowIds(parentRows,TERMID1);
		// verify term has no children
		Assert.assertEquals(0,parentRows.get(0).getChildren().size());
	}
	@Test
	public void testGetParentTermsDifferentialQueryNotUsed() 
	{
		GxdQueryForm query = newFleshedOutQuery();
		query.setStructureID("");
		query.setDifStructureID("");
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixHighLevelRows(parentRows);
	}
	
	@Test
	public void testGetParentTermsExpandRowQueryEmpty() 
	{
		GxdQueryForm query = new GxdQueryForm();
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,TERMID1,null);
		assertMatrixRowIds(parentRows,CHILD_TERM1);
	}
	@Test
	public void testGetParentTermsExpandRowQueryWithNoStructure() 
	{
		GxdQueryForm query = this.newFleshedOutQuery();
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,TERMID1,null);
		assertMatrixRowIds(parentRows,CHILD_TERM1);
	}
	@Test
	public void testGetParentTermsExpandRowQueryWithStructure() 
	{
		GxdQueryForm query = this.newFleshedOutQuery();
		query.setStructureID(TERMID1);
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,TERMID1,null);
		assertMatrixRowIds(parentRows,CHILD_TERM1);
	}
	@Test
	public void testGetParentTermsExpandRowQueryWithChildStructure() 
	{
		GxdQueryForm query = this.newFleshedOutQuery();
		query.setStructureID(CHILD_TERM1);
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,TERMID1,null);
		assertMatrixRowIds(parentRows,CHILD_TERM1);
	}
	@Test
	public void testGetParentTermsExpandRowQueryDifferential() 
	{
		GxdQueryForm query = this.newFleshedOutQuery();
		query.setStructureID(TERMID1);
		query.setDifStructureID(TERMID2);
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,TERMID1,null);
		assertMatrixRowIds(parentRows,CHILD_TERM1);
	}
	@Test
	public void testGetParentTermsExpandRowQueryDifferentialChildStructure() 
	{
		GxdQueryForm query = this.newFleshedOutQuery();
		query.setStructureID(TERMID1);
		query.setDifStructureID(CHILD_TERM1);
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,TERMID1,null);
		assertMatrixRowIds(parentRows,CHILD_TERM1);
	}
	
	@Test
	public void testGetParentTermsStructureQueryEMAPS() 
	{
		GxdQueryForm query = new GxdQueryForm();
		query.setStructureID(EMAPS_TERM1);
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixRowIds(parentRows,TERMID1);
		// verify the child term comes back
		GxdMatrixRow child = parentRows.get(0).getChildren().get(0);
		Assert.assertEquals(CHILD_TERM1,child.getRid());
	}
	
	@Test
	public void testGetParentTermsFilterQuery() 
	{
		GxdQueryForm query = new GxdQueryForm();
		query.setStructureIDFilter(Arrays.asList(TERMID1));
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixRowIds(parentRows,TERMID1);
		// verify that no child terms come back
		Assert.assertEquals(0,parentRows.get(0).getChildren().size());
	}
	
	@Test
	public void testGetParentTermsMultipleFiltersQuery() 
	{
		GxdQueryForm query = new GxdQueryForm();
		query.setStructureIDFilter(Arrays.asList(TERMID1,TERMID2));
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixRowIds(parentRows,TERMID1,TERMID2);
		// verify that no child terms come back
		Assert.assertEquals(0,parentRows.get(0).getChildren().size());
		Assert.assertEquals(0,parentRows.get(1).getChildren().size());
	}
	
	@Test
	public void testGetParentTermsFilterAndStructureQuery() 
	{
		GxdQueryForm query = new GxdQueryForm();
		query.setStructureID(TERMID1);
		query.setStructureIDFilter(Arrays.asList(TERMID2));
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixRowIds(parentRows,TERMID2);
	}

	@Test
	public void testGetParentTermsFilterAndOthersQuery() 
	{
		GxdQueryForm query = newFleshedOutQuery();
		query.setStructureID(""); // blank out structure query
		query.setStructureIDFilter(Arrays.asList(TERMID2));
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixRowIds(parentRows,TERMID2);
	}
	
	@Test
	public void testGetParentTermsFilterAndDifferentialQuery() 
	{
		GxdQueryForm query = new GxdQueryForm();
		query.setStructureID(TERMID1);
		query.setDifStructureID(TERMID2);
		query.setStructureIDFilter(Arrays.asList(CHILD_TERM1));
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		assertMatrixRowIds(parentRows,CHILD_TERM1);
	}
	
	@Test
	public void testGetParentTermsStructureIDQueryWithRowsOpen() 
	{
		GxdQueryForm query = new GxdQueryForm();
		query.setStructureID(TERMID1);
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(
				query,null,Arrays.asList(TERMID1+"|"+CHILD_TERM1)
		);
		assertMatrixRowIds(parentRows,TERMID1);
		// verify the child term comes back
		GxdMatrixRow child = parentRows.get(0).getChildren().get(0);
		Assert.assertEquals(CHILD_TERM1,child.getRid());
		// verify it is opened 2 levels now
		Assert.assertEquals(1,child.getChildren().size());
	}
	
	@Test
	public void testGetParentTermsAnnotatedStructureKey() 
	{
		GxdQueryForm query = new GxdQueryForm();
		query.setAnnotatedStructureKey(EMAPS_TERM1_KEY.toString());
		
		List<GxdMatrixRow> parentRows = handler.getParentTermsToDisplay(query,null,null);
		
		assertMatrixRowIds(parentRows,TERMID1);
		// verify child terms come back
		Assert.assertNotEquals(0,parentRows.get(0).getChildren().size());
	}
	
	
	/*
	 * test getFlatRows()
	 */
	@Test
	public void testGetFlatRowsOneRow()
	{
		List<GxdMatrixRow> rowTree = rows(
			row("row1")
		);
		List<GxdMatrixRow> flatRows = handler.getFlatTermList(rowTree);
		assertFlatRowsIds(flatRows,"row1");
	}
	
	@Test
	public void testGetFlatRowsDuplicateIds()
	{
		List<GxdMatrixRow> rowTree = rows(
			row("row1"),
			row("row1")
		);
		List<GxdMatrixRow> flatRows = handler.getFlatTermList(rowTree);
		assertFlatRowsIds(flatRows,"row1");
	}
	
	@Test
	public void testGetFlatRowsSimpleNest()
	{
		List<GxdMatrixRow> rowTree = rows(
			row("row1", rows(
					row("child1"),
					row("child2")
				)
			)
		);
		List<GxdMatrixRow> flatRows = handler.getFlatTermList(rowTree);
		assertFlatRowsIds(flatRows,"child1","child2","row1");
	}
	
	@Test
	public void testGetFlatRowsDuplicateAndUnevenPaths()
	{
		List<GxdMatrixRow> rowTree = rows(
			row("b1", rows(
					row("c", rows(
							row("d")
						)
					)
				)
			),
			row("b2", rows(
					row("d", rows(
							row("e")
						)
					)
				)
			)
		);
		List<GxdMatrixRow> flatRows = handler.getFlatTermList(rowTree);
		assertFlatRowsIds(flatRows,"b1","b2","c","d","e");
	}
	
	/*
	 * helper asserts
	 */
	private void assertMatrixRowIds(List<GxdMatrixRow> rowsActual,String... rowIdsExpected)
	{
		// assert that rows are same length;
		Assert.assertEquals(rowIdsExpected.length,rowsActual.size());
		// now verify each row
		for(int i=0;i<rowIdsExpected.length;i++)
		{
			Assert.assertEquals(rowIdsExpected[i],rowsActual.get(i).getRid());
		}
	}
	private void assertMatrixHighLevelRows(List<GxdMatrixRow> rowsActual)
	{
		// make sure we just have mouse coming back
		assertMatrixRowIds(rowsActual,MOUSEID);
		
		// verify its children
		GxdMatrixRow mouse = rowsActual.get(0);
		assertMatrixRowIds(mouse.getChildren(),MOUSE_CHILD_ID,ORGAN_SYSTEM_ID);
		
		GxdMatrixRow organSystem = mouse.getChildren().get(1);
		assertMatrixRowIds(organSystem.getChildren(),ORGAN_SYSTEM_CHILD_ID);
	}
	
	private void assertFlatRowsIds(List<GxdMatrixRow> flatRows, String... idsExpected)
	{
		List<String> foundIds = new ArrayList<String>();
		for (GxdMatrixRow row : flatRows)
		{
			foundIds.add(row.getRid());
		}
		java.util.Collections.sort(foundIds);
		Assert.assertEquals(StringUtils.join(idsExpected,","), StringUtils.join(foundIds,","));
	}
	
	/*
	 * helper methods + mock objects
	 */
	private GxdQueryForm newFleshedOutQuery()
	{
		GxdQueryForm query = new GxdQueryForm();
		query.setNomenclature("test");
		query.setAge(Arrays.asList("test"));
		query.setTheilerStage(Arrays.asList(1));
		query.setVocabTerm("term");
		query.setAnnotationId("test");
		query.setAssayType(Arrays.asList("test"));
		query.setStructureID("testID1");
		query.setDetected("test");
		return query;
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
		
		lookup.put(TERMID1,testTerm1);
		lookup.put(TERMID2,testTerm2);
		lookup.put(CHILD_TERM1,testChild1);
		
		VocabTerm mouse = term(MOUSEID,"mouse");
		VocabTerm organSystem = term(ORGAN_SYSTEM_ID,"organ system");
		VocabTerm mouseChild = term(MOUSE_CHILD_ID,"mouseChild");
		VocabTerm osChild = term(ORGAN_SYSTEM_CHILD_ID,"organSystemChild");
		mouse.setChildren(Arrays.asList(mouseChild,organSystem));
		organSystem.setChildren(Arrays.asList(osChild));
		
		lookup.put(MOUSEID,mouse);
		lookup.put(ORGAN_SYSTEM_ID,organSystem);
		
		// make one EMAPS term, and set TERM1 as its EMAPA term
		VocabTerm emapsTerm1 = term(EMAPS_TERM1,"emaps1");
		emapsTerm1.setVocabName("EMAPS");
		emapsTerm1.setTermKey(EMAPS_TERM1_KEY);
		VocabTermEmapInfo emapInfo = new VocabTermEmapInfo();
		emapInfo.setEmapaTerm(testTerm1);
		emapsTerm1.setEmapInfo(emapInfo);
		
		lookup.put(EMAPS_TERM1,emapsTerm1);
		lookup.put(EMAPS_TERM1_KEY.toString(),emapsTerm1);
		
		return new MockVocabularyFinder(lookup);
	}
	
	private GxdMatrixRow row(String id)
	{
		GxdMatrixRow row = new GxdMatrixRow();
		row.setRid(id);
		return row;
	}
	private GxdMatrixRow row(String id, List<GxdMatrixRow> children)
	{
		GxdMatrixRow row = new GxdMatrixRow();
		row.setRid(id);
		for (GxdMatrixRow child : children)
		{
			row.addChild(child);
		}
		return row;
	}
	
	private List<GxdMatrixRow> rows(GxdMatrixRow...gxdMatrixRows)
	{
		return Arrays.asList(gxdMatrixRows);
	}
	
	private VocabTerm term(String id, String term)
	{
		return term(id,term,1,99);
	}
	private VocabTerm term(String id, String term, int startStage, int endStage)
	{
		VocabTerm vt = new VocabTerm();
		vt.setPrimaryId(id);
		vt.setTerm(term);
		
		// set stage range
		VocabTermEmapInfo ei = new VocabTermEmapInfo();
		ei.setStartStage(startStage);
		ei.setEndStage(endStage);
		vt.setEmapInfo(ei);
		
		return vt;
	}
}
