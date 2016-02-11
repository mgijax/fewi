package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.snpdatamodel.ConsensusSNP;
import org.springframework.stereotype.Repository;

@Repository
public class SolrSNPBaseHunter extends SolrHunter<ConsensusSNP> {

	public SolrSNPBaseHunter() {

		propertyMap.put(SearchConstants.SNPID, new SolrPropertyMapper(IndexConstants.SNP_CONSENSUSSNPID));
		propertyMap.put(SearchConstants.FUNCTIONCLASS, new SolrPropertyMapper(IndexConstants.SNP_FUNCTIONCLASS));
		propertyMap.put(SearchConstants.MARKERID, new SolrPropertyMapper(IndexConstants.SNP_MARKERID));
		propertyMap.put(SearchConstants.STARTCOORDINATE, new SolrPropertyMapper(IndexConstants.SNP_STARTCOORDINATE));
		
		propertyMap.put(SearchConstants.STRAINS, new SolrPropertyMapper(IndexConstants.SNP_STRAINS));
		propertyMap.put(SearchConstants.DIFF_STRAINS, new SolrPropertyMapper(IndexConstants.SNP_DIFF_STRAINS));
		propertyMap.put(SearchConstants.SAME_STRAINS, new SolrPropertyMapper(IndexConstants.SNP_SAME_STRAINS));
		
		propertyMap.put(SearchConstants.VARIATIONCLASS, new SolrPropertyMapper(IndexConstants.SNP_VARIATIONCLASS));

		keyString = IndexConstants.SNP_CONSENSUSSNPID;
		otherString = IndexConstants.SNP_JSONSTRING;

		highlightRequireFieldMatch = false;
	}

}
