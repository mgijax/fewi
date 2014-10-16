package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.matrix.HdpGridMapper;
import org.jax.mgi.fewi.matrix.HdpGridMapper.GridCell;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridCluster;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridCluster.SolrDpGridClusterMarker;
import org.jax.mgi.fewi.util.FormatHelper;


/**
 * represents on row in main hdp grid 
 */
public class HdpGridClusterSummaryRow {

	//-------------------
	// instance variables
	//-------------------

	// encapsulated row object
	private final SolrHdpGridCluster gridCluster;
	private final HdpGridMapper diseaseRowMapper;
	private final HdpGridMapper mpHeaderRowMapper;
	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------


    public HdpGridClusterSummaryRow (SolrHdpGridCluster gridCluster,HdpGridMapper diseaseRowMapper, HdpGridMapper mpHeaderRowMapper) {
    	this.gridCluster = gridCluster;
    	this.diseaseRowMapper = diseaseRowMapper;
    	this.mpHeaderRowMapper = mpHeaderRowMapper;
    }

    public SolrHdpGridCluster getGridCluster()
    {
    	return gridCluster;
    }

    public String getHomologeneId()
    {
    	return gridCluster.getHomologeneId();
    }
    
    public int getGridClusterKey()
    {
    	return gridCluster.getGridClusterKey();
    }

    public String getTitle()
    {
    	List<String> symbols = new ArrayList<String>(this.getHumanSymbols());
    	for(SolrDpGridClusterMarker m : this.getMouseMarkers())
    	{
    		symbols.add(m.getSymbol());
    	}
    	return FormatHelper.superscript(StringUtils.join(symbols,", "));
    }
    
    public List<String> getHumanSymbols()
    {
    	return gridCluster.getHumanSymbols();
    }
    public List<SolrDpGridClusterMarker> getHumanMarkers()
    {
    	return gridCluster.getHumanMarkers();
    }

    public List<SolrDpGridClusterMarker> getMouseMarkers()
    {
    	return gridCluster.getMouseMarkers();
    }

    public List<GridCell> getDiseaseCells()
    {
    	return diseaseRowMapper.getGridCells();
    }

    public List<GridCell> getMpHeaderCells()
    {
    	return mpHeaderRowMapper.getGridCells();
    }

}
