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
import org.jax.mgi.fewi.searchUtil.entities.SolrMPCorrelationMatrixCell;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrMPCorrelationMatrixCellHunter extends SolrHunter<SolrMPCorrelationMatrixCell> {

    public SolrMPCorrelationMatrixCellHunter() {
        /*
         * Set up the sort mapping.  These are fields we can sort by in Solr.
         */
        sortMap.put(SortConstants.BY_GENOCLUSTER, new SolrSortMapper(IndexConstants.BY_GENOCLUSTER));
        
        /*
         * Setup the property map.  This maps from the properties of the incoming filter list to the
         * corresponding field names in the Solr implementation.  As we only allow filtering by two
         * fields, those are the only ones we define here.
         */
        propertyMap.put(SearchConstants.CM_MARKER_ID, new SolrPropertyMapper(IndexConstants.MRK_ID));
        propertyMap.put(SearchConstants.CM_PARENT_ANATOMY_ID, new SolrPropertyMapper(IndexConstants.PARENT_ANATOMY_ID));
        propertyMap.put(SearchConstants.GENOCLUSTER_KEY, new SolrPropertyMapper(IndexConstants.GENOCLUSTER_KEY));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
         keyString = IndexConstants.UNIQUE_KEY;
    }

    @Value("${solr.mpCorrelationMatrix.url}")
    public void setSolrUrl(String solrUrl) {
    	super.solrUrl = solrUrl;
    }

	@Override
    protected void packInformation (QueryResponse rsp, SearchResults<SolrMPCorrelationMatrixCell> sr, SearchParams sp) {
    	logger.debug ("Entering SolrMPCorrelationMatrixCellHunter.packInformation()");

    	List<SolrMPCorrelationMatrixCell> cells = new ArrayList<SolrMPCorrelationMatrixCell>();
    	
    	SolrDocumentList sdl = rsp.getResults();
    	SolrMPCorrelationMatrixCell cell;

    	for (SolrDocument doc : sdl) {
    		cell = new SolrMPCorrelationMatrixCell();

    		cell.setUniqueKey((String) doc.getFieldValue(IndexConstants.UNIQUE_KEY));
    		cell.setAllelePairs((String) doc.getFieldValue(IndexConstants.ALLELE_PAIRS));
    		cell.setAnatomyID((String) doc.getFieldValue(IndexConstants.ANATOMY_ID));
    		cell.setAnatomyTerm((String) doc.getFieldValue(IndexConstants.ANATOMY_TERM));
    		cell.setAnnotationCount((Integer) doc.getFieldValue(IndexConstants.ANNOTATION_COUNT));
    		cell.setByGenocluster((Integer) doc.getFieldValue(IndexConstants.BY_GENOCLUSTER));
    		cell.setGenoclusterKey((Integer) doc.getFieldValue(IndexConstants.GENOCLUSTER_KEY));
    		cell.setHasBackgroundSensitivity((Integer) doc.getFieldValue(IndexConstants.HAS_BACKGROUND_SENSITIVITY));
    		cell.setIsNormal((Integer) doc.getFieldValue(IndexConstants.IS_NORMAL));
    		cell.setMarkerSymbol((String) doc.getFieldValue(IndexConstants.MRK_SYMBOL));
    		cell.setChildren((Integer) doc.getFieldValue(IndexConstants.CHILDREN));

	    	cells.add(cell);
    	}
    	sr.setResultObjects(cells);
    	logger.debug ("Exiting packInformation() with " + sr.getResultObjects().size() + " cells");
    }
}
