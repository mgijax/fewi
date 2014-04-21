package allele;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.phenotype.MPAnnot;
import mgi.frontend.datamodel.phenotype.MPSystem;
import mgi.frontend.datamodel.phenotype.MPTerm;

import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockRequest;


public class GenotypePopupSexSpecificityTest extends BaseConcordionTest {
	
    private final String baseUrl = "/allele/genoview/";
    
    @SuppressWarnings("unchecked")
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
    	new ArrayList<String>();
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