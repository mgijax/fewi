package org.jax.mgi.fewi.test.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.hmdc.controller.DiseasePortalController;
import org.jax.mgi.fewi.hmdc.forms.DiseasePortalCondition;
import org.jax.mgi.fewi.hmdc.forms.DiseasePortalConditionGroup;
import org.jax.mgi.fewi.hmdc.forms.DiseasePortalConditionQuery;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope("prototype") 
public class AbstractMockHdpQuery implements MockQuery
{
	public static String AND = "AND";
	public static String OR = "OR";
	
	public String markersUrl = "/diseasePortal/geneQuery";
	public String diseaseUrl = "/diseasePortal/diseaseQuery";
	public String gridUrl = "/diseasePortal/gridQuery";
	public String systemPopupUrl = "/diseasePortal/phenotypePopup";
	public String diseasePopupUrl = "/diseasePortal/diseasePopup";
	
	public int pageSize = 10000;
	public String sort = "default";
	
    // The class being tested is autowired via spring's DI
    protected DiseasePortalController hdpController;
    
    private DiseasePortalConditionGroup cg = new DiseasePortalConditionGroup();
    
    public void setOperator(String operator) throws Exception {
    	if (AND.equals(operator) || OR.equals(operator)) {
    		cg.setOperator(operator);
    	} else {
    		throw new Exception("Unknown operator in AbstractMockHdpQuery.setOperator(" + operator + ")");
    	}
    }

    public void addMarkerSymbolIdClause(String symbolId) {
    	addCondition(new DiseasePortalConditionQuery(DiseasePortalFields.MARKER_ID_SEARCH,
    		new DiseasePortalCondition(symbolId)));
    }
    
    public void addMarkerNameClause(String name) {
    	addCondition(new DiseasePortalConditionQuery(DiseasePortalFields.MARKER_NOMEN_SEARCH,
    		new DiseasePortalCondition(name)));
    }
    
    public void addTermIdClause(String termId) {
    	addCondition(new DiseasePortalConditionQuery(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE_ID,
    		new DiseasePortalCondition(termId)));
    }
    
    public void addTermClause(String term) {
    	addCondition(new DiseasePortalConditionQuery(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE_TEXT,
    		new DiseasePortalCondition(term)));
    }
    
    public void addLocation(String coords, String organism) {
    	DiseasePortalCondition location = new DiseasePortalCondition(coords);
    	List<String> parameters = new ArrayList<String>();
    	parameters.add(organism);
    	location.setParameters(parameters);
    	addCondition(new DiseasePortalConditionQuery(DiseasePortalFields.LOCATION, location));
    }
    
    private void addCondition(DiseasePortalConditionQuery clause) {
    	List<DiseasePortalConditionQuery> clauses = cg.getQueries();
    	if (clauses == null) {
    		clauses = new ArrayList<DiseasePortalConditionQuery>();
    	}
    	clauses.add(clause);
    	cg.setQueries(clauses);
    }
    
    protected String getParametersAsJson() throws JsonGenerationException, JsonMappingException, IOException {
    	ObjectMapper mapper = new ObjectMapper();
    	System.out.println(mapper.writeValueAsString(cg));
    	return mapper.writeValueAsString(cg);
    }
}
