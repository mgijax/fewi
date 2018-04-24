package org.jax.mgi.fewi.searchUtil;

import java.util.Comparator;

import org.jax.mgi.shr.fe.sort.SmartAlphaComparator;

public class AutocompleteResult {
  	private static SmartAlphaComparator smartAlphaComparator = new SmartAlphaComparator();

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
	
	public AutocompleteResultComparator getComparator() {
		return new AutocompleteResultComparator();
	}

	public class AutocompleteResultComparator implements Comparator<AutocompleteResult> {
		@Override
		public int compare(AutocompleteResult a, AutocompleteResult b) {
			// assumes 'value' for 'a' and 'b' must be non-null
			int i = smartAlphaComparator.compare(a.getValue(), b.getValue());
			if (i == 0) {
				// values match, so sort by labels
				i = smartAlphaComparator.compare(a.getLabel(), b.getLabel());
			}
			return i;
		}
	}
}
