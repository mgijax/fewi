package org.jax.mgi.fewi.searchUtil;

public class SortConstants {

	// GXD assay & marker summary page
	public static final String GXD_GENE         = "gene";
	public static final String GXD_ASSAY_ID     = "assayID";
	public static final String GXD_ASSAY_TYPE   = "assayType";
	public static final String GXD_SYSTEM       = "anatomicalSystem";
	public static final String GXD_AGE          = "age";
	public static final String GXD_STRUCTURE    = "structure";
	public static final String GXD_DETECTION    = "detectionLevel";
	public static final String GXD_GENOTYPE     = "genotype";
	public static final String GXD_REFERENCE    = "reference";
	public static final String GXD_LOCATION     = "location";

	// reference summary page
	public static final String REF_JOURNAL         = "journal";
	public static final String REF_AUTHORS         = "authors";
	public static final String REF_BY_AUTHOR       = "authorSort";
	public static final String REF_JOURNAL_AC      = "journalSortLower";
	public static final String REF_YEAR            = "year";

	// recombinase summary page
	public static final String CRE_DRIVER          = "driver";
	public static final String CRE_SYMBOL          = "nomenclature";
	public static final String CRE_TYPE            = "alleleType";
	public static final String CRE_INDUCIBLE       = "inducibleNote";
	public static final String CRE_REF_COUNT       = "countOfReferences";
	public static final String CRE_DETECTED_COUNT  = "detectedCount";
	public static final String CRE_NOT_DETECTED_COUNT  = "notDetectedCount";

    // Cre sorting strings, for the detail page
    public static final String CRE_BY_STRUCTURE                 = "byStructure";
    public static final String CRE_BY_AGE                       = "byAge";
    public static final String CRE_BY_LEVEL                     = "byLevel";
    public static final String CRE_BY_PATTERN                   = "byPattern";
    public static final String CRE_BY_JNUM_ID                   = "byJnumID";
    public static final String CRE_BY_ASSAY_TYPE                = "byAssayType";
    public static final String CRE_BY_REPORTER_GENE             = "byReporterGene";
    public static final String CRE_BY_DETECTION_METHOD          = "byDetectionMethod";
    public static final String CRE_BY_ASSAY_NOTE                = "byAssayNote";
    public static final String CRE_BY_ALLELIC_COMPOSITION       = "byAllelicComposition";
    public static final String CRE_BY_SEX                       = "bySex";
    public static final String CRE_BY_SPECIMEN_NOTE             = "bySpecimenNote";
    public static final String CRE_BY_RESULT_NOTE               = "byResultNote";

    // Default sort
    public static final String BY_DEFAULT						= "byDefaultSort";

    // GXD Lit Summary Page
    public static final String MRK_BY_SYMBOL					= "byMrkSymbol";

	// Sequence Summary page
	public static final String SEQUENCE_TYPE       = "sequenceType";
	public static final String SEQUENCE_PROVIDER   = "sequenceProvider";
	public static final String SEQUENCE_LENGTH     = "sequenceLength";

	// Vocab
	public static final String VOC_TERM            = "term";
	public static final String VOC_TERM_ID         = "termId";
	public static final String VOC_TERM_HEADER 		= "termHeader";
	public static final String VOC_BY_DAG_STRUCT   = "byDagStruct";
	public static final String VOC_BY_DAG_TERM      = "byDagTerm";
	public static final String MARKER_DAG_TERM      = "byMarkerDagTerm";

	// MP annotation page
	public static final String GENOTYPE_TERM = "byGenotypeTerm";

    // Disease Portal Marker Tab Sorts
	public static final String DP_BY_MRK_SYMBOL     = "byMarkerSymbol";
	public static final String DP_BY_MRK_TYPE       = "byMarkerType";
	public static final String DP_BY_ORGANISM       = "byMarkerOrganism";
	public static final String DP_BY_LOCATION       = "byMarkerLocation";
	public static final String DP_BY_HOMOLOGENE_ID       = "byHomologeneId";

	
	// Allele
	public static final String ALL_BY_TRANSMISSION = "byTransmission";	
	public static final String ALL_BY_SYMBOL = "bySymbol";
	public static final String ALL_BY_TYPE = "byType";
	public static final String ALL_BY_CHROMOSOME = "byChr";
	public static final String ALL_BY_DISEASE = "byDisease";

	
    // faux entry;  used by webapp class template
    public static final String FOO_SORT              = "fooSort";
}
