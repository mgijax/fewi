package org.jax.mgi.fewi.searchUtil.entities;

import java.util.ArrayList;
import java.util.List;

public class SolrHdpGridCluster implements SolrHdpEntity
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
				SolrDpGridClusterMarker marker = parseMarker(mouseSymbol);
				if(marker!=null) markers.add(marker);
			}
		}
		return markers;
	}
	
	public List<SolrDpGridClusterMarker> getHumanMarkers()
	{
		List<SolrDpGridClusterMarker> markers = new ArrayList<SolrDpGridClusterMarker>();
		if(humanSymbols != null)
		{
			for(String humanSymbol : humanSymbols)
			{
				SolrDpGridClusterMarker marker = parseMarker(humanSymbol);
				if(marker!=null) markers.add(marker);
			}
		}
		return markers;
	}
	

	public List<String> getMouseSymbols() {
		List<String> symbols = new ArrayList<String>();
		for(SolrDpGridClusterMarker mouseMarker : this.getMouseMarkers())
		{
			symbols.add(mouseMarker.getSymbol());
		}
		return symbols;
	}
	
	public List<String> getHumanSymbols()
	{
		List<String> symbols = new ArrayList<String>();
		for(SolrDpGridClusterMarker humanMarker : this.getHumanMarkers())
		{
			symbols.add(humanMarker.getSymbol());
		}
		return symbols;
	}
	
	private SolrDpGridClusterMarker parseMarker(String markerString)
	{
		String[] tokens = markerString.split("\\|\\|");
		if(tokens.length<1 || markerString.trim().equals("")) return null;
		String symbol = tokens[0];
		String markerKey="",
			name="",
			featureType="";
		if(tokens.length>=2) markerKey = tokens[1];
		if(tokens.length>=3) name = tokens[2];
		if(tokens.length>=4) featureType = tokens[3];
		
		// expected data in format "symbol||markerKey||name||featureType
		return new SolrDpGridClusterMarker(symbol,markerKey,name,featureType);
	}
	
	public class SolrDpGridClusterMarker
	{
		private String symbol;
		private String name;
		private String featureType;
		private String markerKey;
		
		public SolrDpGridClusterMarker(String symbol,String markerKey,String name,String featureType)
		{
			this.symbol=symbol;
			this.name=name;
			this.featureType=featureType;
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

		public String getName() {
			return name;
		}

		public String getFeatureType() {
			return featureType;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setFeatureType(String featureType) {
			this.featureType = featureType;
		}
	}
}
