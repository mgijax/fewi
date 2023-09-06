package gxd;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jax.mgi.fewi.controller.AutoCompleteController;
import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.EmapaACResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.jax.mgi.fewi.test.mock.MockGxdHttpQuery;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AnatomySearchTest extends BaseConcordionTest {

    // The class being tested is autowired via spring's DI
    @Autowired
    private GXDController gxdController;
	    
	public int getResultCount(String structure) throws Exception
	{
		MockGxdHttpQuery mq = getMockQuery().gxdHttp();
		mq.setStructure(structure);
		return mq.getAssayResults().getTotalCount();
	}
	public int getAssayCount(String structure) throws Exception
	{
		MockGxdHttpQuery mq = getMockQuery().gxdHttp();
		mq.setStructure(structure);
		return mq.getAssays().getTotalCount();
	}
	public int getGeneCount(String structure) throws Exception
	{
		MockGxdHttpQuery mq = getMockQuery().gxdHttp();
		mq.setStructure(structure);
		return mq.getGenes().getTotalCount();
	}
	
	public List<String> getGeneSymbols(String structure) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setStructure(structure);
		List<String> symbols = new ArrayList<String>();
		SearchResults<SolrGxdMarker> results = mq.getGenes();
		for(SolrGxdMarker gene : results.getResultObjects())
		{
			symbols.add(gene.getSymbol());
		}
		
		return symbols;
	}
	
	public Set<String> getStructures(String structure) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setStructure(structure);
		Set<String> structures = new HashSet<String>();
		SearchResults<SolrAssayResult> results = mq.getAssayResults();
		for(SolrAssayResult result : results.getResultObjects())
		{
			structures.add(result.getPrintname());
		}
		
		return structures;
	}
	
	// Returns the structures for each result combined with their TS e.g. "TS4: inner cell mass"
	public Set<String> getTSStructures(String structure) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setStructure(structure);
		Set<String> structures = new HashSet<String>();
		SearchResults<SolrAssayResult> results = mq.getAssayResults();
		for(SolrAssayResult result : results.getResultObjects())
		{
			structures.add("TS"+result.getTheilerStage()+": "+result.getPrintname());
		}
		
		return structures;
	}

	// HERE TO LINE 144 COPIED OUT OF AnatomyAutocomplete FOR TEST CONSOLIDATION
	// klf 3/25/2013
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

    	request.setRequestURI("/autocomplete/structure");
    	request.addParameter("query", query);
    	request.setMethod("GET");
    	
    	MockHttpServletResponse response = mockRequest().handle(request);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	SearchResults<EmapaACResult> results = null; 
    	results = mapper.readValue(response.getContentAsString(), 
    			new TypeReference<SearchResults<EmapaACResult>>() { });
    	assertTrue(results.getTotalCount()>0);
    }

	 // ================================================================
    // Concordion Methods
    // ================================================================
    public List<String> getTerms(String query)
    {
    	SearchResults<EmapaACResult> results = 
        		autoCompleteController.emapaAutoComplete(query);
    	List<String> terms = new ArrayList<String>();
    	for(EmapaACResult result : results.getResultObjects())
    	{
    		// synonym is the field we display in the dropdown.
    		terms.add(result.getSynonym());
    	}
    	return terms;
    }
    
    public int getTermCount(String query)
    {
    	int count = autoCompleteController.emapaAutoComplete(query).getTotalCount();
    	if(count < 0) count = 0;
    	return count;
    }

	
	

}

