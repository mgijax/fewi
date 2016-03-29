package org.jax.mgi.fewi.forms.models;

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
	public String[] getTokens() {
		input = input.replace(",", " ");
		input = input.replaceAll("\\s+", " ");
		return input.split(" ");
	}
}
