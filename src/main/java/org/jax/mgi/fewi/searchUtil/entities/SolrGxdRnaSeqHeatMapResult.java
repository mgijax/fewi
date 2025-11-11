package org.jax.mgi.fewi.searchUtil.entities;

import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.snpdatamodel.document.ESEntity;


public class SolrGxdRnaSeqHeatMapResult extends ESEntity implements SolrGxdEntity {
	protected String structure;
	protected String structureID;
	protected Integer theilerStage;
	protected String sex;
	protected String age;
	protected String strain;
	protected String alleles;
	protected String markerSymbol;
	protected String markerMgiID;
	protected String markerEnsemblGeneModelID;
	protected String avergageQNTPM;
	protected String consolidatedSampleKey;
	protected String assayMgiID;
	protected String resultKey;

	public String getStructure() {
		return structure;
	}
	public void setStructure(String structure) {
		this.structure = structure;
	}
	public String getStructureID() {
		return structureID;
	}
	public void setStructureID(String structureID) {
		this.structureID = structureID;
	}
	public Integer getTheilerStage() {
		return theilerStage;
	}
	public void setTheilerStage(Integer theilerStage) {
		this.theilerStage = theilerStage;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getStrain() {
		return strain;
	}
	public void setStrain(String strain) {
		this.strain = strain;
	}
	public String getAlleles() {
		return alleles;
	}
	public void setAlleles(String alleles) {
		this.alleles = alleles;
	}
	public String getMarkerSymbol() {
		return markerSymbol;
	}
	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}
	public String getMarkerMgiID() {
		return markerMgiID;
	}
	public void setMarkerMgiID(String markerMgiID) {
		this.markerMgiID = markerMgiID;
	}
	public String getMarkerEnsemblGeneModelID() {
		return markerEnsemblGeneModelID;
	}
	public void setMarkerEnsemblGeneModelID(String markerEnsemblGeneModelID) {
		this.markerEnsemblGeneModelID = markerEnsemblGeneModelID;
	}
	public String getAvergageQNTPM() {
		return avergageQNTPM;
	}
	public void setAvergageQNTPM(String avergageQNTPM) {
		this.avergageQNTPM = avergageQNTPM;
	}
	public String getConsolidatedSampleKey() {
		return consolidatedSampleKey;
	}
	public void setConsolidatedSampleKey(String consolidatedSampleKey) {
		this.consolidatedSampleKey = consolidatedSampleKey;
	}
	public String getAssayMgiID() {
		return assayMgiID;
	}
	public void setAssayMgiID(String assayMgiID) {
		this.assayMgiID = assayMgiID;
	}
	public String getResultKey() {
		return resultKey;
	}
	public void setResultKey(String resultKey) {
		this.resultKey = resultKey;
	}

	@Override
	public String toString() {
		return "SolrGxdRnaSeqHeatMapResult [structure=" + structure + ", structureID=" + structureID + ", theilerStage="
				+ theilerStage + ", sex=" + sex + ", age=" + age + ", strain=" + strain + ", alleles=" + alleles
				+ ", markerSymbol=" + markerSymbol + ", markerMgiID=" + markerMgiID + ", markerEnsemblGeneModelID="
				+ markerEnsemblGeneModelID + ", avergageQNTPM=" + avergageQNTPM + ", consolidatedSampleKey="
				+ consolidatedSampleKey + ", assayMgiID=" + assayMgiID + ", resultKey=" + resultKey + "]";
	}
}
