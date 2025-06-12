package org.jax.mgi.fewi.searchUtil;

public class FacetConstants {

	// reference facets
	public static final String REF_AUTHORS = "authors";
	public static final String REF_JOURNALS = "journals";
	public static final String REF_YEAR = "year";
	public static final String REF_CURATED_DATA = "curatedData";
	public static final String REF_GROUPING = "grouping";

	// expression facets
	public static final String GXD_SYSTEM = "anatomicalSystem";
	public static final String GXD_ASSAY_TYPE = "assayType";
	public static final String GXD_DETECTED = "detectionLevelFacet";
	public static final String GXD_THEILER_STAGE = "theilerStage";
	public static final String GXD_WILDTYPE = "isWildType";
	public static final String GXD_MARKER_TYPE = "featureTypes";
	public static final String GXD_MP = "mpHeaders";
	public static final String GXD_CO = "coHeaders";
	public static final String GXD_DO = "doHeaders";
	public static final String GXD_GO = "goHeaders";
	public static final String GXD_GO_MF = "goMfHeaders"; 
	public static final String GXD_GO_BP = "goBpHeaders"; 
	public static final String GXD_GO_CC = "goCcHeaders"; 
	public static final String GXD_TMP_LEVEL = "tpmLevel"; 
	
	
	// TODO - this will be split into three
	//public static final String GXD_GO = "goHeaders";

	// interaction facets
	public static final String INT_VALIDATION = "validation";
	public static final String INT_INTERACTION = "interaction";
	public static final String INT_DATA_SOURCE = "dataSource";
	public static final String INT_SCORE = "score";

	// HMDC facets
	public static final String MARKER_FEATURE_TYPE = "featureType";

	// cre facets
	public static final String CRE_INDUCER = "inducer";

	// sequence facets
	public static final String SEQ_TYPE = "sequenceType";
	public static final String SEQ_STRAIN = "strain"; 
}
