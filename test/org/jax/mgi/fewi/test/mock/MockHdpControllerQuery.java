package org.jax.mgi.fewi.test.mock;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jax.mgi.fewi.hmdc.controller.DiseasePortalController;
import org.jax.mgi.fewi.hmdc.models.GridCluster;
import org.jax.mgi.fewi.hmdc.models.GridResult;
import org.jax.mgi.fewi.hmdc.models.GridRow;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpDisease;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpMarker;
import org.jax.mgi.shr.jsonmodel.GridMarker;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

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
		return this.getGrid(session);
	}	
	
	public GridResult getGrid(HttpSession session) throws Exception {
		return hdpController.gridQuery(getParametersAsJson(), session);
	}
	
	
	public HmdcPopupTestApi getPhenoPopup(String geneSymbol, String systemCol) throws Exception {
		
		// make session to hold queryToken
		MockHttpSession session = new MockHttpSession();
		
		HttpServletRequest request = this.generateGridPopupRequest(session, geneSymbol, systemCol);
		ModelAndView mav = hdpController.popup(request, session, "true", request.getParameter("queryToken"));
		
		if (mav.getModel().get("mpGroup")==null) {
			throw new Exception("Could not find popup cell for "+geneSymbol+" x "+systemCol);
		}
		
		return new HmdcPopupTestApi(mav);
	}
	
	public HmdcPopupTestApi getDiseasePopup(String geneSymbol, String diseaseCol) throws Exception {
		
		// make session to hold queryToken
		MockHttpSession session = new MockHttpSession();
		
		HttpServletRequest request = this.generateGridPopupRequest(session, geneSymbol, diseaseCol);
		ModelAndView mav = hdpController.popup(request, session, "false", request.getParameter("queryToken"));
		
		if (mav.getModel().get("omimGroup")==null) {
			throw new Exception("Could not find popup cell for "+geneSymbol+" x "+diseaseCol);
		}
		
		return new HmdcPopupTestApi(mav);
	}
	
	/*
	 * Generates request for HmdcPopup
	 * populates "gridClusterKey" and "header" and "queryToken"
	 */
	private HttpServletRequest generateGridPopupRequest(HttpSession session, String geneSymbol, String colHeader) throws Exception {
		
		// perform grid query to get a queryToken
		GridResult result = this.getGrid(session);
		
		String queryToken = result.getQueryToken();
		Integer foundClusterKey = null;
		
		// get the desired grid cluster key
		for (GridRow row : result.getGridRows()) {
			GridCluster cluster = row.getGridCluster();
			
			List<String> symbols = this.getAllSymbolsInCluster(cluster);
			
			for (String symbol : symbols) {
				if (symbol.equals(geneSymbol)) {
					foundClusterKey = cluster.getHomologyClusterKey();
				}
			}
			
			if (foundClusterKey != null) {
				break;
			}
		}

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("gridClusterKey", foundClusterKey.toString());
		request.addParameter("header", colHeader);
		request.addParameter("queryToken", queryToken);
		
		return request;
	}
	
	private List<String> getAllSymbolsInCluster(GridCluster cluster) {
		List<String> symbols = new ArrayList<String>();
		
		for (GridMarker marker : cluster.getMouseSymbols()) {
			symbols.add(marker.getSymbol());
		}
		
		for (GridMarker marker : cluster.getHumanSymbols()) {
			symbols.add(marker.getSymbol());
		}
		
		return symbols;
	}
}
