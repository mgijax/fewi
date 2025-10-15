package org.jax.mgi.fewi.summary;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdAssay;

public class GxdAssaySummaryRow {

	// -------------------
	// instance variables
	// -------------------

	// encapsulated row object
	private final ESGxdAssay assay;

	// config values
	String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
	String pywiUrl = ContextLoader.getConfigBean().getProperty("WI_URL");

	private String score;

	// -------------
	// constructors
	// -------------
	public GxdAssaySummaryRow(ESGxdAssay assay) {
		this.assay = assay;
	}

	// -------------------------------------------------------------------
	// public instance methods; JSON serializer will call all public methods
	// -------------------------------------------------------------------
	public void setScore(String score) {
		this.score = score;
	}

	public String getScore() {
		return score;
	}

	public String getGene() {

		// return "<a href='" + fewiUrl + "marker/" + mrk.getPrimaryID() + "'>"
		// + mrk.getSymbol() + "</a>";

		return assay.getMarkerSymbol();
	}

	public String getAssayID() {
		// RNA-Seq data have no images, and the data link should add
		// a special filter for this experiment ID.
		if (assay.getAssayType().startsWith("RNA-Seq")) {
			return "<a href='#" + assay.getJNum() + "' onClick='filterByExperiment(\"" + assay.getJNum() + "\"); return false;' onContextMenu='return false;' class='rsFilterLink'><img src='/assets/images/filter.png' width='10' height='10' title='Click to Filter Results by Experiment' class='rsFilterIcon'/></a> data set <span>(" + assay.getJNum() + ")</span>";
		}

		// classical assays
		return "<a href='" + fewiUrl + "assay/" + assay.getAssayMgiid()
				+ "'>data</a><span> (" + assay.getAssayMgiid() + ")</span>";
	}

	public String getAssayType() {
		return assay.getAssayType();
	}

	public String getReference() {
		if (assay.getJNum().startsWith("J:")) {
			return "<a href='" + fewiUrl + "reference/" + assay.getJNum() + "'>"
				+ assay.getJNum() + "</a> " + assay.getMiniCitation();
		}
		return "<a href='" + fewiUrl + "gxd/htexp_index/summary?arrayExpressID=" + assay.getJNum() + "'>"
			+ assay.getJNum() + "</a> " + assay.getMiniCitation();
	}

}
