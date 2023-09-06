package org.jax.mgi.fewi.hmdc.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpCoord;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.hunter.SolrHunter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class SolrDiseasePortalCoordHunter extends SolrHunter<SolrHdpEntityInterface> {
	private Logger logger = LoggerFactory.getLogger(SolrDiseasePortalCoordHunter.class);

	public SolrDiseasePortalCoordHunter() {
		keyString = DiseasePortalFields.UNIQUE_KEY;
		
		// These are added to reduce the amount of data coming back from solr
		// over the wire and speeds up the query.
		// All the search fields are not returned
		returnedFields.add(DiseasePortalFields.MARKER_KEY);
	}

	
	@Override
	protected void packInformation(QueryResponse rsp, SearchResults<SolrHdpEntityInterface> sr, SearchParams sp) {
		SolrDocumentList sdl = rsp.getResults();
		List<String> keys = new ArrayList<String>();

		for (int i = 0; i < sdl.getNumFound(); i++) {
			SolrDocument doc = sdl.get(i);

			SolrHdpCoord hdpCoord = new SolrHdpCoord();
			hdpCoord.setUniqueKey((String)doc.getFieldValue(DiseasePortalFields.UNIQUE_KEY));
			hdpCoord.setMarkerKey((Integer)doc.getFieldValue(DiseasePortalFields.MARKER_KEY));

			sr.addResultObjects(hdpCoord);
			keys.add(hdpCoord.getUniqueKey());
		}

		if (keys != null) {
			sr.setResultKeys(keys);
		}
	}

	@Value("${solr.dp.coords.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
