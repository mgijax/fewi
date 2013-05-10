package gxd;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.springframework.beans.factory.annotation.Autowired;

public class DiffQueryTest extends BaseConcordionTest 
{
	 // The class being tested is autowired via spring's DI
    @Autowired
    private GXDController gxdController;
    
    
    /*
     * input parsing functions
     */
    public List<Integer> parseStageInput(String stages)
    {
    	List<Integer> parsedStages = new ArrayList<Integer>();
    	
    	for(String stage : stages.split(","))
    	{
    		if(stage.equalsIgnoreCase("any")) parsedStages.add(0);
    		else if(stage.equalsIgnoreCase("any stage not selected above")) parsedStages.add(-1);
    		else parsedStages.add(Integer.parseInt(stage));
    		
    	}
    	
    	return parsedStages;
    }
    
    /*
     * Test functions
     */
	public List<String> getGeneSymbols(String structure,String difStructure) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setStructure(structure);
		mq.setDifStructure(difStructure);
		mq.pageSize=4000;
		List<String> symbols = new ArrayList<String>();
		SearchResults<SolrGxdMarker> results = mq.getGenes();
		for(SolrGxdMarker gene : results.getResultObjects())
		{
			symbols.add(gene.getSymbol());
		}
		
		return symbols;
	}
	public List<String> getGeneSymbolsByStage(String stages1,String stages2) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setTheilerStage(parseStageInput(stages1));
		mq.setDifTheilerStage(parseStageInput(stages2));
		mq.pageSize=4000;
		List<String> symbols = new ArrayList<String>();
		SearchResults<SolrGxdMarker> results = mq.getGenes();
		for(SolrGxdMarker gene : results.getResultObjects())
		{
			symbols.add(gene.getSymbol());
		}
		
		return symbols;
	}
	public List<String> getResultStructures(String structure,String difStructure) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setStructure(structure);
		mq.setDifStructure(difStructure);
		mq.pageSize=10000;
		List<String> structures = new ArrayList<String>();
		SearchResults<SolrAssayResult> results = mq.getAssayResults();
		for(SolrAssayResult result : results.getResultObjects())
		{
			structures.add(result.getPrintname());
		}
		
		return structures;
	}
}
