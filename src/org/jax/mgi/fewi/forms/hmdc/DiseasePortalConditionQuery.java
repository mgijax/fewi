package org.jax.mgi.fewi.forms.hmdc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;

import com.google.common.base.Joiner;

public class DiseasePortalConditionQuery {

	private String field;
	private DiseasePortalCondition condition;
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public DiseasePortalCondition getCondition() {
		return condition;
	}
	public void setCondition(DiseasePortalCondition condition) {
		this.condition = condition;
	}
	public Filter genFilter() {
		List<String> tokens = null;
		if(field.equals(DiseasePortalFields.MARKER_ID_SEARCH) || field.equals(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE_ID)) {
			if(condition.getInput().toLowerCase().startsWith("omim:")) {
				condition.setInput(condition.getInput().replaceAll("(?i)omim:", ""));
			}
			tokens = condition.getIdTokens();
			List<Filter> filterList = new ArrayList<Filter>();
			for(String token: tokens) {
				filterList.add(new Filter(field, token, Operator.OP_EQUAL));
			}
			return Filter.or(filterList);
		} else {
			tokens = condition.getWordTokens();
			return new Filter(field, Joiner.on(" ").join(tokens), 100);
		}
	}
}
