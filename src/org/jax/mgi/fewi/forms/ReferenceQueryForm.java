package org.jax.mgi.fewi.forms;

import java.util.HashSet;
import java.util.Set;

public class ReferenceQueryForm {
	
	private String author;
	private String authorScope = "any";
	private Set<String> authorOperator = new HashSet<String>();
	private String Journal;
	private Integer year;
	private String text;
	private boolean inTitle = true;
	private boolean inAbstract = true;
	private String id;
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Set<String> getAuthorOperator() {
		return authorOperator;
	}
	public void setAuthorOperator(Set<String> authorOperator) {
		this.authorOperator = authorOperator;
	}
	public String getJournal() {
		return Journal;
	}
	public void setJournal(String journal) {
		Journal = journal;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
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
	@Override
	public String toString() {
		return "ReferenceQueryForm [Journal=" + Journal + ", author=" + author
				+ ", authorOperator=" + authorOperator + ", authorScope="
				+ authorScope + ", id=" + id + ", inAbstract=" + inAbstract
				+ ", inTitle=" + inTitle + ", text=" + text + ", year=" + year
				+ "]";
	}

}
