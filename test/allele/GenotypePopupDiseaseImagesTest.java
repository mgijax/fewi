package allele;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Genotype;
import mgi.frontend.datamodel.GenotypeDisease;
import mgi.frontend.datamodel.GenotypeDiseaseReference;
import mgi.frontend.datamodel.phenotype.*;

import org.jax.mgi.fewi.controller.AlleleController;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;


public class GenotypePopupDiseaseImagesTest extends BaseConcordionTest 
{
	
    private String baseUrl = "/allele/genoview/";
    
    public Genotype getGenotype(String genotypeID) throws Exception
    {
    	String url = baseUrl+genotypeID;
    	MockRequest mr = mockRequest(url);
    	Genotype g = (Genotype) mr.get("genotype");
    	return g;
    }
    
    public List<GenotypeDisease> getDiseases(String genotypeID) throws Exception
    {
    	Genotype g = getGenotype(genotypeID);
    	List<GenotypeDisease> diseases = g.getDiseases();
    	return diseases;
    }
    
    /*
     * Actual test functions below
     */
    
    public List<String> getDiseaseNames(String genotypeID) throws Exception
    {
    	List<String> dNames = new ArrayList<String>();
    	for(GenotypeDisease d : getDiseases(genotypeID))
    	{
    		dNames.add(d.getTerm());
    	}
    	return dNames;
    }
    
    public List<String> getOmimIDs(String genotypeID) throws Exception
    {
    	List<String> omims = new ArrayList<String>();
    	for(GenotypeDisease d : getDiseases(genotypeID))
    	{
    		omims.add(d.getTermID());
    	}
    	return omims;
    }
    
    public List<String> getJnumsForDisease(String genotypeID,String omimID) throws Exception
    {
    	List<String> jnums = new ArrayList<String>();
    	for(GenotypeDisease d : getDiseases(genotypeID))
    	{
    		if (d.getTermID().equalsIgnoreCase(omimID))
    		{
    			for(GenotypeDiseaseReference r : d.getReferences())
    			{
    				jnums.add(r.getJnumID());
    			}
    		}
    	}
    	return jnums;
    }
    
    public String getThumbnailCaption(String genotypeID) throws Exception
    {
    	Genotype g = getGenotype(genotypeID);
    	return g.getThumbnail().getCaption();
    }
    
    public String getImageMGIID(String genotypeID) throws Exception
    {
    	Genotype g = getGenotype(genotypeID);
    	return g.getPrimaryImage().getMgiID();
    }
    
    public String getThumbnailPixelDBID(String genotypeID) throws Exception
    {
    	Genotype g = getGenotype(genotypeID);
    	return g.getThumbnail().getPixeldbNumericID();
    }
}
