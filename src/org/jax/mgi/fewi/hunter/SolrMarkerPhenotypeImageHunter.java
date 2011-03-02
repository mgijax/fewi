package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrMarkerPhenotypeImageHunter extends SolrHunter {
    
    /***
     * The constructor sets up this hunter so that it is specific to sequence
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
    public SolrMarkerPhenotypeImageHunter() {        
        
    	
    	sortMap.put(SortConstants.BY_DEFAULT, new SolrSortMapper(IndexConstants.BY_DEFAULT));
    	
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        
        propertyMap.put(SearchConstants.MRK_KEY, new SolrPropertyMapper(IndexConstants.MRK_KEY));
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want to actually get a 
         * specific field, and return it rather than a list of keys.
         */       
        keyString = IndexConstants.IMAGE_KEY;
        
    }
    
	protected SearchParams preProcessSearchParams(SearchParams searchParams) {
	    
	    Filter filter = searchParams.getFilter();

	    List<Filter> flist = new ArrayList<Filter> ();
	    flist.add(filter);
	    
	    Filter phenotype = new Filter(SearchConstants.IMG_CLASS, "Phenotypes", Filter.OP_EQUAL);
	    
	    Filter isThumb = new Filter(SearchConstants.IMG_IS_THUMB, "1", Filter.OP_EQUAL);
	    flist.add(phenotype);
	    flist.add(isThumb);
	    
	    Filter modifiedFilter = new Filter();
	    modifiedFilter.setNestedFilters(flist);
	    modifiedFilter.setFilterJoinClause(Filter.FC_AND);
	    
	    searchParams.setFilter(modifiedFilter);
	    return searchParams;
	    
	}
    
	@Value("${solr.image.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
   
}