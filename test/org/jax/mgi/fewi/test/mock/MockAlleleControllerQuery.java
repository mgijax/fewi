package org.jax.mgi.fewi.test.mock;

import mgi.frontend.datamodel.Allele;

import org.jax.mgi.fewi.controller.AlleleController;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.springframework.mock.web.MockHttpServletRequest;

public class MockAlleleControllerQuery extends AbstractMockAlleleQuery
{

    public MockAlleleControllerQuery(AlleleController alleleController) {
    	this.alleleController = alleleController;
    }

    public MockHttpServletRequest generateRequest()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
    	
		request.setParameter("sort", this.sort);
		request.setParameter("dir","asc");
		
		return request;
	}
	
	public SearchResults<Allele> getAlleles() throws Exception
	{
		MockHttpServletRequest request = generateRequest();

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		return alleleController.getAlleles(request, this.aqf, page);
	}
}
