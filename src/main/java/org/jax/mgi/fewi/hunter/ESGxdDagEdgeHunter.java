package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.searchUtil.entities.ESDagEdge;
import org.jax.mgi.snpdatamodel.document.ESEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Hunter for the gxdResultHasImage solr index (like gxdResult, but only with
 * classical data that has associated images)
 * 
 */

@Repository
public class ESGxdDagEdgeHunter<T extends ESEntity> extends ESGxdSummaryBaseHunter<T> {

    public ESGxdDagEdgeHunter() {
    	super((Class<T>)ESDagEdge.class);
    }

    public ESGxdDagEdgeHunter(Class<T> clazz, String host, String port, String index) {
        super(clazz);
        this.esHost = host;
        this.esPort = port;
        this.esIndex = index;
    } 

    @Value("${es.gxddagedge.index}")
	public void setESIndex(String esIndex) {
		super.esIndex = esIndex;
	}
}