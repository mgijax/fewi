package org.jax.mgi.fewi.searchUtil;

/**
* SearchConstants
*
* This will hopefully make the code more readable, maintainable, and
* receptive to index change
*/
public class SearchConstants {
    public static final String UNIQUE_KEY		    = "uniqueKey";

    // reference constants
    public static final String REF_ID			    = "reference_id";
    public static final String REF_KEY              = "reference_key";
    public static final String REF_AUTHOR_ANY		= "author_any";
    public static final String REF_AUTHOR           = "author";
    public static final String REF_AUTHOR_FIRST		= "author_first";
    public static final String REF_AUTHOR_LAST		= "author_last";
    public static final String REF_JOURNAL			= "journal";
    public static final String REF_TEXT_ABSTRACT	= "text_abstract";
    public static final String REF_TEXT_TITLE       = "text_title";
    public static final String REF_YEAR             = "year";

    // Special field for when title and abstract are mushed together.
    public static final String REF_TEXT_TITLE_ABSTRACT = "text_title_abstract";

    // sequence constants
    public static final String SEQ_ID               = "sequence_id";
    public static final String SEQ_KEY              = "sequence_key";
    public static final String SEQ_PROVIDER         = "provider";
    public static final String SEQ_TYPE             = "sequence_type";
    public static final String SEQ_STRAIN           = "strain";

    // marker constants
    public static final String MRK_KEY              = "marker_key";
    public static final String MRK_ID               = "marker_id";
    public static final String MARKER_ID            = "markerID";
	public static final String MRK_TERM_ID 			= "markerTermId";
    public static final String MRK_SYMBOL               = "marker_symbol";
    public static final String MRK_SYMBOL_LOWER         = "markerSymbolLower";
    public static final String MRK_NOMENCLATURE			= "nomenclature";
    public static final String MRK_HOMOLOG_SYMBOLS		= "homologSymbols";
	public static final String FEATURE_TYPE = "featureType";
	public static final String FEATURE_TYPE_KEY = "featureTypeKey";
	public static final String EVIDENCE_CODE		= "evidenceCode";
	public static final String EVIDENCE_TERM		= "evidenceTerm";
	public static final String EVIDENCE_CATEGORY	= "evidenceCategory";

	// marker symbols in marker index
    public static final String MRK_CURRENT_SYMBOL	= "currentSymbol";
    public static final String MRK_HUMAN_SYMBOL		= "humanSymbol";
    public static final String MRK_RAT_SYMBOL		= "ratSymbol";
    public static final String MRK_RHESUS_SYMBOL	= "rhesusMacaqueSymbol";
    public static final String MRK_CATTLE_SYMBOL	= "cattleSymbol";
    public static final String MRK_DOG_SYMBOL		= "dogSymbol";
    public static final String MRK_ZFIN_SYMBOL		= "zebrafishSymbol";
    public static final String MRK_CHICKEN_SYMBOL	= "chickenSymbol";
    public static final String MRK_CHIMP_SYMBOL		= "chimpanzeeSymbol";
    public static final String MRK_FROG_SYMBOL		= "westernClawedFrogSymbol";

    // allele constants
    public static final String ALL_KEY              = "allele_key";
    public static final String ALL_SYSTEM           = "allele_system";
	public static final String ALL_SYSTEM_KEY		 = "allele_system_key";
    public static final String ALL_DRIVER           = "allele_driver";
    public static final String ALL_ID               = "allele_id";
    public static final String ALL_TYPE				= "allele_type";
	public static final String ALL_SUBTYPE 			= "allele_subtype";
    public static final String ALL_IS_WILD_TYPE		= "is_wild_type";
    public static final String ALL_COLLECTION		= "collection";
    public static final String ALL_PHENOTYPE		= "allPhenotype";
	public static final String ALL_NOMEN 			= "allNomen";
	public static final String ALL_HAS_DO			 = "hasDO";
	public static final String ALL_IS_CELLLINE		 = "isCellLine";
    public static final String ALLELE_KEY              = "alleleKey";
    public static final String ALL_MI_MARKER_IDS = "mutationInvolvesMarkerIDs";
	public static final String ALL_MUTATION			 = "mutation";

