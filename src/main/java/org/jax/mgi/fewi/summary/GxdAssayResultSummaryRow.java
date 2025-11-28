package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.matrix.DetectionConverter;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;

/**
 * wrapper around a marker; represents on row in summary
 */
public class GxdAssayResultSummaryRow {
	// -------------------
	// instance variables
	// -------------------

	// encapsulated row object
	private SolrAssayResult result;

	// config values
	String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
	String pywiUrl = ContextLoader.getConfigBean().getProperty("WI_URL");
        private final String expressionAtlasUrl = ContextLoader.getExternalUrls().getProperty("ExpressionAtlas");

	private String score;

	// helper objects
	NotesTagConverter ntc;

	// -------------
	// constructors
	// -------------
	public GxdAssayResultSummaryRow(SolrAssayResult result) {
		this.result = result;

		setNTC();

		return;
	}

	private void setNTC() {
		try {
			ntc = new NotesTagConverter();
		} catch (Exception e) {
		}
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
		return result.getMarkerSymbol();
	}

	public String getAssayID() {
		if (result.getAssayType().startsWith("RNA-Seq")) {
			return "<a class='extUrl' target='_blank' href='" + expressionAtlasUrl.replace("@@@@", result.getJNum()) + "'>data</a><br/><span class='nowrap'> (" + result.getJNum() + ")</span>";
		}
		return "<a href='" + fewiUrl + "assay/" + result.getAssayMgiid()
				+ "'>data</a><span> (" + result.getAssayMgiid() + ")</span>";
	}

	public String getAssayType() {
		return result.getAssayType();
	}

	public String getAge() {
		return result.getAge();
	}

	public String getStructure() {
		return "TS" + result.getTheilerStage() + ": " + result.getPrintname();
	}

	public String getDetectionLevel() {
		if (result.getAssayType().startsWith("RNA-Seq")) {
			if (DetectionConverter.isDetected(result.getDetectionLevel())) {
				return "Yes";
			} else if (DetectionConverter.isNotDetected(result.getDetectionLevel())) {
				return "No";
			}
			return "Not Specified";
		}
		return result.getDetectionLevel();
	}

	public String getFigures() {
		List<String> formattedFigures = new ArrayList<String>(0);
		if (result.getFigures() != null) {
			for (String figure : result.getFigures()) {
				if (!figure.equals("")) {
					formattedFigures.add("<a href='" + fewiUrl + "assay/"
							+ result.getAssayMgiid() + "#"
							+ FormatHelper.makeCssSafe(figure) + "_id'>"
							+ FormatHelper.superscript(figure) + "</a>");
				}
			}
		}
		if (formattedFigures.size() > 0) {
			return StringUtils.join(formattedFigures, ", ");
		}
		return "";
	}

	public String getGenotype() {
		if (result.getGenotype() != null && !result.getGenotype().equals("")) {
			if (ntc == null)
				setNTC(); // fail safe
			return FormatHelper.newline2HTMLBR(ntc.convertNotes(
					result.getGenotype(), '|', true));
		}
		return "";
	}

	public String getReference() {
		if (result.getJNum().startsWith("J:")) {
			return "<a href='" + fewiUrl + "reference/" + result.getJNum() + "'>"
				+ result.getJNum() + "</a> " + result.getShortCitation();
		}
		return "<a href='" + fewiUrl + "gxd/htexp_index/summary?arrayExpressID=" + result.getJNum() + "'>"
			+ result.getJNum() + "</a> " + result.getShortCitation();
	}

	public String getTpmLevel() {
		return result.getTpmLevel();
	}

	public String getBiologicalReplicates() {
		String br = result.getBiologicalReplicates();
		if ((br == null) || ("".equals(br))) {
			if ("RNA-Seq".equals(result.getAssayType())) {
				// RNA-Seq defaults to 1
				return "1";
			}
			// other expt types have no biological replicates
			return null;
		}
		return br;
	}

	public String getStrain() {
		String strain = result.getStrain();
		if ((strain == null) || ("".equals(strain))) {
			return strain;
		}
		return FormatHelper.superscript(strain);
	}

	public String getSex() {
		return result.getSex();
	}

	public String getNotes() {
		return result.getNotes();
	}
	
	public String getCellType() {
		return result.getCellType();
	}
}
