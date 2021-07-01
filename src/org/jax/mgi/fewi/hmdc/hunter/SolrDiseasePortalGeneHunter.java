package org.jax.mgi.fewi.hmdc.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpMarker;
import org.jax.mgi.fewi.hunter.SolrHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrDiseasePortalGeneHunter extends SolrHunter<SolrHdpEntityInterface> {

	public SolrDiseasePortalGeneHunter() {
		keyString = DiseasePortalFields.UNIQUE_KEY;
		
		// These are added to reduce the amount of data coming back from solr
		// over the wire and speeds up the query.
		// All the search fields are not returned
		returnedFields.add(DiseasePortalFields.MARKER_KEY);
		returnedFields.add(DiseasePortalFields.ORGANISM);
		returnedFields.add(DiseasePortalFields.GRID_CLUSTER_KEY);
		returnedFields.add(DiseasePortalFields.HOMOLOGY_CLUSTER_KEY);
		returnedFields.add(DiseasePortalFields.HOMOLOGENE_ID);
		returnedFields.add(DiseasePortalFields.HOMOLOGY_SOURCE);
		returnedFields.add(DiseasePortalFields.MARKER_NAME);
		returnedFields.add(DiseasePortalFields.MARKER_SYMBOL);
		returnedFields.add(DiseasePortalFields.MARKER_MGI_ID);
		returnedFields.add(DiseasePortalFields.MARKER_FEATURE_TYPE);
		returnedFields.add(DiseasePortalFields.FILTERABLE_FEATURE_TYPES);
		returnedFields.add(DiseasePortalFields.LOCATION_DISPLAY);
		returnedFields.add(DiseasePortalFields.COORDINATE_DISPLAY);
		returnedFields.add(DiseasePortalFields.BUILD_IDENTIFIER);
		returnedFields.add(DiseasePortalFields.MARKER_DISEASE);
		returnedFields.add(DiseasePortalFields.MOUSE_MARKER_SYSTEM);
		returnedFields.add(DiseasePortalFields.HUMAN_MARKER_SYSTEM);
		returnedFields.add(DiseasePortalFields.MARKER_ALL_REF_COUNT);
		returnedFields.add(DiseasePortalFields.MARKER_DISEASE_REF_COUNT);
		returnedFields.add(DiseasePortalFields.MARKER_IMSR_COUNT);
		
		groupFields.put(DiseasePortalFields.MARKER_KEY, DiseasePortalFields.MARKER_KEY);
		
	}

	
	@Override
	protected void packInformationByGroup(QueryResponse rsp, SearchResults<SolrHdpEntityInterface> sr, SearchParams sp) {

		GroupResponse gr = rsp.getGroupResponse();
		
		GroupCommand gc = gr.getValues().get(0);

		// total count of groups
		int groupCount = gc.getNGroups();
		sr.setTotalCount(groupCount);
		
		List<Group> groups = gc.getValues();	
		
		List<String> keys = new ArrayList<String>();
		
		
		for (Group g : groups) {
			String key = g.getGroupValue();
			SolrDocument doc = g.getResult().get(0);

			Integer markerKey = (Integer)doc.getFieldValue(DiseasePortalFields.MARKER_KEY);
			// return just the marker key for now
			//sr.addResultObjects(markerKey);
			if(markerKey != null) keys.add(markerKey.toString());
			String markerKeyString = markerKey != null ? markerKey.toString() : "";

			SolrHdpMarker marker = new SolrHdpMarker();
			marker.setMarkerKey(markerKey.toString());
			marker.setOrganism((String)doc.getFieldValue(DiseasePortalFields.ORGANISM));
			marker.setGridClusterKey((Integer)doc.getFieldValue(DiseasePortalFields.GRID_CLUSTER_KEY));
			marker.setHomologyClusterKey((Integer)doc.getFieldValue(DiseasePortalFields.HOMOLOGY_CLUSTER_KEY));
			marker.setHomologeneId((String)doc.getFieldValue(DiseasePortalFields.HOMOLOGENE_ID));
			marker.setHomologySource((String)doc.getFieldValue(DiseasePortalFields.HOMOLOGY_SOURCE));
			marker.setName((String)doc.getFieldValue(DiseasePortalFields.MARKER_NAME));
			marker.setSymbol((String)doc.getFieldValue(DiseasePortalFields.MARKER_SYMBOL));
			marker.setMgiId((String)doc.getFieldValue(DiseasePortalFields.MARKER_MGI_ID));
			marker.setType((String)doc.getFieldValue(DiseasePortalFields.MARKER_FEATURE_TYPE));
			if (null != doc.getFieldValue(DiseasePortalFields.FILTERABLE_FEATURE_TYPES)) {
				marker.setFilterableFeatureType((List<String>) doc.getFieldValue(DiseasePortalFields.FILTERABLE_FEATURE_TYPES));
			}
			marker.setLocation((String)doc.getFieldValue(DiseasePortalFields.LOCATION_DISPLAY));
			marker.setCoordinate((String)doc.getFieldValue(DiseasePortalFields.COORDINATE_DISPLAY));
			marker.setCoordinateBuild((String)doc.getFieldValue(DiseasePortalFields.BUILD_IDENTIFIER));
			marker.setDisease((List<String>)doc.getFieldValue(DiseasePortalFields.MARKER_DISEASE));
			marker.setMouseSystem((List<String>)doc.getFieldValue(DiseasePortalFields.MOUSE_MARKER_SYSTEM));
			marker.setHumanSystem((List<String>)doc.getFieldValue(DiseasePortalFields.HUMAN_MARKER_SYSTEM));
			marker.setAllRefCount((Integer)doc.getFieldValue(DiseasePortalFields.MARKER_ALL_REF_COUNT));
			marker.setDiseaseRefCount((Integer)doc.getFieldValue(DiseasePortalFields.MARKER_DISEASE_REF_COUNT));
			marker.setImsrCount((Integer)doc.getFieldValue(DiseasePortalFields.MARKER_IMSR_COUNT));
			keys.add(markerKeyString);
			sr.addResultObjects(marker);

			if (keyString != null) {
				keys.add(key);
			}
		}

		if (keys != null) {
			sr.setResultKeys(keys);
		}
	}

	@Value("${solr.dp.gene.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
