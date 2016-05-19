package org.jax.mgi.fewi.hmdc.forms;

import java.util.ArrayList;
import java.util.List;

public class DiseasePortalQueryBuilder {
	
	private DiseasePortalConditionGroup queryGroup = new DiseasePortalConditionGroup();
	private List<DiseasePortalConditionQuery> queries = new ArrayList<DiseasePortalConditionQuery>();

	public DiseasePortalQueryBuilder(String groupingConditionOperator) {
		queryGroup.setOperator(groupingConditionOperator);
		queryGroup.setQueries(queries);
	}

	public void addCondition(String conditionField, String conditionInput) {
		queries.add(new DiseasePortalConditionQuery(conditionField, new DiseasePortalCondition(conditionInput)));
	}

	public DiseasePortalConditionGroup getQueryGroup() {
		return queryGroup;
	}
	
}
