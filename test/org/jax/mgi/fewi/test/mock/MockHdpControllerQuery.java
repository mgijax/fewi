package org.jax.mgi.fewi.test.mock;

import java.util.List;

import org.jax.mgi.fewi.hmdc.controller.DiseasePortalController;
import org.jax.mgi.fewi.hmdc.models.GridResult;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpDisease;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpMarker;
import org.springframework.mock.web.MockHttpSession;

public class MockHdpControllerQuery extends AbstractMockHdpQuery {

	public MockHdpControllerQuery(DiseasePortalController hdpController) {
		this.hdpController = hdpController;
	}

	public List<SolrHdpMarker> getGenes() throws Exception {
		return hdpController.geneQuery(getParametersAsJson());
	}	

	public List<SolrHdpDisease> getDiseases() throws Exception {
		return hdpController.diseaseQuery(getParametersAsJson());
	}	

	public GridResult getGrid() throws Exception {
		MockHttpSession session = new MockHttpSession();
		return hdpController.gridQuery(getParametersAsJson(), session);
	}	
}
