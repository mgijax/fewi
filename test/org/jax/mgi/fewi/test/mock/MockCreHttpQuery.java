package org.jax.mgi.fewi.test.mock;

import org.jax.mgi.fewi.forms.RecombinaseQueryForm;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A utility class for mocking GXD queries either through http requests
 * or by direct controller/finder calls, depending on what granularity of data is needed.
 * @author kstone
 *
 */
public class MockCreHttpQuery implements MockQuery
{
    protected MockRequest mr;
    public String creJsonUrl = "/recombinase/json";
    
	RecombinaseQueryForm cqf = new RecombinaseQueryForm();
	
    public MockCreHttpQuery(MockRequest mr) {
    	this.mr=mr;
    }
	
	public MockHttpServletRequest generateRequest()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();

    	if(cqf.getStructure() != null && !cqf.getStructure().equals(""))
    	{
    		request.addParameter("structure", cqf.getStructure());
    	}
    	
		return request;
	}
	
	public JsonSummaryResponse<MockRecombinaseSummary> getRecombinaseSummary() throws Exception
	{
		MockHttpServletRequest request = generateRequest();

    	request.setRequestURI(this.creJsonUrl);
    	request.setMethod("GET");

    	MockHttpServletResponse response = mr.handle(request);

    	ObjectMapper mapper = new ObjectMapper();
    	// tell the mapper to ignore unknown fields in the json response
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	
    	JsonSummaryResponse<MockRecombinaseSummary> results = null; 
    	results = mapper.readValue(response.getContentAsString(), 
    			new TypeReference<JsonSummaryResponse<MockRecombinaseSummary>>() { });
    	return results;
	}
	
	/*
	 * Set the form parameters
	 */
	public void setStructure(String structure)
	{
		cqf.setStructure(structure);
	}
}
