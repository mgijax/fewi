package gxd;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.jax.mgi.fewi.test.mock.MockGxdHttpQuery;
import org.jax.mgi.fewi.test.mock.MockGxdQueryParser;
import org.springframework.beans.factory.annotation.Autowired;

public class AgeSearchTest extends BaseConcordionTest {

    // The class being tested is autowired via spring's DI
    @Autowired
    private GXDController gxdController;
	
	/*
	 * The actual test functions
	 */
	public Integer getResultCountByAge(String ageStr) throws Exception
	{
		List<String> ages = MockGxdQueryParser.parseAgeString(ageStr);
		MockGxdHttpQuery mq = getMockQuery().gxdHttp();
		mq.setAge(ages);
		return mq.getAssayResults().getTotalCount();
	}
	public Integer getResultCountByTS(String tsStr) throws Exception
	{
		List<Integer> stages = MockGxdQueryParser.parseTS(tsStr);
		MockGxdHttpQuery mq = getMockQuery().gxdHttp();
		mq.setTheilerStage(stages);
		return mq.getAssayResults().getTotalCount();
	}
	
	public Set<String> getAgesByAge(String ageStr) throws Exception
	{
		Set<String> ageSet = new HashSet<String>();
		List<String> ageTokens = MockGxdQueryParser.parseAgeString(ageStr);
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setAge(ageTokens);
		mq.pageSize = 30000;
		List<SolrAssayResult> results = mq.getAssayResults().getResultObjects();
		for(SolrAssayResult r : results)
		{
			ageSet.add(r.getAge());
		}
		return ageSet;
	}
}