    // Accession

    public static final String ACC_ID				= "acc_id";

    // Autocomplete

    public static final String AC_FOR_GXD			= "forGXD";

    // batch query constants
    public static final String BATCH_TERM              = "batch_term";
    public static final String BATCH_TYPE              = "batch_type";


    // Cre Constants

    public static final String CRE_SYSTEM_KEY       	= "system_key";
    public static final String CRE_DRIVER           	= "driver";
    public static final String CRE_INDUCER          	= "inducer";
	public static final String CRE_STRUCTURE 			= "structure";
	public static final String CRE_DETECTED 			= "detected";
	public static final String CRE_SYSTEM_HL_GROUP		= "systemHlGroup";
	public static final String CRE_ALL_STRUCTURES	= "allStructures";
	public static final String CRE_ALL_STRUCTURES_DIRECT	= "allStructuresDirect";
	public static final String CRE_EXCLUSIVE_STRUCTURES	= "exclusiveStructures";

	public static final String CRE_SYSTEM_DETECTED		= "alleleSystemDetected";
	public static final String CRE_SYSTEM_NOT_DETECTED	= "alleleSystemNotDetected";

    // GXD Lit

    public static final String GXD_LIT_MRK_NOMEN	= "nomen";
    public static final String GXD_LIT_MRK_NOMEN_BEGINS	= "nomenBegins";
    public static final String GXD_LIT_MRK_SYMBOL	= "symbolAndSynonyms";
    public static final String GXD_LIT_AGE		= "age";
    public static final String GXD_LIT_ASSAY_TYPE	= "assayType";


    // GXD Lit special param
    // This is used to highlight the long citation in the GXD Lit Summaries.
    // We are mapping all of the reference query parameters which highlighting makes sense for
    // to this singular parameter that only exists for the display layer.

    public static final String GXD_LIT_LONG_CITATION= "longCitation";

    // Genotypes
    public static final String GENOTYPE_KEY	    = "genotypeKey";
    public static final String ALLELE_PAIRS	    = "allelePairs";
    public static final String BACKGROUND_STRAIN    = "backgroundStrain";

    // Annotations
    public static final String ANNOTATED_TERM_ID    = "annotatedTermID";
    public static final String JNUM_ID		    = "jnumID";
    public static final String SLIM_TERM	    = "slimTerm";

    // Vocab Constants

    public static final String VOC_VOCAB            = "vocab";
	public static final String VOC_DAG_NAME			= "dagName";
    public static final String VOC_TERM		    = "term";
    public static final String VOC_TERM_ID			= "termId";
    public static final String VOC_TERM_TYPE		= "termType";
    public static final String VOC_DERIVED_TERMS	= "derivedTerms";
    public static final String VOC_RESTRICTION      = "qualifier";
    public static final String TERM_ID		    = "termID";
    public static final String ANNOTATION_KEY	    = "annotationKey";
    public static final String CROSS_REF		    = "crossRef";
    public static final String GENOCLUSTER_KEY		= "genoclusterKey";

    // Images

    public static final String IMG_KEY				= "imageKey";
    public static final String IMG_ID				= "imageID";
    public static final String IMG_CLASS			= "imageClass";
    public static final String IMG_IS_THUMB         = "isThumb";

    // disease constants
    public static final String DISEASE_ID            = "disease_id";

    // mp constants
    public static final String MP_ID            = "mp_id";
    public static final String MP_HEADER        = "mp_header";


    // interpro
	public static final String INTERPRO_TERM = "interProTerm";

	// go
	public static final String GO_ID = "go_id";
	public static final String GO_TERM = "goTerm";
	public static final String GO_PROCESS_TERM = "goProcessTerm";
	public static final String GO_FUNCTION_TERM = "goFunctionTerm";
	public static final String GO_COMPONENT_TERM = "goComponentTerm";

