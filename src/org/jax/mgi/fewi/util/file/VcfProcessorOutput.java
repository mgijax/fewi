package org.jax.mgi.fewi.util.file;

/*
 * Object to store data from processing a VCF file
 */
public class VcfProcessorOutput 
{
	private String coordinates = "";
	private int rowsProcessed = 0;
	private int rowsKicked = 0;
	private int rowsKickedWithId = 0;
	private int rowsKickedWithNotPass = 0;
	private int rowsKickedWithNoData = 0;
	private int rowsWithCoordinates = 0;
	
	public String getCoordinates() {
		return coordinates;
	}
	public int getRowsProcessed() {
		return rowsProcessed;
	}
	public int getRowsKicked() {
		return rowsKicked;
	}
	public int getRowsKickedWithId() {
		return rowsKickedWithId;
	}
	public int getRowsKickedWithNotPass() {
		return rowsKickedWithNotPass;
	}
	public int getRowsKickedWithNoData() {
		return rowsKickedWithNoData;
	}
	public int getRowsWithCoordinates() {
		return rowsWithCoordinates;
	}
	
	public void setCoordinates(String coordinates)
	{
		this.coordinates = coordinates;
	}
	public void addProcessedRow()
	{
		rowsProcessed += 1;
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
	
	public void kickRowWithNoData()
	{
		rowsKickedWithNoData += 1;
		rowsKicked += 1;
	}
	
	public void addRowWithCoordinate()
	{
		rowsWithCoordinates += 1;
	}
	
	@Override
	public String toString() {
		return "VcfProcessorOutput [rowsProcessed=" + rowsProcessed
				+ ", rowsKicked=" + rowsKicked + ", rowsKickedWithId="
				+ rowsKickedWithId + ", rowsKickedWithNotPass="
				+ rowsKickedWithNotPass + ", rowsKickedWithNoData="
				+ rowsKickedWithNoData + ", rowsWithCoordinates="
				+ rowsWithCoordinates + "]";
	}
}
