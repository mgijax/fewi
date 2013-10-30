package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrDpGenoInResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrDpGridCluster;
import org.jax.mgi.fewi.searchUtil.entities.SolrDpGridCluster.SolrDpGridClusterMarker;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.HdpGridMapper;
import org.jax.mgi.fewi.util.HdpGridMapper.GridCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * represents on row in main hdp grid 
 */
public class HdpGridClusterSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(HdpGridClusterSummaryRow.class);

	// encapsulated row object
	private SolrDpGridCluster gridCluster;
	private HdpGridMapper diseaseRowMapper;
	private HdpGridMapper mpHeaderRowMapper;
	private List<SolrDpGenoInResult> genoInResults;
	private List<String> genoTerms;
	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------


    public HdpGridClusterSummaryRow (SolrDpGridCluster gridCluster,HdpGridMapper diseaseRowMapper, HdpGridMapper mpHeaderRowMapper) {
    	this.gridCluster = gridCluster;
    	this.diseaseRowMapper = diseaseRowMapper;
    	this.mpHeaderRowMapper = mpHeaderRowMapper;
    }

    public SolrDpGridCluster getGridCluster()
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
