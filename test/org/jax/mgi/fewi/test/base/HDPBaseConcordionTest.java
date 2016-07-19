package org.jax.mgi.fewi.test.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.hmdc.controller.DiseasePortalController;
import org.jax.mgi.fewi.hmdc.models.GridCluster;
import org.jax.mgi.fewi.hmdc.models.GridResult;
import org.jax.mgi.fewi.hmdc.models.GridRow;
import org.jax.mgi.fewi.hmdc.models.GridTermHeaderAnnotation;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpDisease;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpMarker;
import org.jax.mgi.fewi.matrix.GridDataCell;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.test.mock.MockHdpControllerQuery;
import org.jax.mgi.shr.jsonmodel.GridMarker;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * Base class for the Human Disease Portal (HDP) query functions
 * 	We define them all here, so that they can be accessed in all the HDP test suites
 */

public class HDPBaseConcordionTest extends BaseConcordionTest 
{
    // The class being tested is autowired via spring's DI
    @Autowired
    private DiseasePortalController hdpController;
    
    public List<String> getSymbolsByDiseaseTerm(String disease) throws Exception
    {
    	return getSymbolsByPhenotypeTerm(disease);
    }
    
    public List<String> getSymbolsByDisease(String diseaseID) throws Exception
    {
    	return getSymbolsByPhenotype(diseaseID);
    }
    
    public List<String> getSymbolsByPhenotypeTerm(String mpTerm) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermClause(mpTerm);
    	return getGeneSymbols(mq);
    }

    public List<String> getSymbolsByPhenotype(String mpID) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(mpID);
    	return getGeneSymbols(mq);
    }

    public Integer getGeneCountByDisease(String diseaseID) throws Exception
    {	
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(diseaseID);
    	List<SolrHdpMarker> markers = mq.getGenes();
    	return markers.size();
    }
    
    public List<String> getTermsByDisease(String disease) throws Exception
    {	
    	List<String> termNames = new ArrayList<String>();
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermClause(disease);
    	for(SolrHdpDisease term : mq.getDiseases())
    	{
    		termNames.add(term.getTerm());
    	}
    	
    	return termNames;
    }
    
    public List<String> getTermIdsByDisease(String disease) throws Exception
    {	
    	List<String> termIds = new ArrayList<String>();
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermClause(disease);
    	for(SolrHdpDisease term : mq.getDiseases())
    	{
    		termIds.add(term.getPrimaryId());
    	}
    	
    	return termIds;
    }

    public List<String> getTermsByDiseaseId(String diseaseId) throws Exception
    {	
    	List<String> termNames = new ArrayList<String>();
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(diseaseId);
    	for(SolrHdpDisease term : mq.getDiseases())
    	{
    		termNames.add(term.getTerm());
    	}
    	
    	return termNames;
    }
    
    public List<String> getTermIdsByDiseaseId(String diseaseId) throws Exception
    {	
    	List<String> termIds = new ArrayList<String>();
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(diseaseId);
    	for(SolrHdpDisease term : mq.getDiseases())
    	{
    		termIds.add(term.getPrimaryId());
    	}
    	
    	return termIds;
    }

    public Integer getDiseaseCountByDisease(String diseaseID) throws Exception
    {	
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(diseaseID);
    	List<SolrHdpDisease> terms = mq.getDiseases();
    	return terms.size();
    }

    public List<String> getTermsByPhenotype(String phenotype) throws Exception
    {	
    	return getTermsByDisease(phenotype);
    }

    public List<String> getTermIdsByPhenotype(String phenotype) throws Exception
    {	
    	return getTermIdsByDisease(phenotype);
    }

    public List<String> getTermsByPhenotypeId(String phenotypeId) throws Exception
    {	
    	return getTermsByDiseaseId(phenotypeId);
    }

    public List<String> getTermIdsByPhenotypeId(String phenotypeId) throws Exception
    {	
    	return getTermIdsByDiseaseId(phenotypeId);
    }
