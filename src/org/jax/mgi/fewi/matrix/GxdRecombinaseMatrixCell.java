package org.jax.mgi.fewi.matrix;

/*
 * Extra data points relevant to Recombinase Matrix (tissue x gene [expression] and recombinase [expression])
 */
public class GxdRecombinaseMatrixCell extends GridDataCell {
	protected int detectedCount = 0;
	protected int notDetectedCount = 0;
	protected int ambiguousCount = 0;
	protected int absentCount = 0;
	protected int childCount = 0;
	protected boolean isChild = false;
	protected String cellType;				// type of matrix cell:  GXD or Recombinase
	protected String mgiId;					// gene ID (for expression) or allele ID (for recombinase data)
	protected String symbol;				// gene/allele symbol (ID is stored in colid)
        protected String driverSpecies;                 // Species of the driver gene
	protected int byRecombinase = 0;		// sequence number for sorting by recombinase
	protected int ambiguousOrNotDetectedChildren = 0;
	protected boolean highlightColumn = false;	// should this column be highlighted (in the header)?
	
	public GxdRecombinaseMatrixCell(String cellType, String rowId, String colId, boolean isChild) {
		super("", rowId, colId);
		this.cellType = cellType;
		this.isChild = isChild;
	}
	
	public boolean isHighlightColumn() {
		return highlightColumn;
	}

	public void setHighlightColumn(boolean highlightColumn) {
		this.highlightColumn = highlightColumn;
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

	public void aggregateGxd(String detectionLevel, boolean isChild) {
		aggregateGxd(detectionLevel, 1, isChild);
	}

	public void aggregateGxd(String detectionLevel, Integer countToAdd, boolean isChild) {
		addNewGxdCount(detectionLevel, countToAdd, isChild);
	}

	public int getAmbiguous() {
		return ambiguousCount;
	}

	public String getMgiId() {
		return mgiId;
	}

	public void setMgiId(String mgiId) {
		this.mgiId = mgiId;
	}

	public String getDriverSpecies() {
		return driverSpecies;
	}

	public void setDriverSpecies(String driverSpecies) {
		this.driverSpecies = driverSpecies;
	}

	public int getByRecombinase() {
		return byRecombinase;
	}

	public String getCellType() {
		return cellType;
	}

//	public int getChildren() {
//		return childCount;
//	}

	public int getAmbiguousOrNotDetectedChildren() {
		return ambiguousOrNotDetectedChildren;
	}

	public String getColDisplay() {
		return this.symbol;
	}

	public int getDetected() {
		return detectedCount;
	}

	public int getNotDetected() {
		return notDetectedCount;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getTermId()
	{
		return this.getRid();
	}

	@Override
	public String getVal() {
		return (detectedCount + notDetectedCount + ambiguousCount + childCount) + "";
	}

	public void initializeGxd (String detectionLevel) {
		addNewGxdCount(detectionLevel, 1, this.isChild);
	}

	public void initializeGxd (String detectionLevel, int countToAdd) {
		addNewGxdCount(detectionLevel, countToAdd, this.isChild);
	}

	public boolean isChild() {
		return isChild;
	}

	public void setAmbiguous(int ambiguousCount) {
		this.ambiguousCount = ambiguousCount;
	}

	public void setAmbiguousOrNotDetectedChildren(int ambiguousOrNotDetectedChildren) {
		this.ambiguousOrNotDetectedChildren = ambiguousOrNotDetectedChildren;
	}

	public void setByRecombinase(int byRecombinase) {
		this.byRecombinase = byRecombinase;
	}

	public void setCellType(String cellType) {
		this.cellType = cellType;
	}

	public void setChild(boolean isChild) {
		this.isChild = isChild;
	}

	public void setChildren(int childCount) {
		this.childCount = childCount;
	}
	
	public void setDetected(int detectedCount) {
		this.detectedCount = detectedCount;
	}

	public void setNotDetected(int notDetectedCount) {
		this.notDetectedCount = notDetectedCount;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return "GxdRecombinaseMatrixCell [detectedCount=" + detectedCount + ", notDetectedCount=" + notDetectedCount
				+ ", ambiguousCount=" + ambiguousCount + ", absentCount=" + absentCount + ", childCount=" + childCount
				+ ", isChild=" + isChild + ", cellType=" + cellType + ", symbol=" + symbol + ", byRecombinase="
				+ byRecombinase + ", driverSpecies=" + driverSpecies + "]";
	}
}
