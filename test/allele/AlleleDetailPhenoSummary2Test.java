package allele;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.phenotype.PhenoTableGenotype;
import mgi.frontend.datamodel.phenotype.PhenoTableProvider;
import mgi.frontend.datamodel.phenotype.PhenoTableSystem;
import mgi.frontend.datamodel.phenotype.PhenoTableSystemCell;
import mgi.frontend.datamodel.phenotype.PhenoTableTerm;
import mgi.frontend.datamodel.phenotype.PhenoTableTermCell;

import org.jax.mgi.fewi.controller.AlleleController;
import org.jax.mgi.fewi.test.concordion.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;


public class AlleleDetailPhenoSummary2Test extends BaseConcordionTest 
{
		
	    private String baseUrl = "/allele/phenotable/";
	    
	    public List<PhenoTableSystem> getPhenoTableSystems(String alleleID) throws Exception
	    {
	    	String url = baseUrl+alleleID;
	    	MockRequest mr = mockRequest(url);
	    	List<PhenoTableSystem> ptSystems = (List<PhenoTableSystem>) mr.get("phenoTableSystems");
	    	return ptSystems;
	    }
	    public List<PhenoTableGenotype> getPhenoTableGenotypes(String alleleID) throws Exception
	    {
	    	String url = baseUrl+alleleID;
	    	MockRequest mr = mockRequest(url);
	    	List<PhenoTableGenotype> ptGenotypes = (List<PhenoTableGenotype>) mr.get("phenoTableGenotypes");
	    	return ptGenotypes;
	    }
	    
	    /* utility functions */
	    public int getColumnIndex(String alleleID,String genotypeID,String sex,String provider) throws Exception
	    {
	    	List<PhenoTableGenotype> genotypes = getPhenoTableGenotypes(alleleID);
	    	int cellCount = 0;
	    	// simulate generating the column cells to find out which data column we want
	    	for(PhenoTableGenotype g : genotypes)
	    	{
	    		List<String> sexes = new ArrayList<String>();
	    		if (g.getSplitSex()==1)
	    		{
	    			sexes.add("M");
	    			sexes.add("F");
	    		}
	    		else
	    		{
	    			sexes.add(g.getSexDisplay());
	    		}
	    		for(String sexValue : sexes)
	    		{
		    		for(PhenoTableProvider p : g.getPhenoTableProviders())
		    		{
		    			cellCount++;
		    			if (g.getGenotype().getPrimaryID().equalsIgnoreCase(genotypeID)
		    					&& sexValue.equalsIgnoreCase(sex)
		    					&& p.getProvider().equalsIgnoreCase(provider))
		    			{
		    				return cellCount;
		    			}
		    		}
	    		}
	    	}

			return -1;
	    }
	    
	    public PhenoTableTerm getTerm(String alleleID,String system,String termID) throws Exception
	    {
	    	for(PhenoTableSystem ptSystem : getPhenoTableSystems(alleleID))
	    	{
	    		if(ptSystem.getSystem().equalsIgnoreCase(system))
	    		{
	    			for(PhenoTableTerm ptTerm : ptSystem.getPhenoTableTerms())
	    			{
	    				if(ptTerm.getTermId().equalsIgnoreCase(termID))
	    				{
	    					return ptTerm;
	    				}
	    			}
	    		}
	    	}
	    	return null;
	    }
	    public PhenoTableSystem getSystem(String alleleID,String system) throws Exception
	    {
	    	for(PhenoTableSystem ptSystem : getPhenoTableSystems(alleleID))
	    	{
	    		if(ptSystem.getSystem().equalsIgnoreCase(system))
	    		{
	    			return ptSystem;
	    		}
	    	}
	    	return null;
	    }
	    
		/*
		 * The actual test functions
		 */

	    /*
	     * A function for defining a single cell in the phenotype summary grid
	     * alleleID - gets the correct grid
	     * system,termID - gets the correct row
	     * genotypeID,sex,providerID - gets the correct column
	     */
	    public String getCell(String alleleID,String system, String termID,String genotypeID,String sex,String provider) throws Exception
	    {
	    	// find the column index first
	    	int cellIndex = getColumnIndex(alleleID,genotypeID,sex,provider);
	    	if (cellIndex < 1)
	    	{
	    		return "invalid column ("+genotypeID+","+sex+","+provider+")";
	    	}
	    	// find the correct term
	    	PhenoTableTerm ptTerm = getTerm(alleleID,system,termID);
	    	if (ptTerm==null)
	    	{
	    		return "invalid row ("+system+","+termID+")";
	    	}
	    	PhenoTableTermCell cell = ptTerm.getCells().get(cellIndex);
	    	return cell.getCall();
	    }
	    /*
	     * A function for defining a single cell (for a system) in the phenotype summary grid
	     * alleleID - gets the correct grid
	     * system - gets the correct row
	     * genotypeID,sex,providerID - gets the correct column
	     */
	    public String getSystemCell(String alleleID,String system,String genotypeID,String sex,String provider) throws Exception
	    {
	    	// find the column index first
	    	int cellIndex = getColumnIndex(alleleID,genotypeID,sex,provider);
	    	if (cellIndex < 1)
	    	{
	    		return "invalid column ("+genotypeID+","+sex+","+provider+")";
	    	}
	    	// find the correct system
	    	PhenoTableSystem ptSystem = getSystem(alleleID,system);
	    	if (ptSystem==null)
	    	{
	    		return "invalid row ("+system+")";
	    	}
	    	PhenoTableSystemCell cell = ptSystem.getCells().get(cellIndex);
	    	return cell.getCall();
	    }
	    
	    /*
	     * returns the list of genotype labels (in order that they appear in table headers)
	     * e.g. [hm1,hm2,ht3,...]
	     */
	    public List<String> getGenotypeLabels(String alleleID) throws Exception
	    {
	    	List<String> labels = new ArrayList<String>();
	    	List<PhenoTableGenotype> genotypes = getPhenoTableGenotypes(alleleID);
	    	int count=0;
	    	for(PhenoTableGenotype g : genotypes)
	    	{
	    		count++;
	    		labels.add(g.getGenotype().getGenotypeType()+count);
	    	}
	    	return labels;
	    }
	    public int getSexCountByLabel(String alleleID,String genotypeLabel) throws Exception
	    {
	    	List<PhenoTableGenotype> genotypes = getPhenoTableGenotypes(alleleID);
	    	int count=0;
	    	for(PhenoTableGenotype g : genotypes)
	    	{
	    		count++;
	    		String label = g.getGenotype().getGenotypeType()+count;
	    		if(label.equalsIgnoreCase(genotypeLabel))
	    		{
	    			return g.getSplitSex()==1 ? 2 : 1;
	    		}
	    	}
	    	return -1;
	    }
}
	