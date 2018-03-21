package org.jax.mgi.fewi.searchUtil.entities;

public class SolrGxdPhenoMatrixResult extends SolrGxdGeneMatrixResult {
	// This type of grid has two types of cells:  GXD cells (gene x tissue) and PHENO cells (genocluster x tissue)
	public static final String GXD = "GXD";
	public static final String PHENO = "Pheno";
	
	protected String cellType;				// type of matrix cell:  GXD or PHENO
	protected String allelePairs;			// allele pairs for the genocluster (for PHENO cells)
	protected String genoclusterKey;		// key of the genocluster (for PHENO cells)
	protected String phenoAnnotationCount;	// annotation count for this cell (for PHENO cells)
	
	public SolrGxdPhenoMatrixResult (SolrGxdGeneMatrixResult r) {
		this.cellType = GXD;
		this.printname = r.printname;
		this.structureId = r.structureId;
		this.theilerStage = r.theilerStage;
		this.detectionLevel = r.detectionLevel;
		this.geneSymbol = r.geneSymbol;
		this.count = r.count;
	}
	
	/***--- overrides for standard MatrixResult methods ---***/
	
	// return the column ID for this cell (gene symbol for GXD cells, genocluster key for PHENO cells)
	public String getColumnId() {
		if (GXD.equals(cellType)) {
			return geneSymbol;
		}
		return genoclusterKey;
	}

	// return the value for this cell (detection level for GXD, annotation count for PHENO)
	public String getValue() {
		if (GXD.equals(cellType)) {
			return detectionLevel;
		}
		return phenoAnnotationCount;
	}
	
	/***--- getters and setters ---***/

	public String getCellType() {
		return cellType;
	}

	public void setCellType(String cellType) {
		this.cellType = cellType;
	}

	public String getAllelePairs() {
		return allelePairs;
	}

	public void setAllelePairs(String allelePairs) {
		this.allelePairs = allelePairs;
	}

	public String getGenoclusterKey() {
		return genoclusterKey;
	}

	public void setGenoclusterKey(String genoclusterKey) {
		this.genoclusterKey = genoclusterKey;
	}

	public String getPhenoAnnotationCount() {
		return phenoAnnotationCount;
	}

	public void setPhenoAnnotationCount(String phenoAnnotationCount) {
		this.phenoAnnotationCount = phenoAnnotationCount;
	}
}
