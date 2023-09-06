package org.jax.mgi.fewi.matrix;

/*
 * Extra data points relevant to Correlation Matrix (tissue x gene [expression] and genocluster [phenotype]
 */
public class GxdPhenoMatrixCell extends GridDataCell {
	protected int detectedCount = 0;
	protected int notDetectedCount = 0;
	protected int ambiguousCount = 0;
	protected int childCount = 0;
	protected boolean isChild = false;

	protected String cellType;				// type of matrix cell:  GXD or Pheno
	protected String markerId;				// gene's primary ID (for GXD cells)
	protected String symbol;				// gene symbol (for GXD cells)
	protected String allelePairs;			// allele pairs for the genocluster (for Pheno cells)
	protected String genoclusterKey;		// key of the genocluster (for Pheno cells)
	protected int phenoAnnotationCount = 0;	// annotation count for this cell (for Pheno cells)
	protected int byGenocluster = 0;		// sequence number for sorting by genocluster
	private int isNormal = 0;					// do all MP annotations in this cell have a "normal" qualifier (1) or not (0)?
	private int hasBackgroundSensitivity = 0;	// is at least 1 MP annotation in this cell flagged for background sensitivity?
	protected boolean highlightColumn = false;	// should this column be highlighted (in the header)?
	
	public GxdPhenoMatrixCell(String cellType, String rowId, String colId, boolean isChild) {
		super("", rowId, colId);
		this.cellType = cellType;
		this.isChild = isChild;
	}
	
	public void initializeGxd (String detectionLevel) {
		addNewGxdCount(detectionLevel, 1, this.isChild);
	}

	public void initializeGxd (String detectionLevel, int countToAdd) {
		addNewGxdCount(detectionLevel, countToAdd, this.isChild);
	}

	public void aggregateGxd(String detectionLevel, boolean isChild) {
		aggregateGxd(detectionLevel, 1, isChild);
	}

	public void aggregateGxd(String detectionLevel, Integer countToAdd, boolean isChild) {
		addNewGxdCount(detectionLevel, countToAdd, isChild);
	}

	public boolean isHighlightColumn() {
		return highlightColumn;
	}

	public void setHighlightColumn(boolean highlightColumn) {
		this.highlightColumn = highlightColumn;
	}

	public String getMarkerId() {
		return markerId;
	}

	public void setMarkerId(String markerId) {
		this.markerId = markerId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	protected void addNewGxdCount(String detectionLevel, Integer countToAdd, boolean isChild) {
		if (countToAdd == null || countToAdd <= 0)
			countToAdd = 1;

		if (detectionLevel == null)
			return;
		if (detectionLevel.equals("Yes")) {
			detectedCount += countToAdd;
		} else if (isChild) {
			childCount += countToAdd;
		}
		// only count No and Ambiguous if not a child annotation
		else if (detectionLevel.equals("No")) {
			notDetectedCount += countToAdd;
		} else {
			ambiguousCount += countToAdd;
		}
	}

	@Override
	public String getVal() {
		return (detectedCount + notDetectedCount + ambiguousCount + childCount) + "";
	}

	public int getByGenocluster() {
		return byGenocluster;
	}

	public void setByGenocluster(int byGenocluster) {
		this.byGenocluster = byGenocluster;
	}

	public int getDetected() {
		return detectedCount;
	}

	public void setDetected(int detectedCount) {
		this.detectedCount = detectedCount;
	}

	public int getNotDetected() {
		return notDetectedCount;
	}

	public void setNotDetected(int notDetectedCount) {
		this.notDetectedCount = notDetectedCount;
	}

	public int getAmbiguous() {
		return ambiguousCount;
	}

	public void setAmbiguous(int ambiguousCount) {
		this.ambiguousCount = ambiguousCount;
	}

//	public int getChildren() {
//		return childCount;
//	}

	public void setChildren(int childCount) {
		this.childCount = childCount;
	}
	
	public String getTermId()
	{
		return this.getRid();
	}

	public String getAllelePairs() {
		return allelePairs;
	}

	public void setAllelePairs(String allelePairs) {
		this.allelePairs = allelePairs;
	}

	public int getIsNormal() {
		return isNormal;
	}

	public void setIsNormal(int isNormal) {
		this.isNormal = isNormal;
	}

	public int getHasBackgroundSensitivity() {
		return hasBackgroundSensitivity;
	}

	public void setHasBackgroundSensitivity(int hasBackgroundSensitivity) {
		this.hasBackgroundSensitivity = hasBackgroundSensitivity;
	}

	public String getGenoclusterKey() {
		return genoclusterKey;
	}

	public void setGenoclusterKey(String genoclusterKey) {
		this.genoclusterKey = genoclusterKey;
	}

	public int getPhenoAnnotationCount() {
		return phenoAnnotationCount;
	}

	public void setPhenoAnnotationCount(int phenoAnnotationCount) {
		this.phenoAnnotationCount = phenoAnnotationCount;
	}

	public String getCellType() {
		return cellType;
	}

	public void setCellType(String cellType) {
		this.cellType = cellType;
	}

	public String getColDisplay() {
		if ("GXD".equals(this.cellType)) {
			return this.symbol;
		}
		return this.allelePairs;
	}
	
	@Override
	public String toString() {
		return "GxdPhenoMatrixCell [detectedCount=" + detectedCount + ", notDetectedCount=" + notDetectedCount
				+ ", ambiguousCount=" + ambiguousCount + ", childCount=" + childCount + ", isChild=" + isChild
				+ ", cellType=" + cellType + ", allelePairs=" + allelePairs + ", genoclusterKey=" + genoclusterKey
				+ ", phenoAnnotationCount=" + phenoAnnotationCount + "]";
	}
}
