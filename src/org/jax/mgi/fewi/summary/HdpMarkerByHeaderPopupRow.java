package org.jax.mgi.fewi.summary;

import java.util.List;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.matrix.HdpGridMapper;
import org.jax.mgi.fewi.matrix.HdpGridMapper.GridCell;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;


/**
 * wrapper around a HdpGenoCluster and column controls
 */
public class HdpMarkerByHeaderPopupRow {

	//-------------------
	// instance variables
	//-------------------

	// encapsulated row object
	private final SolrDiseasePortalMarker marker;
	private final HdpGridMapper gridMapper;

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
