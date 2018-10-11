package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.summary.Accession;
import org.jax.mgi.snpdatamodel.ConsensusSNP;

import java.util.List;

import org.jax.mgi.fewi.controller.ImageController;
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
import mgi.frontend.datamodel.Antibody;
import mgi.frontend.datamodel.ExpressionAssay;
import mgi.frontend.datamodel.Genotype;
import mgi.frontend.datamodel.HomologyCluster;
import mgi.frontend.datamodel.Image;
import mgi.frontend.datamodel.MappingExperiment;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.OrganismOrtholog;
import mgi.frontend.datamodel.Probe;
import mgi.frontend.datamodel.ProbeSequence;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.Strain;
import mgi.frontend.datamodel.Term;

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
    
    @Autowired
    private AntibodyFinder antibodyFinder;
    
    @Autowired
    private ProbeFinder probeFinder;
    
    @Autowired
    private MappingFinder mappingFinder;
    
    @Autowired
    private AssayFinder assayFinder;
    
    @Autowired
    private ImageController imageController;
    
    @Autowired
    private HomologyFinder homologyFinder;
    
    @Autowired
    private TermFinder termFinder;
    
    @Autowired
    private SequenceFinder sequenceFinder;
    
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
        getAntibodies(accID, searchResults);
        getProbes(accID, searchResults);
        getMapping(accID, searchResults);
        getAssays(accID, searchResults);
        getImages(accID, searchResults);
        getHomology(accID, searchResults);
        getTerms(accID, searchResults);
        getSequences(accID, searchResults);
       
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

    // Find any antibodies that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getAntibodies(String accID, SearchResults<Accession> searchResults) {
    	List<Antibody> antibodyResults = antibodyFinder.getAntibodyByID(accID);
    	if (antibodyResults.size() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + antibodyResults.size());
    		for (Antibody antibody : antibodyResults) {
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.ANTIBODY, "Antibody", antibody.getPrimaryID(), "MGI",
    					antibody.getAntibodyKey(), "antibody") );
    		}
    	}
    	return searchResults;
    }

    // Find any probes that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getProbes(String accID, SearchResults<Accession> searchResults) {
    	List<Probe> probeResults = probeFinder.getProbeByID(accID);
    	if (probeResults.size() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + probeResults.size());
    		for (Probe probe : probeResults) {
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.PROBECLONE, "Probe/Clone", probe.getPrimaryID(), "MGI",
    					probe.getProbeKey(), probe.getName()) );
    			List<ProbeSequence> seqs = probe.getSequences();
    			if (seqs != null) {
    				for (ProbeSequence seq : probe.getSequences()) {
    					searchResults.addResultObjects(
    						new Accession(ObjectTypes.SEQUENCE, "Sequence", seq.getPrimaryID(), "MGI",
    							seq.getUniqueKey(), seq.getSequence().getDescription()) );
    				}
    			}
    		}
    	}
    	return searchResults;
    }

    // Find any mapping experiments that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getMapping(String accID, SearchResults<Accession> searchResults) {
    	List<MappingExperiment> mappingResults = mappingFinder.getExperimentByID(accID);
    	if (mappingResults.size() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + mappingResults.size());
    		for (MappingExperiment expt : mappingResults) {
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.MAPPING, "Mapping Experiment", expt.getPrimaryID(), "MGI",
    					expt.getExperimentKey(), "mapping experiment") );
    		}
    	}
    	return searchResults;
    }

    // Find any expression assays that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getAssays(String accID, SearchResults<Accession> searchResults) {
    	List<ExpressionAssay> assayResults = assayFinder.getAssayByID(accID);
    	if (assayResults.size() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + assayResults.size());
    		for (ExpressionAssay assay : assayResults) {
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.ASSAY, "Assay", assay.getPrimaryID(), "MGI",
    					assay.getAssayKey(), "expression assay") );
    		}
    	}
    	return searchResults;
    }

    // Find any images that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getImages(String accID, SearchResults<Accession> searchResults) {
    	List<Image> imageResults = imageController.getImageForID(accID);
    	if (imageResults.size() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + imageResults.size());
    		for (Image image : imageResults) {
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.IMAGE, "Image", image.getMgiID(), "MGI",
    					image.getImageKey(), image.getFigureLabel() + ", " + image.getCaption()) );
    		}
    	}
    	return searchResults;
    }

    // Find any homology clusters that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getHomology(String accID, SearchResults<Accession> searchResults) {
    	List<HomologyCluster> homologyResults = homologyFinder.getHomologyClusterByID(accID);
    	if (homologyResults.size() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + homologyResults.size());
    		for (HomologyCluster hg : homologyResults) {
    			StringBuffer organisms = new StringBuffer();
    			for (OrganismOrtholog oo : hg.getOrthologs()) {
    				if (organisms.length() > 0) {
    					organisms.append(", ");
    				}
    				organisms.append(oo.getOrganism().replaceAll(", laboratory", "").replaceAll(", domestic", ""));
    			}
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.HOMOLOGY, "Homology Class", hg.getPrimaryID(), "MGI",
    					hg.getClusterKey(), "Class with " + organisms.toString()) );
    		}
    	}
    	return searchResults;
    }

    // Find any vocabulary terms that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getTerms(String accID, SearchResults<Accession> searchResults) {
    	List<Term> termResults = termFinder.getTermsByID(accID);
    	if (termResults.size() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + termResults.size());
    		for (Term term : termResults) {
    			String termID = term.getPrimaryID();
    			String displayType = "Vocabulary Term";

    			if ((termID != null) && (termID.indexOf(":") >= 0)) {
    				displayType = termID.substring(0, termID.indexOf(":") + 1);
    			}
    			searchResults.addResultObjects(
    				new Accession(ObjectTypes.VOCAB_TERM, displayType, term.getPrimaryID(), "MGI",
    					term.getTermKey(), term.getTerm()) );
    		}
    	}
    	return searchResults;
    }

    // Find any sequences that match the given accession ID and add them to the searchResults.
    private SearchResults<Accession> getSequences(String accID, SearchResults<Accession> searchResults) {
    	List<Sequence> sequenceResults = sequenceFinder.getSequencesByID(accID);
    	if (sequenceResults.size() > 0) {
    		searchResults.setTotalCount(searchResults.getTotalCount() + sequenceResults.size());
    		for (Sequence sequence : sequenceResults) {
    			// If this is a gene model sequence for a strain marker, then link to the associated marker.
    			// Otherwise link to the sequence itself.
    			
    			if ((sequence.getPrimaryID().startsWith("MGP_") || sequence.getPrimaryID().startsWith("MGI_")) 
   					&& (sequence.getMarkers().size() == 1) ) {
    					for (Marker m : sequence.getMarkers()) {
    						searchResults.addResultObjects(
    							new Accession(ObjectTypes.MARKER, m.getMarkerSubtype(), m.getPrimaryID(), "MGI",
    								m.getMarkerKey(), m.getSymbol() + ", " + m.getName() + ", Chr " + m.getChromosome()) );
    					}
    			} else {
    				searchResults.addResultObjects(
    					new Accession(ObjectTypes.SEQUENCE, "Sequence", sequence.getPrimaryID(), "MGI",
    						sequence.getSequenceKey(), sequence.getDescription()) );
    			}
    		}
    	}
    	return searchResults;
    }
}
