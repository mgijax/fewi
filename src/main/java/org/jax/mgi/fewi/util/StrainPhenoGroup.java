package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jax.mgi.fe.datamodel.Genotype;

/* Is: a collection of data for a strain/pheno-header popup (linked from the MP slimgrid
 * 		on the strain detail page)
 * Has: column header strings, data rows (each with genotype data plus data cells)
 */
public class StrainPhenoGroup {
	List<String> headers = new ArrayList<String>();		// list of column header strings
	Set<String> headerSet = new HashSet<String>();		// set of column header strings (for quick access)
	List<StrainPhenoRow> rows = new ArrayList<StrainPhenoRow>();	// list of data rows
	StrainPhenoRow currentRow = null;
	
	// just use a basic, no-argument constructor
	public StrainPhenoGroup() {}
	
	// create a new StrainPhenoRow for the specified Genotype and make it the current row (for
	// when we add cells)
	// Assumes: 1. all rows will have the same number of cells, 2. caller won't add a new row until finished
	//		populating the current row with cells
	public void addRow(Genotype genotype) {
		currentRow = new StrainPhenoRow(genotype);
		rows.add(currentRow);
	}
	
	// create and add a new cell to the current row.  Also manages the list of header strings.
	// Assumes: 1. will only be called after addRow() has added a row, 2. caller will add cells in order for
	//		the row from left to right
	public void addCell(String header, int color) {
		currentRow.addCell(color);
		if (!headerSet.contains(header)) {
			headers.add(header);
			headerSet.add(header);
		}
	}
	
	// get the ordered list of column headers
	public List<String> getHeaders() {
		return this.headers;
	}

	// return the ordered list of data rows
	public List<StrainPhenoRow> getRows() {
		return this.rows;
	}
}
