package gxd;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdHttpQuery;
import org.jax.mgi.fewi.test.mock.MockGxdQueryParser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public class LitIndexLinksFromQueryOrMarkerTest extends BaseConcordionTest {

	 @Autowired
	    private AnnotationMethodHandlerAdapter handler;

	    // The class being tested is autowired via spring's DI
	    @Autowired
	    private GXDController gxdController;


		 // ================================================================
	    // Unit tests
	    // ================================================================
	    @Test
	    public void testUrlRouting () throws Exception
	    {
	    	MockGxdHttpQuery q = new MockGxdHttpQuery(handler,gxdController);
	    	q.setNomenclature("pax6");
	    	Integer count = q.getGxdLitCount();
	    	assertTrue(count>0);
	    }
		 // ================================================================
	    // Concordion Methods
	    // ================================================================
	    public Integer getCountByNomen(String nomen) throws Exception
	    {
	    	MockGxdHttpQuery q = new MockGxdHttpQuery(handler,gxdController);
	    	q.setNomenclature(nomen);
	    	return q.getGxdLitCount();
	    }
	    public Integer getCountByTermID(String termID) throws Exception
	    {
	    	MockGxdHttpQuery q = new MockGxdHttpQuery(handler,gxdController);
	    	q.setAnnotationId(termID);
	    	return q.getGxdLitCount();
	    }
		public Integer getCountByAge(String ageStr) throws Exception
		{
			List<String> ages = MockGxdQueryParser.parseAgeString(ageStr);
			MockGxdHttpQuery mq = new MockGxdHttpQuery(handler,gxdController);
			mq.setAge(ages);
			return mq.getGxdLitCount();
		}
		public Integer getCountByTS(String tsStr) throws Exception
		{
			List<Integer> stages = MockGxdQueryParser.parseTS(tsStr);
			MockGxdHttpQuery mq = new MockGxdHttpQuery(handler,gxdController);
			mq.setTheilerStage(stages);
			return mq.getGxdLitCount();
		}
		public Integer getCountByAssayType(String assayTypeStr) throws Exception
		{
			List<String> assayTypes = MockGxdQueryParser.convertStringToAssayTypes(assayTypeStr);
			MockGxdHttpQuery mq = new MockGxdHttpQuery(handler,gxdController);
			mq.setAssayType(assayTypes);
			return mq.getGxdLitCount();
		}
		public Integer getCountByNomenAgeAndAssayType(String nomen,String ageStr,String assayTypeStr) throws Exception
		{
			List<String> ages = MockGxdQueryParser.parseAgeString(ageStr);
			List<String> assayTypes = MockGxdQueryParser.convertStringToAssayTypes(assayTypeStr);
			MockGxdHttpQuery mq = new MockGxdHttpQuery(handler,gxdController);
			mq.setNomenclature(nomen);
			mq.setAssayType(assayTypes);
			mq.setAge(ages);
			return mq.getGxdLitCount();
		}
}
