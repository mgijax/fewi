package org.jax.mgi.fewi.test.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mgi.frontend.datamodel.HdpGenoCluster;

import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrDpGridCluster.SolrDpGridClusterMarker;
import org.jax.mgi.fewi.summary.HdpGenoByHeaderPopupRow;
import org.jax.mgi.fewi.summary.HdpGridClusterSummaryRow;
import org.jax.mgi.fewi.util.HdpGridMapper.GridCell;
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
	
	public List<HdpGridClusterSummaryRow> getGridClusters() throws Exception
	{
		this.pageSize=50;
		MockHttpServletRequest request = generateRequest();
		request.setRequestURI(this.gridUrl);
    	request.setMethod("GET");
    	
    	Map<String,Object> map = mr.handleRequest(request);
    	return (List<HdpGridClusterSummaryRow>) map.get("gridClusters");
	}

	public List<String> getDiseaseColumnIds() throws Exception
	{
		this.pageSize=50;
		MockHttpServletRequest request = generateRequest();
		request.setRequestURI(this.gridUrl);
    	request.setMethod("GET");
    	
    	return (List<String>) mr.handleRequest(request).get("diseaseNames");
	}
	
	public List<String> getMpHeaderColumns() throws Exception
	{
		this.pageSize=50;
		MockHttpServletRequest request = generateRequest();
		request.setRequestURI(this.gridUrl);
    	request.setMethod("GET");
    	
    	return (List<String>) mr.handleRequest(request).get("mpHeaders");
	}
	
	// ----------------- POPUP Functions ---------------------
	public List<HdpGenoCluster> getDiseasePopupGenoClusters(String geneSymbol,String diseaseCol) throws Exception
	{
		this.pageSize=50;
		// generate a grid request, and iterate through the rows, cols to find the correct data to build the popup link
		List<HdpGridClusterSummaryRow> clusters = this.getGridClusters();
		Integer gridClusterKey = gridClusterKeyByMarker(clusters,geneSymbol);
		String diseaseId = diseaseCellIdByName(clusters,diseaseCol);
		
		if(gridClusterKey==null) return new ArrayList<HdpGenoCluster>();
		
		MockHttpServletRequest request = generateRequest();
		request.addParameter("gridClusterKey",gridClusterKey.toString());
		request.addParameter("termHeader",diseaseCol);
		
		request.setRequestURI(this.diseasePopupUrl);
    	request.setMethod("GET");
		
    	return (List<HdpGenoCluster>) mr.handleRequest(request).get("genoClusters");
	}
	
	public List<SolrDiseasePortalMarker> getDiseasePopupHumanMarkers(String geneSymbol,String diseaseCol) throws Exception
	{
		this.pageSize=50;
		// generate a grid request, and iterate through the rows, cols to find the correct data to build the popup link
		List<HdpGridClusterSummaryRow> clusters = this.getGridClusters();
		Integer gridClusterKey = gridClusterKeyByMarker(clusters,geneSymbol);
		String diseaseId = diseaseCellIdByName(clusters,diseaseCol);
		
		if(gridClusterKey==null) return new ArrayList<SolrDiseasePortalMarker>();
		
		MockHttpServletRequest request = generateRequest();
		request.addParameter("gridClusterKey",gridClusterKey.toString());
		request.addParameter("termHeader",diseaseCol);
		
		request.setRequestURI(this.diseasePopupUrl);
    	request.setMethod("GET");
		
    	return (List<SolrDiseasePortalMarker>) mr.handleRequest(request).get("humanMarkers");
	}
	
	public List<HdpGenoCluster> getSystemPopupGenoClusters(String geneSymbol,String systemCol) throws Exception
	{
		this.pageSize=50;
		// generate a grid request, and iterate through the rows, cols to find the correct data to build the popup link
		List<HdpGridClusterSummaryRow> clusters = this.getGridClusters();
		Integer gridClusterKey = gridClusterKeyByMarker(clusters,geneSymbol);
		
		if(gridClusterKey==null) return new ArrayList<HdpGenoCluster>();
		
		MockHttpServletRequest request = generateRequest();
		request.addParameter("gridClusterKey",gridClusterKey.toString());
		request.addParameter("termHeader",systemCol);
		
		request.setRequestURI(this.systemPopupUrl);
    	request.setMethod("GET");
		
    	return (List<HdpGenoCluster>) mr.handleRequest(request).get("genoClusters");
	}
	public List<HdpGenoByHeaderPopupRow> getSystemPopupRows(String geneSymbol,String systemCol) throws Exception
	{
		this.pageSize=50;
		// generate a grid request, and iterate through the rows, cols to find the correct data to build the popup link
		List<HdpGridClusterSummaryRow> clusters = this.getGridClusters();
		Integer gridClusterKey = gridClusterKeyByMarker(clusters,geneSymbol);

		if(gridClusterKey==null) return new ArrayList<HdpGenoByHeaderPopupRow>();
		
		MockHttpServletRequest request = generateRequest();
		request.addParameter("gridClusterKey",gridClusterKey.toString());
		request.addParameter("termHeader",systemCol);
		
		request.setRequestURI(this.systemPopupUrl);
    	request.setMethod("GET");
		
    	return (List<HdpGenoByHeaderPopupRow>) mr.handleRequest(request).get("popupRows");
	}
	public List<String> getSystemPopupTerms(String geneSymbol,String systemCol) throws Exception
	{
		this.pageSize=50;
		// generate a grid request, and iterate through the rows, cols to find the correct data to build the popup link
		List<HdpGridClusterSummaryRow> clusters = this.getGridClusters();
		Integer gridClusterKey = gridClusterKeyByMarker(clusters,geneSymbol);
		
		if(gridClusterKey==null) return new ArrayList<String>();
		
		MockHttpServletRequest request = generateRequest();
		request.addParameter("gridClusterKey",gridClusterKey.toString());
		request.addParameter("termHeader",systemCol);
		
		request.setRequestURI(this.systemPopupUrl);
    	request.setMethod("GET");
		
    	return (List<String>) mr.handleRequest(request).get("terms");
	}
	
	
	// Helper methods to drive getting to the popup
    private Integer gridClusterKeyByMarker(List<HdpGridClusterSummaryRow> clusters, String geneSymbol) throws Exception
    {
    	for(HdpGridClusterSummaryRow cluster : clusters)
    	{
    		List<String> markers = new ArrayList<String>();
    		
    		for(SolrDpGridClusterMarker marker : cluster.getMouseMarkers())
    		{
    			markers.add(marker.getSymbol());
    		}
    		markers.addAll(cluster.getHumanSymbols());
    		for(String marker : markers)
    		{
    			if(marker.equals(geneSymbol))
    			{
    				return cluster.getGridClusterKey();
    			}
    		}
    	}
    	return null;
    }
    private String diseaseCellIdByName(List<HdpGridClusterSummaryRow> clusters, String diseaseCol) throws Exception
    {
    	for(HdpGridClusterSummaryRow cluster : clusters)
    	{
    		for(GridCell diseaseCell : cluster.getDiseaseCells())
    		{
    			if(diseaseCol.equalsIgnoreCase(diseaseCell.getTerm()))
    			{
    				return diseaseCell.getTermId();
    			}
    		}
    	}
    	return null;
    }
}
