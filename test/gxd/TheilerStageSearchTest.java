package gxd;


import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mgi.frontend.datamodel.GxdAssayResult;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.jax.mgi.fewi.test.mock.MockGxdHttpQuery;
import org.jax.mgi.fewi.test.mock.MockJSONGXDAssayResult;
import org.jax.mgi.fewi.test.mock.MockJSONGXDMarker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;


public class TheilerStageSearchTest extends BaseConcordionTest {

    @Autowired
    private AnnotationMethodHandlerAdapter handler;

    // The class being tested is autowired via spring's DI
    @Autowired
    private GXDController gxdController;

	/*
	 * Helper functions for mocking queries
	 */

    private List<Integer> convertStringToTS(String stages)
    {
		List<Integer> ts = new ArrayList<Integer>();
		try {
	    	if (stages.equals("All")) {
	    		for (int i = 1; i<=28; i++) {
	    			if (i != 27) {
	            		ts.add(new Integer(i));
	    			}    			
	    		}    		
	    	} else {
	    		for (String s : stages.replaceAll(" ", "").split(",")) {
	        		ts.add(new Integer(s));    			
	    		}
	    	}
		} catch (Exception e) {
			// Can't convert the passed in string to a list of 
			// stages
    		fail("Call to getGeneCountByTS with an invalid list of stages: ("+stages+")");
    	}
		return ts;
    }

    public SearchResults<SolrAssayResult> getResultsByStage(Integer stage) throws Exception
	{
		MockGxdControllerQuery mq = new MockGxdControllerQuery(gxdController);
		mq.setTheilerStage(stage);
		return mq.getAssayResults();
	}
	
	
	
	public JsonSummaryResponse<MockJSONGXDAssayResult> mockResultQuery(String nomen) throws Exception
    {
    	MockGxdHttpQuery mq = new MockGxdHttpQuery(handler, gxdController);
    	mq.setNomenclature(nomen);
    	return mq.getAssayResults();
    }
	
    public JsonSummaryResponse<MockJSONGXDAssayResult> mockResultQuery(List<Integer> theilerStages) throws Exception
    {
    	MockGxdHttpQuery mq = new MockGxdHttpQuery(handler, gxdController);
    	mq.setTheilerStage(theilerStages);
    	return mq.getAssayResults();
    }

    public JsonSummaryResponse<MockJSONGXDAssayResult> mockResultQuery(String nomen, List<Integer> theilerStages) throws Exception
    {
    	MockGxdHttpQuery mq = new MockGxdHttpQuery(handler, gxdController);
    	mq.setNomenclature(nomen);
    	mq.setTheilerStage(theilerStages);
    	return mq.getAssayResults();
    }

    /**
     * Get genes by theiler stage
     * @param theilerStages
     * @return
     * @throws Exception
     */
    public JsonSummaryResponse<MockJSONGXDMarker> mockGeneQuery(List<Integer> theilerStages) throws Exception
    {
    	MockGxdHttpQuery mq = new MockGxdHttpQuery(handler, gxdController);
    	mq.setTheilerStage(theilerStages);
    	return mq.getGenes();
    }
    

    /**
     * Return the count of GXD assay results associated with a gene
     * symbol
     * 
     * @param query The gene symbol
     * @return count of the number of assay results 
     * @throws Exception
     */
    public int getResultCountByGeneSymbol(String nomen) throws Exception
    {
    	JsonSummaryResponse<MockJSONGXDAssayResult> results = mockResultQuery(nomen);
    	return results.getTotalCount();
    }
    
    /**
     * Return the count of GXD genes associated with a theiler stage
     * 
     * @param query The theiler stage(s)
     * @return count of the number of assay results 
     * @throws Exception
     */
    public int getGeneCountByTS(/* any stage */) throws Exception
    {
    	JsonSummaryResponse<MockJSONGXDMarker> results = mockGeneQuery(new ArrayList<Integer>());
    	
    	return results.getTotalCount();
    }
    public int getGeneCountByTS(Integer stage1) throws Exception
    {
    	JsonSummaryResponse<MockJSONGXDMarker> results = mockGeneQuery(new ArrayList<Integer>(Arrays.asList(stage1)));
    	
    	return results.getTotalCount();
    }
 
    public int getGeneCountByTS(String stages) throws Exception
    {
		List<Integer> ts = convertStringToTS(stages);
    	JsonSummaryResponse<MockJSONGXDMarker> results = mockGeneQuery(ts);    	
    	return results.getTotalCount();
    }

    /**
     * Return the count of GXD assay results associated with a theiler stage
     * 
     * @param query The theiler stage(s)
     * @return count of the number of assay results 
     * @throws Exception
     */
    public int getResultCountByTS(/* all stages */) throws Exception
    {
    	JsonSummaryResponse<MockJSONGXDAssayResult> results = mockResultQuery(new ArrayList<Integer>());    	
    	return results.getTotalCount();
    }
    public int getResultCountByTS(Integer stage1) throws Exception
    {
    	JsonSummaryResponse<MockJSONGXDAssayResult> results = mockResultQuery(new ArrayList<Integer>(Arrays.asList(stage1)));
    	
    	return results.getTotalCount();
    }
    public int getResultCountByTS(Integer stage1,Integer stage2) throws Exception
    {
    	JsonSummaryResponse<MockJSONGXDAssayResult> results = mockResultQuery(new ArrayList<Integer>(Arrays.asList(stage1,stage2)));
    	
    	return results.getTotalCount();
    }
    public int getResultCountByTS(String stages) throws Exception
    {
		List<Integer> ts = convertStringToTS(stages);
    	JsonSummaryResponse<MockJSONGXDAssayResult> results = mockResultQuery(ts);    	
    	return results.getTotalCount();
    }

    
    public int getResultCountByNomenAndTS(String nomen,Integer stage) throws Exception
    {
    	JsonSummaryResponse<MockJSONGXDAssayResult> results = mockResultQuery(nomen,new ArrayList<Integer>(Arrays.asList(stage)));
    	
    	return results.getTotalCount();
    }
    
    public List<String> getTheilerStagesByStage(Integer stage) throws Exception
    {
    	SearchResults<SolrAssayResult> results = getResultsByStage(stage);
    	List<String> stages = new ArrayList<String>();
    	for(SolrAssayResult result : results.getResultObjects())
    	{
    		stages.add(result.getTheilerStage().toString());
    	}
    	
    	return stages;
    }
}
