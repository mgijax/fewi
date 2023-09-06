package org.jax.mgi.fewi.summary;

import org.jax.mgi.fe.datamodel.Allele;
import org.jax.mgi.fe.datamodel.AlleleRelatedMarker;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.FormatHelper;


/**
 * wrapper around a AlleleRelatedMarker;  represents a row in the summary
 * table for the 'mutation involves' page for an allele
 */
public class MutationInvolvesSummaryRow {

    //-------------------
    // instance variables
    //-------------------

    // config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
    AlleleRelatedMarker arm = null; 
    Allele allele = null;

    //-------------
    // constructors
    //-------------

    public MutationInvolvesSummaryRow (AlleleRelatedMarker arm, Allele allele) {
	this.arm = arm;
	this.allele = allele;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getMutation() {
    	return FormatHelper.superscript(this.allele.getSymbol());
    }

    public String getEffects() {
	StringBuffer sb = new StringBuffer();
	if (this.arm.getQualifier() != null) {
	    if (!this.arm.getQualifier().equals("Not Specified")) {
		sb.append("<B>");
		sb.append(this.arm.getQualifier());
		sb.append("</B> ");
	    }
	}
	sb.append (this.arm.getRelationshipTerm());
	sb.append (" ");
	sb.append ("<a href='");
	sb.append (fewiUrl);
	sb.append ("marker/");
	sb.append (this.arm.getRelatedMarkerID());
	sb.append ("' target='_blank'>");
	sb.append (FormatHelper.superscript(this.arm.getRelatedMarkerSymbol()));
	sb.append ("</a>");
	return sb.toString();
    }

    public String getFeatureType() {
    	return this.arm.getRelatedMarker().getMarkerSubtype();
    }

    public String getLocation() {
	return this.arm.getRelatedMarker().getLocation();
    }

    public String getReference() {
	StringBuffer sb = new StringBuffer();
	sb.append ("<a href='");
	sb.append (fewiUrl);
	sb.append ("reference/");
	sb.append (this.arm.getJnumID());
	sb.append ("' target='_blank'>");
	sb.append (this.arm.getJnumID());
	sb.append ("</a>");
    	return sb.toString();
    }

    public String getNotes() {
    	return this.arm.getProperty("note");
    }
}
