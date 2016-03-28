package org.jax.mgi.fewi.forms.models;

import java.util.List;

public class ConditionGroup {

	private String operator;
	private List<ConditionQuery> queries;
	

	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public List<ConditionQuery> getQueries() {
		return queries;
	}
	public void setQueries(List<ConditionQuery> queries) {
		this.queries = queries;
	}
}
