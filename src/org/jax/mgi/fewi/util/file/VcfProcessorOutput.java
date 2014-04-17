package org.jax.mgi.fewi.util.file;

/*
 * Object to store data from processing a VCF file
 */
public class VcfProcessorOutput extends FileProcessorOutput
{
	private int rowsKickedWithId = 0;
	private int rowsKickedWithNotPass = 0;
	
	public String getCoordinates() {
		return this.valueString;
	}
	
	public int getRowsKickedWithId() {
		return rowsKickedWithId;
	}
	public int getRowsKickedWithNotPass() {
		return rowsKickedWithNotPass;
	}
	public int getRowsWithCoordinates() {
		return validRows;
	}
	
	public void setCoordinates(String coordinates)
	{
		this.setValueString(coordinates);
	}
	public void kickRowWithId()
	{
		rowsKickedWithId += 1;
		rowsKicked += 1;
	}
	
	public void kickRowWithNotPass()
	{
		rowsKickedWithNotPass += 1;
		rowsKicked += 1;
	}
	
	public void addRowWithCoordinate()
	{
		this.addValidRow();
	}
	
	@Override
	public String toString() {
		return "VcfProcessorOutput [rowsProcessed=" + rowsProcessed
				+ ", rowsKicked=" + rowsKicked + ", rowsKickedWithId="
				+ rowsKickedWithId + ", rowsKickedWithNotPass="
				+ rowsKickedWithNotPass + ", rowsKickedWithNoData="
				+ rowsKickedWithNoData + ", validRows="
				+ validRows + "]";
	}
}
