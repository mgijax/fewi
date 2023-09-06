package org.jax.mgi.fewi.hmdc.forms;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiseasePortalCondition {

	private String input;
	private List<String> parameters;
	
	public DiseasePortalCondition() {}
	
	public DiseasePortalCondition(String input) {
		this.input = input;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public List<String> getParameters() {
		return parameters;
	}
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	@JsonIgnore
	public List<String> getIdTokens() {

//	removed so we can match marker symbols containing pound signs.  There are currently no IDs with a # sign,
//	no term synonyms with a # sign, and only one term with a # sign.  (It's a GO term, so doesn't affect HMDC.)
//		input = input.replaceAll("\\#", "");

//	removed so we can match marker symbols containing asterisks, which makes us miss a single DO synonym.
//	SME okayed this trade-off:
//		input = input.replaceAll("\\*", "");

		input = input.replaceAll("[^\\w:\\.\\-\\(\\)\\/\\#\\@\\<\\>\\*]+", " ");
		input = input.replaceAll("\\^", " ");
		input = input.replaceAll("\"", " ");
		input = input.replaceAll("\\s+", " ").trim();
		return Arrays.asList(input.split(" "));
	}
	
	@JsonIgnore
	public List<String> getWordTokens() {
		input = input.replaceAll("[^\\w^\\*]+", " ");
		input = input.replaceAll("\\^", " ");
		input = input.replaceAll("_", " ");
		input = input.replaceAll("\\s+", " ").trim();
		return Arrays.asList(input.split(" "));
	}
}
