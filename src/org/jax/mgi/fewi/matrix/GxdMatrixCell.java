package org.jax.mgi.fewi.matrix;
/*
 * Extra data points relevant to GXD tissue matrices
 */
public class GxdMatrixCell extends GridDataCell {
	protected int detectedCount = 0;
	protected int notDetectedCount = 0;
	protected int ambiguousCount = 0;
	protected int childCount = 0;

	public GxdMatrixCell(String rowId, String colId, String detectionLevel,
			boolean isChild) {
		super("", rowId, colId);
		addNewCount(detectionLevel, 1, isChild);
	}

	public GxdMatrixCell(String rowId, String colId, String detectionLevel,
			Integer countToAdd, boolean isChild) {
		super("", rowId, colId);
		addNewCount(detectionLevel, countToAdd, isChild);
	}

	public void aggregate(String detectionLevel, boolean isChild) {
		aggregate(detectionLevel, 1, isChild);
	}

	public void aggregate(String detectionLevel, Integer countToAdd,
			boolean isChild) {
		addNewCount(detectionLevel, countToAdd, isChild);
	}

	protected void addNewCount(String detectionLevel, Integer countToAdd,
			boolean isChild) {
		if (countToAdd == null || countToAdd <= 0)
			countToAdd = 1;

		if (detectionLevel == null)
			return;
		if (DetectionConverter.isDetected(detectionLevel)) {
			detectedCount += countToAdd;
		} else if (isChild) {
			childCount += countToAdd;
		}
		// only count No and Ambiguous if not a child annotation
		else if (DetectionConverter.isNotDetected(detectionLevel)) {
			notDetectedCount += countToAdd;
		} else {
			ambiguousCount += countToAdd;
		}
	}

	@Override
	public String getVal() {
		return (detectedCount + notDetectedCount + ambiguousCount + childCount)
				+ "";
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

	public int getChildren() {
		return childCount;
	}

	public void setChildren(int childCount) {
		this.childCount = childCount;
	}
	
	public String getTermId()
	{
		return this.getRid();
	}

	@Override
	public String toString() {
		return "GxdMatrixCell [detectedCount=" + detectedCount
				+ ", notDetectedCount=" + notDetectedCount
				+ ", ambiguousCount=" + ambiguousCount + ", childCount="
				+ childCount + "]";
	}
}
