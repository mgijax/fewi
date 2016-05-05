package org.jax.mgi.fewi.hmdc.forms;

import java.util.List;

public class DiseasePortalConditionGroup {

	private String operator;
	private List<DiseasePortalConditionQuery> queries;
	

	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public List<DiseasePortalConditionQuery> getQueries() {
		return queries;
	}
	public void setQueries(List<DiseasePortalConditionQuery> queries) {
		this.queries = queries;
	}
}