/*
    public Integer getGeneCountByPhenotype(String phenotype) throws Exception
    //klf added on 7/30/2013 modifying from getGeneCountByDisease above
    {	
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setPhenotypes(phenotype);
    	SearchResults<SolrHdpMarker> markers = mq.getGenes();
    	return markers.getTotalCount();
    }
*/  
    // returns <br/> delimited string
    public String getMarkerDiseaseByPhenotypeSymbol(String phenotype,String symbol) throws Exception
    {
    	List<String> diseases = new ArrayList<String>();
    	
    	SolrHdpMarker m = getMarkerByPhenotypeIdSymbol(phenotype,symbol);
    	if(m!=null)
    	{
    		if (m.getDisease() != null) {
    			for(String disease : m.getDisease())
				{
					diseases.add(disease);
				}
			}
    	}
    	return StringUtils.join(diseases,"\n");
    }

 // returns , delimited string
    public String getMarkerSystemByPhenotypeSymbol(String phenotype,String symbol) throws Exception
    {
    	List<String> systems = new ArrayList<String>();
    	
    	SolrHdpMarker m = getMarkerByPhenotypeSymbol(phenotype,symbol);
    	if(m!=null)
    	{
    		if (m.getMouseSystem() != null) {
    			for(String system : m.getMouseSystem())
    			{
    				systems.add(system);
    			}
    		}
    	}
    	return StringUtils.join(systems,", ");
    }

    public SolrHdpMarker getMarkerByPhenotypeSymbol(String phenotype,String symbol) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermClause(phenotype);
    	for(SolrHdpMarker m : mq.getGenes())
    	{
    		if(m.getSymbol().equals(symbol))
    		{
    			return m;
    		}
    	}
    	return null;
    }

    public SolrHdpMarker getMarkerByPhenotypeIdSymbol(String phenotypeId,String symbol) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(phenotypeId);
    	for(SolrHdpMarker m : mq.getGenes())
    	{
    		if(m.getSymbol().equals(symbol))
    		{
    			return m;
    		}
    	}
    	return null;
    }

    // ------------------ genes queries --------------------
    public List<String> getSymbolsByGene(String genes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setOperator(MockHdpControllerQuery.AND);
    	mq.addMarkerSymbolIdClause(genes);
    	return getGeneSymbols(mq);
    }

    public List<String> getSymbolsByGeneNameOrSynonym(String genes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setOperator(MockHdpControllerQuery.AND);
    	mq.addMarkerNameClause(genes);
    	return getGeneSymbols(mq);
    }

    // ------------------ location queries --------------------
    public List<String> getSymbolsByMouseLocation(String locations) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addLocation(locations, "mouse");
    	return getGeneSymbols(mq);
    }
    
    public List<String> getSymbolsByHumanLocation(String locations) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addLocation(locations, "human");
    	return getGeneSymbols(mq);
    }
    
    // ------------- Grid related functions -----------
    public Integer gridCountByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermClause(phenotypes);
    	return mq.getGrid().getGridRows().size();
    }
    public Integer gridDiseaseCountByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermClause(phenotypes);
    	return mq.getGrid().getGridOMIMHeaders().size();
    }
    public List<String> gridDiseaseIdsByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermClause(phenotypes);
    	List<String> diseases = mq.getGrid().getGridOMIMHeaders();
    	Collections.sort(diseases);
    	return diseases;
    }
    
    public List<String> gridAllSymbolsByGene(String genes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addMarkerSymbolIdClause(genes);
    	return getGridSymbols(mq,true,true);
    }
    public List<String> gridHumanSymbolsByGene(String genes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addMarkerSymbolIdClause(genes);
    	return getGridSymbols(mq,true,false);
    }
    public List<String> gridMouseSymbolsByGene(String genes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addMarkerSymbolIdClause(genes);
    	return getGridSymbols(mq,false,true);
    }
    public List<String> gridAllSymbolsByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermClause(phenotypes);
    	return getGridSymbols(mq,true,true);
    }
    public List<String> gridHumanSymbolsByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermClause(phenotypes);
    	return getGridSymbols(mq,true,false);
    }
    public List<String> gridMouseSymbolsByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermClause(phenotypes);
    	return getGridSymbols(mq,false,true);
    }
    public List<String> gridAllSymbolsByPhenotypeId(String phenotypes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(phenotypes);
    	return getGridSymbols(mq,true,true);
    }
    public List<String> gridHumanSymbolsByPhenotypeId(String phenotypes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(phenotypes);
    	return getGridSymbols(mq,true,false);
    }
    public List<String> gridMouseSymbolsByPhenotypeId(String phenotypes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(phenotypes);
    	return getGridSymbols(mq,false,true);
    }
    public List<String> gridDiseasesByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(phenotypes);
    	List<String> diseases = getGridDiseases(mq);
    	Collections.sort(diseases);
    	return diseases;
    }
    public List<String> gridDiseasesByGene(String genes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addMarkerSymbolIdClause(genes);
    	List<String> diseases = getGridDiseases(mq);
    	Collections.sort(diseases);
    	return diseases;
    }
    public List<String> gridMpHeadersByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(phenotypes);
    	List<String> headers = mq.getGrid().getGridMPHeaders();
    	Collections.sort(headers);
    	return headers;
    }

    public List<String> gridMpHeadersByGene(String genes) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addMarkerSymbolIdClause(genes);
    	List<String> headers = mq.getGrid().getGridMPHeaders();
    	Collections.sort(headers);
    	return headers;
    }

    public String gridLastMpHeaderByGene(String genes) throws Exception
    {
    	List<String> headers = gridMpHeadersByGene(genes);
    	if(headers.size() < 1) return "";
    	return headers.get(headers.size()-1);
    }

    public List<String> gridMpHeadersByLocation(String locations) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addLocation(locations, "mouse");
    	List<String> headers = mq.getGrid().getGridMPHeaders();
    	Collections.sort(headers);
    	return headers;
    }

    // returns "" or "check" if there is a hit for the query + geneSymbol + diseaseId combination
    public String gridCheckForDiseaseByPhenotype(String phenotype,String geneSymbol,String diseaseId) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(phenotype);
    	mq.setOperator(MockHdpControllerQuery.AND);
    	mq.addMarkerSymbolIdClause(geneSymbol);
    	mq.addTermIdClause(diseaseId);
    	return gridCheckForDisease(mq,geneSymbol,diseaseId);
    }
    // returns "" or "check" if there is a hit for the query + geneSymbol + diseaseId combination
    public String gridCheckForDiseaseByGene(String genes,String geneSymbol,String diseaseId) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addMarkerSymbolIdClause(genes);
    	mq.addTermIdClause(diseaseId);
    	List<String> headers = mq.getGrid().getGridOMIMHeaders();
    	if (headers.size() > 0) {
    		return "check";
    	}
    	return "";
    }
    // returns "" or "check" if there is a hit for the query + geneSymbol + mpHeader combination
    public String gridCheckForMpHeaderByPhenotypeId(String phenotypeId,String geneSymbol,String mpHeader) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(phenotypeId);
    	mq.addMarkerSymbolIdClause(geneSymbol);
    	return gridCheckForMpHeader(mq,geneSymbol,mpHeader);
    }
    // returns "" or "check" if there is a hit for the query + geneSymbol + mpHeader combination
    public String gridCheckForMpHeaderByPhenotype(String phenotype,String geneSymbol,String mpHeader) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermClause(phenotype);
    	mq.addMarkerSymbolIdClause(geneSymbol);
    	return gridCheckForMpHeader(mq,geneSymbol,mpHeader);
    }
    public String gridCheckForMpHeaderByGene(String genes,String geneSymbol,String mpHeader) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addMarkerSymbolIdClause(geneSymbol);
    	return gridCheckForMpHeader(mq,geneSymbol,mpHeader);
    }
    public List<String> gridMpHeadersWithChecksByPhenotype(String phenotypes, String geneSymbol) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.addTermIdClause(phenotypes);
    	return gridMpHeadersWithChecks(mq,geneSymbol);
    }
    		
