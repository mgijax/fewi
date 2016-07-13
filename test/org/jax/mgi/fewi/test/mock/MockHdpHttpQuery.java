package org.jax.mgi.fewi.test.mock;

import java.util.List;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * A utility class for mocking HDP queries either through http requests
 * or by direct controller/finder calls, depending on what granularity of data is needed.
 * @author kstone
 *
 */
public class MockHdpHttpQuery extends AbstractMockHdpQuery
{
    protected MockRequest mr;

    public MockHdpHttpQuery(MockRequest mr) {
    	this.mr=mr;
    }
/*	
	public MockHttpServletRequest generateRequest()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();

    	if (qf.getGenes()!= null && !qf.getGenes().equals(""))
    	{
    		request.addParameter("genes", qf.getGenes());
    	}
    	if (qf.getPhenotypes() != null && !qf.getPhenotypes().equals(""))
    	{
    		request.addParameter("phenotypes", qf.getPhenotypes());
    	}
    	if (qf.getLocations() != null && !qf.getLocations().equals(""))
    	{
    		request.addParameter("locations", qf.getLocations());
    		request.addParameter("organism", qf.getOrganism());
    	}
    	
    	request.addParameter("results",""+this.pageSize);

		return request;
	}

	@SuppressWarnings("unchecked")
	public List<String> getDiseaseColumnIds() throws Exception
	{
		this.pageSize=50;
		MockHttpServletRequest request = generateRequest();
		request.setRequestURI(this.gridUrl);
    	request.setMethod("GET");
    	
    	return (List<String>) mr.handleRequest(request).get("diseaseNames");
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getMpHeaderColumns() throws Exception
	{
		this.pageSize=50;
		MockHttpServletRequest request = generateRequest();
		request.setRequestURI(this.gridUrl);
    	request.setMethod("GET");
    	
    	return (List<String>) mr.handleRequest(request).get("mpHeaders");
	}
*/




	
	

}