	// phenotype
	public static final String PHENOTYPE = "phenotype";

    // query form options' constants
    public static final String FORM_NAME	= "form_name";
    public static final String FIELD_NAME	= "field_name";

    // faux entry;  used by webapp class template
    public static final String FOO_ID            = "foo_id";

    // GXD constants
    public static final String GXD_AGE_MIN = "ageMin";
    public static final String GXD_AGE_MAX = "ageMax";
    public static final String GXD_ASSAY_KEY = "assayKey";
    public static final String GXD_ASSAY_ID = "assayID";
    public static final String GXD_ASSAY_TYPE = "assayType";
	public static final String GXD_THEILER_STAGE = "theilerStage";
	public static final String GXD_DETECTED = "detectionLevel";
	public static final String GXD_MUTATED_IN = "mutatedIn";
	public static final String GXD_IS_WILD_TYPE = "isWildType";
	public static final String SYNONYM = "synonym";
	public static final String STRUCTURE = "structure";
	public static final String STRUCTURE_EXACT = "structureExact";
	public static final String STRUCTURE_KEY = "structureKey";
	public static final String STRUCTURE_ID = "structureID";
	public static final String CELLTYPE_ID = "celltypeID";
	public static final String PROBE_KEY = "probeKey";
	public static final String ANTIBODY_KEY = "antibodyKey";
	public static final String POS_STRUCTURE = "posStructure";
	public static final String ANATOMICAL_SYSTEM = "anatomicalSystem";
	public static final String ANATOMY_ID = "anatomyID";
	public static final String ANCESTOR_ANATOMY_KEY	= "ancestorAnatomyKey";
	// profile constants
	public static final String PROF_POS_C_EXACT = "posCExact";
	public static final String PROF_POS_C_ANC = "posCAnc";
	public static final String PROF_POS_R_EXACT = "posRExact";
	public static final String PROF_POS_R_ANC = "posRAnc";

	public static final String PROF_POS_C_EXACT_A = "posCExactA";
	public static final String PROF_POS_C_ANC_A = "posCAncA";
	public static final String PROF_POS_R_EXACT_A = "posRExactA";
	public static final String PROF_POS_R_ANC_A = "posRAncA";

	public static final String PRIMARY_KEY = "pKey";

	// GXD high-throughput expression data
	public static final String GXDHT_EXPERIMENT_KEY = "experimentKey";
	public static final String GXDHT_SAMPLE_KEY = "sampleKey";
	public static final String GXDHT_EXPERIMENT_ID = "experimentID";
	public static final String GXDHT_STRUCTURE_SEARCH = "structureSearch";
	public static final String GXDHT_CT_SEARCH_IDS = "ctSearchIDs";
	public static final String GXDHT_CT_SEARCH_TERMS = "ctSearchTerms";
	public static final String GXDHT_CT_FACET_TERMS = "ctFacetTerms";
	public static final String GXDHT_SEX = "sex";
	public static final String GXDHT_MUTATED_GENE = "mutatedGene";
	public static final String GXDHT_MUTANT_ALLELE_IDS = "mutantAlleleIds";
	public static final String GXDHT_METHOD = "method";
	public static final String GXDHT_METHODS = "methods";
	public static final String GXDHT_STRAIN = "strainSearch";
	public static final String GXDHT_TITLE = "title";
	public static final String GXDHT_DESCRIPTION = "description";
	public static final String GXDHT_TITLE_DESCRIPTION = "titleDescription";
	public static final String GXDHT_EXPERIMENTAL_VARIABLE = "experimentalVariable";
	public static final String GXDHT_RELEVANCY = "relevancy";
	public static final String GXDHT_STUDY_TYPE = "studyType";
	
	// Homology constants
	public static final String HOMOLOGY_ID            = "homologyID";
	public static final String DP_GRID_CLUSTER_KEY = "gridClusterKey";
	public static final String DP_GENO_CLUSTER_KEY = "genoClusterKey";


