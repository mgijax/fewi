package org.jax.mgi.fewi.summary;

import java.util.*;

import org.jax.mgi.fewi.searchUtil.entities.SolrInteraction;
import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.config.ContextLoader;

import javax.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a SolrInteraction object;  represents one row in summary
 */
public class InteractionSummaryRow {

    //-------------------
    // instance variables
    //-------------------

    private Logger logger = LoggerFactory.getLogger(InteractionSummaryRow.class);

    // encapsulated row object
    private SolrInteraction jr;

    // config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

    //-------------
    // constructors
    //-------------

    // hide the default constructor - we NEED a SolrInteraction object to wrap
    private InteractionSummaryRow () {}

    public InteractionSummaryRow (SolrInteraction jr) {
    	this.jr = jr;
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getMiKey() {
	return this.jr.getRegKey();
    }

    public String getLinkedOrganizer() {
	StringBuffer sb = new StringBuffer();
	sb.append("<a href='");
	sb.append(fewiUrl);
	sb.append("marker/");
	sb.append(this.jr.getOrganizerID());
	sb.append("' target='_blank'>");
	sb.append(this.jr.getOrganizerSymbol());
	sb.append("</a>");
	return sb.toString();
    }

    public String getLinkedParticipant() {
	StringBuffer sb = new StringBuffer();
	sb.append("<a href='");
	sb.append(fewiUrl);
	sb.append("marker/");
	sb.append(this.jr.getParticipantID());
	sb.append("' target='_blank'>");
	sb.append(this.jr.getParticipantSymbol());
	sb.append("</a>");
	return sb.toString();
    }

    public String getDataSource() {
	return jr.getScoreSource();
    }
    public String getScore() {
	return jr.getScore();
    }
    public String getValidation() {
	return jr.getValidation();
    }

    public String getReference() {
	StringBuffer sb = new StringBuffer();
	sb.append("<a href='");
	sb.append(fewiUrl);
	sb.append("reference/");
	sb.append(jr.getJnumID());
	sb.append("' target='_blank'>");
	sb.append(jr.getJnumID());
	sb.append("</a>");
	return sb.toString();
    }

    public String getOrganizerSymbol() {
    	return this.jr.getOrganizerSymbol();
    }
    public String getOrganizerID() {
    	return this.jr.getOrganizerID();
    }

    public String getParticipantSymbol() {
    	return this.jr.getParticipantSymbol();
    }
    public String getParticipantID() {
    	return this.jr.getParticipantID();
    }
    public String getRelationshipTerm() {
	return this.jr.getRelationshipTerm();
    }
    public String getEvidenceCode() {
	return this.jr.getEvidenceCode();
    }
}
