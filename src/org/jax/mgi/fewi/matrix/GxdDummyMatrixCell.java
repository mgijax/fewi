package org.jax.mgi.fewi.matrix;

public class GxdDummyMatrixCell extends GxdMatrixCell {
	// dummy cell types
	public static enum DUMMY_TYPE {
		STAGE("dS");

		private final String type;

		DUMMY_TYPE(String type) {
			this.type = type;
		}

		public String getType() {
			return this.type;
		}
	}

	public GxdDummyMatrixCell(String rowid, String colid, DUMMY_TYPE dummyType) {
		super(rowid, colid, "Yes", false);
	}

	public boolean getIsDummy() {
		return true;
	}

	@Override
	public String toString() {
		return "GxdDummyMatrixCell [isDummy=true]";
	}
}
