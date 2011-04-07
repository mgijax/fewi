package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.propertyMapper.SolrReferenceTextSearchPropertyMapper;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrGxdLitSummaryHunter extends SolrHunter {

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * a sequence key given any possible sequence id.
     */
    public SolrGxdLitSummaryHunter() {
    	
    	

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.GXD_LIT_MRK_NOMEN, new SolrPropertyMapper(IndexConstants.GXD_MRK_NOMEN));
        propertyMap.put(SearchConstants.REF_YEAR, new SolrPropertyMapper(IndexConstants.REF_YEAR));
        propertyMap.put(SearchConstants.REF_JOURNAL, new SolrPropertyMapper(IndexConstants.REF_JOURNAL));
        propertyMap.put(SearchConstants.REF_AUTHOR_ANY, new SolrPropertyMapper(IndexConstants.REF_AUTHOR_FORMATTED));
        propertyMap.put(SearchConstants.REF_AUTHOR_FIRST, new SolrPropertyMapper(IndexConstants.REF_FIRST_AUTHOR));
        propertyMap.put(SearchConstants.REF_AUTHOR_LAST, new SolrPropertyMapper(IndexConstants.REF_LAST_AUTHOR));

        propertyMap.put(SearchConstants.GXD_LIT_AGE, new SolrPropertyMapper(IndexConstants.GXD_LIT_AGE));
        propertyMap.put(SearchConstants.GXD_LIT_ASSAY_TYPE, new SolrPropertyMapper(IndexConstants.GXD_LIT_ASSAY_TYPE));
        
        ArrayList <String> titleList = new ArrayList <String> ();
        titleList.add(IndexConstants.REF_TITLE_STEMMED);
        titleList.add(IndexConstants.REF_TITLE_UNSTEMMED);
        
        ArrayList <String> abstractList = new ArrayList <String> ();
        abstractList.add(IndexConstants.REF_ABSTRACT_STEMMED);
        abstractList.add(IndexConstants.REF_ABSTRACT_UNSTEMMED);
        
        propertyMap.put(SearchConstants.REF_TEXT_ABSTRACT, new SolrReferenceTextSearchPropertyMapper(abstractList, "OR"));
        propertyMap.put(SearchConstants.REF_TEXT_TITLE, new SolrReferenceTextSearchPropertyMapper(titleList, "OR"));
        
        ArrayList <String> titleAbstractList = new ArrayList <String> ();
        titleAbstractList.add(IndexConstants.REF_TITLE_ABSTRACT_STEMMED);
        titleAbstractList.add(IndexConstants.REF_TITLE_ABSTRACT_UNSTEMMED);
        
        propertyMap.put(SearchConstants.REF_TEXT_TITLE_ABSTRACT, new SolrReferenceTextSearchPropertyMapper(titleAbstractList, "OR"));
        
        propertyMap.put(SearchConstants.MRK_KEY, new SolrPropertyMapper(IndexConstants.MRK_KEY));
        propertyMap.put(SearchConstants.REF_KEY, new SolrPropertyMapper(IndexConstants.REF_KEY));
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
        keyString = IndexConstants.GXD_LIT_SINGLE_KEY;

        highlightFields.add(IndexConstants.REF_TITLE_STEMMED);
        highlightFields.add(IndexConstants.REF_TITLE_UNSTEMMED);
        highlightFields.add(IndexConstants.REF_TITLE_ABSTRACT_STEMMED);
        highlightFields.add(IndexConstants.REF_TITLE_ABSTRACT_UNSTEMMED);
        highlightFields.add(IndexConstants.REF_FIRST_AUTHOR);
        highlightFields.add(IndexConstants.REF_LAST_AUTHOR);
        highlightFields.add(IndexConstants.REF_AUTHOR_FORMATTED);
        
        /* A reverse Mapping of Highlightable fields in the index to what 
         * parameter it came from
         */
        
        fieldToParamMap.put(IndexConstants.REF_TITLE_STEMMED, SearchConstants.GXD_LIT_LONG_CITATION);
        fieldToParamMap.put(IndexConstants.REF_TITLE_UNSTEMMED, SearchConstants.GXD_LIT_LONG_CITATION);
        fieldToParamMap.put(IndexConstants.REF_TITLE_ABSTRACT_STEMMED, SearchConstants.GXD_LIT_LONG_CITATION);
        fieldToParamMap.put(IndexConstants.REF_TITLE_ABSTRACT_UNSTEMMED, SearchConstants.GXD_LIT_LONG_CITATION);
        fieldToParamMap.put(IndexConstants.REF_AUTHOR_FORMATTED, SearchConstants.GXD_LIT_LONG_CITATION);
        fieldToParamMap.put(IndexConstants.REF_FIRST_AUTHOR, SearchConstants.GXD_LIT_LONG_CITATION);
        fieldToParamMap.put(IndexConstants.REF_LAST_AUTHOR, SearchConstants.GXD_LIT_LONG_CITATION);
        
    }
    
	private void checkFilter (Filter filter) {
	    if (filter.isBasicFilter()) {
	        return;
	    }
	    else {
	        List <Filter> flist = filter.getNestedFilters();
	        Boolean foundTitle = Boolean.FALSE;
	        Boolean foundAbstract = Boolean.FALSE;
	        String textToSearch = "";
	        
	        for (Filter f: flist) {
	            if (f.isBasicFilter()) {
	                if (f.getProperty().equals(SearchConstants.REF_TEXT_ABSTRACT)) {
	                    textToSearch = f.getValue();
	                    foundAbstract = Boolean.TRUE;
	                }
                    if (f.getProperty().equals(SearchConstants.REF_TEXT_TITLE)) {
                        textToSearch = f.getValue();
                        foundTitle = Boolean.TRUE;
                    }	                
	                
	            }
	            else {
	                checkFilter(f);
	            }
	        }
	        
	        if (foundTitle && foundAbstract) {
	            filter.setProperty(SearchConstants.REF_TEXT_TITLE_ABSTRACT);
	            filter.setValue(textToSearch);
	            filter.setOperator(Filter.OP_CONTAINS);
	            filter.setNestedFilters(new ArrayList<Filter> ());
	        }
	    }
	}
		
	protected SearchParams preProcessSearchParams(SearchParams searchParams) {
	    
	    Filter filter = searchParams.getFilter();
	    if (!filter.isBasicFilter()) {
	        checkFilter(filter);
	    }
	    return searchParams;
	}

    

	@Value("${solr.gxdLitIndex.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}