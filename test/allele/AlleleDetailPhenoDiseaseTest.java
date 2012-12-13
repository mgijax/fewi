package allele;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.AlleleDisease;
import mgi.frontend.datamodel.phenotype.*;

import org.jax.mgi.fewi.controller.AlleleController;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

public class AlleleDetailPhenoDiseaseTest extends BaseConcordionTest {
	
    private String baseUrl = "/allele/phenotable/";
    
    public List<AlleleDisease> getDiseases(String alleleID) throws Exception
    {
    	String url = baseUrl+alleleID;
    	MockRequest mr = mockRequest(url);
    	List<AlleleDisease> diseases = (List<AlleleDisease>) mr.get("diseases");
    	return diseases;
    }
    
	/*
	 * The actual test functions
	 */
}
