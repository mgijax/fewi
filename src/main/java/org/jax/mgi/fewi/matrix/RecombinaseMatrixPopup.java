package org.jax.mgi.fewi.matrix;

public class RecombinaseMatrixPopup {

	private String term = new String("");
	private String termId = new String("");
	private String markerId = null;
	private String symbol = new String("");
	private boolean hasImage = true;
	private Integer countPosResults = 0;
	private Integer countNegResults = 0;
	private Integer countAmbResults = 0;
	private String cellType = null;						// GXD or Recombinase
	private String allele = null;						// for Recombinase cells: allele symbol
	private String alleleLink = null;					// for Recombinase cells: parameter string to link to allele detail page

	public RecombinaseMatrixPopup(){}

	public RecombinaseMatrixPopup(GxdGeneMatrixPopup gxdPopup) {
		this.term = gxdPopup.getTerm();
		this.termId = gxdPopup.getTermId();
		this.symbol = gxdPopup.getSymbol();
		this.hasImage = gxdPopup.getHasImage();
		this.countPosResults = gxdPopup.getCountPosResults();
		this.countNegResults = gxdPopup.getCountNegResults();
		this.countAmbResults = gxdPopup.getCountAmbResults();
		this.cellType = "GXD";
	}
	
	public RecombinaseMatrixPopup(String termId, String term)
	{
		this.termId = termId;
		this.term = term;
		this.cellType = "Recombinase";
		this.hasImage = false;
	}

	public String getMarkerId() {
		return markerId;
	}

	public void setMarkerId(String markerId) {
		this.markerId = markerId;
	}

	public String getCellType() {
		return cellType;
	}

	// count of undetermined results
	public Integer getCountAmbResults() {
		return countAmbResults;
	}

	// negative result count
	public Integer getCountNegResults() {
		return countNegResults;
	}

	// positive result count
	public Integer getCountPosResults() {
		return countPosResults;
	}

	// hasImage
	public boolean getHasImage() {
		return hasImage;
	}

	// symbol
	public String getSymbol() {
		return symbol;
	}

	// term
	public String getTerm() {
		return term;
	}

	// termId
	public String getTermId() {
		return termId;
	}

	public String getAllele() {
		return allele;
	}

	public void setAllele(String allele) {
		this.allele = allele;
	}

	public String getAlleleLink() {
		return alleleLink;
	}

	public void setAlleleLink(String alleleLink) {
		this.alleleLink = alleleLink;
	}

	public void setCountPosResults(Integer countPosResults) {
		this.countPosResults = countPosResults;
	}

	public void setCountNegResults(Integer countNegResults) {
		this.countNegResults = countNegResults;
	}

	public void setCountAmbResults(Integer countAmbResults) {
		this.countAmbResults = countAmbResults;
	}

	public void setCellType(String cellType) {
		this.cellType = cellType;
	}

	public void setHasImage(boolean hasImage)
	{
		this.hasImage = hasImage;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	
	public void setTermId(String termId) {
		this.termId = termId;
	}
}
