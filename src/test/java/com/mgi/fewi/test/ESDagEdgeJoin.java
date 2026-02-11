package com.mgi.fewi.test;

import java.util.List;

import org.jax.mgi.fewi.searchUtil.entities.ESDagEdge;
import org.jax.mgi.shr.fe.indexconstants.DagEdgeFields;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class ESDagEdgeJoin extends ESDagEdge  {

	private String markerSymbol;
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@JsonSetter(nulls = Nulls.AS_EMPTY)
	private List<Integer> posRExact;
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@JsonSetter(nulls = Nulls.AS_EMPTY)	
	private List<String> emapsId;
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@JsonSetter(nulls = Nulls.AS_EMPTY)	
	private List<String> assayMgiid;
	
	public static final List<String> RETURN_FIELDS = List.of(DagEdgeFields.PARENT_ID, DagEdgeFields.PARENT_TERM,
			DagEdgeFields.PARENT_START_STAGE, DagEdgeFields.PARENT_END_STAGE, DagEdgeFields.CHILD_ID,
			DagEdgeFields.CHILD_TERM, DagEdgeFields.CHILD_START_STAGE, DagEdgeFields.CHILD_END_STAGE);
	
	@Override
	public String toString() {
		String pos = to(posRExact);
		String emaps = to(emapsId);
		String mgiid = to(assayMgiid);
		return markerSymbol + " : " + mgiid + "," + emaps + "," + getParentId() + "," + getChildId() + "," + getParentTerm() + "," 
			+ getChildTerm() + " : " + pos;
	}	
	
	private String to(List list) {
		if ( list == null || list.size() < 1) {
			return "";
		}
		String ret = "";
		int i = 0;
		for (Object e: list) {
			if ( i > 0 ) {
				ret += ",";
			}
			ret += e;
			i++;
			if ( i > 2) {
				break;
			}
		}
		return ret;
	}
	
	public String getMarkerSymbol() {
		return markerSymbol;
	}

	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}

	public List<String> getEmapsId() {
		return emapsId;
	}

	public void setEmapsId(List<String> emapsId) {
		this.emapsId = emapsId;
	}

	public List<Integer> getPosRExact() {
		return posRExact;
	}

	public void setPosRExact(List<Integer> posRExact) {
		this.posRExact = posRExact;
	}

	public List<String> getAssayMgiid() {
		return assayMgiid;
	}

	public void setAssayMgiid(List<String> assayMgiid) {
		this.assayMgiid = assayMgiid;
	}
	
}
