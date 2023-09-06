package org.jax.mgi.fewi.summary;

public class AutocompleteAuthorResult {
	
	private String author;
	
	private Boolean isGenerated;

	public AutocompleteAuthorResult(String author, Boolean isGenerated) {
		this.author = author;
		this.isGenerated = isGenerated;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Boolean getIsGenerated() {
		return isGenerated;
	}

	public void setIsGenerated(Boolean isGenerated) {
		this.isGenerated = isGenerated;
	}

}
