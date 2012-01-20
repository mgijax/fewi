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

    // Special field for when title and abstract are mushed together.
    public static final String REF_TEXT_TITLE_ABSTRACT = "text_title_abstract";

    // sequence constants
    public static final String SEQ_ID               = "sequence_id";
    public static final String SEQ_KEY              = "sequence_key";
    public static final String SEQ_PROVIDER         = "provider";

    // marker constants
    public static final String MRK_KEY              = "marker_key";
    public static final String MRK_ID               = "marker_id";
    public static final String MRK_SYMBOL               = "marker_symbol";

    // allele constants
    public static final String ALL_KEY              = "allele_key";
    public static final String ALL_SYSTEM           = "allele_system";
    public static final String ALL_DRIVER           = "allele_driver";
    public static final String ALL_ID               = "allele_id";

    // Accession

    public static final String ACC_ID				= "acc_id";

    // Autocomplete

    public static final String AC_FOR_GXD			= "forGXD";

    // batch query constants
    public static final String BATCH_TERM              = "batch_term";
    public static final String BATCH_TYPE              = "batch_type";


    // Cre Constants

    public static final String CRE_SYSTEM_KEY       = "system_key";

    // GXD Lit

    public static final String GXD_LIT_MRK_NOMEN	= "nomen";
    public static final String GXD_LIT_MRK_NOMEN_BEGINS	= "nomenBegins";
    public static final String GXD_LIT_AGE		= "age";
    public static final String GXD_LIT_ASSAY_TYPE	= "assayType";

    // GXD Lit special param
    // This is used to highlight the long citation in the GXD Lit Summaries.
    // We are mapping all of the reference query parameters which highlighting makes sense for
    // to this singular parameter that only exists for the display layer.

    public static final String GXD_LIT_LONG_CITATION= "longCitation";

    // Vocab Constants

    public static final String VOC_VOCAB            = "vocab";
    public static final String VOC_RESTRICTION      = "qualifier";

    // Images

    public static final String IMG_KEY				= "imageKey";
    public static final String IMG_ID				= "imageID";
    public static final String IMG_CLASS			= "imageClass";
    public static final String IMG_IS_THUMB         = "isThumb";

    // faux entry;  used by webapp class template
    public static final String FOO_ID              = "foo_id";
}
