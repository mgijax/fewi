package mgi.frontend.controller;

import java.util.ArrayList;
import java.util.List;

public class MarkerQuery {
	
	// selected query parameters
	private String query;
	private List<String> vocabularies = new ArrayList<String>();
	private List<String> chromosomes = new ArrayList<String>();	
	private String genomeCoordinates;
	private boolean hasCoordinateRange;
	private String coordinateUnits;	
	private String cmPosition;
	
	// option lists for query form
	private List<String> chrOptions = new ArrayList<String>();
	private List<String> coordOptions = new ArrayList<String>();
	
	// sort & pagination parameters
	private String sort;
	private String dir;
	private Integer startIndex = 1;
	private Integer results = 25;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public List<String> getVocabularies() {
		return vocabularies;
	}
	public void setVocabularies(List<String> vocabularies) {
		this.vocabularies = vocabularies;
	}
	public void addVocabulary(String vocab){
		this.vocabularies.add(vocab);
	}
	public List<String> getChromosomes() {
		return chromosomes;
	}
	public void setChromosomes(List<String> chromosomes) {
		this.chromosomes = chromosomes;
	}
	public void addChromosome(String chr){
		this.chromosomes.add(chr);
	}
	public List<String> getChrOptions() {
		return chrOptions;
	}
	public void setChrOptions(List<String> chrOptions) {
		this.chrOptions = chrOptions;
	}
	public boolean isHasCoordinateRange() {
		return hasCoordinateRange;
	}
	public String getGenomeCoordinates() {
		return genomeCoordinates;
	}
	public void setGenomeCoordinates(String genomeCoordinates) {
		this.genomeCoordinates = genomeCoordinates;
		if (this.genomeCoordinates.contains("-")){
			hasCoordinateRange = true;
		}
	}
	public void setCoordinateUnits(String coordinateUnits) {
		this.coordinateUnits = coordinateUnits;
	}
	public String getCoordinateUnits() {
		return coordinateUnits;
	}
	public List<String> getCoordOptions() {
		return coordOptions;
	}
	public void setCoordOptions(List<String> coordOptions) {
		this.coordOptions = coordOptions;
	}
	public Integer getCoordinateRangeStart(){
		return new Integer(1000);
	}
	public Integer getCoordinateRangeEnd(){
		return new Integer(10000);
	}
	public String getCmPosition() {
		return cmPosition;
	}
	public void setCmPosition(String cmPosition) {
		this.cmPosition = cmPosition;
	}

	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public Integer getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}
	public Integer getResults() {
		return results;
	}
	public void setResults(Integer results) {
		this.results = results;
	}
	@Override
	public String toString() {
		return "MarkerQuery [chrOptions=" + chrOptions + ", chromosomes="
				+ chromosomes + ", cmPosition=" + cmPosition
				+ ", coordOptions=" + coordOptions + ", coordinateUnits="
				+ coordinateUnits + ", dir=" + dir + ", genomeCoordinates="
				+ genomeCoordinates + ", query=" + query + ", results="
				+ results + ", sort=" + sort + ", startIndex=" + startIndex
				+ ", vocabularies=" + vocabularies + "]";
	}

}
