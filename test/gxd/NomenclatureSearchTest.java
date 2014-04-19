package gxd;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.jax.mgi.fewi.test.mock.MockGxdHttpQuery;
import org.jax.mgi.fewi.test.mock.MockJSONGXDMarker;
import org.springframework.beans.factory.annotation.Autowired;

public class NomenclatureSearchTest extends BaseConcordionTest {

    // The class being tested is autowired via spring's DI
    @Autowired
    private GXDController gxdController;

	/*
	 * Helper functions for mocking queries
	 */
	public SearchResults<SolrGxdMarker> getGenesByNomen(String nomen) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
    	mq.setNomenclature(nomen);
    	return mq.getGenes();
	}
	
	public JsonSummaryResponse<MockJSONGXDMarker> mockResultQuery(String nomen) throws Exception
    {
    	MockGxdHttpQuery mq = getMockQuery().gxdHttp();
    	mq.setNomenclature(nomen);
    	return mq.getGenes();
    }

    /**
     * Return the count of markers with expression data
     * 
     * @param query The gene symbol
     * @return count of the number of markers
     * @throws Exception
     */
    public Integer getGeneCountByNomen(String nomen) throws Exception
    {
    	JsonSummaryResponse<MockJSONGXDMarker> results = mockResultQuery(nomen);
    	return results.getTotalCount();
    }
    
    public String getFirstGeneSymbolByNomen(String nomen) throws Exception
    {
    	SearchResults<SolrGxdMarker> results = getGenesByNomen(nomen);
    	if(results.getTotalCount() > 0) return results.getResultObjects().get(0).getSymbol();
    	return "";
    }
    
    public List<String> getGeneSymbolsByNomen(String nomen) throws Exception
    {
    	SearchResults<SolrGxdMarker> results = getGenesByNomen(nomen);
    	List<String> symbols = new ArrayList<String>();
    	for(SolrGxdMarker m : results.getResultObjects())
    	{
    		symbols.add(m.getSymbol());
    	}
    	return symbols;
    }
    
}