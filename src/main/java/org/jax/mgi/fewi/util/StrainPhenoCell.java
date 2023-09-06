package org.jax.mgi.fewi.util;

/* Is: a collection of data for a strain/pheno-header popup (linked from the MP slimgrid
 * 		on the strain detail page)
 * Has: column header strings, data rows (each with genotype data plus data cells)
 */
public class StrainPhenoCell {
	public int count = 0;
		
	// hide the default constructor
	private StrainPhenoCell() {}

	// constructor -- a cell only has a count value
	public StrainPhenoCell(int count) {
		this.count = count;
	}
	
	public int getCount() {
		return this.count;
	}
}
