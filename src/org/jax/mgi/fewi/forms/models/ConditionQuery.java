package org.jax.mgi.fewi.forms.models;

import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;

public class ConditionQuery {

	private String field;
	private Condition condition;
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public Condition getCondition() {
		return condition;
	}
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	public Filter genFilter() {
		List<String> tokens = Arrays.asList(condition.getTokens());
		return new Filter(field, tokens, Operator.OP_HAS_WORD);
	}
}
