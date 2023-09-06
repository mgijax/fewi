package org.mgi.fewi.matrix;

import org.jax.mgi.fewi.matrix.GxdMatrixCell;
import org.junit.Assert;
import org.junit.Test;

public class GxdMatrixCellTest {

	private static String YES = "Yes";
	private static String NO = "No";
	private static String AMBIGUOUS = "Ambiguous";
	private static String NOTSPECIFIED = "Not Specified";

	/*
	 * test aggregate()
	 */
	@Test
	public void testAggregateYesYes() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", YES, false);
		cell.aggregate(YES, false);
		Assert.assertEquals(2, cell.getDetected());
	}

	@Test
	public void testAggregateNoYes() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", NO, false);
		cell.aggregate(YES, false);
		Assert.assertEquals(1, cell.getDetected());
	}

	@Test
	public void testAggregateAmbiguousYes() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", AMBIGUOUS, false);
		cell.aggregate(YES, false);
		Assert.assertEquals(1, cell.getDetected());
	}

	@Test
	public void testAggregateNotSpecifiedYes() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", NOTSPECIFIED,
				false);
		cell.aggregate(YES, false);
		Assert.assertEquals(1, cell.getDetected());
	}

	@Test
	public void testAggregateNoNo() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", NO, false);
		cell.aggregate(NO, false);
		Assert.assertEquals(2, cell.getNotDetected());
	}

	@Test
	public void testAggregateYesNo() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", YES, false);
		cell.aggregate(NO, false);
		Assert.assertEquals(1, cell.getNotDetected());
	}

	@Test
	public void testAggregateAmbiguousNo() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", AMBIGUOUS, false);
		cell.aggregate(NO, false);
		Assert.assertEquals(1, cell.getNotDetected());
	}

	@Test
	public void testAggregateNotSpecifiedNo() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", NOTSPECIFIED,
				false);
		cell.aggregate(NO, false);
		Assert.assertEquals(1, cell.getNotDetected());
	}

	@Test
	public void testAggregateAmbiguousAmbiguous() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", AMBIGUOUS, false);
		cell.aggregate(AMBIGUOUS, false);
		Assert.assertEquals(2, cell.getAmbiguous());
	}

	@Test
	public void testAggregateYesAmbiguous() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", YES, false);
		cell.aggregate(AMBIGUOUS, false);
		Assert.assertEquals(1, cell.getAmbiguous());
	}

	@Test
	public void testAggregateNoAmbiguous() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", NO, false);
		cell.aggregate(AMBIGUOUS, false);
		Assert.assertEquals(1, cell.getAmbiguous());
	}

	@Test
	public void testAggregateNotSpecifiedAmbiguous() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", NOTSPECIFIED,
				false);
		cell.aggregate(AMBIGUOUS, false);
		Assert.assertEquals(2, cell.getAmbiguous());
	}

	@Test
	public void testAggregateAmbiguousNotSpecified() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", AMBIGUOUS, false);
		cell.aggregate(NOTSPECIFIED, false);
		Assert.assertEquals(2, cell.getAmbiguous());
	}

	@Test
	public void testAggregateYesNotSpecified() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", YES, false);
		cell.aggregate(NOTSPECIFIED, false);
		Assert.assertEquals(1, cell.getAmbiguous());
	}

	@Test
	public void testAggregateNoNotSpecified() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", NO, false);
		cell.aggregate(NOTSPECIFIED, false);
		Assert.assertEquals(1, cell.getAmbiguous());
	}

	@Test
	public void testAggregateNotSpecifiedNotSpecified() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", NOTSPECIFIED,
				false);
		cell.aggregate(NOTSPECIFIED, false);
		Assert.assertEquals(2, cell.getAmbiguous());
	}

	@Test
	public void testAggregateYesChild() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", YES, false);
		cell.aggregate(YES, true);
		Assert.assertEquals(2, cell.getDetected());
		Assert.assertEquals(0, cell.getChildren());
	}

	@Test
	public void testAggregateNoChild() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", YES, false);
		cell.aggregate(NO, true);
		Assert.assertEquals(0, cell.getNotDetected());
		Assert.assertEquals(1, cell.getChildren());
	}

	@Test
	public void testAggregateAmbiguousChild() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", YES, false);
		cell.aggregate(AMBIGUOUS, true);
		Assert.assertEquals(0, cell.getAmbiguous());
		Assert.assertEquals(1, cell.getChildren());
	}

	@Test
	public void testAggregateNotSpecified() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", YES, false);
		cell.aggregate(NOTSPECIFIED, true);
		Assert.assertEquals(0, cell.getAmbiguous());
		Assert.assertEquals(1, cell.getChildren());
	}

	/*
	 * Test aggregate with count
	 */
	@Test
	public void testAggregateYesWithCount() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", YES, false);
		cell.aggregate(YES, 20, false);
		Assert.assertEquals(21, cell.getDetected());
		Assert.assertEquals(0, cell.getChildren());
	}

	@Test
	public void testAggregateNoChildWithCount() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", NO, false);
		cell.aggregate(NO, 20, true);
		Assert.assertEquals(1, cell.getNotDetected());
		Assert.assertEquals(20, cell.getChildren());
	}

	/*
	 * Test new cell with count
	 */
	@Test
	public void testNewCellWithYesCount() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", YES, 20, false);
		Assert.assertEquals(20, cell.getDetected());
	}

	@Test
	public void testNewCellWithAmbiguousChildCount() {
		GxdMatrixCell cell = new GxdMatrixCell("brain", "28", AMBIGUOUS, 20,
				true);
		Assert.assertEquals(20, cell.getChildren());
	}
}
