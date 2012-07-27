package gxd;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mgi.frontend.datamodel.GxdAssayResult;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.forms.GxdQueryForm;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.jax.mgi.fewi.test.mock.MockGxdHttpQuery;
import org.jax.mgi.fewi.test.mock.MockJSONGXDAssayResult;
import org.jax.mgi.fewi.test.mock.MockGxdQueryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public class AssayTypeSearchTest extends BaseConcordionTest {

	@Autowired
    private AnnotationMethodHandlerAdapter handler;

    // The class being tested is autowired via spring's DI
    @Autowired
    private GXDController gxdController;

	 
    public int getAssayResultsCountByAssayType(String assayTypeStr) throws Exception
    {
		List<String> assayTypes = MockGxdQueryParser.convertStringToAssayTypes(assayTypeStr);
		MockGxdHttpQuery mq = new MockGxdHttpQuery(handler,gxdController);  	
		mq.setAssayType(assayTypes);
    	return mq.getAssayResults().getTotalCount();
    }

    public List<String> getTypesByAssayType(String assayType) throws Exception {
    	MockGxdControllerQuery mq = new MockGxdControllerQuery(gxdController);
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
