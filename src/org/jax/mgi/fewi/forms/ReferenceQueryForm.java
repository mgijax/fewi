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
	
	private Integer seqKey;
	private Integer alleleKey;
	
	private List<String> authorFilter = new ArrayList<String>();
	private List<String> journalFilter = new ArrayList<String>();
	private List<Integer> yearFilter = new ArrayList<Integer>();
	private List<String> curatedDataFilter = new ArrayList<String>();
	
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
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isInTitle() {
		return inTitle;
	}
	public void setInTitle(boolean inTitle) {
		this.inTitle = inTitle;
	}
	public boolean isInAbstract() {
		return inAbstract;
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
	public List<String> getCuratedDataFilter() {
		return curatedDataFilter;
	}
	public void setCuratedDataFilter(List<String> curatedDateFilter) {
		this.curatedDataFilter = curatedDateFilter;
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
	@Override
	public String toString() {
		return "ReferenceQueryForm [Journal=" + Journal + ", alleleKey="
				+ alleleKey + ", author=" + author + ", authorFilter="
				+ authorFilter + ", authorScope=" + authorScope
				+ ", curatedDataFilter=" + curatedDataFilter + ", id=" + id
				+ ", inAbstract=" + inAbstract + ", inTitle=" + inTitle
				+ ", journalFilter=" + journalFilter + ", seqKey=" + seqKey
				+ ", text=" + text + ", year=" + year + ", yearFilter="
				+ yearFilter + "]";
	}
	
}
