package org.jax.mgi.fewi.hmdc.hunter;

import org.jax.mgi.fewi.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.hunter.SolrHunter;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrDiseasePortalGridHighlightHunter extends SolrHunter<SolrHdpEntityInterface> {

	public SolrDiseasePortalGridHighlightHunter() {
		keyString = DiseasePortalFields.UNIQUE_KEY;
		groupFields.put(DiseasePortalFields.TERM_HEADER, DiseasePortalFields.TERM_HEADER);
		//groupReturnedFields.put(DiseasePortalFields.TERM_HEADER, DiseasePortalFields.TERM_HEADER);
	}
	
	@Value("${solr.dp.gridAnnotation.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
