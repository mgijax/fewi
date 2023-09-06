package org.mgi.fewi.highlight;

import java.util.Arrays;
import java.util.Set;

import org.jax.mgi.fewi.highlight.RecombinaseHighlightInfo;
import org.jax.mgi.fewi.searchUtil.entities.SolrCreSystemHighlight;
import org.junit.Assert;
import org.junit.Test;

public class RecombinaseHighlightInfoTestCase {

	@Test
	public void testSystemHighlightDetected() {
		// detected systems
		SolrCreSystemHighlight hl1 = mockSystemHighlight("1",true, "nervous system", "embryo");
		// not detected systems
		SolrCreSystemHighlight hl2 = mockSystemHighlight("1",false, "skeletal");
		
		RecombinaseHighlightInfo highlightInfo = new RecombinaseHighlightInfo();
		highlightInfo.addSystemHighlights(Arrays.asList(hl1, hl2));
		
		
		Set<String> actualHighlights = highlightInfo.getDetectedHighlights("1");
		Assert.assertEquals(2, actualHighlights.size());
		assertContains(actualHighlights, "nervous system");
		assertContains(actualHighlights, "embryo");
	}
	
	@Test
	public void testSystemHighlightNotDetected() {
		// detected systems
		SolrCreSystemHighlight hl1 = mockSystemHighlight("1",true, "nervous system", "embryo");
		// not detected systems
		SolrCreSystemHighlight hl2 = mockSystemHighlight("1",false, "skeletal");
		
		RecombinaseHighlightInfo highlightInfo = new RecombinaseHighlightInfo();
		highlightInfo.addSystemHighlights(Arrays.asList(hl1, hl2));
		
		
		Set<String> actualHighlights = highlightInfo.getNotDetectedHighlights("1");
		Assert.assertEquals(1, actualHighlights.size());
		assertContains(actualHighlights, "skeletal");
	}
	
	@Test
	public void testSystemHighlightMultipleAlleles() {
		// detected systems
		SolrCreSystemHighlight hl1 = mockSystemHighlight("1",true, "nervous system", "embryo");
		// not detected systems
		SolrCreSystemHighlight hl2 = mockSystemHighlight("2",false, "skeletal");
		
		RecombinaseHighlightInfo highlightInfo = new RecombinaseHighlightInfo();
		highlightInfo.addSystemHighlights(Arrays.asList(hl1, hl2));
		
		// test allele 1
		Set<String> actualHighlights = highlightInfo.getDetectedHighlights("1");
		Assert.assertEquals(2, actualHighlights.size());
		assertContains(actualHighlights, "nervous system");
		assertContains(actualHighlights, "embryo");
		
		// test allele 2
		actualHighlights = highlightInfo.getNotDetectedHighlights("2");
		Assert.assertEquals(1, actualHighlights.size());
		assertContains(actualHighlights, "skeletal");
	}
	
	
	/*
	 * Helpers
	 */
	
	private void assertContains(Set<String> highlights, String system) {
		String msg = "highlights do not contain " + system;
		Assert.assertTrue(msg, highlights.contains(system));
	}
	
	private SolrCreSystemHighlight mockSystemHighlight(String alleleKey,
				boolean detected, String...systems ) {
		
		SolrCreSystemHighlight systemHighlight = new SolrCreSystemHighlight();
		
		systemHighlight.setAlleleKey(alleleKey);
		systemHighlight.setDetected(detected);
		systemHighlight.setSystems(Arrays.asList(systems));
		
		return systemHighlight;
	}

}
