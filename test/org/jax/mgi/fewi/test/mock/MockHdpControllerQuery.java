package org.jax.mgi.fewi.test.mock;

import mgi.frontend.datamodel.Marker;

import org.jax.mgi.fewi.controller.DiseasePortalController;
import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

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

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		return hdpController.getSummaryResultsByGene(request, this.qf, page);
	}	
	
	public SearchResults<SolrVocTerm> getDiseases() throws Exception
	{
		MockHttpServletRequest request = generateRequest();

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		return hdpController.getSummaryResultsByDisease(request, this.qf, page);
	}	
	
	public SearchResults<SolrVocTerm> getPhenotypes() throws Exception
	{
		MockHttpServletRequest request = generateRequest();

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		return hdpController.getSummaryResultsByDisease(request, this.qf, page);
	}	
}
