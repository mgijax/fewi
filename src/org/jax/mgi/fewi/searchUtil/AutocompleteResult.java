package org.jax.mgi.fewi.searchUtil;

public class AutocompleteResult {
	private String label;
	private String value;
	
	public AutocompleteResult() {}

	public AutocompleteResult(String value) {
		this.value = convertAngleBrackets(value);
		this.label = this.value;
	}

	private String convertAngleBrackets(String s) {
		if (s == null) { return s; }
		return s.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
	
	public String getLabel() {
		return label;
	}

	public void setSynonym(String synonym) {
		this.label = convertAngleBrackets(synonym + ", syn. of [" + this.value + "]");
	}

	public String getValue() {
		return value;
	}

	public void setLabel(String label) {
		this.label = convertAngleBrackets(label);
	}

	public void setValue(String value) {
		this.value = convertAngleBrackets(value);
	}
}
