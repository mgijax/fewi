package org.jax.mgi.fewi.searchUtil;

/**
* SearchConstants
*
* This will hopefully make the code more readable, maintainable, and
* receptive to index change
*/
public class SearchConstants {

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
    
    // Special new field for when title and abstract are mushed 
    // together.
    
    public static final String REF_TEXT_TITLE_ABSTRACT = "text_title_abstract";
    
    // sequence constants
    public static final String SEQ_ID               = "sequence_id";
    public static final String SEQ_KEY              = "sequence_key";

    // marker constants
    public static final String MRK_KEY              = "marker_key";
    public static final String MRK_ID               = "marker_id";

    // allele constants
    public static final String ALL_KEY              = "allele_key";
    public static final String ALL_SYSTEM           = "allele_system";
    public static final String ALL_DRIVER           = "allele_driver";
    public static final String ALL_ID               = "allele_id";


    // faux entry;  used by webapp class template
    public static final String FOO_ID              = "foo_id";
}
