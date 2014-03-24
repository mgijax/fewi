package org.jax.mgi.fewi.test.mock;

import org.jax.mgi.fewi.controller.MarkerController;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrSummaryMarker;
import org.springframework.mock.web.MockHttpServletRequest;

public class MockMarkerControllerQuery extends AbstractMockMarkerQuery
{

    public MockMarkerControllerQuery(MarkerController markerController) {
    	this.markerController = markerController;
    }

    public MockHttpServletRequest generateRequest()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
    	
		request.setParameter("sort", this.sort);
		request.setParameter("dir","asc");
		
		return request;
	}
	
	public SearchResults<SolrSummaryMarker> getMarkers() throws Exception
	{
		MockHttpServletRequest request = generateRequest();

		Paginator page = new Paginator();
		page.setResults(this.pageSize);
		
		return markerController.getSummaryMarkers(request, this.mqf, page);
	}

}
