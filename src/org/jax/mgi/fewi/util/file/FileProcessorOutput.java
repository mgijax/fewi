package org.jax.mgi.fewi.util.file;

/*
 * Object to store data from processing an uploaded data file
 */
public class FileProcessorOutput 
{
	protected String valueString = "";
	protected int rowsProcessed = 0;
	protected int rowsKicked = 0;
	protected int rowsKickedWithNoData = 0;
	protected int validRows = 0;
	
	public String getValueString() {
		return valueString;
	}
	public int getRowsProcessed() {
		return rowsProcessed;
	}
	public int getRowsKicked() {
		return rowsKicked;
	}
	public int getRowsKickedWithNoData() {
		return rowsKickedWithNoData;
	}
	public int getValidRows() {
		return validRows;
	}
	
	public void setValueString(String value) {
		this.valueString = value;
	}
	
	public void addProcessedRow()
	{
		rowsProcessed += 1;
	}
	
	public void kickRowWithNoData()
	{
		rowsKickedWithNoData += 1;
		rowsKicked += 1;
	}
	
	public void addValidRow()
	{
		validRows += 1;
	}
	@Override
	public String toString() {
		return "SingleColProcessorOutput [rowsProcessed=" + rowsProcessed + ", rowsKicked="
				+ rowsKicked + ", rowsKickedWithNoData=" + rowsKickedWithNoData
				+ ", validRows=" + validRows + "]";
	}
}
