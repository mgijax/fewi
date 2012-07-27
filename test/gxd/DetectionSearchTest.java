package gxd;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdHttpQuery;
import org.jax.mgi.fewi.test.mock.MockJSONGXDAssayResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public class DetectionSearchTest extends BaseConcordionTest {
	
    @Autowired
    private AnnotationMethodHandlerAdapter handler;

    // The class being tested is autowired via spring's DI
    @Autowired
    private GXDController gxdController;
    
    /*
     * Returns the word "yes" or "no"
     * detected can be "either", "detected", or "not detected"
     */
    public String containsDetectionLevelByNomen(String detected,String nomen, String level) throws Exception
    {
    	String yes = "yes", no = "no";
    	MockGxdHttpQuery mq = new MockGxdHttpQuery(handler,gxdController);
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
