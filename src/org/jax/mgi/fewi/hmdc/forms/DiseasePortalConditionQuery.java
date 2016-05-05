package org.jax.mgi.fewi.hmdc.forms;

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
	public Filter genHighlightFilter() {
		
		if(field.equals(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE_TEXT)) {
			List<String> fields = Arrays.asList(
				DiseasePortalFields.TERM,
				DiseasePortalFields.TERM_SYNONYM,
				DiseasePortalFields.TERM_ANCESTOR_TEXT
			);
			
			List<Filter> filterList = new ArrayList<Filter>();
			for(String f: fields) {
				filterList.add(new Filter(f, condition.getInput(), 100));
			}
			return Filter.or(filterList);
				
		} else if(field.equals(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE_ID)) {
			List<String> fields = Arrays.asList(
				DiseasePortalFields.TERM_ID,
				DiseasePortalFields.TERM_ALT_ID,
				DiseasePortalFields.TERM_ANCESTOR_ID
			);
			List<Filter> filterList = new ArrayList<Filter>();

			if(condition.getInput().toLowerCase().startsWith("omim:")) {
				condition.setInput(condition.getInput().replaceAll("(?i)omim:", ""));
			}
			List<String> tokens = condition.getIdTokens();
			for(String token: tokens) {
				for(String f: fields) {
					filterList.add(new Filter(f, token, Operator.OP_EQUAL));
				}
			}
			return Filter.or(filterList);
		}
		return null;
		
	}
}
