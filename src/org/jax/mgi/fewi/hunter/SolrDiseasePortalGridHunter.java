package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpEntityInterface;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpGridRow;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrDiseasePortalGridHunter extends SolrHunter<SolrHdpEntityInterface> {


	public SolrDiseasePortalGridHunter() {
		keyString = DiseasePortalFields.UNIQUE_KEY;
	}

	@Override
	protected void packInformation(QueryResponse rsp, SearchResults<SolrHdpEntityInterface> sr, SearchParams sp) {

		SolrDocumentList sdl = rsp.getResults();

		List<String> keys = new ArrayList<String>();
		
		for (SolrDocument doc : sdl) {
			System.out.println(doc);
			
			Integer gridClusterKey;
			Integer genoClusterKey;
			Integer markerKey;
			String term;
			String termType;
			String termId;
			String vocabName;
			String qualifier;
			Integer annotCount;
			Integer humanAnnotCount;
			
			SolrHdpGridRow gridResult = new SolrHdpGridRow();
			gridResult.setGridClusterKey((Integer) doc.getFieldValue(DiseasePortalFields.GRID_KEY));
			gridResult.setGenoClusterKey((Integer) doc.getFieldValue(DiseasePortalFields.GENO_CLUSTER_KEY));
			gridResult.setMarkerKey((Integer) doc.getFieldValue(DiseasePortalFields.MARKER_KEY));
			gridResult.setTerm((String) doc.getFieldValue(DiseasePortalFields.TERM));
			gridResult.setTermId((String) doc.getFieldValue(DiseasePortalFields.TERM_ID));
			gridResult.setTermType((String) doc.getFieldValue(DiseasePortalFields.TERM_TYPE));
			gridResult.setVocabName((String) doc.getFieldValue(DiseasePortalFields.VOCAB_NAME));
			gridResult.setQualifier((String) doc.getFieldValue(DiseasePortalFields.TERM_QUALIFIER));
			gridResult.setAnnotCount((Integer) doc.getFieldValue(DiseasePortalFields.ANNOT_COUNT));
			gridResult.setHumanAnnotCount((Integer) doc.getFieldValue(DiseasePortalFields.HUMAN_ANNOT_COUNT));
			
			sr.addResultObjects(gridResult);
			keys.add(gridResult.getGridClusterKey().toString());
		}

		if (keys != null) {
			sr.setResultKeys(keys);
		}
	}

	@Value("${solr.dp.grid.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

}