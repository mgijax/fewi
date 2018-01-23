package org.jax.mgi.fewi.searchUtil.entities;

public class SolrGxdRecombinaseMatrixResult extends SolrGxdGeneMatrixResult {
	// This type of grid has two types of cells:  GXD cells (gene x tissue) and Recombinase cells (allele x tissue)
	public static final String GXD = "GXD";
	public static final String RECOMBINASE = "Recombinase";

	protected String cellType;				// type of matrix cell:  GXD or RECOMBINASE
	protected String symbol;				// gene or allele symbol
	
	public SolrGxdRecombinaseMatrixResult (SolrGxdGeneMatrixResult r) {
		this.cellType = GXD;
		this.printname = r.printname;
		this.structureId = r.structureId;
		this.theilerStage = r.theilerStage;
		this.detectionLevel = r.detectionLevel;
		this.symbol = r.geneSymbol;
		this.count = r.count;
	}
	
	/***--- overrides for standard MatrixResult methods ---***/
	
	// return the column ID for this cell (gene symbol for GXD cells, allele symbol for recombinase cells)
	public String getColumnId() {
		return symbol;
	}

	// return the value for this cell (detection level for both types)
	public String getValue() {
		return detectionLevel;
	}
	
	/***--- getters and setters ---***/

	public String getCellType() {
		return cellType;
	}

	public void setCellType(String cellType) {
		this.cellType = cellType;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
