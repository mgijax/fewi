package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrJoinMapper;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.ResultSetMetaData;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.StructureACResult;
import org.jax.mgi.fewi.searchUtil.entities.VocabACResult;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.shr.fe.indexconstants.ImagePaneFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Hunter for the GXD Result solr index
 *
 */

@Repository
public class SolrGxdResultHunter extends SolrGxdSummaryBaseHunter
{
    /***
     * The constructor sets up this hunter so that it is specific to sequence
     * summary pages.  Each item in the constructor sets a value that it has
     * inherited from its superclass, and then relies on the superclass to
     * perform all of the needed work via the hunt() method.
     */
    public SolrGxdResultHunter()
    {
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want to actually get a
         * specific field, and return it rather than a list of keys.
         */
        keyString = GxdResultFields.RESULT_KEY;
    }

    @Value("${solr.gxd_result.url}")
    public void setSolrUrl(String solrUrl) {
	super.solrUrl = solrUrl;
    }
}
