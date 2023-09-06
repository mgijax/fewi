package org.mgi.fewi.summary;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jax.mgi.fewi.summary.RecombinaseSummary;
import org.junit.Assert;
import org.junit.Test;



/**
 * Testing any logic methods in the recombinase summary object
 */
public class RecombinaseSummaryTest {
	

	@Test
	public void testIsSystemHighlighted() 
	{
		// set up mock object
		Set<String> highlights = new HashSet<String>(Arrays.asList("tail",
						"reproductive system",
						"cavities and their linings"
					));
		
		Assert.assertTrue("single word system should match",
				RecombinaseSummary.isSystemHighlighted("tail",highlights));
		Assert.assertTrue("two word system should match",
				RecombinaseSummary.isSystemHighlighted("reproductive system",highlights));
		Assert.assertTrue("multiple word system should match",
				RecombinaseSummary.isSystemHighlighted("cavities and their linings",highlights));
	}
	
	@Test
	public void testIsNotHighlighted() 
	{
		// set up mock object
		Set<String> highlights = new HashSet<String>(Arrays.asList("tail",
				"reproductive system",
				"cavities and their linings"
			));
		
		Assert.assertFalse("blank should not match",
				RecombinaseSummary.isSystemHighlighted("",highlights));
		Assert.assertFalse("not existing system should not match",
				RecombinaseSummary.isSystemHighlighted("blah blah",highlights));
		Assert.assertFalse("solr field should not match",
				RecombinaseSummary.isSystemHighlighted("reproductiveSystem",highlights));
	}

}
