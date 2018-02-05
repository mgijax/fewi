package org.jax.mgi.fewi.searchUtil.entities;

// Is: a single recombinase cell in a correlation matrix (linked from marker detail; has anatomy x (expression + phenotype data) )
public class SolrRecombinaseMatrixCell
{
	private String uniqueKey;
	private String anatomyID;
	private String anatomyTerm;
	private String symbol;
	private String cellType;
	private String organism;
	private String columnID;
	private int allResults = 0;
	private int detectedResults = 0;
	private int notDetectedResults = 0;
	private int anyAmbiguous = 0;
	private int byColumn = 0;
	private int children = 0;
	private int ambiguousOrNotDetectedChildren = 0;
	
	/***--- getters ---***/
	
	public int getAllResults() {
		return allResults;
	}
	public String getAnatomyID() {
		return anatomyID;
	}
	public String getAnatomyTerm() {
		return anatomyTerm;
	}
	public int getAmbiguousOrNotDetectedChildren() {
		return ambiguousOrNotDetectedChildren;
	}
	public int getAnyAmbiguous() {
		return anyAmbiguous;
	}
	public int getByColumn() {
		return byColumn;
	}
	public String getCellType() {
		return cellType;
	}
	public int getChildren() {
		return children;
	}
	public String getColumnID() {
		return columnID;
	}
	public int getDetectedResults() {
		return detectedResults;
	}
	public int getNotDetectedResults() {
		return notDetectedResults;
	}
	public String getOrganism() {
		return organism;
	}
	public String getSymbol() {
		return symbol;
	}
	public String getUniqueKey() {
		return uniqueKey;
	}

	/***--- setters ---***/
	
	public void setAllResults(int allResults) {
		this.allResults = allResults;
	}
	public void setAmbiguousOrNotDetectedChildren(int ambiguousOrNotDetectedChildren) {
		this.ambiguousOrNotDetectedChildren = ambiguousOrNotDetectedChildren;
	}
	public void setAnatomyID(String anatomyID) {
		this.anatomyID = anatomyID;
	}
	public void setAnatomyTerm(String anatomyTerm) {
		this.anatomyTerm = anatomyTerm;
	}
	public void setAnyAmbiguous(int anyAmbiguous) {
		this.anyAmbiguous = anyAmbiguous;
	}
	public void setByColumn(int byColumn) {
		this.byColumn = byColumn;
	}
	public void setCellType(String cellType) {
		this.cellType = cellType;
	}
	public void setChildren(int children) {
		this.children = children;
	}
	public void setColumnID(String columnID) {
		this.columnID = columnID;
	}
	public void setDetectedResults(int detectedResults) {
		this.detectedResults = detectedResults;
	}
	public void setNotDetectedResults(int notDetectedResults) {
		this.notDetectedResults = notDetectedResults;
	}
	public void setOrganism(String organism) {
		this.organism = organism;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	@Override
	public String toString() {
		return "SolrRecombinaseMatrixCell [uniqueKey=" + uniqueKey + ", anatomyID=" + anatomyID + ", anatomyTerm="
				+ anatomyTerm + ", symbol=" + symbol + ", cellType=" + cellType + ", organism=" + organism
				+ ", columnID=" + columnID + ", allResults=" + allResults + ", detectedResults=" + detectedResults
				+ ", notDetectedResults=" + notDetectedResults + ", anyAmbiguous=" + anyAmbiguous + ", byColumn="
				+ byColumn + "]";
	}
}
