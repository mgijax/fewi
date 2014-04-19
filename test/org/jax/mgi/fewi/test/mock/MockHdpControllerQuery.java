package org.jax.mgi.fewi.test.mock;

import org.jax.mgi.fewi.controller.DiseasePortalController;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

public class MockHdpControllerQuery extends AbstractMockHdpQuery
{

    public MockHdpControllerQuery(DiseasePortalController hdpController) {
    	this.hdpController = hdpController;
    }

    public MockHttpServletRequest generateRequest()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
    	
		request.setParameter("sort", this.sort);
		request.setParameter("dir","asc");
		
		return request;
	}
	
	public SearchResults<SolrDiseasePortalMarker> getGenes() throws Exception
	{
		MockHttpServletRequest request = generateRequest();
		MockHttpSession session = new MockHttpSession();

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		return hdpController.getSummaryResultsByGene(request, this.qf, page,session);
	}	
	
	public SearchResults<SolrVocTerm> getDiseases() throws Exception
	{
		MockHttpServletRequest request = generateRequest();
		MockHttpSession session = new MockHttpSession();

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		return hdpController.getSummaryResultsByDisease(request, this.qf, page,session);
	}	
	
	public SearchResults<SolrVocTerm> getPhenotypes() throws Exception
	{
		MockHttpServletRequest request = generateRequest();
		MockHttpSession session = new MockHttpSession();

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		return hdpController.getSummaryResultsByDisease(request, this.qf, page,session);
	}	
}
