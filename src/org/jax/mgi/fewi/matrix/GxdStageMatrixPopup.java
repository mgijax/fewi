package org.jax.mgi.fewi.matrix;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMatrixResult;

public class GxdStageMatrixPopup {

	private String term = new String("");
	private String termId = new String("");
	private boolean hasImage = true;
	private Integer countPosGenes = new Integer(0);
	private Integer countNegGenes = new Integer(0);
	private Integer countAmbGenes = new Integer(0);
	private Integer countPosResults = new Integer(0);
	private Integer countNegResults = new Integer(0);
	private Integer countAmbResults = new Integer(0);

	public GxdStageMatrixPopup(){}

	public GxdStageMatrixPopup(String termId, String term)
	{
		this.termId = termId;
		this.term = term;
	}

	// parse the set of results for display counts
	public void setAssayResultList(List<SolrGxdMatrixResult> assayResultList) {

        // generate counts
		Set<String> uniquePosMarkers = new HashSet<String>();
		Set<String> uniqueNegMarkers = new HashSet<String>();
		Set<String> uniqueAmbMarkers = new HashSet<String>();
		int posCount = 0;
		int negCount = 0;
		int ambCount = 0;
        for (SolrGxdMatrixResult assayResult : assayResultList) {
			if (assayResult != null){

				if (DetectionConverter.isDetected(assayResult.getDetectionLevel())) {
					uniquePosMarkers.add(assayResult.getGeneSymbol());
					posCount++;
				}
				else if (DetectionConverter.isNotDetected(assayResult.getDetectionLevel())&&
						assayResult.getStructureId().equals(termId)) {
					uniqueNegMarkers.add(assayResult.getGeneSymbol());
					negCount++;
				}
				else if (assayResult.getDetectionLevel().equals("Ambiguous") &&
						assayResult.getStructureId().equals(termId)) {
					uniqueAmbMarkers.add(assayResult.getGeneSymbol());
					ambCount++;
				}
				else if (assayResult.getDetectionLevel().equals("Not Specified")&&
						assayResult.getStructureId().equals(termId)) {
					uniqueAmbMarkers.add(assayResult.getGeneSymbol());
					ambCount++;
				}

			} else {
				System.out.println("--> Null assayResult object in GxdStageMatrixPopup");
			}
		}

		// set fields
		countPosGenes = uniquePosMarkers.size();
		countNegGenes = uniqueNegMarkers.size();
		countAmbGenes = uniqueAmbMarkers.size();
		countPosResults = new Integer(posCount);
		countNegResults = new Integer(negCount);
		countAmbResults = new Integer(ambCount);
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
