package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.List;


public class InteractionQueryForm {
	
	private List<String> markerIDs = new ArrayList<String>();
	private List<String> relationshipTermFilter = new ArrayList<String>();
	private List<String> validationFilter = new ArrayList<String>();
	private List<String> dataSourceFilter = new ArrayList<String>();
	private String scoreFilter = null;
	private boolean requireScore = false;

	public List<String> getMarkerIDs() {
	    return markerIDs;
	}
	public void setMarkerIDs(List<String> markerIDs) {
	    this.markerIDs = markerIDs;
	}

	public List<String> getRelationshipTermFilter() {
	    return relationshipTermFilter;
	}
	public void setRelationshipTermFilter(List<String> relationshipTermFilter) {
	    this.relationshipTermFilter = relationshipTermFilter;
	}

	public List<String> getValidationFilter() {
	    return validationFilter;
	}
	public void setValidationFilter(List<String> validationFilter) {
	    this.validationFilter = validationFilter;
	}

	public List<String> getDataSourceFilter() {
	    return dataSourceFilter;
	}
	public void setDataSourceFilter(List<String> dataSourceFilter) {
	    this.dataSourceFilter = dataSourceFilter;
	}

	public String getScoreFilter() {
	    return scoreFilter;
	}
	public void setScoreFilter(String scoreFilter) {
	    this.scoreFilter = scoreFilter;
	}

	public boolean getRequireScore() {
	    return requireScore;
	}
	public void setRequireScore(boolean requireScore) {
	    this.requireScore = requireScore;
	}

	@Override
	public String toString() {
		return "InteractionQueryForm [markerIDs = " + markerIDs
			+ ", dataSourceFilter = " + dataSourceFilter
			+ ", relationshipTermFilter = " + relationshipTermFilter
			+ ", validationFilter = " + validationFilter + "]";

	}
}
