package org.jax.mgi.fewi.searchUtil.entities;

import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;

public class SolrGxdMarker implements SolrGxdEntity {

	String mgiid;
	String symbol;
	String name;
	String chr;
	String strand;
	String startCoord;
	String endCoord;
	String cytoband;
	String cm;
	String type;

	public String getMgiid() {
		return mgiid;
	}

	public void setMgiid(String mgiid) {
		this.mgiid = mgiid;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChr() {
		return chr;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public String getStrand() {
		return strand;
	}

	public void setStrand(String strand) {
		this.strand = strand;
	}

	public String getStartCoord() {
		return startCoord;
	}

	public void setStartCoord(String startCoord) {
		this.startCoord = startCoord;
	}

	public String getEndCoord() {
		return endCoord;
	}

	public void setEndCoord(String endCoord) {
		this.endCoord = endCoord;
	}

	public String getCytoband() {
		return cytoband;
	}

	public void setCytoband(String cytoband) {
		this.cytoband = cytoband;
	}

	public String getCm() {
		return cm;
	}

	public void setCm(String cm) {
		this.cm = cm;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
