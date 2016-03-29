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
		
		
		if(field.equals(DiseasePortalFields.MARKER_NOMEN_SEARCH)) {
			return new Filter(field, tokens, Operator.OP_HAS_WORD);
		} else if(field.equals(DiseasePortalFields.MARKER_ID_SEARCH)) {
			List<String> quoted = new ArrayList<String>();
			for(String t: tokens) {
				quoted.add("\"" + t + "\"");
			}
			return new Filter(field, quoted, Operator.OP_HAS_WORD);
		} else if(field.equals(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE)) {
			List<Filter> list = new ArrayList<Filter>();
			list.add(new Filter(field, tokens, Operator.OP_IN));
			list.add(new Filter(DiseasePortalFields.TERM_TYPE, "OMIM", Operator.OP_EQUAL));
			list.add(new Filter(DiseasePortalFields.MARKER_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD));
			return Filter.and(list);
		} else if(field.equals(DiseasePortalFields.TERM_SEARCH)) {
			List<String> quoted = new ArrayList<String>();
			for(String t: tokens) {
				quoted.add("\"" + t + "\"");
			}
			return new Filter(field, quoted, Operator.OP_HAS_WORD);
		} else {
			return new Filter(field, tokens, Operator.OP_HAS_WORD);
		}
		
	}
}
