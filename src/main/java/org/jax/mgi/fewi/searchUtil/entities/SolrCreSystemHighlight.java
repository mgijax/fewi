package org.jax.mgi.fewi.searchUtil.entities;

import java.util.List;

import org.jax.mgi.fe.datamodel.group.RecombinaseEntity;

/**
 * Holds information for highlighting systems (either anatomical or cell type header) on cre summary.
 */
public class SolrCreSystemHighlight implements RecombinaseEntity {

	private String alleleKey;
	private List<String> systems;
	private boolean detected;
	
	public String getAlleleKey() {
		return alleleKey;
	}
	public List<String> getSystems() {
		return systems;
	}
	public boolean getDetected() {
		return detected;
	}
	public void setAlleleKey(String alleleKey) {
		this.alleleKey = alleleKey;
	}
	public void setSystems(List<String> systems) {
		this.systems = systems;
	}
	public void setDetected(boolean detected) {
		this.detected = detected;
	}
	
}
