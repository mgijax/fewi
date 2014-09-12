package org.jax.mgi.fewi.test.mock;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.matrix.GxdStageGridJsonResponse;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

public class MockGxdControllerQuery extends AbstractMockGxdQuery
{

    public MockGxdControllerQuery(GXDController gxdController) {
    	this.gxdController = gxdController;
    }

    public MockHttpServletRequest generateRequest()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
    	
		request.setParameter("sort", this.sort);
		request.setParameter("dir","asc");
		
		return request;
	}
	
	public SearchResults<SolrGxdMarker> getGenes() throws Exception
	{
		MockHttpServletRequest request = generateRequest();

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		BindingResult br = new BeanPropertyBindingResult(new Object(),"mock");
		return gxdController.getGxdMarkerResults(request, this.gqf, page, br);
	}
	
	public SearchResults<SolrAssayResult> getAssayResults() throws Exception
	{
		MockHttpServletRequest request = generateRequest();

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		BindingResult br = new BeanPropertyBindingResult(new Object(),"mock");
		return gxdController.getGxdAssayResults(request, this.gqf, page, br);
	}

	public SearchResults<SolrGxdAssay> getAssays() throws Exception
	{
		MockHttpServletRequest request = generateRequest();

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		BindingResult br = new BeanPropertyBindingResult(new Object(),"mock");
		return gxdController.getGxdAssays(request, this.gqf, page, br);
	}
	
	public SearchResults<SolrGxdImage> getImages() throws Exception
	{
		MockHttpServletRequest request = generateRequest();

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		BindingResult br = new BeanPropertyBindingResult(new Object(),"mock");
		return gxdController.getGxdImages(request, this.gqf, page, br);
	}
	
	public GxdStageGridJsonResponse getStageGrid() throws Exception
	{
		return getStageGrid(null);
	}
	
	public GxdStageGridJsonResponse getStageGrid(String mapChildrenOf) throws Exception
	{
		MockHttpServletRequest request = generateRequest();
		MockHttpSession session = new MockHttpSession();
		
		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		return gxdController.gxdStageGridJson(request,this.gqf,page,mapChildrenOf,null,session);
	}
}
