package org.jax.mgi.fewi.hunter;

import org.springframework.stereotype.Repository;

@Repository
public class SolrQSStrainResultFacetHunter extends SolrQSStrainResultBaseHunter {

    public SolrQSStrainResultFacetHunter() {
    	super();
        facetString = null;		// leave as null, so we can be flexible and set via method call
    }

    public void setFacetString(String facetString) {
    	this.facetString = facetString;
    }
}
