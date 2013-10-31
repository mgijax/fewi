package org.jax.mgi.fewi.summary;

import java.util.List;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.util.HdpGridMapper;
import org.jax.mgi.fewi.util.HdpGridMapper.GridCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a HdpGenoCluster and column controls
 */
public class HdpMarkerByHeaderPopupRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(HdpMarkerByHeaderPopupRow.class);

	// encapsulated row object
	private SolrDiseasePortalMarker marker;
	private HdpGridMapper gridMapper;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------


    public HdpMarkerByHeaderPopupRow (SolrDiseasePortalMarker marker, HdpGridMapper gridMapper) {
    	this.marker = marker;
    	this.gridMapper = gridMapper;
    }

    public SolrDiseasePortalMarker getMarker()
    {
    	return marker;
    }

    public List<GridCell> getGridCells()
    {
    	return gridMapper.getGridCells();
    }
}
