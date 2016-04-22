package org.jax.mgi.fewi.entities.hmdc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jax.mgi.fewi.entities.hmdc.models.GridResult;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpGridAnnotationEntry;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpGridEntry;
import org.jax.mgi.fewi.searchUtil.SearchResults;

public class JsonGridMapper implements Serializable {
	
	private SearchResults<SolrHdpGridEntry> results;
	private SearchResults<SolrHdpGridAnnotationEntry> annotationResults;
	
	private List<String> gridMPHeaders = new ArrayList<String>();
	private List<String> gridOMIMHeaders = new ArrayList<String>();
	private List<String> gridHighLights = new ArrayList<String>();




}
