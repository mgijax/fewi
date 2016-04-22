package org.jax.mgi.fewi.entities.hmdc.models;

import org.jax.mgi.fewi.entities.hmdc.visitors.GridVisitorInterface;

public class GridTermHeaderAnnotation extends AbstractGridModel {

	private boolean normal = true;
	private Integer annotCount = 0;
	private Integer humanAnnotCount = 0;
	
	public boolean isNormal() {
		return normal;
	}
	public void setNormal(boolean normal) {
		this.normal = normal;
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
	
	public void incNormal(boolean normal) {
		this.normal &= normal;
	}
	public void incAnnotCount() {
		annotCount += 1;
	}
	public void incHumanAnnotCount() {
		humanAnnotCount += 1;
	}

	@Override
	public void Accept(GridVisitorInterface pi) {
		pi.Visit(this);
	}
}
