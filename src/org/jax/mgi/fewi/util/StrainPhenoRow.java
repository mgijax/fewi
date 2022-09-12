package org.jax.mgi.fewi.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mgi.frontend.datamodel.Genotype;

/* Is: a collection of data for a strain/pheno-header popup (linked from the MP slimgrid
 * 		on the strain detail page)
 * Has: column header strings, data rows (each with genotype data plus data cells)
 */
public class StrainPhenoRow {
	public Genotype genotype = null;
	public List<StrainPhenoCell> cells = new ArrayList<StrainPhenoCell>();
		
	// hide the no-argument constructor
	private StrainPhenoRow() {}

	// constructor -- create a new StrainPhenoRow containing the given Genotype
	public StrainPhenoRow(Genotype genotype) {
		this.genotype = genotype;
	}
	
	// add a StrainPhenoCell with the specified color value to this row
	public void addCell(int color) {
		cells.add(new StrainPhenoCell(color));
	}
	
	public String getAllelePairs() {
		try {
			NotesTagConverter ntc = new NotesTagConverter("MP");
			return ntc.convertNotes(this.genotype.getCombination2(), '|', true);
		} catch (IOException e) {
			return this.genotype.getCombination3();
		}
	}
	
	public Genotype getGenotype() {
		return this.genotype;
	}
	
	public List<StrainPhenoCell> getCells() {
		return this.cells;
	}
}
