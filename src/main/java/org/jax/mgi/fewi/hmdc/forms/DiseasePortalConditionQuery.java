package org.jax.mgi.fewi.hmdc.forms;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.jax.mgi.shr.fe.query.SolrLocationTranslator;
import org.jax.mgi.fewi.hmdc.finder.DiseasePortalFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

public class DiseasePortalConditionQuery {
	private Logger logger = LoggerFactory.getLogger(DiseasePortalConditionQuery.class);

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
	
	public Filter genFilter(DiseasePortalFinder hdpFinder) {
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
			String coords = condition.getInput();
			String organism = "mouse";
			
			if (condition.getParameters().contains("human")) {
				organism = "human";
			}
			
			List<String> markerKeys = new ArrayList<String>();

			List<Integer> keys = hdpFinder.getMarkersByCoord(organism, coords);
			if (keys != null) {
				for (Integer key : keys) {
					markerKeys.add(key.toString());
				}
			}

			return new Filter(DiseasePortalFields.MARKER_KEY, markerKeys, Filter.Operator.OP_IN);
			
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
