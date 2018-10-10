package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.summary.Accession;
import org.jax.mgi.snpdatamodel.ConsensusSNP;

import java.util.List;

//import org.jax.mgi.fewi.util.FewiUtil;
//import org.jax.mgi.fewi.hunter.HibernateAccessionSummaryHunter;
import org.jax.mgi.fewi.searchUtil.ObjectTypes;
//import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.Genotype;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.Strain;

/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding accessionable objects and bundling them into Accession objects.
 */
@Repository
public class AccessionFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(AccessionFinder.class);

    @Autowired
    private MarkerFinder markerFinder;
    
    @Autowired
    private AlleleFinder alleleFinder;
    
    @Autowired
    private ReferenceFinder referenceFinder;
    
    @Autowired
    private SnpFinder snpFinder;
    
    @Autowired
    private GenotypeFinder genotypeFinder;
    
    @Autowired
    private StrainFinder strainFinder;
    
//    @Autowired
//    private HibernateAccessionSummaryHunter<Accession> accessionSummaryHunter;

    /*---------------------------------------*/
    /* Retrieval of multiple accession objects
    /*---------------------------------------*/
    public SearchResults<Accession> getAccessions(String accID) {
        logger.debug("->getAccessions(" + accID + ")");

        // result object to be returned
        SearchResults<Accession> searchResults = new SearchResults<Accession>();
        searchResults.setTotalCount(0);

        // could probably optimize this by studying which IDs can return multiple data types; any without
        // multiple types could return early before doing any more searching...
        
        // could also monitor which types of objects are returned most frequently and prioritize them
        
        getMouseMarkers(accID, searchResults);
        getAlleles(accID, searchResults);
        getReferences(accID, searchResults);
        getSNPs(accID, searchResults);
        getGenotypes(accID, searchResults);
        getStrains(accID, searchResults);
       
        return searchResults;
    }
    
    // Find any mouse markers that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getMouseMarkers(String accID, SearchResults<Accession> searchResults) {
    	SearchResults<Marker> markerResults = markerFinder.getMarkerByID(accID);
    	if (markerResults.getTotalCount() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + markerResults.getTotalCount());
    		for (Marker marker : markerResults.getResultObjects()) {
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.MARKER, marker.getMarkerSubtype(), marker.getPrimaryID(), "MGI",
    					marker.getMarkerKey(), marker.getSymbol() + ", " + marker.getName() + ", Chr " + marker.getChromosome()) );
    		}
    	}
    	return searchResults;
    }

    // Find any alleles that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getAlleles(String accID, SearchResults<Accession> searchResults) {
    	List<Allele> alleleResults = alleleFinder.getAlleleByID(accID);
    	if (alleleResults.size() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + alleleResults.size());
    		for (Allele allele : alleleResults) {
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.ALLELE, allele.getAlleleType(), allele.getPrimaryID(), "MGI",
    					allele.getAlleleKey(), allele.getSymbol() + ", " + allele.getName()) );
    		}
    	}
    	return searchResults;
    }

    // Find any references that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getReferences(String accID, SearchResults<Accession> searchResults) {
    	SearchResults<Reference> refsResults = referenceFinder.getReferenceByID(accID);
    	if (refsResults.getTotalCount() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + refsResults.getTotalCount());
    		for (Reference ref : refsResults.getResultObjects()) {
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.REFERENCE, "Reference", ref.getJnumID(), "MGI",
    					ref.getReferenceKey(), ref.getMiniCitation()) );
    		}
    	}
    	return searchResults;
    }

    // Find any SNPs that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getSNPs(String accID, SearchResults<Accession> searchResults) {
    	SearchResults<ConsensusSNP> snpResults = snpFinder.getSnpByID(accID);
    	if (snpResults.getTotalCount() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + snpResults.getTotalCount());
    		for (ConsensusSNP snp : snpResults.getResultObjects()) {
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.SNP, "SNP", snp.getAccid(), "MGI",
    					snp.getConsensusKey(), snp.getAlleleSummary()) );
    		}
    	}
    	return searchResults;
    }

    // Find any genotypes that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getGenotypes(String accID, SearchResults<Accession> searchResults) {
    	List<Genotype> genotypeResults = genotypeFinder.getGenotypeByID(accID);
    	if (genotypeResults.size() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + genotypeResults.size());
    		for (Genotype genotype : genotypeResults) {
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.GENOTYPE, "Genotype", genotype.getPrimaryID(), "MGI",
    					genotype.getGenotypeKey(), "genotype") );
    		}
    	}
    	return searchResults;
    }

    // Find any mouse strains that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getStrains(String accID, SearchResults<Accession> searchResults) {
    	List<Strain> strainResults = strainFinder.getStrainByID(accID);
    	if (strainResults.size() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + strainResults.size());
    		for (Strain strain : strainResults) {
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.STRAIN, "Strain", strain.getPrimaryID(), "MGI",
    					strain.getStrainKey(), "strain") );
    		}
    	}
    	return searchResults;
    }
}
