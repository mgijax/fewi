package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrAnatomyTerm;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrAnatomyTermHunter extends SolrHunter<SolrAnatomyTerm> {

    public SolrAnatomyTermHunter() {

        /*
         * Set up the sort mapping.  These are fields we can sort by in Solr.
         */
        sortMap.put(SortConstants.GXD_STRUCTURE,
        		new SolrSortMapper(IndexConstants.BY_GENOTYPE_TERM));
        
        /*
         * Setup the property map.  This maps from the properties of the
		 * incoming filter list to the corresponding field names in the Solr
		 * implementation.  As we only allow filtering by two fields, those
		 * are the only ones we define here.
         */
        propertyMap.put(SearchConstants.STRUCTURE,
        		new SolrPropertyMapper(IndexConstants.STRUCTUREAC_STRUCTURE));
        propertyMap.put(SearchConstants.SYNONYM,
        		new SolrPropertyMapper(IndexConstants.STRUCTUREAC_SYNONYM));
        
        propertyMap.put(SearchConstants.ACC_ID,
        		new SolrPropertyMapper(IndexConstants.ACC_ID));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
         keyString = IndexConstants.STRUCTUREAC_KEY;
    }

    @Value("${solr.anatomyTerm.url}")
    public void setSolrUrl(String solrUrl) {
	super.solrUrl = solrUrl;
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void packInformation (QueryResponse rsp, SearchResults<SolrAnatomyTerm> sr, SearchParams sp) {

	logger.debug ("Entering SolrAnatomyTermHunter.packInformation()");

	List<SolrAnatomyTerm> terms = new ArrayList<SolrAnatomyTerm>();

	SolrDocumentList sdl = rsp.getResults();
	SolrAnatomyTerm term;

	for (SolrDocument doc : sdl) {
	    term = new SolrAnatomyTerm();

	    term.setStructureKey ((String)
	    		doc.getFieldValue(IndexConstants.STRUCTUREAC_KEY));
	    term.setAccID((String)
	    		doc.getFieldValue(IndexConstants.ACC_ID));
	    term.setStructure ((String)
	    		doc.getFieldValue(IndexConstants.STRUCTUREAC_STRUCTURE));
	    term.setStartStage ((String)
	    		doc.getFieldValue(IndexConstants.GXD_START_STAGE));
	    term.setEndStage ((String)
	    		doc.getFieldValue(IndexConstants.GXD_END_STAGE));
	    term.setSynonyms ((List<String>)
	    		doc.getFieldValue(IndexConstants.STRUCTUREAC_SYNONYM));

	    terms.add(term);
	}
	sr.setResultObjects(terms);
	logger.debug ("Exiting packInformation() with "
		+ sr.getResultObjects().size() + " terms");
    }
}
