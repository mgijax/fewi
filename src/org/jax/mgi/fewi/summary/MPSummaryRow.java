package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrMPAnnotation;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around one or more MP annotations which all use the same Genotype;
 * represents on row on an MP annotation summary page
 */
public class MPSummaryRow {

	private final Logger logger = LoggerFactory.getLogger(MPSummaryRow.class);

	
    //-------------------
    // instance variables
    //-------------------

    // encapsulated row object
    private final List<SolrMPAnnotation> annotations = 
	    new ArrayList<SolrMPAnnotation>();

    // genotype key shared by all annotations
    private int genotypeKey = -1;

    // config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

    private NotesTagConverter ntc;

    //-------------
    // constructors
    //-------------

    // require a Genotype when we create an MPSummaryRow
    public MPSummaryRow (int genotypeKey) {
	this.genotypeKey = genotypeKey;
    }

    //----------------
    // mutator methods
    //----------------

    // adds the given annotation to this MPSummaryRow -- does not validate
    // that the genotypes match
    public void addAnnotation(SolrMPAnnotation a) {
	this.annotations.add(a);
    }

    private void addNotesTagConverter() {
	try {
	    this.ntc = new NotesTagConverter();
	} catch (Exception e) {
		logger.error(e.getMessage(),e);
		}
    }

    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    // get all the Annotation objects
    public List<SolrMPAnnotation> getAnnotations() {
	return this.annotations;
    }

    // get the strain ID, if available
    public String getStrainID() {
    	if (this.annotations.size() > 0) {
    		return this.annotations.get(0).getStrainID();
    	}
    	return null;
    }

    // get just the first Annotation
    public SolrMPAnnotation getFirstAnnotation() {
	return this.annotations.get(0);
    }

    // get the second and later annotations
    public List<SolrMPAnnotation> getExtraAnnotations() {
	List<SolrMPAnnotation> extras = new ArrayList<SolrMPAnnotation>();
	boolean isFirst = true;

	for (SolrMPAnnotation a : this.annotations) {
	    if (isFirst) {
		isFirst = false;
	    } else {
		extras.add(a);
	    }
	}

	return extras;
    }

    // get the alleleic composition for the genotype
    public String getAllelicComp() {
	if (ntc == null) { this.addNotesTagConverter(); }

	String cleanAllelicComp = this.getFirstAnnotation().getAllelePairs();

	if (cleanAllelicComp==null) {return "";}
	return FormatHelper.newline2HTMLBR (
	    ntc.convertNotes(cleanAllelicComp, '|'));
    }

    // get the genetic background for the genotype
    public String getGenBackground() {
	if (ntc == null) { this.addNotesTagConverter(); }

	String cleanGenBackground =
		this.getFirstAnnotation().getBackgroundStrain();

	if (cleanGenBackground==null) {return "";}
	return FormatHelper.superscript(cleanGenBackground);
    }

    public int getGenotypeKey() {
	return this.genotypeKey;
    }
}
