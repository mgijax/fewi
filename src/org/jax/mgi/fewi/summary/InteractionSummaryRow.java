package org.jax.mgi.fewi.summary;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrInteraction;

/**
 * wrapper around a SolrInteraction object;  represents one row in summary
 */
public class InteractionSummaryRow {

	// encapsulated row object
	private SolrInteraction jr;

	// config values
	String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	public InteractionSummaryRow (SolrInteraction jr) {
		this.jr = jr;
		return;
	}

	//------------------------------------------------------------------------
	// public instance methods;  JSON serializer will call all public methods
	//------------------------------------------------------------------------

	public String getMiKey() {
		return jr.getRegKey();
	}
	public String getAlgorithm() {
		return jr.getAlgorithm();
	}
	public String getOrganizerProductID() {
		return jr.getOrganizerProductID();
	}
	public String getParticipantProductID() {
		return jr.getParticipantProductID();
	}
	public String getOtherReferences() {
		return jr.getOtherReferences();
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
	public String getJnumID() {
		return jr.getJnumID();
	}
	public String getOrganizerSymbol() {
		return jr.getOrganizerSymbol();
	}
	public String getOrganizerID() {
		return jr.getOrganizerID();
	}
	public String getParticipantSymbol() {
		return jr.getParticipantSymbol();
	}
	public String getParticipantID() {
		return jr.getParticipantID();
	}
	public String getRelationshipTerm() {
		return jr.getRelationshipTerm();
	}
	public String getEvidenceCode() {
		return jr.getEvidenceCode();
	}
	public String getNotes() {
		return jr.getNotes();
	}

	public String getLinkedOrganizer() {
		StringBuffer sb = new StringBuffer();
		sb.append("<a href='");
		sb.append(fewiUrl);
		sb.append("marker/");
		sb.append(jr.getOrganizerID());
		sb.append("' target='_blank'>");
		sb.append(jr.getOrganizerSymbol());
		sb.append("</a>");
		return sb.toString();
	}

	public String getLinkedParticipant() {
		StringBuffer sb = new StringBuffer();
		sb.append("<a href='");
		sb.append(fewiUrl);
		sb.append("marker/");
		sb.append(jr.getParticipantID());
		sb.append("' target='_blank'>");
		sb.append(jr.getParticipantSymbol());
		sb.append("</a>");
		return sb.toString();
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
}
