package org.jax.mgi.fewi.searchUtil.entities;

import org.jax.mgi.fewi.searchUtil.entities.group.SolrInteractionEntity;


public class SolrInteraction implements SolrInteractionEntity {
	
	private String regKey;
	private String organizerID;
	private String organizerSymbol;
	private String participantID;
	private String markerID;
	private String participantSymbol;
	private String relationshipTerm;
	private String validation;
	private String qualifier;
	private String evidenceCode;
	private String jnumID;
	private String scoreSource;
	private String score;
	private String matureTranscript;
	private String participantProductID;
	private String organizerProductID;
	private String algorithm;
	private String otherReferences;
	private String notes;
	
	public String getRegKey() {
		return regKey;
	}
	public void setRegKey(String regKey) {
		this.regKey = regKey;
	}
	public String getOrganizerID() {
		return organizerID;
	}
	public void setOrganizerID(String organizerID) {
		this.organizerID = organizerID;
	}
	public String getOrganizerSymbol() {
		return organizerSymbol;
	}
	public void setOrganizerSymbol(String organizerSymbol) {
		this.organizerSymbol = organizerSymbol;
	}
	public String getParticipantID() {
		return participantID;
	}
	public void setParticipantID(String participantID) {
		this.participantID = participantID;
	}
	public String getMarkerID() {
		return markerID;
	}
	public void setMarkerID(String markerID) {
		this.markerID = markerID;
	}
	public String getParticipantSymbol() {
		return participantSymbol;
	}
	public void setParticipantSymbol(String participantSymbol) {
		this.participantSymbol = participantSymbol;
	}
	public String getRelationshipTerm() {
		return relationshipTerm;
	}
	public void setRelationshipTerm(String relationshipTerm) {
		this.relationshipTerm = relationshipTerm;
	}
	public String getValidation() {
		return validation;
	}
	public void setValidation(String validation) {
		this.validation = validation;
	}
	public String getQualifier() {
		return qualifier;
	}
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	public String getEvidenceCode() {
		return evidenceCode;
	}
	public void setEvidenceCode(String evidenceCode) {
		this.evidenceCode = evidenceCode;
	}
	public String getJnumID() {
		return jnumID;
	}
	public void setJnumID(String jnumID) {
		this.jnumID = jnumID;
	}
	public String getScoreSource() {
		return scoreSource;
	}
	public void setScoreSource(String scoreSource) {
		this.scoreSource = scoreSource;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getMatureTranscript() {
		return matureTranscript;
	}
	public void setMatureTranscript(String matureTranscript) {
		this.matureTranscript = matureTranscript;
	}
	public String getParticipantProductID() {
		return participantProductID;
	}
	public void setParticipantProductID(String participantProductID) {
		this.participantProductID = participantProductID;
	}
	public String getOrganizerProductID() {
		return organizerProductID;
	}
	public void setOrganizerProductID(String organizerProductID) {
		this.organizerProductID = organizerProductID;
	}
	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	public String getOtherReferences() {
		return otherReferences;
	}
	public void setOtherReferences(String otherReferences) {
		this.otherReferences = otherReferences;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
}
