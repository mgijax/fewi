package org.jax.mgi.fewi.forms.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;

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
		
		if(field.equals(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE)) {
			List<Filter> list = new ArrayList<Filter>();
			list.add(new Filter(field, tokens, Operator.OP_HAS_WORD));
			list.add(new Filter(DiseasePortalFields.TERM_TYPE, "OMIM", Operator.OP_EQUAL));
			return Filter.and(list);
		} else {
			return new Filter(field, tokens, Operator.OP_HAS_WORD);
		}
	}
}
