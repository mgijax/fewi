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
import org.jax.mgi.fewi.searchUtil.entities.SolrMPAnnotation;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrMPAnnotationHunter extends SolrHunter<SolrMPAnnotation> {

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * a sequence key given any possible sequence id.
     */
    public SolrMPAnnotationHunter() {

        /*
         * Set up the sort mapping.  These are fields we can sort by in Solr.
         */
        sortMap.put(SortConstants.GENOTYPE_TERM,
		new SolrSortMapper(IndexConstants.BY_GENOTYPE_TERM));
        
        /*
         * Setup the property map.  This maps from the properties of the
	 * incoming filter list to the corresponding field names in the Solr
	 * implementation.  As we only allow filtering by two fields, those
	 * are the only ones we define here.
         */
        propertyMap.put(SearchConstants.MRK_KEY,
		new SolrPropertyMapper(IndexConstants.MRK_KEY));
        propertyMap.put(SearchConstants.TERM_ID,
		new SolrPropertyMapper(IndexConstants.TERM_ID));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
         keyString = IndexConstants.ANNOTATION_KEY;
    }

    @Value("${solr.mpAnnotation.url}")
    public void setSolrUrl(String solrUrl) {
	super.solrUrl = solrUrl;
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void packInformation (QueryResponse rsp, SearchResults<SolrMPAnnotation> sr,
	SearchParams sp) {

	logger.debug ("Entering SolrMPAnnotationHunter.packInformation()");

	List<SolrMPAnnotation> annots = new ArrayList<SolrMPAnnotation>();

	SolrDocumentList sdl = rsp.getResults();
	SolrMPAnnotation annot;

	for (SolrDocument doc : sdl) {
	    annot = new SolrMPAnnotation();

	    annot.setAnnotationKey ((String) doc.getFieldValue(IndexConstants.ANNOTATION_KEY));
	    annot.setGenotypeKey ((String) doc.getFieldValue(IndexConstants.GENOTYPE_KEY));
	    annot.setAllelePairs ((String) doc.getFieldValue(IndexConstants.ALLELE_PAIRS));
	    annot.setBackgroundStrain ((String) doc.getFieldValue(IndexConstants.BACKGROUND_STRAIN));
	    annot.setTerm ((String) doc.getFieldValue(IndexConstants.TERM));
	    annot.setTermID ((String) doc.getFieldValue(IndexConstants.ANNOTATED_TERM_ID));
	    annot.setReferences ((List<String>) doc.getFieldValue(IndexConstants.JNUM_ID));
	    annot.setStrainID((String) doc.getFieldValue(IndexConstants.STRAIN_ID));

	    annots.add(annot);
	}
	sr.setResultObjects(annots);
	logger.debug ("Exiting packInformation() with "
		+ sr.getResultObjects().size() + " annotations");
    }
}