	// location constants
	public static final String MOUSE_COORDINATE = "mouseCoordinate";
	public static final String HUMAN_COORDINATE = "humanCoordinate";
    public static final String START_COORD = "startCoord";
    public static final String END_COORD = "endCoord";
    public static final String CHROMOSOME = "chromosome";
    public static final String GENETIC_CHROMOSOME = "genetic_chromosome";
    public static final String GENOMIC_CHROMOSOME = "genomic_chromosome";
    public static final String CM_OFFSET = "cmOffset";
    public static final String CYTOGENETIC_OFFSET = "cytogeneticOffset";
    public static final String STRAND = "strand";

	// genotype constants
	public static final String GENOTYPE_TYPE = "genotypeType";

	// interaction constants
    public static final String RELATIONSHIP_TERM = "relationshipTerm";
    public static final String VALIDATION = "validation";
    public static final String DATA_SOURCE = "dataSource";
    public static final String SCORE_NUMERIC = "score_numeric";
    public static final String SCORE_FILTERABLE = "score_filterable";

	// matrix view groups
	public static final String STAGE_MATRIX_GROUP = "stageMatrixGroup";
	public static final String GENE_MATRIX_GROUP = "geneMatrixGroup";
	
	// snp index constants
	public static final String SNPID = "snpId";
	public static final String STARTCOORDINATE = "startcoordinate";
	public static final String VARIATIONCLASS = "variationclass";
	public static final String FUNCTIONCLASS = "functionclass";
	public static final String MARKERID = "markerId";
	public static final String STRAINS = "strains";
	public static final String ALLELE = "allele";
	public static final String DIFF_STRAINS = "diffstrains";
	public static final String SAME_STRAINS = "samestrains";
	public static final String CRE_SYSTEM = "creSystem";

	// cDNA clones
	public static final String CDNA_KEY = "cloneKey";
	public static final String CDNA_MARKER_ID = "markerID";
	public static final String CDNA_SEQUENCE_NUM = "sequenceNum";
	public static final String CDNA_CLONE = "clone";
	
	// probes
	public static final String PRB_KEY = "probeKey";
	public static final String PRB_MARKER_ID = "markerID";
	public static final String PRB_REFERENCE_ID = "referenceID";
	public static final String PRB_SEGMENT_TYPE = "segmentType";
	public static final String PRB_PROBE = "probe";

	// genetic mapping experiments
	public static final String MLD_EXPERIMENT_KEY = "experimentKey";
	public static final String MLD_EXPERIMENT = "experiment";
	public static final String MLD_REFERENCE_ID = "referenceID";
	public static final String MLD_MARKER_ID = "markerID";
	
	// shared vocabulary browser fields
	public static final String VB_PRIMARY_ID = "primaryID";
	public static final String VB_ACC_ID = "accID";
	public static final String VB_TERM = "term";
	public static final String VB_SYNONYM = "synonym";
	public static final String VB_PARENT_ID = "parentID";
	public static final String VB_SEQUENCE_NUM = "sequenceNum";
	public static final String VB_VOCAB_NAME = "vocabName";
	public static final String VB_DAG_NAME = "dagName";
	public static final String VB_CROSSREF = "crossRef";
	
	// correlation matrix fields
	public static final String CM_MARKER_ID = "markerID";
	public static final String CM_PARENT_ANATOMY_ID = "parentAnatomyID";

	// strain fields
	public static final String STRAIN_KEY = "strainKey";
	public static final String STRAIN_TYPE = "strainType";
	public static final String STRAIN_NAME = "strainName";
	public static final String STRAIN_NAME_LOWER = "strainNameLower";
	public static final String STRAIN_ATTRIBUTE_LOWER = "attributeLower";
	public static final String STRAIN_GROUPS = "groups";
	public static final String STRAIN_TAGS = "tags";
	public static final String STRAIN_IS_SEQUENCED = "isSequenced";
	
