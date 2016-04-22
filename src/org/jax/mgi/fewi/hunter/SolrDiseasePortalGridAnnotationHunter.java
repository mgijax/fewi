package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpGridAnnotationEntry;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrDiseasePortalGridAnnotationHunter extends SolrHunter<SolrHdpEntityInterface> {


	public SolrDiseasePortalGridAnnotationHunter() {
		keyString = DiseasePortalFields.UNIQUE_KEY;
	}

	@Override
	protected void packInformation(QueryResponse rsp, SearchResults<SolrHdpEntityInterface> sr, SearchParams sp) {

		SolrDocumentList sdl = rsp.getResults();

		List<String> keys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {

			SolrHdpGridAnnotationEntry gridAnnotationResult = new SolrHdpGridAnnotationEntry();

			gridAnnotationResult.setUniqueKey((String)doc.getFieldValue(DiseasePortalFields.UNIQUE_KEY));
			gridAnnotationResult.setGridKey((Integer)doc.getFieldValue(DiseasePortalFields.GRID_KEY));

			gridAnnotationResult.setTerm((String)doc.getFieldValue(DiseasePortalFields.TERM));
			gridAnnotationResult.setTermHeader((String)doc.getFieldValue(DiseasePortalFields.TERM_HEADER));
			gridAnnotationResult.setTermType((String)doc.getFieldValue(DiseasePortalFields.TERM_TYPE));
			gridAnnotationResult.setTermId((String)doc.getFieldValue(DiseasePortalFields.TERM_ID));
			gridAnnotationResult.setQualifier((String)doc.getFieldValue(DiseasePortalFields.TERM_QUALIFIER));
			// TODO come back and add these
			gridAnnotationResult.setByTermHeader((Integer)doc.getFieldValue(DiseasePortalFields.BY_TERM_HEADER));
			gridAnnotationResult.setByTermName((Integer)doc.getFieldValue(DiseasePortalFields.BY_TERM_NAME));

			sr.addResultObjects(gridAnnotationResult);
			keys.add(gridAnnotationResult.getUniqueKey());
		}

		if (keys != null) {
			sr.setResultKeys(keys);
		}
	}

	@Value("${solr.dp.gridAnnotation.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

}