package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fe.datamodel.Annotation;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrMarkerAnnotationSummaryHunter extends SolrHunter<Annotation> {

	/***
	 * The constructor sets up this hunter so that it is specific to finding
	 * a sequence key given any possible sequence id.
	 */
	public SolrMarkerAnnotationSummaryHunter() {

		/*
		 * Set up the sort mapping
		 */

		sortMap.put(SortConstants.VOC_TERM, new SolrSortMapper(IndexConstants.VOC_TERM));
		sortMap.put(SortConstants.VOC_BY_DAG_STRUCT, new SolrSortMapper(IndexConstants.VOC_BY_DAG_STRUCT));
		sortMap.put(SortConstants.VOC_DAG_NAME, new SolrSortMapper(IndexConstants.VOC_DAG_NAME));
		sortMap.put(SortConstants.MRK_BY_EVIDENCE_CODE, new SolrSortMapper(IndexConstants.BY_EVIDENCE_CODE));
		sortMap.put(SortConstants.MRK_BY_EVIDENCE_TERM, new SolrSortMapper(IndexConstants.BY_EVIDENCE_TERM));
		sortMap.put(SortConstants.BY_CATEGORY, new SolrSortMapper(IndexConstants.BY_CATEGORY));
		sortMap.put(SortConstants.BY_REFERENCE, new SolrSortMapper(IndexConstants.BY_REFERENCE));
		sortMap.put(SortConstants.MRK_BY_SYMBOL, new SolrSortMapper(IndexConstants.MRK_BY_SYMBOL));
		sortMap.put(SortConstants.BY_ISOFORM, new SolrSortMapper(IndexConstants.BY_ISOFORM));

		/*
		 * Setup the property map.  This maps from the properties of the incoming
		 * filter list to the corresponding field names in the Solr implementation.
		 */
		propertyMap.put(SearchConstants.VOC_DAG_NAME, new SolrPropertyMapper(IndexConstants.VOC_DAG_NAME));
		propertyMap.put(SearchConstants.EVIDENCE_CODE, new SolrPropertyMapper(IndexConstants.BY_EVIDENCE_CODE));
		propertyMap.put(SearchConstants.EVIDENCE_TERM, new SolrPropertyMapper(IndexConstants.BY_EVIDENCE_TERM));
		propertyMap.put(SearchConstants.REF_KEY, new SolrPropertyMapper(IndexConstants.REF_KEY));
		propertyMap.put(SearchConstants.MRK_KEY, new SolrPropertyMapper(IndexConstants.MRK_KEY));
		propertyMap.put(SearchConstants.VOC_VOCAB, new SolrPropertyMapper(IndexConstants.VOC_VOCAB));
		propertyMap.put(SearchConstants.VOC_RESTRICTION, new SolrPropertyMapper(IndexConstants.VOC_QUALIFIER));
		propertyMap.put(SearchConstants.GO_ID, new SolrPropertyMapper(IndexConstants.VOC_ID));

		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output.  In this case we want the standard list of
		 * object keys returned.
		 */

		keyString = IndexConstants.ANNOTATION_KEY;

	}

	@Value("${solr.markerAnnotation.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	public void setFacet(String facet) {
		facetString = facet;
	}
}
