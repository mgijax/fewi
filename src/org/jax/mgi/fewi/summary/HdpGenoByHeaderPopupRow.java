package org.jax.mgi.fewi.summary;

import java.util.List;

import mgi.frontend.datamodel.Genotype;
import mgi.frontend.datamodel.hdp.HdpGenoCluster;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.HdpGridMapper;
import org.jax.mgi.fewi.util.HdpGridMapper.GridCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a HdpGenoCluster and column controls
 */
public class HdpGenoByHeaderPopupRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(HdpGenoByHeaderPopupRow.class);

	// encapsulated row object
	private HdpGenoCluster genoCluster;
	private HdpGridMapper gridMapper;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------


    public HdpGenoByHeaderPopupRow (HdpGenoCluster genoCluster, HdpGridMapper gridMapper) {
    	this.genoCluster = genoCluster;
    	this.gridMapper = gridMapper;
    }

    public Genotype getGenotype()
    {
    	return genoCluster.getGenotype();
    }

    public List<GridCell> getGridCells()
    {
    	return gridMapper.getGridCells();
    }

    public HdpGenoCluster getGenoCluster()
    {
    	return genoCluster;
    }

    public int getGenoClusterKey()
    {
    	return genoCluster.getGenoClusterKey();
    }
}
