package org.jax.mgi.fewi.searchUtil.entities;

import org.jax.mgi.fewi.matrix.DagEdge;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.snpdatamodel.document.ESEntity;

public class ESDagEdge extends ESEntity implements SolrGxdEntity,DagEdge {
	private String parentId;
	private String parentTerm;
	private Integer parentStartStage;
	private Integer parentEndStage;
	private String childId;
	private String childTerm;
	private Integer childStartStage;
	private Integer childEndStage;
	
	public String getParentId() {
		return parentId;
	}
	public String getParentTerm() {
		return parentTerm;
	}
	public Integer getParentStartStage() {
		return parentStartStage;
	}
	public Integer getParentEndStage() {
		return parentEndStage;
	}
	public String getChildId() {
		return childId;
	}
	public String getChildTerm() {
		return childTerm;
	}
	public Integer getChildStartStage() {
		return childStartStage;
	}
	public Integer getChildEndStage() {
		return childEndStage;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public void setParentTerm(String parentTerm) {
		this.parentTerm = parentTerm;
	}
	public void setParentStartStage(Integer parentStartStage) {
		this.parentStartStage = parentStartStage;
	}
	public void setParentEndStage(Integer parentEndStage) {
		this.parentEndStage = parentEndStage;
	}
	public void setChildId(String childId) {
		this.childId = childId;
	}
	public void setChildTerm(String childTerm) {
		this.childTerm = childTerm;
	}
	public void setChildStartStage(Integer childStartStage) {
		this.childStartStage = childStartStage;
	}
	public void setChildEndStage(Integer childEndStage) {
		this.childEndStage = childEndStage;
	}
	
	@Override
	public String toString() {
		return "edge (" + parentId + "," + childId
				+ ")";
	}
}
