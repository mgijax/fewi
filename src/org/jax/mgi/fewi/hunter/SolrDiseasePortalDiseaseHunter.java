package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpDisease;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrDiseasePortalDiseaseHunter extends SolrHunter<SolrHdpEntityInterface> {


	public SolrDiseasePortalDiseaseHunter() {
		keyString = DiseasePortalFields.UNIQUE_KEY;
	}

	@Override
	protected void packInformation(QueryResponse rsp, SearchResults<SolrHdpEntityInterface> sr, SearchParams sp) {

		SolrDocumentList sdl = rsp.getResults();

		List<String> keys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {

			SolrHdpDisease vt = new SolrHdpDisease();

			// this is a term object
			String termId =(String)doc.getFieldValue(DiseasePortalFields.TERM_ID);

			vt.setTerm((String)doc.getFieldValue(DiseasePortalFields.TERM));
			vt.setPrimaryId(termId);
			String vocab = (String)doc.getFieldValue(DiseasePortalFields.TERM_TYPE);
			vt.setVocabName(vocab);

			vt.setDiseaseRefCount((Integer)doc.getFieldValue(DiseasePortalFields.DISEASE_REF_COUNT));
			vt.setDiseaseModelCount(((Integer)doc.getFieldValue(DiseasePortalFields.DISEASE_MODEL_COUNTS)));
			vt.setDiseaseMouseMarkers(((List<String>)doc.getFieldValue(DiseasePortalFields.TERM_MOUSESYMBOL)));
			vt.setDiseaseHumanMarkers(((List<String>)doc.getFieldValue(DiseasePortalFields.TERM_HUMANSYMBOL)));

			// return just the term name for now
			sr.addResultObjects(vt);
			keys.add(termId);

		}

		if (keys != null) {
			sr.setResultKeys(keys);
		}

	}

	@Value("${solr.dp.disease.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

}
