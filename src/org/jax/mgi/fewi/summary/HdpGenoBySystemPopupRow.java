package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.HdpGenoCluster;
import mgi.frontend.datamodel.Genotype;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.controller.DiseasePortalController.GridMapper;
import org.jax.mgi.fewi.controller.DiseasePortalController.GridMapper.GridCell;

import javax.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a HdpGenoCluster and column controls
 */
public class HdpGenoBySystemPopupRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(HdpGenoBySystemPopupRow.class);

	// encapsulated row object
	private HdpGenoCluster genoCluster;
	private GridMapper mpMapper;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------


    public HdpGenoBySystemPopupRow (HdpGenoCluster genoCluster, GridMapper mpMapper) {
    	this.genoCluster = genoCluster;
    	this.mpMapper = mpMapper;
    }

    public Genotype getGenotype()
    {
    	return genoCluster.getGenotype();
    }

    public List<GridCell> getMpCells()
    {
    	return mpMapper.getGridCells();
    }

    public HdpGenoCluster getGenoCluster()
    {
    	return genoCluster;
    }

    public int getGenoClusterKey()
    {
    	return genoCluster.getGenoClusterKey();
    }

    public List<GridCell> getMpHeaderCells()
    {
    	return mpMapper.getGridCells();
    }



}
