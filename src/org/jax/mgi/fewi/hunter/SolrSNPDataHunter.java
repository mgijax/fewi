package org.jax.mgi.fewi.hunter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jackson.map.ObjectMapper;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.snpdatamodel.ConsensusSNP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrSNPDataHunter extends SolrSNPBaseHunter {

	private ObjectMapper mapper = new ObjectMapper();

	public SolrSNPDataHunter() {

	}

	@Override
	protected void packInformation (QueryResponse rsp, SearchResults<ConsensusSNP> sr, SearchParams sp) {

		SolrDocumentList sdl = rsp.getResults();

		List<String> resultKeys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.SNP_CONSENSUSSNPID);

			try {
				ConsensusSNP snp = (ConsensusSNP)mapper.readValue((String)doc.getFieldValue(IndexConstants.SNP_JSONSTRING), ConsensusSNP.class);
				sr.addResultObjects(snp);
			} catch (IOException e) {
				e.printStackTrace();
			}

			resultKeys.add(key);
		}
		sr.setResultKeys(resultKeys);

	}

	@Value("${solr.consensussnp.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

}
