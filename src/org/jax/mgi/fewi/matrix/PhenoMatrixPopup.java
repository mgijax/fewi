package org.jax.mgi.fewi.matrix;

import java.util.List;

import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMatrixResult;

public class PhenoMatrixPopup {

	private String term = new String("");
	private String termId = new String("");
	private String markerId = null;
	private String symbol = new String("");
	private boolean hasImage = true;
	private Integer countPosResults = new Integer(0);
	private Integer countNegResults = new Integer(0);
	private Integer countAmbResults = new Integer(0);
	private String cellType = null;						// GXD or Pheno
	private String alleles = null;						// for Pheno cells: allele combinations
	private String genoclusterLink = null;				// for Pheno cells: parameter string to link to genocluster page

	public PhenoMatrixPopup(){}

	public PhenoMatrixPopup(GxdGeneMatrixPopup gxdPopup) {
		this.term = gxdPopup.getTerm();
		this.termId = gxdPopup.getTermId();
		this.symbol = gxdPopup.getSymbol();
		this.hasImage = gxdPopup.getHasImage();
		this.countPosResults = gxdPopup.getCountPosResults();
		this.countNegResults = gxdPopup.getCountNegResults();
		this.countAmbResults = gxdPopup.getCountAmbResults();
		this.cellType = "GXD";
	}
	
	public PhenoMatrixPopup(String termId, String term)
	{
		this.termId = termId;
		this.term = term;
		this.cellType = "Pheno";
		this.hasImage = false;
	}

	public String getMarkerId() {
		return markerId;
	}

	public void setMarkerId(String markerId) {
		this.markerId = markerId;
	}

	public String getAlleles() {
		return alleles;
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

	public String getGenoclusterLink() {
		return genoclusterLink;
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

	public void setAlleles(String alleles) {
		this.alleles = alleles;
	}
	// parse the set of results for display counts
	public void setAssayResultList(List<SolrGxdMatrixResult> assayResultList) {

        // generate counts
		int posCount = 0;
		int negCount = 0;
		int ambCount = 0;
        for (SolrGxdMatrixResult assayResult : assayResultList) {
			if (assayResult != null){

				if (assayResult.getDetectionLevel().equals("Yes")) {
					posCount++;
				}
				else if (assayResult.getDetectionLevel().equals("No")&&
						assayResult.getStructureId().equals(termId)) {
					negCount++;
				}
				else if (assayResult.getDetectionLevel().equals("Ambiguous") &&
						assayResult.getStructureId().equals(termId)) {
					ambCount++;
				}
				else if (assayResult.getDetectionLevel().equals("Not Specified")&&
						assayResult.getStructureId().equals(termId)) {
					ambCount++;
				}

			} else {
				System.out.println("--> Null assayResult object in GxdGeneMatrixPopup");
			}
		}

		// set fields
		countPosResults = new Integer(posCount);
		countNegResults = new Integer(negCount);
		countAmbResults = new Integer(ambCount);
	}

	public void setCellType(String cellType) {
		this.cellType = cellType;
	}
	public void setGenoclusterLink(String genoclusterLink) {
		this.genoclusterLink = genoclusterLink;
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
