package org.jax.mgi.fewi.test.concordion;

import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionBuilder;
import org.jax.mgi.fewi.test.data.DynamicTestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-ci.xml"})
@TransactionConfiguration
@Transactional
public class BaseConcordionTest {

    /**
     * 
     * Use spring test runner to setup and run concordion unit tests
     * 
     * @throws Exception
     */
	@Test
    public void run() throws Exception {
		// kicks off concordion process that runs your actual tests
		// Make custom extensions available to concordion
	    System.setProperty("concordion.extensions", "org.jax.mgi.fewi.test.concordion.FewiConcordionExtension");
	    
        ResultSummary resultSummary = new ConcordionBuilder().build().process(this);
        resultSummary.print(System.out, this);
        resultSummary.assertIsSatisfied(this);
		//ConcordionUtils.initConcordionTestCase(this);
    }
	
	// Makes the dynamic test data available to concordion tests
	public static String get(String id)
	{
		return DynamicTestData.get(id);
	}
}
