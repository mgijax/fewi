package org.jax.mgi.fewi.matrix;

import java.util.List;

import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMatrixResult;

public class GxdGeneMatrixPopup {

	private String term = new String("");
	private String termId = new String("");
	private String symbol = new String("");
	private boolean hasImage = true;
	private Integer countPosResults = new Integer(0);
	private Integer countNegResults = new Integer(0);
	private Integer countAmbResults = new Integer(0);

	public GxdGeneMatrixPopup(){}

	public GxdGeneMatrixPopup(String termId, String term)
	{
		this.termId = termId;
		this.term = term;
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
