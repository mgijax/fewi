package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.ESPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.snpdatamodel.ConsensusSNP;
import org.springframework.stereotype.Repository;

@Repository
public class ESSNPBaseHunter extends ESHunter<ConsensusSNP> {

	public ESSNPBaseHunter() {
		super(ConsensusSNP.class);
		propertyMap.put(SearchConstants.SNPID, new ESPropertyMapper(IndexConstants.SNP_CONSENSUSSNPID));
		propertyMap.put(SearchConstants.FUNCTIONCLASS, new ESPropertyMapper(IndexConstants.SNP_FUNCTIONCLASS));
		propertyMap.put(SearchConstants.MARKERID, new ESPropertyMapper(IndexConstants.SNP_MARKERID));
		propertyMap.put(SearchConstants.STARTCOORDINATE, new ESPropertyMapper(IndexConstants.SNP_STARTCOORDINATE));
		
		propertyMap.put(SearchConstants.STRAINS, new ESPropertyMapper(IndexConstants.SNP_STRAINS));
		propertyMap.put(SearchConstants.DIFF_STRAINS, new ESPropertyMapper(IndexConstants.SNP_DIFF_STRAINS));
		propertyMap.put(SearchConstants.SAME_STRAINS, new ESPropertyMapper(IndexConstants.SNP_SAME_STRAINS));
		
		propertyMap.put(SearchConstants.VARIATIONCLASS, new ESPropertyMapper(IndexConstants.SNP_VARIATIONCLASS));

		keyString = IndexConstants.SNP_CONSENSUSSNPID;
		otherString = IndexConstants.SNP_JSONSTRING;

		highlightRequireFieldMatch = false;
	}

}
