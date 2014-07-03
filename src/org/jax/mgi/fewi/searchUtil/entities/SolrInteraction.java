package org.jax.mgi.fewi.searchUtil.entities;

import java.util.List;

public class SolrInteraction
{
	String regKey;
	String organizerSymbol;
	String organizerID;
	String participantSymbol;
	String participantID;
	String relationshipTerm;
	String evidenceCode;
	String score;
	String scoreSource;
	String validation;
	String jnumID;
	String matureTranscript;
	String notes; 
	
	// getters and setters

	public String getRegKey() {
		return regKey;
	}
	public void setRegKey(String regKey) {
		this.regKey = regKey;
	}
	
	public String getOrganizerSymbol() {
		return organizerSymbol;
	}
	public void setOrganizerSymbol(String organizerSymbol) {
		this.organizerSymbol = organizerSymbol;
	}
	
	public String getOrganizerID() {
		return organizerID;
	}
	public void setOrganizerID(String organizerID) {
		this.organizerID = organizerID;
	}
	
	public String getParticipantSymbol() {
		return participantSymbol;
	}
	public void setParticipantSymbol(String participantSymbol) {
		this.participantSymbol = participantSymbol;
	}
	
	public String getParticipantID() {
		return participantID;
	}
	public void setParticipantID(String participantID) {
		this.participantID = participantID;
	}
	
	public String getRelationshipTerm() {
		return relationshipTerm;
	}
	public void setRelationshipTerm(String relationshipTerm) {
		this.relationshipTerm = relationshipTerm;
	}
	
	public String getEvidenceCode() {
		return evidenceCode;
	}
	public void setEvidenceCode(String evidenceCode) {
		this.evidenceCode = evidenceCode;
	}
	
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	
	public String getScoreSource() {
		return scoreSource;
	}
	public void setScoreSource(String scoreSource) {
		this.scoreSource = scoreSource;
	}
	
	public String getValidation() {
		return validation;
	}
	public void setValidation(String validation) {
		this.validation = validation;
	}
	
	public String getJnumID() {
		return jnumID;
	}
	public void setJnumID(String jnumID) {
		this.jnumID = jnumID;
	}
	
	public String getMatureTranscript() {
		return matureTranscript;
	}
	public void setMatureTranscript(String matureTranscript) {
		this.matureTranscript = matureTranscript;
	}
	
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
}
