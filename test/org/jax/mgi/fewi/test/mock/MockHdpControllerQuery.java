package org.jax.mgi.fewi.test.mock;

import java.util.List;

import org.jax.mgi.fewi.controller.DiseasePortalController;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpDisease;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpMarker;

public class MockHdpControllerQuery extends AbstractMockHdpQuery {

	public MockHdpControllerQuery(DiseasePortalController hdpController) {
		this.hdpController = hdpController;
	}

	public List<SolrHdpMarker> getGenes() throws Exception {
		return hdpController.geneQuery("");
	}	

	public List<SolrHdpDisease> getDiseases() throws Exception {
		return hdpController.diseaseQuery("");
	}	

	public String getGrid() throws Exception {
		return hdpController.gridQuery("");
	}	
}
