package gxd;

import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdHttpQuery;
import org.jax.mgi.fewi.test.mock.MockJSONGXDAssayResult;

public class DetectionSearchTest extends BaseConcordionTest {
    /*
     * Returns the word "yes" or "no"
     * detected can be "either", "detected", or "not detected"
     */
    public String containsDetectionLevelByNomen(String detected,String nomen, String level) throws Exception
    {
    	String yes = "yes", no = "no";
    	MockGxdHttpQuery mq = getMockQuery().gxdHttp();
    	mq.pageSize=1000;
    	mq.setNomenclature(nomen);
    	if(detected.equalsIgnoreCase("detected")) mq.setDetected("Yes");
    	else if(detected.equalsIgnoreCase("not detected")) mq.setDetected("No");
    	
    	JsonSummaryResponse<MockJSONGXDAssayResult> results = mq.getAssayResults();
    	for(MockJSONGXDAssayResult r : results.getSummaryRows())
    	{
    		if(r.getDetectionLevel().equalsIgnoreCase(level))
    		{
    			return yes;
    		}
    	}
    	return no;
    }

}
