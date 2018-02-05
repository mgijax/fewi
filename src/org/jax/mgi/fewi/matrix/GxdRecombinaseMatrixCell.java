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
	protected String symbol;				// gene/allele symbol (ID is stored in colid)
	protected int byRecombinase = 0;		// sequence number for sorting by recombinase
	protected int ambiguousOrNotDetectedChildren = 0;
	
	public GxdRecombinaseMatrixCell(String cellType, String rowId, String colId, boolean isChild) {
		super("", rowId, colId);
		this.cellType = cellType;
		this.isChild = isChild;
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
				+ byRecombinase + "]";
	}
}
