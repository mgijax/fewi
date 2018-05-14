package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.List;


public class ReferenceQueryForm {
	
	private String author = "";
	private String authorScope = "any";
	private String Journal = "";
	private String year = "";
	private String text = "";
	private boolean inTitle = true;
	private boolean inAbstract = true;
	private String id = "";
	private String key = "";
	private String diseaseRelevantMarkerId = "";
	private String diseaseId = "";
	private String goMarkerId = "";
	private String phenoMarkerId = "";
	private String strainId = "";
	
	private Integer seqKey;
	private Integer alleleKey;
	private Integer markerKey;
	
	private List<String> authorFilter = new ArrayList<String>();
	private List<String> journalFilter = new ArrayList<String>();
	private List<Integer> yearFilter = new ArrayList<Integer>();
	private List<String> dataFilter = new ArrayList<String>();
	private List<String> typeFilter = new ArrayList<String>();
	
	public String getGoMarkerId() {
		return goMarkerId;
	}
	public void setGoMarkerId(String goMarkerId) {
		this.goMarkerId = goMarkerId;
	}
	public String getPhenoMarkerId() {
		return phenoMarkerId;
	}
	public void setPhenoMarkerId(String phenoMarkerId) {
		this.phenoMarkerId = phenoMarkerId;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getJournal() {
		return Journal;
	}
	public void setJournal(String journal) {
		Journal = journal;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getStrainId() {
		return strainId;
	}
	public void setStrainId(String strainId) {
		this.strainId = strainId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isInTitle() {
		return inTitle;
	}
	public String getInTitle(){
		if (this.inTitle){
			return "true";
		}
		return "false";
	}
	public void setInTitle(boolean inTitle) {
		this.inTitle = inTitle;
	}
	public boolean isInAbstract() {
		return inAbstract;
	}
	public String getInAbstract(){
		if (this.inAbstract){
			return "true";
		}
		return "false";
	}
	public void setInAbstract(boolean inAbstract) {
		this.inAbstract = inAbstract;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAuthorScope() {
		return authorScope;
	}
	public void setAuthorScope(String authorScope) {
		this.authorScope = authorScope;
	}
	public List<String> getAuthorFilter() {
		return authorFilter;
	}
	public void setAuthorFilter(List<String> authorFilter) {
		this.authorFilter = authorFilter;
	}
	public List<String> getCleanedTypeFilter() {
		List<String> baseFilter = this.getTypeFilter();
		List<String> cleanedFilter = new ArrayList<String>();

		StringBuffer other = new StringBuffer();

		for (String s : baseFilter) {
			if ("Literature".equals(s)) {
				cleanedFilter.add(s);
			} else if (other.length() == 0) {
				other.append(s);
			} else {
				other.append(", ");
				other.append(s);
			}
		}
		if (other.length() > 0) {
			cleanedFilter.add(other.toString());
		}
		return cleanedFilter;
	}
	public List<String> getTypeFilter() {
		return typeFilter;
	}
	public void setTypeFilter(List<String> typeFilter) {
		this.typeFilter = typeFilter;
	}
	public List<String> getJournalFilter() {
		return journalFilter;
	}
	public void setJournalFilter(List<String> journalFilter) {
		this.journalFilter = journalFilter;
	}
	public List<Integer> getYearFilter() {
		return yearFilter;
	}
	public void setYearFilter(List<Integer> yearFilter) {
		this.yearFilter = yearFilter;
	}
	public List<String> getDataFilter() {
		return dataFilter;
	}
	public void setDataFilter(List<String> dateFilter) {
		this.dataFilter = dateFilter;
	}
	public Integer getSeqKey() {
		return seqKey;
	}
	public void setSeqKey(Integer seqKey) {
		this.seqKey = seqKey;
	}
	public Integer getAlleleKey() {
		return alleleKey;
	}
	public void setAlleleKey(Integer alleleKey) {
		this.alleleKey = alleleKey;
	}
	public Integer getMarkerKey() {
		return markerKey;
	}
	public void setMarkerKey(Integer markerKey) {
		this.markerKey = markerKey;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getDiseaseRelevantMarkerId() {
		return diseaseRelevantMarkerId;
	}
	public void setDiseaseRelevantMarkerId(String diseaseRelevantMarkerId) {
		this.diseaseRelevantMarkerId = diseaseRelevantMarkerId;
	}
	public String getDiseaseId() {
		return diseaseId;
	}
	public void setDiseaseId(String diseaseId) {
		this.diseaseId = diseaseId;
	}
	
	/* return a subset of parameters than can be appended to a URL to
	 * restrict the query
	 */
	public String getUrlFragment() {
		StringBuffer sb = new StringBuffer();
		
		for (String s : this.getCleanedTypeFilter()) {
			sb.append("&typeFilter=");
			sb.append(s);
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return "ReferenceQueryForm [author=" + author + ", authorScope="
				+ authorScope + ", Journal=" + Journal + ", year=" + year
				+ ", text=" + text + ", inTitle=" + inTitle + ", inAbstract="
				+ inAbstract + ", id=" + id + ", key=" + key + ", seqKey="
				+ seqKey + ", alleleKey=" + alleleKey + ", markerKey="
				+ markerKey + ", authorFilter=" + authorFilter
				+ ", journalFilter=" + journalFilter + ", yearFilter="
				+ yearFilter + ", typeFilter=" + typeFilter
				+ ", dataFilter=" + dataFilter + "]";
	}
	
}
