package gxd;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.springframework.beans.factory.annotation.Autowired;

public class LocationSearchTest extends BaseConcordionTest {

    // The class being tested is autowired via spring's DI
    @Autowired
    private GXDController gxdController;

	/*
	 * Helper functions for mocking queries
	 */
	public SearchResults<SolrGxdMarker> getGenesByLocation(String locations) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
    	mq.setLocations(locations);
    	return mq.getGenes();
	}

    /**
     * Return the count of markers with expression data
     * 
     * @param query The gene location(s)
     * @return count of the number of markers
     * @throws Exception
     */
    public Integer getGeneCountByLocation(String locations) throws Exception
    {
    	SearchResults<SolrGxdMarker> results = getGenesByLocation(locations);
    	return results.getTotalCount();
    }
    
    public List<String> getGeneSymbolsByLocation(String locations) throws Exception
    {
    	SearchResults<SolrGxdMarker> results = getGenesByLocation(locations);
    	List<String> symbols = new ArrayList<String>();
    	for(SolrGxdMarker m : results.getResultObjects())
    	{
    		symbols.add(m.getSymbol());
    	}
    	return symbols;
    }
    
}