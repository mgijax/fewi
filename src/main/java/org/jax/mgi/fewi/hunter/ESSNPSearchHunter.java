package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class ESSNPSearchHunter extends ESSNPBaseHunter {

	public ESSNPSearchHunter() {
		//groupFields.put(SearchConstants.SNPID, IndexConstants.SNP_CONSENSUSSNPID);
		facetString = IndexConstants.SNP_STRAINS;
	}

	@Value("${es.searchsnp.index}")
	public void setESIndex(String esIndex) {
		super.esIndex = esIndex;
	}

}