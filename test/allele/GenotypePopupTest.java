package allele;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.phenotype.MPAnnot;
import mgi.frontend.datamodel.phenotype.MPAnnotationNote;
import mgi.frontend.datamodel.phenotype.MPReference;
import mgi.frontend.datamodel.phenotype.MPSystem;
import mgi.frontend.datamodel.phenotype.MPTerm;

import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockRequest;


public class GenotypePopupTest extends BaseConcordionTest 
{
		
	    private final String baseUrl = "/allele/genoview/";
	    
	    @SuppressWarnings("unchecked")
		public List<MPSystem> getMPSystems(String genotypeID) throws Exception
	    {
	    	String url = baseUrl+genotypeID;
	    	MockRequest mr = mockRequest(url);
	    	List<MPSystem> ptSystems = (List<MPSystem>) mr.get("mpSystems");
	    	return ptSystems;
	    }
	    
	    /*
	     * Actual Test functions
	     */
	    public List<String> getSystems(String genotypeID) throws Exception
	    {
	    	List<String> systems = new ArrayList<String>();
	    	for(MPSystem system : getMPSystems(genotypeID))
	    	{
	    		systems.add(system.getSystem());
	    	}
	    	return systems;
	    }
	    public List<String> getTermsForSystem(String genotypeID,String systemName) throws Exception
	    {
	    	List<String> terms = new ArrayList<String>();
	    	for(MPSystem system : getMPSystems(genotypeID))
	    	{
	    		if(system.getSystem().equalsIgnoreCase(systemName))
	    		{	
	    			for(MPTerm t : system.getTerms())
	    			{
	    				terms.add(t.getTerm());
	    			}
	    		}
	    	}
	    	return terms;
	    }
	    public int getTermIndentBySystem(String genotypeID,String systemName,String termToMatch) throws Exception
	    {
	    	for(MPSystem system : getMPSystems(genotypeID))
	    	{
	    		if(system.getSystem().equalsIgnoreCase(systemName))
	    		{	
	    			for(MPTerm t : system.getTerms())
	    			{
	    				if(t.getTerm().equalsIgnoreCase(termToMatch))
	    				{
	    					return t.getIndentationDepth();
	    				}
	    			}
	    		}
	    	}
	    	// error (not found) case
	    	return -1;
	    }
	    public int getTermSeqBySystem(String genotypeID,String systemName,String termToMatch) throws Exception
	    {
	    	for(MPSystem system : getMPSystems(genotypeID))
	    	{
	    		if(system.getSystem().equalsIgnoreCase(systemName))
	    		{	
	    			for(MPTerm t : system.getTerms())
	    			{
	    				if(t.getTerm().equalsIgnoreCase(termToMatch))
	    				{
	    					return t.getTermSeq();
	    				}
	    			}
	    		}
	    	}
	    	// error (not found) case
	    	return -1;
	    }
	    public List<String> getTermJnumsBySystem(String genotypeID,String systemName,String termToMatch) throws Exception
	    {
	    	List<String> jnums = new ArrayList<String>();
	    	for(MPSystem system : getMPSystems(genotypeID))
	    	{
	    		if(system.getSystem().equalsIgnoreCase(systemName))
	    		{	
	    			for(MPTerm t : system.getTerms())
	    			{
	    				if(t.getTerm().equalsIgnoreCase(termToMatch))
	    				{
	    					for(MPAnnot annot : t.getAnnots())
	    					{
		    					for(MPReference ref : annot.getReferences())
		    					{
		    						jnums.add(ref.getJnumID());
		    					}
	    					}
	    				}
	    			}
	    		}
	    	}
	    	return jnums;
	    }
	    
	    public int getNumberOfNotesForTerm(String genotypeID,String systemName,String termToMatch) throws Exception
	    {
	    	for(MPSystem system : getMPSystems(genotypeID))
	    	{
	    		if(system.getSystem().equalsIgnoreCase(systemName))
	    		{	
	    			for(MPTerm t : system.getTerms())
	    			{
	    				if(t.getTerm().equalsIgnoreCase(termToMatch))
	    				{

	    					int noteSize = 0;
	    					for(MPAnnot annot : t.getAnnots())
	    					{
		    					for(MPReference ref : annot.getReferences())
		    					{
			    					noteSize += ref.getNotes().size();
		    					}
	    					}
	    					return noteSize;
	    				}
	    			}
	    		}
	    	}
	    	// error (not found) case
	    	return -1;
	    }
	    
	    public List<String> getNotesByTerm(String genotypeID,String systemName,String termToMatch) throws Exception
	    {
	    	List<String> notes = new ArrayList<String>();
	    	for(MPSystem system : getMPSystems(genotypeID))
	    	{
	    		if(system.getSystem().equalsIgnoreCase(systemName))
	    		{	
	    			for(MPTerm t : system.getTerms())
	    			{
	    				if(t.getTerm().equalsIgnoreCase(termToMatch))
	    				{
	    					for(MPAnnot annot : t.getAnnots())
	    					{
		    					for(MPReference ref : annot.getReferences())
		    					{
		    						for(MPAnnotationNote note : ref.getNotes())
		    						{
		    							notes.add(note.getNote());
		    						}
		    					}
	    					}
	    				}
	    			}
	    		}
	    	}
	    	return notes;
	    }
	    
	    public List<String> getNotesByTermIgnoreSystem(String genotypeID,String termID) throws Exception
	    {
	    	for(MPSystem system : getMPSystems(genotypeID))
	    	{
    			for(MPTerm t : system.getTerms())
    			{
    				if(t.getTermId().equalsIgnoreCase(termID))
    				{
    					List<String> notes = new ArrayList<String>();
    					for(MPAnnot annot : t.getAnnots())
    					{
	    					for(MPReference ref : annot.getReferences())
	    					{
	    						for(MPAnnotationNote note : ref.getNotes())
	    						{
	    							notes.add(note.getNote());
	    						}
	    					}
    					}
    					return notes;
    				}
    			}
	    	}
	    	return new ArrayList<String>();
	    }
}