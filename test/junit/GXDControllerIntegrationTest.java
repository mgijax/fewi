package junit;

import org.jax.mgi.fewi.controller.GXDController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-ci.xml"})
@TransactionConfiguration
@Transactional
public class GXDControllerIntegrationTest 
{
    @Autowired
    private AnnotationMethodHandlerAdapter handler;
    @Autowired
    private MockHttpServletRequest request;
    @Autowired
    private MockHttpServletResponse response;
    
    // The class being tested is autowired via spring's DI
    @Autowired
    private GXDController gxdController;

    @Before
    public void setUp() {
    }

    @Test
    public void testQueryReturnsResults() throws Exception
    {
    	String query = "a";
    	request.setRequestURI("/gxd");
    	request.addParameter("nomenclature", query);
    	request.setMethod("GET");
    	final ModelAndView mav = handler.handle(request,response,gxdController);
    	ModelAndViewAssert.assertViewName(mav, "gxd_query");
//    	ModelMap modelMap = mav.getModelMap();
//    	int totalCount = (Integer) modelMap.get("totalCount");
//    	assertTrue(totalCount>0);
    }

}
