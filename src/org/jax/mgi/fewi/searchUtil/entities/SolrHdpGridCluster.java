package org.jax.mgi.fewi.searchUtil.entities;

import java.util.*;

public class SolrHdpGridCluster
{
	Integer gridClusterKey;
	String homologeneId;
	// mouseSymbols are symbol & marker key concatenated by double pipe '||'
	List<String> mouseSymbols;
	// humanSymbols are just marker symbols
	List<String> humanSymbols;
	
	public Integer getGridClusterKey() {
		return gridClusterKey;
	}
	public String getHomologeneId() {
		return homologeneId;
	}
	public List<String> getMouseSymbols() {
		return mouseSymbols;
	}
	public List<String> getHumanSymbols() {
		return humanSymbols;
	}
	public void setGridClusterKey(Integer gridClusterKey) {
		this.gridClusterKey = gridClusterKey;
	}
	public void setHomologeneId(String homologeneId) {
		this.homologeneId = homologeneId;
	}
	public void setMouseSymbols(List<String> mouseSymbols) {
		if(mouseSymbols==null) this.mouseSymbols=new ArrayList<String>();
		else this.mouseSymbols = mouseSymbols;
	}
	public void setHumanSymbols(List<String> humanSymbols) {
		if(humanSymbols==null) this.humanSymbols=new ArrayList<String>();
		else this.humanSymbols = humanSymbols;
	}
	
	public List<SolrDpGridClusterMarker> getMouseMarkers()
	{
		List<SolrDpGridClusterMarker> markers = new ArrayList<SolrDpGridClusterMarker>();
		if(mouseSymbols != null)
		{
			for(String mouseSymbol : mouseSymbols)
			{
				String[] tokens = mouseSymbol.split("\\|\\|");
				if(tokens.length!=2) continue;
				SolrDpGridClusterMarker marker = new SolrDpGridClusterMarker(tokens[0],tokens[1]);
				markers.add(marker);
			}
		}
		return markers;
	}
	
	public class SolrDpGridClusterMarker
	{
		private String symbol;
		private String markerKey;
		
		public SolrDpGridClusterMarker(String symbol,String markerKey)
		{
			this.symbol=symbol;
			this.markerKey=markerKey;
		}
		
		public String getSymbol()
		{
			return symbol;
		}
		public void setSymbol(String symbol)
		{
			this.symbol=symbol;
		}
		public String getMarkerKey()
		{
			return markerKey;
		}
		public void setMarkerKey(String markerKey)
		{
			this.markerKey=markerKey;
		}
	}
}
