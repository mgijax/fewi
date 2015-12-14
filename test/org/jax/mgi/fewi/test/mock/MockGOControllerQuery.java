package org.jax.mgi.fewi.test.mock;

import mgi.frontend.datamodel.Annotation;

import org.jax.mgi.fewi.controller.GOController;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.springframework.mock.web.MockHttpServletRequest;

public class MockGOControllerQuery extends AbstractMockGOQuery
{

    public MockGOControllerQuery(GOController goController) {
    	this.goController = goController;
    }

    public MockHttpServletRequest generateRequest()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
    	
		request.setParameter("sort", this.sort);
		request.setParameter("dir","asc");
		
		return request;
	}
	
	public SearchResults<Annotation> getAnnotations() throws Exception
	{
		MockHttpServletRequest request = generateRequest();

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		return goController.getAnnotations(request, this.goqf, page);
	}
}
