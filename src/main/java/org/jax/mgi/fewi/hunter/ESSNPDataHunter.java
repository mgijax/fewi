package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.snpdatamodel.ConsensusSNP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class ESSNPDataHunter extends ESSNPBaseHunter {


	public ESSNPDataHunter() {

	}

	//@Override
	protected void packInformation (QueryResponse rsp, SearchResults<ConsensusSNP> sr, SearchParams sp) {

		SolrDocumentList sdl = rsp.getResults();

		List<String> resultKeys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.SNP_CONSENSUSSNPID);

			// TODO Fix this to be what is coming back from ES
			ConsensusSNP snp = new ConsensusSNP();
			sr.addResultObjects(snp);

			resultKeys.add(key);
		}
		sr.setResultKeys(resultKeys);

	}

	@Value("${es.consensussnp.index}")
	public void setESIndex(String esIndex) {
		super.esIndex = esIndex;
	}

}
