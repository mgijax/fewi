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
import org.jax.mgi.fewi.test.mock.MockGxdHttpQuery;
import org.springframework.beans.factory.annotation.Autowired;

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
}

