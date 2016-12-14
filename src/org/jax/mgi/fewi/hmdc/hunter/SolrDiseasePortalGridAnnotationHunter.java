package org.jax.mgi.fewi.hmdc.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpGridAnnotationEntry;
import org.jax.mgi.fewi.hunter.SolrHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrDiseasePortalGridAnnotationHunter extends SolrHunter<SolrHdpEntityInterface> {


	public SolrDiseasePortalGridAnnotationHunter() {
		keyString = DiseasePortalFields.UNIQUE_KEY;
		
		// These are added to reduce the amount of data coming back from solr
		// over the wire and speeds up the query.
		// All the search fields are not returned
		returnedFields.add(DiseasePortalFields.UNIQUE_KEY);
		returnedFields.add(DiseasePortalFields.GRID_KEY);
		returnedFields.add(DiseasePortalFields.TERM);
		returnedFields.add(DiseasePortalFields.TERM_HEADER);
		returnedFields.add(DiseasePortalFields.TERM_TYPE);
		returnedFields.add(DiseasePortalFields.TERM_ID);
		returnedFields.add(DiseasePortalFields.TERM_QUALIFIER);
		returnedFields.add(DiseasePortalFields.SOURCE_TERM);
		returnedFields.add(DiseasePortalFields.SOURCE_TERM_ID);
		returnedFields.add(DiseasePortalFields.BY_TERM_DAG);
		returnedFields.add(DiseasePortalFields.BACKGROUND_SENSITIVE);
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
			gridAnnotationResult.setSourceTerm((String)doc.getFieldValue(DiseasePortalFields.SOURCE_TERM));
			gridAnnotationResult.setSourceId((String)doc.getFieldValue(DiseasePortalFields.SOURCE_TERM_ID));
			gridAnnotationResult.setByDagTerm((Integer)doc.getFieldValue(DiseasePortalFields.BY_TERM_DAG));
			gridAnnotationResult.setBackgroundSensitive((String)doc.getFieldValue(DiseasePortalFields.BACKGROUND_SENSITIVE));

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