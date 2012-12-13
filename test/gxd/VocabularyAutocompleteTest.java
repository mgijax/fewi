package gxd;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jax.mgi.fewi.controller.AutoCompleteController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.VocabACResult;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.VocabACSummaryRow;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;


public class VocabularyAutocompleteTest extends BaseConcordionTest {

    // The class being tested is autowired via spring's DI
    @Autowired
    private AutoCompleteController autoCompleteController;


	 // ================================================================
    // Unit tests
    // ================================================================
    @Test
    public void testUrlRouting () throws Exception
    {
    	String query = "an";
    	JsonSummaryResponse<VocabACSummaryRow> results = mockHttpQuery(query);
    	assertTrue(results.getTotalCount()>0);
    }
    // ===============================================================
    // Helper functions
    // ===============================================================
    public JsonSummaryResponse<VocabACSummaryRow> mockHttpQuery(String query) throws Exception
    {
    	MockHttpServletRequest request = new MockHttpServletRequest();

    	request.setRequestURI("/autocomplete/vocabTerm");
    	request.addParameter("query", query);
    	request.setMethod("GET");

    	MockHttpServletResponse response = mockRequest().handle(request);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	JsonSummaryResponse<VocabACSummaryRow> results = null; 
    	results = mapper.readValue(response.getContentAsString(), 
    			new TypeReference<JsonSummaryResponse<VocabACSummaryRow>>() { });
    	
    	return results;
    }

	 // ================================================================
    // Concordion Methods
    // ================================================================
    public List<String> getTerms(String query)
    {
    	SearchResults<VocabACResult> results = 
        		autoCompleteController.getVocabAutoCompleteResults(query);
    	List<String> terms = new ArrayList<String>();
    	for(VocabACResult result : results.getResultObjects())
    	{
    		// term is the field we display in the dropdown (this field is unformatted).
    		terms.add(result.getTerm());
    	}
    	return terms;
    }
    public List<String> getFormattedTerms(String query) throws Exception
    {
    	JsonSummaryResponse<VocabACSummaryRow> results = mockHttpQuery(query);
    	
    	List<String> formattedTerms = new ArrayList<String>();
    	for(VocabACSummaryRow result : results.getSummaryRows())
    	{
    		// This is the formatted text we put in the input box when user selects a term
    		formattedTerms.add(result.getInputTerm());
    	}
    	return formattedTerms;
    }
    
    public int getTermCount(String query)
    {
    	int count = autoCompleteController.vocabAutoComplete(query).getTotalCount();
    	if(count < 0) count = 0;
    	return count;
    }

}

