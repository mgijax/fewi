package gxd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.jax.mgi.fewi.test.mock.MockGxdHttpQuery;
import org.jax.mgi.fewi.test.mock.MockGxdQueryParser;
import org.springframework.beans.factory.annotation.Autowired;

public class AssayTypeSearchTest extends BaseConcordionTest {

    // The class being tested is autowired via spring's DI
    @Autowired
    private GXDController gxdController;

	 
    public int getAssayResultsCountByAssayType(String assayTypeStr) throws Exception
    {
		List<String> assayTypes = MockGxdQueryParser.convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery mq = getMockQuery().gxdHttp();
		mq.setAssayType(assayTypes);
    	return mq.getAssayResults().getTotalCount();
    }

    public List<String> getTypesByAssayType(String assayType) throws Exception {
    	MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
    	mq.sort = "random";
    	
    	mq.setAssayType(assayType);
		SearchResults<SolrAssayResult> results = mq.getAssayResults();
		List<String> assayTypes = new ArrayList<String>();
		for (SolrAssayResult result : results.getResultObjects()) {
			assayTypes.add(result.getAssayType());
		}

		return assayTypes;

	}
}
