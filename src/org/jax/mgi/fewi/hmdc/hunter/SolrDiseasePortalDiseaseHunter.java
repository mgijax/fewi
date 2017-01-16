package org.jax.mgi.fewi.hmdc.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpDisease;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.hunter.SolrHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrDiseasePortalDiseaseHunter extends SolrHunter<SolrHdpEntityInterface> {


	public SolrDiseasePortalDiseaseHunter() {
		keyString = DiseasePortalFields.UNIQUE_KEY;
		
		// These are added to reduce the amount of data coming back from solr
		// over the wire and speeds up the query.
		// All the search fields are not returned
		returnedFields.add(DiseasePortalFields.TERM_ID);
		returnedFields.add(DiseasePortalFields.TERM);
		returnedFields.add(DiseasePortalFields.TERM_TYPE);
		returnedFields.add(DiseasePortalFields.DISEASE_REF_COUNT);
		returnedFields.add(DiseasePortalFields.DISEASE_MODEL_COUNTS);
		returnedFields.add(DiseasePortalFields.TERM_MOUSESYMBOL);
		returnedFields.add(DiseasePortalFields.TERM_HUMANSYMBOL);
		returnedFields.add(DiseasePortalFields.DO_ID);
		returnedFields.add(DiseasePortalFields.OMIM_ID);

		groupFields.put(DiseasePortalFields.TERM_ID, DiseasePortalFields.TERM_ID);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void packInformationByGroup(QueryResponse rsp, SearchResults<SolrHdpEntityInterface> sr, SearchParams sp) {
		GroupResponse gr = rsp.getGroupResponse();
		
		GroupCommand gc = gr.getValues().get(0);
	
		// total count of groups
		int groupCount = gc.getNGroups();
		sr.setTotalCount(groupCount);
		
		List<Group> groups = gc.getValues();	
		
		List<String> keys = new ArrayList<String>();
		
		
		for (Group g : groups) {
			String key = g.getGroupValue();
			SolrDocument doc = g.getResult().get(0);
			
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
			vt.setDoIds((List<String>)doc.getFieldValue(DiseasePortalFields.DO_ID));
			vt.setOmimIds((List<String>)doc.getFieldValue(DiseasePortalFields.OMIM_ID));

			// return just the term name for now
			sr.addResultObjects(vt);
			
			if(keyString != null) {
				keys.add(key);
			}
		}
		
		if (keys != null) {
			sr.setResultKeys(keys);
		}
	}

//	@Override
//	protected void packInformation(QueryResponse rsp, SearchResults<SolrHdpEntityInterface> sr, SearchParams sp) {
//
//		SolrDocumentList sdl = rsp.getResults();
//
//		List<String> keys = new ArrayList<String>();
//
//		for (SolrDocument doc : sdl) {
//
//			SolrHdpDisease vt = new SolrHdpDisease();
//
//			// this is a term object
//			String termId =(String)doc.getFieldValue(DiseasePortalFields.TERM_ID);
//
//			vt.setTerm((String)doc.getFieldValue(DiseasePortalFields.TERM));
//			vt.setPrimaryId(termId);
//			String vocab = (String)doc.getFieldValue(DiseasePortalFields.TERM_TYPE);
//			vt.setVocabName(vocab);
//
//			vt.setDiseaseRefCount((Integer)doc.getFieldValue(DiseasePortalFields.DISEASE_REF_COUNT));
//			vt.setDiseaseModelCount(((Integer)doc.getFieldValue(DiseasePortalFields.DISEASE_MODEL_COUNTS)));
//			vt.setDiseaseMouseMarkers(((List<String>)doc.getFieldValue(DiseasePortalFields.TERM_MOUSESYMBOL)));
//			vt.setDiseaseHumanMarkers(((List<String>)doc.getFieldValue(DiseasePortalFields.TERM_HUMANSYMBOL)));
//
//			// return just the term name for now
//			sr.addResultObjects(vt);
//			keys.add(termId);
//
//		}
//
//		if (keys != null) {
//			sr.setResultKeys(keys);
//		}
//
//	}

	@Value("${solr.dp.disease.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

}
