package org.jax.mgi.fewi.forms.hmdc;

import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class DiseasePortalCondition {

	private String input;
	private List<String> parameters;
	
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
		input = input.replaceAll("[^\\w^:]+", " ");
		input = input.replaceAll("\\^", " ");
		input = input.replaceAll("_", " ");
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
