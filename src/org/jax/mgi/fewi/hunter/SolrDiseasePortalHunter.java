package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
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
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridCluster;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridData;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrHdpEntity;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * main Hunter for the diseasePortal solr index
 *
 * This is capable of grouping by various object types to return different summaries
 *
 * 		When grouping, stored fields for objects lower in the relationship will not be relevant (and should not be accessed).
 *		However, you are free, and expected, to query on any field, regardless of which object is grouped.
 *
 *		The relationship order currently looks like this cluster->marker->genotype->term
 *		Hence, if you group by genotype, you would have access to the marker and the cluster of that genotype.
 *			But you would not be able to get every term for that genotype (only the first one, depending on the sort order).
 *
 * @author kstone
 *
 */

@Repository
public class SolrDiseasePortalHunter extends SolrDiseasePortalBaseHunter
{
    /***
     * The constructor sets up this hunter so that it is specific to diseasePortal
     * summary pages.  Each item in the constructor sets a value that it has
     * inherited from its superclass, and then relies on the superclass to
     * perform all of the needed work via the hunt() method.
     */
    public SolrDiseasePortalHunter()
    {
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want to actually get a
         * specific field, and return it rather than a list of keys.
         */
        keyString = DiseasePortalFields.UNIQUE_KEY;
    }
}
