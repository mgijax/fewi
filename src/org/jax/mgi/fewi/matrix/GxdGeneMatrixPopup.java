package org.jax.mgi.fewi.matrix;

import java.util.List;

import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMatrixResult;

public class GxdGeneMatrixPopup {

	private String term = new String("");
	private String termId = new String("");
	private String symbol = new String("");
	private boolean hasImage = false;
	private Integer countPosResults = new Integer(0);
	private Integer countNegResults = new Integer(0);
	private Integer countAmbResults = new Integer(0);

	public GxdGeneMatrixPopup(){}

	public GxdGeneMatrixPopup(String termId, String term)
	{
		this.termId = termId;
		this.term = term;
	}

	// positive result count
	public Integer getCountPosResults() {
		return countPosResults;
	}

	// negative result count
	public Integer getCountNegResults() {
		return countNegResults;
	}

	// count of undetermined results
	public Integer getCountAmbResults() {
		return countAmbResults;
	}

	// positive result count
	public void setCountPosResults(Integer countPosResults) {
		this.countPosResults = countPosResults;
	}

	// negative result count
	public void setCountNegResults(Integer countNegResults) {
		this.countNegResults = countNegResults;
	}

	// count of undetermined results
	public void setCountAmbResults(Integer countAmbResults) {
		this.countAmbResults = countAmbResults;
	}

	// term
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}

	// termId
	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}

	// symbol
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	// hasImage
	public boolean getHasImage() {
		return hasImage;
	}
	
	public void setHasImage(boolean hasImage)
	{
		this.hasImage = hasImage;
	}
}
