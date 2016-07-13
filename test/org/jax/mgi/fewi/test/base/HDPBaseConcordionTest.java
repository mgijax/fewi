package org.jax.mgi.fewi.test.base;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.hmdc.controller.DiseasePortalController;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpMarker;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.test.mock.MockHdpControllerQuery;
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
    
    /*
    public List<String> getSymbolsByDisease(String diseaseID) throws Exception
    {
    	return getSymbolsByPhenotype(diseaseID);
    }
    

    public List<String> getSymbolsByPhenotype(String mpID) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setPhenotypes(mpID);
    	return getGeneSymbols(mq);
    }


    public Integer getGeneCountByDisease(String diseaseID) throws Exception
    {	
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setPhenotypes(diseaseID);
    	SearchResults<SolrHdpMarker> markers = mq.getGenes();
    	return markers.getTotalCount();
    }
    
    public List<String> getTermsByDisease(String disease) throws Exception
    {	
    	List<String> termNames = new ArrayList<String>();
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setPhenotypes(disease);
    	SearchResults<SolrHdpDisease> sr = mq.getDiseases();
    	for(SolrHdpDisease term : sr.getResultObjects())
    	{
    		termNames.add(term.getTerm());
    	}
    	
    	return termNames;
    }
    public List<String> getTermIdsByDisease(String disease) throws Exception
    {	
    	List<String> termIds = new ArrayList<String>();
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setPhenotypes(disease);
    	SearchResults<SolrHdpDisease> sr = mq.getDiseases();
    	for(SolrHdpDisease term : sr.getResultObjects())
    	{
    		termIds.add(term.getPrimaryId());
    	}
    	
    	return termIds;
    }
    
    public Integer getDiseaseCountByDisease(String diseaseID) throws Exception
    {	
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setPhenotypes(diseaseID);
    	SearchResults<SolrHdpDisease> terms = mq.getDiseases();
    	return terms.getTotalCount();
    }

    public List<String> getTermsByPhenotype(String phenotype) throws Exception
    {	
    	List<String> termNames = new ArrayList<String>();
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setPhenotypes(phenotype);
    	SearchResults<SolrHdpDisease> sr = mq.getPhenotypes();
    	for(SolrHdpDisease term : sr.getResultObjects())
    	{
    		termNames.add(term.getTerm());
    	}
    	
    	return termNames;
    }
    public List<String> getTermIdsByPhenotype(String phenotype) throws Exception
    {	
    	List<String> termIds = new ArrayList<String>();
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setPhenotypes(phenotype);
    	SearchResults<SolrHdpDisease> sr = mq.getPhenotypes();
    	for(SolrHdpDisease term : sr.getResultObjects())
    	{
    		termIds.add(term.getPrimaryId());
    	}
    	
    	return termIds;
    }

    public Integer getGeneCountByPhenotype(String phenotype) throws Exception
    //klf added on 7/30/2013 modifying from getGeneCountByDisease above
    {	
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setPhenotypes(phenotype);
    	SearchResults<SolrHdpMarker> markers = mq.getGenes();
    	return markers.getTotalCount();
    }
    
    // returns <br/> delimited string
    public String getMarkerDiseaseByPhenotypeSymbol(String phenotype,String symbol) throws Exception
    {
    	List<String> diseases = new ArrayList<String>();
    	
    	SolrHdpMarker m = getMarkerByPhenotypeSymbol(phenotype,symbol);
    	if(m!=null)
    	{
			for(String disease : m.getDisease())
			{
				diseases.add(disease);
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
			for(String system : m.getSystem())
			{
				systems.add(system);
			}
    	}
    	return StringUtils.join(systems,", ");
    }
    
    public SolrHdpMarker getMarkerByPhenotypeSymbol(String phenotype,String symbol) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setPhenotypes(phenotype);
    	SearchResults<SolrHdpMarker> markers = mq.getGenes();
    	for(SolrHdpMarker m : markers.getResultObjects())
    	{
    		if(m.getSymbol().equals(symbol))
    		{
    			return m;
    		}
    	}
    	return null;
    }
*/ 
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
/*    
    // ------------------ location queries --------------------
    public List<String> getSymbolsByMouseLocation(String locations) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setMouseLocations(locations);
    	return getGeneSymbols(mq);
    }
    
    public List<String> getSymbolsByHumanLocation(String locations) throws Exception
    {
    	MockHdpControllerQuery mq = getMockQuery().diseasePortalController(hdpController);
    	mq.setHumanLocations(locations);
    	return getGeneSymbols(mq);
    }
    
    
    // ------------- Grid related functions -----------
    public Integer gridCountByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotypes);
    	
    	return mq.getGridClusters().size();
    }
    public Integer gridDiseaseCountByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotypes);
    	
    	return mq.getDiseaseColumnIds().size();
    }
    
    public List<String> gridAllSymbolsByGene(String genes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	
    	return getGridSymbols(mq,true,true);
    }
    public List<String> gridHumanSymbolsByGene(String genes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	
    	return getGridSymbols(mq,true,false);
    }
    public List<String> gridMouseSymbolsByGene(String genes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	
    	return getGridSymbols(mq,false,true);
    }
    public List<String> gridAllSymbolsByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotypes);
    	
    	return getGridSymbols(mq,true,true);
    }
    public List<String> gridHumanSymbolsByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotypes);
    	
    	return getGridSymbols(mq,true,false);
    }
    public List<String> gridMouseSymbolsByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotypes);
    	
    	return getGridSymbols(mq,false,true);
    }

    public List<String> gridDiseaseIdsByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotypes);
    	return mq.getDiseaseColumnIds();
    }
    public List<String> gridDiseaseIdsByGene(String genes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	return mq.getDiseaseColumnIds();
    }
    
    public List<String> gridMpHeadersByPhenotype(String phenotypes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotypes);
    	return mq.getMpHeaderColumns();
    }
    public List<String> gridMpHeadersByGene(String genes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	return mq.getMpHeaderColumns();
    }
    public String gridLastMpHeaderByGene(String genes) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	
    	List<String> headerCols = mq.getMpHeaderColumns();
    	if(headerCols.size() < 1) return "";
    	return headerCols.get(headerCols.size()-1);
    }
    public List<String> gridMpHeadersByLocation(String locations) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setMouseLocations(locations);
    	
    	return mq.getMpHeaderColumns();
    }
    
    // returns "" or "check" if there is a hit for the query + geneSymbol + diseaseId combination
    public String gridCheckForDiseaseByPhenotype(String phenotype,String geneSymbol,String diseaseId) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(geneSymbol);
    	mq.setPhenotypes(phenotype);
    	return gridCheckForDisease(mq,geneSymbol,diseaseId);
    }
    // returns "" or "check" if there is a hit for the query + geneSymbol + diseaseId combination
    public String gridCheckForDiseaseByGene(String genes,String geneSymbol,String diseaseId) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	return gridCheckForDisease(mq,geneSymbol,diseaseId);
    }
    
    // returns "" or "check" if there is a hit for the query + geneSymbol + mpHeader combination
    public String gridCheckForMpHeaderByPhenotype(String phenotype,String geneSymbol,String mpHeader) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotype);
    	mq.setGenes(geneSymbol);
    	return gridCheckForMpHeader(mq,geneSymbol,mpHeader);
    }
    public String gridCheckForMpHeaderByGene(String genes,String geneSymbol,String mpHeader) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setGenes(genes);
    	return gridCheckForMpHeader(mq,geneSymbol,mpHeader);
    }
    
    public List<String> gridMpHeadersWithChecksByPhenotype(String phenotypes, String geneSymbol) throws Exception
    {
    	MockHdpHttpQuery mq = getMockQuery().diseasePortalHttp();
    	mq.setPhenotypes(phenotypes);
    	return gridMpHeadersWithChecks(mq,geneSymbol);
    }
    		
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
/*    
    private List<String> getGridSymbols(MockHdpHttpQuery mq, boolean includeHuman,boolean includeMouse) throws Exception
    {
    	List<String> symbols = new ArrayList<String>();
    	for(HdpGridClusterSummaryRow cluster : mq.getGridClusters())
    	{
    		if(includeHuman)
    		{
    			symbols.addAll(cluster.getHumanSymbols());
    		}
    		if(includeMouse)
    		{
    			for(SolrHdpGene m : cluster.getMouseMarkers())
    			{
    				symbols.add(m.getSymbol());
    			}
    		}
    	}
    	return symbols;
    }
    
    // returns "" or "check" if there is a hit for the query + geneSymbol + diseaseCluster combination
    private String gridCheckForDisease(MockHdpHttpQuery mq,String geneSymbol,String diseaseCluster) throws Exception
    {
    	for(HdpGridClusterSummaryRow cluster : mq.getGridClusters())
    	{
    		List<String> markers = new ArrayList<String>();
    		for(SolrHdpGene m : cluster.getMouseMarkers())
    		{
    			markers.add(m.getSymbol());
    		}
    		markers.addAll(cluster.getHumanSymbols());
    		for(String marker : markers)
    		{
    			if(marker.equals(geneSymbol))
    			{
    				for(GridCell cell : cluster.getDiseaseCells())
    				{
    					if(diseaseCluster.equals(cell.getTerm()))
    					{
    						if(cell.getHasPopup())
    						{
    							return cell.getIsNormal() ? "N" : "check";
    						}
    						return ""; 
    					}
    				}
		    		return "";
    			}
    		}
    	}
    	return "";
    }
    // returns "" or "check" if there is a hit for the query + geneSymbol + mpHeader combination
    private String gridCheckForMpHeader(MockHdpHttpQuery mq,String geneSymbol,String mpHeader) throws Exception
    {
    	for(HdpGridClusterSummaryRow cluster : mq.getGridClusters())
    	{
    		List<String> markers = new ArrayList<String>();
    		for(SolrHdpGene m : cluster.getMouseMarkers())
    		{
    			markers.add(m.getSymbol());
    		}
    		markers.addAll(cluster.getHumanSymbols());
    		for(String marker : markers)
    		{
    			if(marker.equals(geneSymbol))
    			{
    				for(GridCell cell : cluster.getMpHeaderCells())
    				{
    					if(mpHeader.equalsIgnoreCase(cell.getTerm()))
    					{
    						if(cell.getHasPopup())
    						{
    							return cell.getIsNormal() ? "N" : "check";
    						}
    						return ""; 
    					}
    				}
		    		return "";
    			}
    		}
    	}
    	return "";
    }
    
    // returns all headers that have checks for the gene row
    private List<String> gridMpHeadersWithChecks(MockHdpHttpQuery mq,String geneSymbol) throws Exception
    {
    	List<String> mpHeadersWithChecks = new ArrayList<String>();
    	for(HdpGridClusterSummaryRow cluster : mq.getGridClusters())
    	{
    		List<String> markers = new ArrayList<String>();
    		for(SolrHdpGene m : cluster.getMouseMarkers())
    		{
    			markers.add(m.getSymbol());
    		}
    		markers.addAll(cluster.getHumanSymbols());
    		for(String marker : markers)
    		{
    			if(marker.equals(geneSymbol))
    			{
    				for(GridCell cell : cluster.getMpHeaderCells())
    				{
    					if(cell.getHasPopup() && !cell.getIsNormal()) mpHeadersWithChecks.add(cell.getTerm());
    				}
    				return mpHeadersWithChecks;
    			}
    		}
    	}
    	return mpHeadersWithChecks;
    }
    
    
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
	
