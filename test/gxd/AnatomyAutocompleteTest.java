package gxd;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jax.mgi.fewi.controller.AutoCompleteController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.StructureACResult;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public class AnatomyAutocompleteTest extends BaseConcordionTest {

    @Autowired
    private AnnotationMethodHandlerAdapter handler;

    // The class being tested is autowired via spring's DI
    @Autowired
    private AutoCompleteController autoCompleteController;


	 // ================================================================
    // Unit tests
    // ================================================================
    @Test
    public void testUrlRouting () throws Exception
    {
    	String query = "brain";

    	MockHttpServletRequest request = new MockHttpServletRequest();
    	MockHttpServletResponse response = new MockHttpServletResponse();

    	request.setRequestURI("/autocomplete/structure");
    	request.addParameter("query", query);
    	request.setMethod("GET");

    	handler.handle(request, response, autoCompleteController);

    	
    	ObjectMapper mapper = new ObjectMapper();
    	SearchResults<StructureACResult> results = null; 
    	results = mapper.readValue(response.getContentAsString(), 
    			new TypeReference<SearchResults<StructureACResult>>() { });
    	assertTrue(results.getTotalCount()>0);
    }

	 // ================================================================
    // Concordion Methods
    // ================================================================
    public List<String> getTerms(String query)
    {
    	SearchResults<StructureACResult> results = 
        		autoCompleteController.structureAutoComplete(query);
    	List<String> terms = new ArrayList<String>();
    	for(StructureACResult result : results.getResultObjects())
    	{
    		// synonym is the field we display in the dropdown.
    		terms.add(result.getSynonym());
    	}
    	return terms;
    }
    
    public int getTermCount(String query)
    {
    	int count = autoCompleteController.structureAutoComplete(query).getTotalCount();
    	if(count < 0) count = 0;
    	return count;
    }


}
