package gxd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    
    private static final int MAX_GENES=4000;
    private static final int MAX_RESULTS=10000;

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
    // gene symbols 1st ribbon query
	public List<String> getGeneSymbols(String structure,String difStructure) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setStructure(structure);
		mq.setDifStructure(difStructure);
		mq.pageSize=MAX_GENES;
		List<String> symbols = new ArrayList<String>();
		SearchResults<SolrGxdMarker> results = mq.getGenes();
		for(SolrGxdMarker gene : results.getResultObjects())
		{
			symbols.add(gene.getSymbol());
		}
		
		return symbols;
	}
	// gene symbyols 2nd ribbon query
	public List<String> getGeneSymbolsByStage(String stages,String difStages) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setTheilerStage(parseStageInput(stages));
		mq.setDifTheilerStage(parseStageInput(difStages));
		mq.pageSize=MAX_GENES;
		List<String> symbols = new ArrayList<String>();
		SearchResults<SolrGxdMarker> results = mq.getGenes();
		for(SolrGxdMarker gene : results.getResultObjects())
		{
			symbols.add(gene.getSymbol());
		}
		
		return symbols;
	}
	
	// gene symbols 3rd ribbon query
	public List<String> getGeneSymbolsByBoth(String structure,String stages,String difStructure,String difStages) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setStructure(structure);
		mq.setTheilerStage(parseStageInput(stages));
		mq.setDifStructure(difStructure);
		mq.setDifTheilerStage(parseStageInput(difStages));
		mq.pageSize=MAX_GENES;
		
		List<String> symbols = new ArrayList<String>();
		SearchResults<SolrGxdMarker> results = mq.getGenes();
		for(SolrGxdMarker gene : results.getResultObjects())
		{
			symbols.add(gene.getSymbol());
		}
		
		return symbols;
	}
	
	/*
	 *  get structure names 1st ribbon query
	 *  
	 *  Can use any of the following:
	 *  getResultStructures(#structure,#difStructure) - returns only positives
	 *  getResultStructuresNegatives(#structure,#difStructure) - returns only negatives
	 *  getResultStructures(#structure,#difStructure,#detectionLevel) - returns structures for the specified detection level
	 */
	public List<String> getResultStructures(String structure,String difStructure) throws Exception
	{
		return getResultStructures(structure,difStructure,"Yes");
	}
	public List<String> getResultStructuresNegatives(String structure,String difStructure) throws Exception
	{
		return getResultStructures(structure,difStructure,"No");
	}
	public List<String> getResultStructures(String structure,String difStructure,String detected) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setStructure(structure);
		mq.setDifStructure(difStructure);
		mq.pageSize=MAX_RESULTS;
		List<String> structures = new ArrayList<String>();
		SearchResults<SolrAssayResult> results = mq.getAssayResults();
		for(SolrAssayResult result : results.getResultObjects())
		{
			if(detected.equalsIgnoreCase(result.getDetectionLevel()))
			{
				structures.add(result.getPrintname());
			}
		}
		
		return structures;
	}
	
	/*
	 * get stages 2nd ribbon query
	 * 	 
	 *  Can use any of the following:
	 *  getResultStages(#stages,#difStages) - returns only positives
	 *  getResultStagesNegatives(#stages,#difStages) - returns only negatives
	 *  getResultStages(#stages,#difStages,#detectionLevel) - returns structures for the specified detection level
	 */
	public List<String> getResultStages(String stages,String difStages) throws Exception
	{
		return getResultStages(stages,difStages,"Yes");
	}
	public List<String> getResultStagesNegatives(String stages,String difStages) throws Exception
	{
		return getResultStages(stages,difStages,"No");
	}
	public List<String> getResultStages(String stages,String difStages,String detected) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setTheilerStage(parseStageInput(stages));
		mq.setDifTheilerStage(parseStageInput(difStages));
		mq.pageSize=MAX_RESULTS;
		
		Set<String> returnStages = new HashSet<String>();
		SearchResults<SolrAssayResult> results = mq.getAssayResults();
		for(SolrAssayResult result : results.getResultObjects())
		{
			if(detected.equalsIgnoreCase(result.getDetectionLevel()))
			{
				returnStages.add(result.getTheilerStage().toString());
			}
		}
		
		return new ArrayList<String>(returnStages);
	}
	
	/*
	 *  get structure names 3rd ribbon query
	 *  
	 *  Can use any of the following:
	 *  getResultStructuresByBoth(#structure,#stages,#difStructure,#difStages) - returns only positives
	 *  getResultStructuresByBothNegatives(#structure,#stages,#difStructure,#difStages) - returns only negatives
	 *  getResultStructuresByBoth(#structure,#stages,#difStructure,#difStages,#detectionLevel) - returns structures for the specified detection level
	 */
	public List<String> getResultStructuresByBoth(String structure,String stages,String difStructure,String difStages) throws Exception
	{
		return getResultStructuresByBoth(structure,stages,difStructure,difStages,"Yes");
	}
	public List<String> getResultStructuresByBothNegatives(String structure,String stages,String difStructure,String difStages) throws Exception
	{
		return getResultStructuresByBoth(structure,stages,difStructure,difStages,"No");
	}
	public List<String> getResultStructuresByBoth(String structure,String stages,String difStructure,String difStages,String detected) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setStructure(structure);
		mq.setTheilerStage(parseStageInput(stages));
		mq.setDifStructure(difStructure);
		mq.setDifTheilerStage(parseStageInput(difStages));
		mq.pageSize=MAX_RESULTS;
		
		List<String> structures = new ArrayList<String>();
		SearchResults<SolrAssayResult> results = mq.getAssayResults();
		for(SolrAssayResult result : results.getResultObjects())
		{
			if(detected.equalsIgnoreCase(result.getDetectionLevel()))
			{
				structures.add(result.getPrintname());
			}
		}
		
		return structures;
	}
}
