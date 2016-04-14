package org.jax.mgi.fewi.summary;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.jax.mgi.fewi.util.FormatHelper;

public class HdpDiseaseSummaryRow {

	private final SolrVocTerm term;
	private String score;
	private List<String> highlightedFields;

	private String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

	public HdpDiseaseSummaryRow (SolrVocTerm term) {
		this.term = term;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getScore() {
		return score;
	}	

	public void setHighlightedFields(List<String> highlightedFields) {
		this.highlightedFields = highlightedFields;
	}

	public String getHighlightedFields() {
		if(highlightedFields == null) return "";
		return "<span class=\"hl\">" + StringUtils.join(highlightedFields,"<br/>") + "</span>";
	}

	public String getDisease() {
		// disease links to disease detail
		return term.getTerm();
	}

	public String getDiseaseId() {
		return term.getPrimaryId();
	}

	public int getDiseaseModels() {
		if(term.getDiseaseModelCount() == null || term.getDiseaseModelCount() < 1) return 0;
		return term.getDiseaseModelCount();
	}

	public String getMouseMarkers(){
		List<String> markers = term.getDiseaseMouseMarkers();
		String markersToDisplay =  "";
		if (markers != null) {
			markersToDisplay = FormatHelper.commaDelimit(markers);
		}
		return FormatHelper.superscript(markersToDisplay);
	}

	public String getHumanMarkers(){
		List<String> markers = term.getDiseaseHumanMarkers();
		String markersToDisplay =  "";
		if (markers != null) {
			markersToDisplay = FormatHelper.commaDelimit(markers);
		}
		return FormatHelper.superscript(markersToDisplay);
	}

	public int getRefCount(){
		if(term.getDiseaseRefCount() != null && term.getDiseaseRefCount() > 0) {
			return term.getDiseaseRefCount();
		}
		return 0;
	}
}