	// general
	public static final String COLUMN_ID = "columnID";
	public static final String HAS_IMAGE = "hasImage";
	public static final String ORGANISM = "organism";
	public static final String COORD_TYPE = "coordType";
	
	// quick search
	public static final String QS_SEARCH_TERM_EXACT = "searchTermExact";
	public static final String QS_SEARCH_TERM_INEXACT = "searchTermInexact";
	public static final String QS_SEARCH_TERM_STEMMED = "searchTermStemmed";
	public static final String QS_SEARCH_TERM_DISPLAY = "searchTermDisplay";
	public static final String QS_SEARCH_TERM_TYPE = "searchTermType";
	public static final String QS_SEARCH_TERM_WEIGHT = "searchTermWeight";

	public static final String QS_SEARCH_COORD_TYPE = "searchCoordType";
	public static final String QS_SEARCH_CHROMOSOME = "searchChromosome";
	public static final String QS_SEARCH_START_COORD = "searchStartCoord";
	public static final String QS_SEARCH_END_COORD = "searchEndCoord";
	public static final String QS_COORD_SEQUENCE_NUM = "coordSequenceNum";

	public static final String QS_OBJECT_TYPE = "objectType";			// special for other bucket:
	public static final String QS_OBJECT_SUBTYPE = "objectSubType";		// special for other bucket:
	public static final String QS_PRIMARY_ID = "primaryID";
	public static final String QS_DETAIL_URI = "detailUri";
	public static final String QS_SEQUENCE_NUM = "sequenceNum";
	public static final String QS_SYMBOL = "symbol";
	public static final String QS_NAME = "name";
	public static final String QS_IS_MARKER = "isMarker";
	public static final String QS_FEATURE_TYPE = "featureType";
	public static final String QS_CHROMOSOME = "chromosome";
	public static final String QS_START_COORD = "startCoord";
	public static final String QS_END_COORD = "endCoord";
	public static final String QS_STRAND = "strand";
	public static final String QS_LOCATION = "location";

	public static final String QS_TERM = "term";
	public static final String QS_TERM_TYPE = "termType";
	public static final String QS_VOCAB_NAME = "vocabName";				// for display in QS
	public static final String QS_RAW_VOCAB_NAME = "rawVocabName";		// for certain filtering
	public static final String QS_ANNOTATION_COUNT = "annotationCount";
	public static final String QS_ANNOTATION_TEXT = "annotationText";
	public static final String QS_ANNOTATION_URI = "annotationUri";
	public static final String QS_IMSR_ID = "imsrID";

	public static final String QS_GO_PROCESS_FACETS = "goProcessFacets";
	public static final String QS_GO_FUNCTION_FACETS = "goFunctionFacets";
	public static final String QS_GO_COMPONENT_FACETS = "goComponentFacets";
	public static final String QS_DISEASE_FACETS = "diseaseFacets";
	public static final String QS_PHENOTYPE_FACETS = "phenotypeFacets";
	public static final String QS_MARKER_TYPE_FACETS = "markerTypeFacets";
	public static final String QS_EXPRESSION_FACETS = "expressionFacets";
	public static final String QS_CELL_TYPE_FACETS = "cellTypeFacets";
	
	public static final String QS_ATTRIBUTES = "attributes";			// special for strain bucket
	public static final String QS_REFERENCE_COUNT = "referenceCount";
	public static final String QS_REFERENCE_URI = "referenceUri";

	public static final int SEARCH_MAX_RESULT_WINDOW = 500_000;
	public static final int SEARCH_MAX_GROUP_BASE_MAX_SIZE = 500_000;
	public static final int SEARCH_PRECISION_THRESHOLD = 400_000;  
	public static final int SEARCH_MAX_POST_SIZE = 50_000; 
	public static final int SEARCH_MAX_PAGE_SIZE_FOR_MARKER_PROFILE = 60_000; 

}
