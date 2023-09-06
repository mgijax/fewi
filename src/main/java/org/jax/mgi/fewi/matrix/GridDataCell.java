package org.jax.mgi.fewi.matrix;

/**
 * JSON object for grid data cells
 * 
 */
public class GridDataCell {
	protected String val;
	protected String rowid;
	protected String colid;

	public GridDataCell(String val, String rowid, String colid) {
		this.val = val;
		this.rowid = rowid;
		this.colid = colid;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getRid() {
		return rowid;
	}

	public void setRid(String rowid) {
		this.rowid = rowid;
	}

	public String getCid() {
		return colid;
	}

	public void setCid(String colid) {
		this.colid = colid;
	}
}
