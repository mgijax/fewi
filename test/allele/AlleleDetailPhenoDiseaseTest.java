package allele;

import java.util.List;

import mgi.frontend.datamodel.AlleleDisease;

import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockRequest;

public class AlleleDetailPhenoDiseaseTest extends BaseConcordionTest {
	
    private final String baseUrl = "/allele/phenotable/";
    
    @SuppressWarnings("unchecked")
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
