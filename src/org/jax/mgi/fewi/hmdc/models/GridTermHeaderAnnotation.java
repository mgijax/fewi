package org.jax.mgi.fewi.hmdc.models;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.hmdc.visitors.GridVisitorInterface;

public class GridTermHeaderAnnotation extends AbstractGridModel {

	private Integer normalCount = 0;
	private Integer annotCount = 0;
	private Integer humanAnnotCount = 0;
	private List<String> terms = new ArrayList<String>();
	private String header = "";
	private Integer gridClusterKey = 0;
	
	public Integer getNormalCount() {
		return normalCount;
	}
	public void setNormalCount(Integer normalCount) {
		this.normalCount = normalCount;
	}
	public Integer getAnnotCount() {
		return annotCount;
	}
	public void setAnnotCount(Integer annotCount) {
		this.annotCount = annotCount;
	}
	public Integer getHumanAnnotCount() {
		return humanAnnotCount;
	}
	public void setHumanAnnotCount(Integer humanAnnotCount) {
		this.humanAnnotCount = humanAnnotCount;
	}
	public void incNormalCount() {
		normalCount += 1;
	}
	public void incAnnotCount() {
		annotCount += 1;
	}
	public void incHumanAnnotCount() {
		humanAnnotCount += 1;
	}
	public List<String> getTerms() {
		return terms;
	}
	public void setTerms(List<String> terms) {
		this.terms = terms;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public Integer getGridClusterKey() {
		return gridClusterKey;
	}
	public void setGridClusterKey(Integer gridClusterKey) {
		this.gridClusterKey = gridClusterKey;
	}
	
	@Override
	public void Accept(GridVisitorInterface pi) {
		pi.Visit(this);
	}
}
