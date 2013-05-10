package cre;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.controller.AutoCompleteController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.StructureACResult;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockCreHttpQuery;
import org.jax.mgi.fewi.test.mock.MockRecombinaseSummary;
import org.springframework.beans.factory.annotation.Autowired;


public class RecomActivitySearchTest extends BaseConcordionTest 
{
	 // The class being tested is autowired via spring's DI
   @Autowired
   private AutoCompleteController autoCompleteController;
   
   /*
    * Concordion tests for the search
    */
	public int getAlleleCountByStructure(String structure) throws Exception
    {
    	MockCreHttpQuery mq = getMockQuery().creHttp();
    	mq.setStructure(structure);
    	
    	JsonSummaryResponse<MockRecombinaseSummary> summary = mq.getRecombinaseSummary();

    	return summary.getTotalCount();
    }
	
	public List<String> getAlleleSymbolsByStructure(String structure) throws Exception
	{
    	MockCreHttpQuery mq = getMockQuery().creHttp();
    	mq.setStructure(structure);
    	
    	JsonSummaryResponse<MockRecombinaseSummary> summary = mq.getRecombinaseSummary();

    	List<String> alleleSymbols = new ArrayList<String>();
    	for(MockRecombinaseSummary sr : summary.getSummaryRows())
    	{
    		alleleSymbols.add(sr.getAlleleSymbol());
    	}
    	return alleleSymbols;
    }

    
	/*
	 * Concordion methods for the autocomplete
	 */
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
    
    public List<String> getTermsWithCre(String query)
    {
    	SearchResults<StructureACResult> results = 
        		autoCompleteController.structureAutoComplete(query);
    	List<String> terms = new ArrayList<String>();
    	for(StructureACResult result : results.getResultObjects())
    	{
    		if (result.getHasCre())
    		{
	    		// synonym is the field we display in the dropdown.
	    		terms.add(result.getSynonym());
    		}
    	}
    	return terms;
    }
    
    public List<String> getTermsWithoutCre(String query)
    {
    	SearchResults<StructureACResult> results = 
        		autoCompleteController.structureAutoComplete(query);
    	List<String> terms = new ArrayList<String>();
    	for(StructureACResult result : results.getResultObjects())
    	{
    		if (!result.getHasCre())
    		{
	    		// synonym is the field we display in the dropdown.
	    		terms.add(result.getSynonym());
    		}
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
	
