package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * ESGxdAssayResultHunter is a specialized hunter for retrieving assay results
 * from the GXD (Gene Expression Database) Elasticsearch index. 
 * <p>
 * It extends {@link ESGxdSummaryBaseHunter} with {@link SolrAssayResult} as the
 * type parameter, enabling it to work specifically with assay result entities.
 * This class relies on the superclass {@code hunt()} method to execute searches.
 * </p>
 */
@Repository
public class ESGxdAssayResultHunter  extends ESGxdSummaryBaseHunter<SolrAssayResult> {
	/***
	 * The constructor sets up this hunter so that it is specific to sequence
	 * summary pages. Each item in the constructor sets a value that it has
	 * inherited from its superclass, and then relies on the superclass to
	 * perform all of the needed work via the hunt() method.
	 */
	public ESGxdAssayResultHunter() {
		super(SolrAssayResult.class);
	}
	
	@Value("${es.gxdresult.index}")
	public void setESIndex(String esIndex) {
		super.esIndex = esIndex;
	}	
}
