package org.jax.mgi.fewi.test.mock;

import java.util.List;

import org.jax.mgi.fewi.forms.GxdQueryForm;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A utility class for mocking GXD queries either through http requests
 * or by direct controller/finder calls, depending on what granularity of data is needed.
 * @author kstone
 *
 */
public class MockGxdHttpQuery extends AbstractMockGxdQuery
{
    protected MockRequest mr;
    public String gxdLitCountUrl = "/gxd/gxdLitCount";

    public MockGxdHttpQuery(MockRequest mr) {
    	this.mr=mr;
    }
	
	public MockHttpServletRequest generateRequest()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();

    	
    	if (gqf.getNomenclature() != null && !gqf.getNomenclature().equals(""))
    	{
    		request.addParameter("nomenclature", gqf.getNomenclature());
    	}
    	if (gqf.getAnnotationId() != null && !gqf.getAnnotationId().equals(""))
    	{
    		request.addParameter("annotationId", gqf.getAnnotationId());
    	}
    	if (gqf.getTheilerStage().size() > 0 && !gqf.getTheilerStage().contains(GxdQueryForm.ANY_STAGE))
    	{
    		List<Integer> theilerStages = gqf.getTheilerStage();
    		String[] stages = new String[theilerStages.size()];
        	for(int i=0;i<theilerStages.size();i++)
        	{
        		stages[i] = theilerStages.get(i).toString();
        	}
        	request.addParameter("theilerStage", stages);
    	}
    	else if (gqf.getAge().size() > 0 && !gqf.getAge().contains(GxdQueryForm.ANY_AGE))
    	{
    		List<String> ages = gqf.getAge();
    		String[] ageParameter = new String[ages.size()];
    		for(int i=0;i<ages.size();i++)
    		{
    			ageParameter[i] = ages.get(i);
    		}
    		request.addParameter("age",ageParameter);
    	}
    	if(gqf.getAssayType().size()>0)
    	{
    		List<String> assayTypes = gqf.getAssayType();
    		String[] types = new String[assayTypes.size()];
        	for(int i=0;i<assayTypes.size();i++)
        	{
        		types[i] = assayTypes.get(i).toString();
        	}
    		request.addParameter("assayType", types);
    	}

    	if (gqf.getDetected() != null && !gqf.getDetected().equals(""))
    	{
    		request.addParameter("detected", gqf.getDetected());
    	}
    	
    	if(gqf.getStructure() != null && !gqf.getStructure().equals(""))
    	{
    		request.addParameter("structure", gqf.getStructure());
    	}
    	
    	if(gqf.getIsWildType() != null && gqf.getIsWildType().equals("true"))
    	{
    		request.addParameter("isWildType","true");
    	}
    	
    	if(gqf.getMutatedIn() != null && !gqf.getMutatedIn().equals(""))
    	{
    		request.addParameter("mutatedIn", gqf.getMutatedIn());
    	}
    	
    	request.addParameter("results",""+this.pageSize);

		return request;
	}
	
	public JsonSummaryResponse<MockJSONGXDMarker> getGenes() throws Exception
	{
		MockHttpServletRequest request = generateRequest();
    	
    	request.setRequestURI(this.markersUrl);
    	request.setMethod("GET");

    	MockHttpServletResponse response = mr.handle(request);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	JsonSummaryResponse<MockJSONGXDMarker> results = null; 
    	results = mapper.readValue(response.getContentAsString(), 
    			new TypeReference<JsonSummaryResponse<MockJSONGXDMarker>>() { });
    	return results;
	}
	
	public JsonSummaryResponse<MockJSONGXDAssay> getAssays() throws Exception
	{
		MockHttpServletRequest request = generateRequest();
    	
    	request.setRequestURI(this.assaysUrl);
    	request.setMethod("GET");

    	MockHttpServletResponse response = mr.handle(request);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	JsonSummaryResponse<MockJSONGXDAssay> results = null; 
    	results = mapper.readValue(response.getContentAsString(), 
    			new TypeReference<JsonSummaryResponse<MockJSONGXDAssay>>() { });
    	return results;
	}
	
	public JsonSummaryResponse<MockJSONGXDAssayResult> getAssayResults() throws Exception
	{
		MockHttpServletRequest request = generateRequest();

    	request.setRequestURI(this.resultsUrl);
    	request.setMethod("GET");

    	MockHttpServletResponse response = mr.handle(request);

    	ObjectMapper mapper = new ObjectMapper();
    	JsonSummaryResponse<MockJSONGXDAssayResult> results = null; 
    	results = mapper.readValue(response.getContentAsString(), 
    			new TypeReference<JsonSummaryResponse<MockJSONGXDAssayResult>>() { });
    	return results;
	}
	
	public Integer getGxdLitCount() throws Exception
	{
		MockHttpServletRequest request = generateRequest();

    	request.setRequestURI(this.gxdLitCountUrl);
    	request.setMethod("GET");
    
    	MockHttpServletResponse response = mr.handle(request);

    	ObjectMapper mapper = new ObjectMapper();
    	Integer count = null; 
    	count = mapper.readValue(response.getContentAsString(), 
    			new TypeReference<Integer>() { });
    	return count;
	}
}
