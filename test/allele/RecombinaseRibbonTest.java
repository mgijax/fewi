package allele;

import java.util.List;

import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockRequest;

import mgi.frontend.datamodel.AlleleSystem;
import mgi.frontend.datamodel.RecombinaseSystemStructure;


public class RecombinaseRibbonTest extends BaseConcordionTest 
{
	private final String baseUrl = "/recombinase/allele/";
    
    @SuppressWarnings("unchecked")
	private List<AlleleSystem> getCreAlleleSystems(String alleleID) throws Exception
    {
    	String url = baseUrl+alleleID;
    	MockRequest mr = mockRequest(url);
    	List<AlleleSystem> aSystems = (List<AlleleSystem>) mr.get("alleleSystems");
    	return aSystems;
    }
    private String translateAgeCategory(String age)
    {
    	if(age.contains("8.9")) return "E1";
    	if(age.contains("13.9")) return "E2";
    	if(age.contains("14")) return "E3";
    	if(age.contains("21")) return "P1";
    	if(age.contains("22")) return "P2";
    	if(age.contains("43")) return "P3";
    	return age;
    }
    public Boolean intToBool(Integer i)
    {	
    	if(i != null) {
    		if(i.equals(1)) return true;
    		if(i.equals(0)) return false;
    	}
    	return null;
    }
    private Boolean getExpressedForSystem(AlleleSystem as,String translatedAge)
    {
    	//if(translatedAge.equals("E1")) return intToBool(as.getAgeE1());
    	//if(translatedAge.equals("E2")) return intToBool(as.getAgeE2());
    	//if(translatedAge.equals("E3")) return intToBool(as.getAgeE3());
    	//if(translatedAge.equals("P1")) return intToBool(as.getAgeP1());
    	//if(translatedAge.equals("P2")) return intToBool(as.getAgeP2());
    	//if(translatedAge.equals("P3")) return intToBool(as.getAgeP3());
    			
    	return null;
    }
    private Boolean getExpressedForStructure(RecombinaseSystemStructure ss,String translatedAge)
    {
    	//if(translatedAge.equals("E1")) return intToBool(ss.getAgeE1());
    	//if(translatedAge.equals("E2")) return intToBool(ss.getAgeE2());
    	//if(translatedAge.equals("E3")) return intToBool(ss.getAgeE3());
    	//if(translatedAge.equals("P1")) return intToBool(ss.getAgeP1());
    	//if(translatedAge.equals("P2")) return intToBool(ss.getAgeP2());
    	//if(translatedAge.equals("P3")) return intToBool(ss.getAgeP3());
    	
    	return null;
    }
    
    
    /*
     * Test functions
     */
    public String isExpressedIn(String alleleID,String system,String structure,String ageCategory) throws Exception
    {
    	Boolean isExpressed=null;
    	List<AlleleSystem> systems = getCreAlleleSystems(alleleID);
    	ageCategory = translateAgeCategory(ageCategory);
    	
    	if(system.equalsIgnoreCase("none"))
    	{
    		// simply search for anything in the specified age category
    		// search at structure level
    		for(AlleleSystem as : systems)
    		{
    			if(system.equalsIgnoreCase(as.getSystem()))
    			{
    				// now we can check the age col
					Boolean systemExprValue = getExpressedForSystem(as,ageCategory);
					if(systemExprValue!=null) isExpressed = isExpressed==null ? systemExprValue : isExpressed || systemExprValue;
    				for(RecombinaseSystemStructure ss : as.getRecombinaseSystemStructures())
    				{
    					if(structure.equalsIgnoreCase(ss.getStructure()))
    					{
    						// now we can check the age col
    						Boolean strExprValue = getExpressedForStructure(ss,ageCategory);
    						if(strExprValue!=null) isExpressed = isExpressed || strExprValue;
    					}
    				}
    			}
    		}
    	}
    	else if(structure.equals("") || structure.contains("at system level"))
    	{
    		// search at system level
    		for(AlleleSystem as : systems)
    		{
    			if(system.equalsIgnoreCase(as.getSystem()))
    			{
					// now we can check the age col
					Boolean exprValue = getExpressedForSystem(as,ageCategory);
					if(exprValue!=null) isExpressed = exprValue;
    			}
    		}
    	}
    	else
    	{
    		// search at structure level
    		for(AlleleSystem as : systems)
    		{
    			if(system.equalsIgnoreCase(as.getSystem()))
    			{
    				for(RecombinaseSystemStructure ss : as.getRecombinaseSystemStructures())
    				{
    					if(structure.equalsIgnoreCase(ss.getStructure()))
    					{
    						// now we can check the age col
    						Boolean exprValue = getExpressedForStructure(ss,ageCategory);
    						if(exprValue!=null) isExpressed = exprValue;
    					}
    				}
    			}
    		}
    	}
    	
    	if(isExpressed==null) return "";
    	
    	return isExpressed ? "Expressed" : "Not Expr";
    }
}
	
