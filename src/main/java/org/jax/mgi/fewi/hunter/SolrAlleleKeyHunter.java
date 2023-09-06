package org.jax.mgi.fewi.hunter;

import java.util.Arrays;

import org.jax.mgi.fe.datamodel.Allele;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrAlleleKeyHunter extends SolrHunter<Allele> {
    
    /***
     * The constructor sets up this hunter so that it is specific to finding
     * a allele key given any possible allele id.
     */
    public SolrAlleleKeyHunter() {        
        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        propertyMap.put(SearchConstants.ALL_KEY, new SolrPropertyMapper(IndexConstants.ALL_KEY));
        propertyMap.put(SearchConstants.ALL_ID, new SolrPropertyMapper(IndexConstants.ALL_ID));
        propertyMap.put(SearchConstants.ALL_TYPE, new SolrPropertyMapper(IndexConstants.ALL_TYPE));
        propertyMap.put(SearchConstants.ALL_SUBTYPE, new SolrPropertyMapper(IndexConstants.ALL_SUBTYPE));
        propertyMap.put(SearchConstants.ALL_IS_WILD_TYPE, new SolrPropertyMapper(IndexConstants.ALL_IS_WILD_TYPE));
        propertyMap.put(SearchConstants.ALL_COLLECTION, new SolrPropertyMapper(IndexConstants.ALL_COLLECTION));
        propertyMap.put(SearchConstants.ALL_IS_CELLLINE, new SolrPropertyMapper(IndexConstants.ALL_IS_CELLLINE));
        
        /*
         * Phenotype searches
         */
        propertyMap.put(SearchConstants.ALL_PHENOTYPE,
    		new SolrPropertyMapper(Arrays.asList(
    				IndexConstants.ALL_PHENO_ID,
    				IndexConstants.ALL_PHENO_TEXT),"OR"));
     
        /*
         * Reference searches
         */
        propertyMap.put(SearchConstants.REF_KEY,new SolrPropertyMapper(IndexConstants.REF_KEY));
        propertyMap.put(SearchConstants.JNUM_ID,new SolrPropertyMapper(IndexConstants.JNUM_ID));
        propertyMap.put(SearchConstants.ALL_HAS_DO,new SolrPropertyMapper(IndexConstants.ALL_HAS_DO));

        
        /*
         * Marker searches
         */
        propertyMap.put(SearchConstants.MRK_ID,new SolrPropertyMapper(IndexConstants.MRK_ID));
        propertyMap.put(SearchConstants.ALL_NOMEN,new SolrPropertyMapper(IndexConstants.ALL_NOMEN));
        
        /*
         * Location searchs
         */
        propertyMap.put(SearchConstants.CHROMOSOME,new SolrPropertyMapper(IndexConstants.CHROMOSOME));
        propertyMap.put(SearchConstants.START_COORD,new SolrPropertyMapper(IndexConstants.START_COORD));
        propertyMap.put(SearchConstants.END_COORD,new SolrPropertyMapper(IndexConstants.END_COORD));
        propertyMap.put(SearchConstants.CM_OFFSET,new SolrPropertyMapper(IndexConstants.CM_OFFSET));
        propertyMap.put(SearchConstants.CYTOGENETIC_OFFSET,new SolrPropertyMapper(IndexConstants.CYTOGENETIC_OFFSET));

        
        /*
         * Sorts
         */
        this.sortMap.put(SortConstants.ALL_BY_TRANSMISSION,new SolrSortMapper(IndexConstants.ALL_TRANSMISSION_SORT));
        this.sortMap.put(SortConstants.ALL_BY_SYMBOL,new SolrSortMapper(IndexConstants.ALL_SYMBOL_SORT));
        this.sortMap.put(SortConstants.ALL_BY_TYPE,new SolrSortMapper(IndexConstants.ALL_TYPE_SORT));
        this.sortMap.put(SortConstants.ALL_BY_CHROMOSOME,new SolrSortMapper(IndexConstants.ALL_CHR_SORT));
        this.sortMap.put(SortConstants.ALL_BY_DISEASE,new SolrSortMapper(IndexConstants.ALL_DISEASE_SORT));

        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
        
        keyString = IndexConstants.ALL_KEY;
        
        
        returnedFields.add(IndexConstants.ALL_KEY);
        
    }
	
	@Value("${solr.allele.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}