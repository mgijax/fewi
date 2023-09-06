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
import org.jax.mgi.fewi.searchUtil.entities.SolrRecombinaseMatrixCell;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrRecombinaseMatrixCellHunter extends SolrHunter<SolrRecombinaseMatrixCell> {

    public SolrRecombinaseMatrixCellHunter() {
        /*
         * Set up the sort mapping.  These are fields we can sort by in Solr.
         */
        sortMap.put(SortConstants.BY_COLUMN, new SolrSortMapper(IndexConstants.BY_COLUMN));
        
        /*
         * Setup the property map.  This maps from the properties of the incoming filter list to the
         * corresponding field names in the Solr implementation.  As we only allow filtering by three
         * fields, those are the only ones we define here.
         */
        propertyMap.put(SearchConstants.CM_MARKER_ID, new SolrPropertyMapper(IndexConstants.MRK_ID));
        propertyMap.put(SearchConstants.CM_PARENT_ANATOMY_ID, new SolrPropertyMapper(IndexConstants.PARENT_ANATOMY_ID));
        propertyMap.put(SearchConstants.COLUMN_ID, new SolrPropertyMapper(IndexConstants.COLUMN_ID));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
         keyString = IndexConstants.UNIQUE_KEY;
    }

    @Value("${solr.recombinaseMatrix.url}")
    public void setSolrUrl(String solrUrl) {
    	super.solrUrl = solrUrl;
    }

	@Override
    protected void packInformation (QueryResponse rsp, SearchResults<SolrRecombinaseMatrixCell> sr, SearchParams sp) {
    	logger.debug ("Entering SolrMPCorrelationMatrixCellHunter.packInformation()");

    	List<SolrRecombinaseMatrixCell> cells = new ArrayList<SolrRecombinaseMatrixCell>();
    	
    	SolrDocumentList sdl = rsp.getResults();
    	SolrRecombinaseMatrixCell cell;

    	for (SolrDocument doc : sdl) {
    		cell = new SolrRecombinaseMatrixCell();

    		cell.setUniqueKey((String) doc.getFieldValue(IndexConstants.UNIQUE_KEY));
    		cell.setAnatomyID((String) doc.getFieldValue(IndexConstants.ANATOMY_ID));
    		cell.setAnatomyTerm((String) doc.getFieldValue(IndexConstants.ANATOMY_TERM));
    		cell.setAllResults((Integer) doc.getFieldValue(IndexConstants.ALL_RESULTS));
    		cell.setDetectedResults((Integer) doc.getFieldValue(IndexConstants.DETECTED_RESULTS));
    		cell.setNotDetectedResults((Integer) doc.getFieldValue(IndexConstants.NOT_DETECTED_RESULTS));
    		cell.setAnyAmbiguous((Integer) doc.getFieldValue(IndexConstants.ANY_AMBIGUOUS));
    		cell.setCellType((String) doc.getFieldValue(IndexConstants.CELL_TYPE));
    		cell.setOrganism((String) doc.getFieldValue(IndexConstants.ORGANISM));
    		cell.setColumnID((String) doc.getFieldValue(IndexConstants.COLUMN_ID));
    		cell.setSymbol((String) doc.getFieldValue(IndexConstants.SYMBOL));
    		cell.setByColumn((Integer) doc.getFieldValue(IndexConstants.BY_COLUMN));
    		cell.setChildren((Integer) doc.getFieldValue(IndexConstants.CHILDREN));
    		cell.setAmbiguousOrNotDetectedChildren((Integer) doc.getFieldValue(IndexConstants.AMBIGUOUS_OR_NOT_DETECTED_DESCENDANTS));

	    	cells.add(cell);
    	}
    	sr.setResultObjects(cells);
    	logger.debug ("Exiting packInformation() with " + sr.getResultObjects().size() + " cells");
    }
}
