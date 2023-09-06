package org.jax.mgi.fewi.matrix;

public class GxdStageMatrixPopup {

	private String term = new String("");
	private String termId = new String("");
	private boolean hasImage = false;
	private Integer countPosGenes = 0;
	private Integer countNegGenes = 0;
	private Integer countAmbGenes = 0;
	private Integer countPosResults = 0;
	private Integer countNegResults = 0;
	private Integer countAmbResults = 0;

	public GxdStageMatrixPopup(){}

	public GxdStageMatrixPopup(String termId, String term)
	{
		this.termId = termId;
		this.term = term;
	}

	// count of genes with positive results
	public void setCountPosGenes(Integer countPosGenes) {
		this.countPosGenes = countPosGenes;
	}

	// count of genes with negative results
	public void setCountNegGenes(Integer countNegGenes) {
		this.countNegGenes = countNegGenes;
	}

	// count of genes involved in undetermined results
	public void setCountAmbGenes(Integer countAmbGenes) {
		this.countAmbGenes = countAmbGenes;
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

	// count of genes with positive results
	public Integer getCountPosGenes() {
		return countPosGenes;
	}

	// count of genes with negative results
	public Integer getCountNegGenes() {
		return countNegGenes;
	}

	// count of genes involved in undetermined results
	public Integer getCountAmbGenes() {
		return countAmbGenes;
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

	// hasImage
	public boolean getHasImage() {
		return hasImage;
	}
	
	public void setHasImage(boolean hasImage)
	{
		this.hasImage = hasImage;
	}
}
