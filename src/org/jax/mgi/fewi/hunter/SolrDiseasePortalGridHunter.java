package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpEntityInterface;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpGridEntry;
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
			
			SolrHdpGridEntry gridResult = new SolrHdpGridEntry();
			gridResult.setAllelePairs((String)doc.getFieldValue(DiseasePortalFields.ALLELE_PAIRS));
			gridResult.setGenoClusterKey((Integer) doc.getFieldValue(DiseasePortalFields.GENO_CLUSTER_KEY));
			gridResult.setGridClusterKey((Integer) doc.getFieldValue(DiseasePortalFields.GRID_KEY));
			gridResult.setGridHumanSymbols((List<String>)doc.getFieldValue(DiseasePortalFields.GRID_HUMAN_SYMBOLS));
			gridResult.setGridKey((Integer)doc.getFieldValue(DiseasePortalFields.GRID_KEY));
			gridResult.setGridMouseSymbols((List<String>)doc.getFieldValue(DiseasePortalFields.GRID_MOUSE_SYMBOLS));
			gridResult.setHomologyClusterKey((Integer)doc.getFieldValue(DiseasePortalFields.HOMOLOGY_CLUSTER_KEY));
			
			sr.addResultObjects(gridResult);
			keys.add(gridResult.getGridKey().toString());
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