package allele;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.phenotype.*;

import org.jax.mgi.fewi.controller.AlleleController;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;


public class GenotypePopupSexSpecificityTest extends BaseConcordionTest {
	
    private String baseUrl = "/allele/genoview/";
    
    public List<MPSystem> getMPSystems(String genotypeID) throws Exception
    {
    	String url = baseUrl+genotypeID;
    	MockRequest mr = mockRequest(url);
    	List<MPSystem> ptSystems = (List<MPSystem>) mr.get("mpSystems");
    	return ptSystems;
    }
    
    /* Actual test functions */
   
    public String getFirstSexGroup(String genotypeID, String system,String termID ) throws Exception
    {
    	List<String> sexGroups = new ArrayList<String>();
    	for(MPSystem s : getMPSystems(genotypeID))
    	{
    		if(s.getSystem().equalsIgnoreCase(system))
    		{
	    		for(MPTerm t : s.getTerms())
	    		{
	    			if(t.getTermId().equalsIgnoreCase(termID))
	    			{
	    				for(MPAnnot annot :t.getAnnots())
	    				{
	    					return formatBlanks(annot.getCombinedSymbol());
	    				}
	    			}
	    		}
    		}
    	}
    	
    	// this really is an error condition, but we don't want to return "", because that could be a legit value
    	return "ERROR";
    }
    
    private String formatBlanks(String str)
    {
    	if(str.equals("")) return "empty string";
    	return str;
    }
    
}