package org.jax.mgi.fewi.forms;


public class ReferenceQueryForm {
	
	private String author;
	private String authorScope = "any";
	private String Journal;
	private String year;
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
	@Override
	public String toString() {
		return "ReferenceQueryForm [Journal=" + Journal + ", author=" + author
				+ ", authorScope=" + authorScope + ", id=" + id
				+ ", inAbstract=" + inAbstract + ", inTitle=" + inTitle
				+ ", text=" + text + ", year=" + year + "]";
	}
}
