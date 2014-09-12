package unittest.fewi.matrix;

import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.matrix.GxdGeneMatrixPopup;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMatrixResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that the popup correctly rolls up its counts of results based on detection level
 */
public class GxdGeneMatrixPopupTest {

	private final String NO = "No";
	private final String YES = "Yes";
	private final String AMBIGUOUS = "Ambiguous";
	private final String NOT_SPECIFIED = "Not Specified";

	private final String PARENT_ID = "parentId";
	private final String CHILD_ID = "childId";

	private GxdGeneMatrixPopup popup;

	@Before
	public void setup() {
		this.popup = new GxdGeneMatrixPopup(PARENT_ID,"parent");
		//this.popup = new GxdGeneMatrixPopup();
	}

	@Test
	public void testNoResults() {
		popup.setAssayResultList(results());
		Assert.assertEquals(Int(0),popup.getCountPosResults());
		Assert.assertEquals(Int(0),popup.getCountAmbResults());
		Assert.assertEquals(Int(0),popup.getCountNegResults());
	}

	@Test
	public void testOnePositive() {
		List<SolrGxdMatrixResult> results = results(result("Pax6",YES,PARENT_ID));
		popup.setAssayResultList(results);
		Assert.assertEquals(Int(1),popup.getCountPosResults());
		Assert.assertEquals(Int(0),popup.getCountAmbResults());
		Assert.assertEquals(Int(0),popup.getCountNegResults());
	}

	@Test
	public void testOneAmbiguousParent() {
		List<SolrGxdMatrixResult> results = results(result("Pax6",AMBIGUOUS,PARENT_ID));
		popup.setAssayResultList(results);
		Assert.assertEquals(Int(0),popup.getCountPosResults());
		Assert.assertEquals(Int(1),popup.getCountAmbResults());
		Assert.assertEquals(Int(0),popup.getCountNegResults());
	}

	@Test
	public void testOneNotSpecifiedParent() {
		List<SolrGxdMatrixResult> results = results(result("Pax6",NOT_SPECIFIED,PARENT_ID));
		popup.setAssayResultList(results);
		Assert.assertEquals(Int(0),popup.getCountPosResults());
		Assert.assertEquals(Int(1),popup.getCountAmbResults());
		Assert.assertEquals(Int(0),popup.getCountNegResults());
	}

	@Test
	public void testOneNegativeParent() {
		List<SolrGxdMatrixResult> results = results(result("Pax6",NO,PARENT_ID));
		popup.setAssayResultList(results);
		Assert.assertEquals(Int(0),popup.getCountPosResults());
		Assert.assertEquals(Int(0),popup.getCountAmbResults());
		Assert.assertEquals(Int(1),popup.getCountNegResults());
	}

	@Test
	public void testOnePositiveChild() {
		List<SolrGxdMatrixResult> results = results(result("Pax6",YES,CHILD_ID));
		popup.setAssayResultList(results);
		Assert.assertEquals(Int(1),popup.getCountPosResults());
		Assert.assertEquals(Int(0),popup.getCountAmbResults());
		Assert.assertEquals(Int(0),popup.getCountNegResults());
	}

	@Test
	public void testOneAmbiguousChild() {
		List<SolrGxdMatrixResult> results = results(result("Pax6",AMBIGUOUS,CHILD_ID));
		popup.setAssayResultList(results);
		Assert.assertEquals(Int(0),popup.getCountPosResults());
		Assert.assertEquals(Int(0),popup.getCountAmbResults());
		Assert.assertEquals(Int(0),popup.getCountNegResults());
	}

	@Test
	public void testOneNotSpecifiedChild() {
		List<SolrGxdMatrixResult> results = results(result("Pax6",NOT_SPECIFIED,CHILD_ID));
		popup.setAssayResultList(results);
		Assert.assertEquals(Int(0),popup.getCountPosResults());
		Assert.assertEquals(Int(0),popup.getCountAmbResults());
		Assert.assertEquals(Int(0),popup.getCountNegResults());
	}

	@Test
	public void testOneNegativeChild() {
		List<SolrGxdMatrixResult> results = results(result("Pax6",NO,CHILD_ID));
		popup.setAssayResultList(results);
		Assert.assertEquals(Int(0),popup.getCountPosResults());
		Assert.assertEquals(Int(0),popup.getCountAmbResults());
		Assert.assertEquals(Int(0),popup.getCountNegResults());
	}

	@Test
	public void testMultipleChildResults() {
		List<SolrGxdMatrixResult> results = results(result("Pax6",YES,CHILD_ID),
				result("Pax7",YES,CHILD_ID),
				result("Pax6",NO,CHILD_ID),
				result("Pax6",AMBIGUOUS,CHILD_ID),
				result("Pax6",NOT_SPECIFIED,CHILD_ID));
		popup.setAssayResultList(results);
		Assert.assertEquals(Int(2),popup.getCountPosResults());
		Assert.assertEquals(Int(0),popup.getCountAmbResults());
		Assert.assertEquals(Int(0),popup.getCountNegResults());
	}
	@Test
	public void testMultipleParentResults() {
		List<SolrGxdMatrixResult> results = results(result("Pax6",YES,PARENT_ID),
				result("Pax7",YES,PARENT_ID),
				result("Pax6",NO,PARENT_ID),
				result("Pax7",NO,PARENT_ID),
				result("Pax6",AMBIGUOUS,PARENT_ID),
				result("Pax7",NOT_SPECIFIED,PARENT_ID));
		popup.setAssayResultList(results);
		Assert.assertEquals(Int(2),popup.getCountPosResults());
		Assert.assertEquals(Int(2),popup.getCountAmbResults());
		Assert.assertEquals(Int(2),popup.getCountNegResults());
	}


	/*
	 * helper functions
	 */
	private Integer Int(int i)
	{
		return new Integer(i);
	}
	private List<SolrGxdMatrixResult> results(SolrGxdMatrixResult... resultList)
	{
		return Arrays.asList(resultList);
	}

	private SolrGxdMatrixResult result(String symbol,String detectionLevel, String structureId)
	{
		SolrGxdMatrixResult result = new SolrGxdMatrixResult();
		result.setGeneSymbol(symbol);
		result.setDetectionLevel(detectionLevel);
		result.setTheilerStage(1);
		result.setStructureId(structureId);

		return result;
	}
}
