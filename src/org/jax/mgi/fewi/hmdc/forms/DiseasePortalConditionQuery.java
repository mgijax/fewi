package org.jax.mgi.fewi.hmdc.forms;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.jax.mgi.shr.fe.query.SolrLocationTranslator;

import com.google.common.base.Joiner;

public class DiseasePortalConditionQuery {

	private String field;
	private DiseasePortalCondition condition;
	
	public DiseasePortalConditionQuery() {}
	
	public DiseasePortalConditionQuery(String field, DiseasePortalCondition condition) {
		this.field = field;
		this.condition = condition;
	}
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
		if(field.equals(DiseasePortalFields.MARKER_ID_SEARCH) || field.equals(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE_ID) || field.equals(DiseasePortalFields.TERM_SEARCH_GENE_UPLOAD)) {
			tokens = condition.getIdTokens();
			List<Filter> filterList = new ArrayList<Filter>();
			for(String token: tokens) {
				if(field.equals(DiseasePortalFields.TERM_SEARCH_GENE_UPLOAD)) {
					filterList.add(new Filter(DiseasePortalFields.MARKER_ID_SEARCH, token, Operator.OP_EQUAL));
				} else {
					filterList.add(new Filter(field, token, Operator.OP_EQUAL));
				}
			}
			return Filter.or(filterList);
		} else if(field.equals(DiseasePortalFields.LOCATION)) {
			String locationQuery = SolrLocationTranslator.getQueryValue(condition.getInput());
			
			if(condition.getParameters().contains("mouse")) {
				return new Filter(DiseasePortalFields.MOUSE_COORDINATE, locationQuery, Operator.OP_HAS_WORD);
			} else if(condition.getParameters().contains("human")) {
				return new Filter(DiseasePortalFields.HUMAN_COORDINATE, locationQuery, Operator.OP_HAS_WORD);
			}
			return null;
			
		} else {
			tokens = condition.getWordTokens();
			return new Filter(field, Joiner.on(" ").join(tokens), 100);
			/* The following code would make wildcards
			 * work with one token and proximity with
			 * multiple words
			// If we have one word run it as a word search
			// Otherwise run it as a proximity search
			if(tokens.size() == 1) {
				return new Filter(field, tokens.get(0), Operator.OP_HAS_WORD);
			} else {
				return new Filter(field, Joiner.on(" ").join(tokens), 100);
			}
			*/
		}
	}
	
	public Filter genHighlightFilter() {
		
		if(field.equals(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE_TEXT)) {
			return new Filter(DiseasePortalFields.TERM_TEXT_HIGHLIGHT, condition.getInput(), 100);
		} else if(field.equals(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE_ID)) {
			List<Filter> filterList = new ArrayList<Filter>();

			List<String> tokens = condition.getIdTokens();
			for(String token: tokens) {
				filterList.add(new Filter(DiseasePortalFields.TERM_ID_HIGHLIGHT, token, Operator.OP_EQUAL));
			}
			return Filter.or(filterList);
		}
		return null;
		
	}
}
