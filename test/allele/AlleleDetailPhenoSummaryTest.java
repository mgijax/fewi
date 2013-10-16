package allele;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.phenotype.*;

import org.jax.mgi.fewi.controller.AlleleController;
import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class AlleleDetailPhenoSummaryTest extends BaseConcordionTest {
	
    private String baseUrl = "/allele/phenotable/";
    
    public List<PhenoTableSystem> getPhenoTableSystems(String alleleID) throws Exception
    {
    	String url = baseUrl+alleleID;
    	MockRequest mr = mockRequest(url);
    	List<PhenoTableSystem> ptSystems = (List<PhenoTableSystem>) mr.get("phenoTableSystems");
    	return ptSystems;
    }
    
	/*
	 * The actual test functions
	 */

    public List<String> getSystemsForAllele(String alleleID) throws Exception
    {
    	List<String> systems = new ArrayList<String>();
    	for(PhenoTableSystem ptSystem : getPhenoTableSystems(alleleID))
    	{
    		systems.add(ptSystem.getSystem());
    	}
    	return systems;
    }
    
    public List<String> getTermsForSystem(String alleleID, String systemName) throws Exception
    {
    	List<String> terms = new ArrayList<String>();
    	for(PhenoTableSystem ptSystem : getPhenoTableSystems(alleleID))
    	{
    		if(ptSystem.getSystem().equalsIgnoreCase(systemName))
    		{
	    		for(PhenoTableTerm ptTerm : ptSystem.getPhenoTableTerms())
	    		{
	    			terms.add(ptTerm.getTerm());
	    		}
    		}
    	}
    	return terms;
    }
    public List<String> getTermIDsForSystem(String alleleID, String systemName) throws Exception
    {
    	List<String> termIDs = new ArrayList<String>();
    	for(PhenoTableSystem ptSystem : getPhenoTableSystems(alleleID))
    	{
    		if(ptSystem.getSystem().equalsIgnoreCase(systemName))
    		{
	    		for(PhenoTableTerm ptTerm : ptSystem.getPhenoTableTerms())
	    		{
	    			termIDs.add(ptTerm.getTermId());
	    		}
    		}
    	}
    	return termIDs;
    }
    
    public int getTermSeqBySystem(String alleleID,String systemName,String termToMatch) throws Exception
    {
    	for(PhenoTableSystem ptSystem : getPhenoTableSystems(alleleID))
    	{
    		if(ptSystem.getSystem().equalsIgnoreCase(systemName))
    		{
	    		for(PhenoTableTerm ptTerm : ptSystem.getPhenoTableTerms())
	    		{
	    			if(ptTerm.getTerm().equalsIgnoreCase(termToMatch))
	    			{
	    				return ptTerm.getTermSeq();
	    			}
	    		}
    		}
    	}
    	// error condition
    	return -1;
    }
    public int getTermIndentBySystem(String alleleID,String systemName,String termToMatch) throws Exception
    {
    	for(PhenoTableSystem ptSystem : getPhenoTableSystems(alleleID))
    	{
    		if(ptSystem.getSystem().equalsIgnoreCase(systemName))
    		{
	    		for(PhenoTableTerm ptTerm : ptSystem.getPhenoTableTerms())
	    		{
	    			if(ptTerm.getTerm().equalsIgnoreCase(termToMatch))
	    			{
	    				return ptTerm.getIndentationDepth();
	    			}
	    		}
    		}
    	}
    	// error condition
    	return -1;
    }
    
    
    // Helper assert functions
    public boolean assertGreaterThan(int value1,int value2)
    {
    	return value1 > value2;
    }
    public boolean assertOneGreater(int value1, int value2)
    {
    	return value1 == (value2+1);
    }
}
