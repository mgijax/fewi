package org.jax.mgi.fewi.forms.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;

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
		List<String> tokens = Arrays.asList(condition.getTokens());
		// TODO this is where tokenization will happen
		if(
			field.equals(DiseasePortalFields.MARKER_NOMEN_SEARCH) ||
			field.equals(DiseasePortalFields.MARKER_ID_SEARCH) ||
			field.equals(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE_TEXT) ||
			field.equals(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE_ID)
			) {
			if(condition.getInput().toLowerCase().startsWith("omim:")) {
				condition.setInput(condition.getInput().replace("(?i)omim:", ""));
			}
			return new Filter(field, "\"" + condition.getInput() + "\"", Operator.OP_HAS_WORD);
		} else {
			return new Filter(field, tokens, Operator.OP_HAS_WORD);
		}
		
	}
}