/*
    // ------------- Grid POPUP related functions -----------
    public Integer systemGridPopupGenoCountByGene(String genes,String markerRow,String systemCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	
    	// get the grid cluster key
    	List<HdpGenoCluster> genoClusters = mq.getSystemPopupGenoClusters(markerRow,systemCol);
    	return genoClusters.size();
    }
    public List<String> systemGridPopupGenoClustersByGene(String genes,String markerRow,String systemCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	
    	// get the grid cluster key
    	List<HdpGenoCluster> genoClusters = mq.getSystemPopupGenoClusters(markerRow,systemCol);
    	List<String> genoDisplays = new ArrayList<String>();
    	NotesTagConverter ntc = new NotesTagConverter();
    	
    	for(HdpGenoCluster genoCluster : genoClusters)
    	{
    		String genoDisplay = ntc.convertNotes(genoCluster.getGenotype().getCombination1(),'|',true,true);
    		genoDisplays.add(genoDisplay.trim());
    	}
    	
    	return genoDisplays;
    }
    public List<String> systemGridPopupTermsByGene(String genes,String markerRow,String systemCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	
    	return mq.getSystemPopupTerms(markerRow,systemCol);
    }
    // return whether or not a cell in the MP popup has a check
    public String systemGridPopupCheckByGene(String genes,String markerRow,String systemCol,String genotypeRow,String mpCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	
    	return this.popupMpGridCheckFor(mq,markerRow,systemCol,genotypeRow,mpCol);
    }
    public Integer systemGridPopupGenoCountByPheno(String phenotype,String markerRow,String systemCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotype);
    	
    	// get the grid cluster key
    	List<HdpGenoCluster> genoClusters = mq.getSystemPopupGenoClusters(markerRow,systemCol);
    	return genoClusters.size();
    }
    public List<String> systemGridPopupGenoClustersByPheno(String phenotype,String markerRow,String systemCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotype);
    	
    	// get the grid cluster key
    	List<HdpGenoCluster> genoClusters = mq.getSystemPopupGenoClusters(markerRow,systemCol);
    	List<String> genoDisplays = new ArrayList<String>();
    	NotesTagConverter ntc = new NotesTagConverter();
    	
    	for(HdpGenoCluster genoCluster : genoClusters)
    	{
    		String genoDisplay = ntc.convertNotes(genoCluster.getGenotype().getCombination1(),'|',true,true);
    		genoDisplays.add(genoDisplay.trim());
    	}
    	
    	return genoDisplays;
    }
    public List<String> systemGridPopupTermsByPheno(String phenotype,String markerRow,String systemCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotype);
    	
    	return mq.getSystemPopupTerms(markerRow,systemCol);
    }
    // return whether or not a cell in the MP popup has a check
    public String systemGridPopupCheckByPheno(String phenotype,String markerRow,String systemCol,String genotypeRow,String mpCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotype);
    	
    	return this.popupMpGridCheckFor(mq,markerRow,systemCol,genotypeRow,mpCol);
    }
    
    // return whether or not a cell in the Disease popup has a check
    public String diseaseGridPopupCheckByGene(String genes,String markerRow,String diseaseHeader,String genotypeRow,String diseaseCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	
    	return this.popupDiseaseGridCheckFor(mq,markerRow,diseaseHeader,genotypeRow,diseaseCol);
    }
    // return whether or not a cell in the Disease popup has a check
    public String diseaseGridPopupCheckByPhenotype(String phenotype,String markerRow,String diseaseHeader,String genotypeRow,String diseaseCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotype);
    	
    	return this.popupDiseaseGridCheckFor(mq,markerRow,diseaseHeader,genotypeRow,diseaseCol);
    }
    // return whether or not a cell in the Disease popup has a check
    public String diseaseGridPopupHumanCheckByGene(String genes,String markerRow,String diseaseHeader,String humanPopupRow,String diseaseCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	
    	return this.popupDiseaseHumanGridCheckFor(mq,markerRow,diseaseHeader,humanPopupRow,diseaseCol);
    }
    // return whether or not a cell in the Disease popup has a check
    public String diseaseGridPopupHumanCheckByPhenotype(String phenotype,String markerRow,String diseaseHeader,String humanPopupRow,String diseaseCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotype);
    	
    	return this.popupDiseaseHumanGridCheckFor(mq,markerRow,diseaseHeader,humanPopupRow,diseaseCol);
    }
    public Integer diseaseGridPopupGenoCountByPheno(String phenotype,String markerRow,String diseaseCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotype);
    	
    	// get the grid cluster key
    	List<HdpGenoCluster> genoClusters = mq.getDiseasePopupGenoClusters(markerRow,diseaseCol);
    	return genoClusters.size();
    }
    public Integer diseaseGridPopupGenoCountByGene(String genes,String markerRow,String diseaseCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	
    	// get the grid cluster key
    	List<HdpGenoCluster> genoClusters = mq.getDiseasePopupGenoClusters(markerRow,diseaseCol);
    	return genoClusters.size();
    }
    public List<String> diseaseGridPopupGenoClustersByPheno(String phenotype,String markerRow,String diseaseCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotype);
    	
    	// get the grid cluster key
    	List<HdpGenoCluster> genoClusters = mq.getDiseasePopupGenoClusters(markerRow,diseaseCol);
    	List<String> genoDisplays = new ArrayList<String>();
    	NotesTagConverter ntc = new NotesTagConverter();
    	
    	for(HdpGenoCluster genoCluster : genoClusters)
    	{
    		String genoDisplay = ntc.convertNotes(genoCluster.getGenotype().getCombination1(),'|',true,true);
    		genoDisplays.add(genoDisplay);
    	}
    	
    	return genoDisplays;
    }
    public List<String> diseaseGridPopupHumanMarkersByPheno(String phenotype,String markerRow,String diseaseCol) throws Exception
    {
    	// set the form
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotype);
    	
    	// get the grid cluster key
    	List<SolrHdpMarker> humanMarkers = mq.getDiseasePopupHumanMarkers(markerRow,diseaseCol);
    	List<String> markerSymbols = new ArrayList<String>();
    	
    	for(SolrHdpMarker marker : humanMarkers)
    	{
    		markerSymbols.add(marker.getSymbol());
    	}
    	
    	return markerSymbols;
    }
    
    // ###############################################
    // --------------private helper methods ----------
    // ###############################################
*/ 
    private List<String> getGeneSymbols(MockHdpControllerQuery mq) throws Exception
    {
		List<String> symbols = new ArrayList<String>();
    	for(SolrHdpMarker m : mq.getGenes())
    	{
    		symbols.add(m.getSymbol());
    	}
    	
    	return symbols;
    }

    private List<String> getGridSymbols(MockHdpControllerQuery mq, boolean includeHuman, boolean includeMouse) throws Exception
    {
    	List<String> symbols = new ArrayList<String>();
    	for (GridRow gr : mq.getGrid().getGridRows()) {
    		GridCluster gc = gr.getGridCluster();
    		if (includeHuman && (gc.getHumanSymbols() != null)) {
    			for (GridMarker hm : gc.getHumanSymbols()) {
    				symbols.add(hm.getSymbol());
    			}
    		} 
    		if (includeMouse && (gc.getMouseSymbols() != null)) {
    			for (GridMarker mm : gc.getMouseSymbols()) {
    				symbols.add(mm.getSymbol());
    			}
    		}
    	}
    	return symbols;
    }

    private List<String> getGridDiseases(MockHdpControllerQuery mq) throws Exception
    {
    	return mq.getGrid().getGridOMIMHeaders();
    }

    public List<String> getGridPhenotypes(MockHdpControllerQuery mq) throws Exception
    {
    	return mq.getGrid().getGridMPHeaders();
    }

    // returns "" or "check" if there is a hit for the query + geneSymbol + diseaseCluster combination
    private String gridCheckForDisease(MockHdpControllerQuery mq,String geneSymbol,String diseaseCluster) throws Exception
    {
    	GridResult gr = mq.getGrid();
    	List<String> diseaseHeaders = gr.getGridOMIMHeaders();

    	// if no disease column, no hit
    	if (diseaseHeaders == null) {
    		return "";
    	}

    	// if marker doesn't appear, no hit
    	for (GridRow row : gr.getGridRows()) {
    		Map<String,GridTermHeaderAnnotation> dc = row.getDiseaseCells();
    		for (String key : dc.keySet()) {
    			GridTermHeaderAnnotation gtha = dc.get(key);
    			if (gtha.getAnnotCount() == gtha.getNormalCount()) {
    				return "N";
    			} else {
    				return "check";
    			}
    		}
    	}
    	return "";
    }

    // returns "" or "check" if there is a hit for the query + geneSymbol + mpHeader combination
    private String gridCheckForMpHeader(MockHdpControllerQuery mq,String geneSymbol,String mpHeader) throws Exception
    {
    	for (GridRow gr: mq.getGrid().getGridRows()) {
    		boolean foundMarker = false;
    		
    		GridCluster gc = gr.getGridCluster();
    		if (gc.getMouseSymbols() != null) {
    			for (GridMarker mm : gc.getMouseSymbols()) {
    				foundMarker = foundMarker || geneSymbol.equals(mm.getSymbol());
    			}
    		}
    		if (!foundMarker && (gc.getHumanSymbols() != null)) {
    			for (GridMarker hm : gc.getHumanSymbols()) {
    				foundMarker = foundMarker || geneSymbol.equals(hm.getSymbol());
    			}
    		}
    		
    		if (foundMarker) {
    			HashMap<String, GridTermHeaderAnnotation> cells = gr.getMpHeaderCells();
    			if (cells.containsKey(mpHeader)) {
    				GridTermHeaderAnnotation gtha = cells.get(mpHeader);
    				if (gtha.getNormalCount() == gtha.getAnnotCount()) {
    					return "N";
    				} else {
    					return "check";
    				}
    			}
    		}
    	}
    	return "";
    }

    // returns all headers that have checks for the gene row
    private List<String> gridMpHeadersWithChecks(MockHdpControllerQuery mq, String geneSymbol) throws Exception
    {
    	List<String> mpHeadersWithChecks = new ArrayList<String>();
    	
    	for (GridRow gr: mq.getGrid().getGridRows()) {
    		boolean foundMarker = false;
    		
    		GridCluster gc = gr.getGridCluster();
    		if (gc.getMouseSymbols() != null) {
    			for (GridMarker mm : gc.getMouseSymbols()) {
    				foundMarker = foundMarker || geneSymbol.equals(mm.getSymbol());
    			}
    		}
    		if (!foundMarker && (gc.getHumanSymbols() != null)) {
    			for (GridMarker hm : gc.getHumanSymbols()) {
    				foundMarker = foundMarker || geneSymbol.equals(hm.getSymbol());
    			}
    		}
    		
    		if (foundMarker) {
    			HashMap<String, GridTermHeaderAnnotation> cells = gr.getMpHeaderCells();
    			for (String mpHeader : cells.keySet()) {
    				GridTermHeaderAnnotation gtha = cells.get(mpHeader);
    				if (gtha.getNormalCount() != gtha.getAnnotCount()) {
    					mpHeadersWithChecks.add(mpHeader);
    				}
    			}
    		}
    	}
    	Collections.sort(mpHeadersWithChecks);
    	return mpHeadersWithChecks;
    }
    
 /*       
    private String popupMpGridCheckFor(MockHdpHttpQuery mq,String geneSymbol,String header,String genotype,String termId) throws Exception
    {
    	List<HdpGenoByHeaderPopupRow> popupRows = mq.getSystemPopupRows(geneSymbol,header);
    	return popupGridCheckFor(popupRows, genotype, termId);
    }
    private String popupDiseaseGridCheckFor(MockHdpHttpQuery mq,String geneSymbol,String header,String genotype,String termId) throws Exception
    {
    	List<HdpGenoByHeaderPopupRow> popupRows = mq.getDiseasePopupRows(geneSymbol,header);
    	String check = popupGridCheckFor(popupRows, genotype, termId);
    	if("yes".equalsIgnoreCase(check)) check = "check";
    	return check;
    }
    private String popupDiseaseHumanGridCheckFor(MockHdpHttpQuery mq,String geneSymbol,String header,String genotype,String termId) throws Exception
    {
    	List<HdpMarkerByHeaderPopupRow> popupRows = mq.getDiseaseMarkerPopupRows(geneSymbol,header);
    	String check = popupMarkerGridCheckFor(popupRows, genotype, termId);
    	if("yes".equalsIgnoreCase(check)) check = "check";
    	return check;
    }
    private String popupGridCheckFor(List<HdpGenoByHeaderPopupRow> popupRows,String genotype,String termId) throws Exception
    {
    	NotesTagConverter ntc = new NotesTagConverter();
    	boolean foundGeno = false;
    	for(HdpGenoByHeaderPopupRow popupRow : popupRows)
    	{
    		String genotypeDisplay = ntc.convertNotes(popupRow.getGenotype().getCombination1(),'|',true,true).trim();
    		if(genotypeDisplay.equals(genotype))
    		{
    			foundGeno = true;
	    		for(GridCell cell : popupRow.getGridCells())
	    		{
	    			if(cell.getTermId().equals(termId))
	    			{
	    				return cell.getHasPopup() ? "yes" : "no";
	    			}
	    		}
    		}
    	}
    	if(!foundGeno) return "row does not exist";
    	// otherwise return no, because we can't easily determine if the column exists or not
    	return "no";
    }
    private String popupMarkerGridCheckFor(List<HdpMarkerByHeaderPopupRow> popupRows,String marker,String termId) throws Exception
    {
    	boolean foundMarker = false;
    	for(HdpMarkerByHeaderPopupRow popupRow : popupRows)
    	{
    		String symbol = popupRow.getMarker().getSymbol();
    		if(symbol.equals(marker))
    		{
    			foundMarker = true;
	    		for(GridCell cell : popupRow.getGridCells())
	    		{
	    			if(cell.getTermId().equals(termId))
	    			{
	    				return cell.getHasPopup() ? "yes" : "no";
	    			}
	    		}
    		}
    	}
    	if(!foundMarker) return "row does not exist";
    	// otherwise return no, because we can't easily determine if the column exists or not
    	return "no";
    }
    */
}
	
