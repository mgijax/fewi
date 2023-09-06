package org.mgi.fewi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.util.FewiUtil;
import org.junit.Assert;
import org.junit.Test;

public class FewiUtilTest {

	/*
	 * getBatches()
	 */
	@Test
	public void testGetBatchesEmptyList() {
		List<List<String>> batches = FewiUtil.getBatches(new ArrayList<String>(), 100);
		Assert.assertEquals(0,batches.size());
	}
	@Test
	public void testGetBatchesOneElement() {
		List<List<String>> batches = FewiUtil.getBatches(Arrays.asList("test"), 100);
		Assert.assertEquals(1,batches.size());
		Assert.assertEquals(1,batches.get(0).size());
	}
	
	@Test
	public void testGetBatchesTwoSmallBatches() {
		List<List<String>> batches = FewiUtil.getBatches(Arrays.asList("test","test2"), 1);
		Assert.assertEquals(2,batches.size());
	}
	
	@Test
	public void testGetBatchesTwoBiggerBatches() {
		List<List<Integer>> batches = FewiUtil.getBatches(Arrays.asList(1,2,3,4,5,6,7,8,9,10), 5);
		Assert.assertEquals(2,batches.size());
	}
	
	@Test
	public void testGetBatchesUnevenBatches() {
		List<List<Integer>> batches = FewiUtil.getBatches(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11), 5);
		Assert.assertEquals(3,batches.size());
		Assert.assertEquals(1,batches.get(2).size());
	}
	@Test
	public void testGetBatchesUnevenBatchesUpperBound() {
		List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9);
		List<List<Integer>> batches = FewiUtil.getBatches(list, 5);
		Assert.assertEquals(2,batches.size());
		Assert.assertEquals(4,batches.get(1).size());
	}
}
