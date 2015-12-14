package gxd;

import java.util.HashSet;
import java.util.Set;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.jax.mgi.fewi.test.mock.MockGxdHttpQuery;
import org.jax.mgi.fewi.test.mock.MockJSONGXDAssayResult;
import org.springframework.beans.factory.annotation.Autowired;


public class MutantWildtypeSearchTest extends BaseConcordionTest 
{

    // The class being tested is autowired via spring's DI
    @Autowired
    private GXDController gxdController;
	    
    /*
     * Specimen mutated in ... functions
     */
    
    public int getCountByMutatedGene(String mutatedGene) throws Exception
	{
		MockGxdHttpQuery mq = getMockQuery().gxdHttp();
		mq.setMutatedIn(mutatedGene);
		
		return mq.getAssayResults().getTotalCount();
	}
    public int getCountByMutatedGeneAndAssayId(String mutatedGene,String assayId) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setMutatedIn(mutatedGene);
		
		int count = 0;
		SearchResults<SolrAssayResult> results = mq.getAssayResults();
		for(SolrAssayResult result : results.getResultObjects())
		{
			if(result.getAssayMgiid().equalsIgnoreCase(assayId)) count++;
		}
		
		return count;
	}

    public int getCountByNomenAndMutatedGeneAndAssayId(String nomen,String mutatedGene,String assayId) throws Exception
	{
		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
		mq.setMutatedIn(mutatedGene);
		mq.setNomenclature(nomen);
		
		int count = 0;
		SearchResults<SolrAssayResult> results = mq.getAssayResults();
		for(SolrAssayResult result : results.getResultObjects())
		{
			if(result.getAssayMgiid().equalsIgnoreCase(assayId)) count++;
		}
		
		return count;
	}
    
    public Set<String> getGenotypes(String mutatedGene) throws Exception
    {
    	Set<String> genotypeStrings = new HashSet<String>();
    	MockGxdHttpQuery mq = getMockQuery().gxdHttp();
		mq.setMutatedIn(mutatedGene);
		JsonSummaryResponse<MockJSONGXDAssayResult> results = mq.getAssayResults();
		for(MockJSONGXDAssayResult result : results.getSummaryRows())
		{
			genotypeStrings.add(result.getGenotype());
		}
		return genotypeStrings;
    }
    
    // returns the unique set of genes for all the genotypes found, including "" genotypes
    public Set<String> getMutatedGenes(String mutatedGene) throws Exception
    {
    	Set<String> mutatedGenes = new HashSet<String>();
    	MockGxdHttpQuery mq = getMockQuery().gxdHttp();
		mq.setMutatedIn(mutatedGene);
		JsonSummaryResponse<MockJSONGXDAssayResult> results = mq.getAssayResults();
		for(MockJSONGXDAssayResult result : results.getSummaryRows())
		{
			String genotype = result.getGenotype();
			if(genotype == null || genotype.equals(""))
			{
				mutatedGenes.add("");
				break;
			}
			genotype = genotype.replace("SUP", "sup");
			String[] allelePairs = genotype.split("<br>");
			for(String allelePair : allelePairs)
			{
				String[] alleles = allelePair.split("</sup>/");
				for(String allele : alleles)
				{
					String geneSymbol = allele.substring(0, allele.indexOf("<sup>"));
					mutatedGenes.add(geneSymbol);
				}
			}
		}
		System.out.println(mutatedGenes);	
		return mutatedGenes;
    }
    
    /*
     * Wild type query functions
     */
    
    public int getWildTypeCountByNomenAndAssayId(String nomen,String assayId) throws Exception
   	{
   		MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
   		mq.setNomenclature(nomen);
   		mq.setIsWildType(true);
   		
   		int count = 0;
   		SearchResults<SolrAssayResult> results = mq.getAssayResults();
   		for(SolrAssayResult result : results.getResultObjects())
   		{
   			if(result.getAssayMgiid().equalsIgnoreCase(assayId)) count++;
   		}
   		
   		return count;
   	}
    
    public Set<String> getWildTypeAssayIdsByNomen(String nomen) throws Exception
    {
    	MockGxdControllerQuery mq = getMockQuery().gxdController(gxdController);
   		mq.setNomenclature(nomen);
   		mq.setIsWildType(true);
   		Set<String> assayIds = new HashSet<String>();
   		
   		SearchResults<SolrAssayResult> results = mq.getAssayResults();
   		for(SolrAssayResult result : results.getResultObjects())
   		{
   			assayIds.add(result.getAssayMgiid());
   		}
   		
   		return assayIds;
    }
}
	

