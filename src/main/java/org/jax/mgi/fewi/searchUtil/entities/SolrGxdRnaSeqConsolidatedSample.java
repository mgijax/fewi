package org.jax.mgi.fewi.searchUtil.entities;

import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;

public class SolrGxdRnaSeqConsolidatedSample implements SolrGxdEntity {
	protected String structure;
	protected String structureID;
	protected Integer theilerStage;
	protected String sex;
	protected String age;
	protected String strain;
	protected String alleles;
	protected String consolidatedSampleKey;
	protected String assayMgiID;
	protected Integer bioreplicateCount;

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

	public Integer getBioreplicateCount() {
		return bioreplicateCount;
	}
	public void setBioreplicateCount(Integer bioreplicateCount) {
		this.bioreplicateCount = bioreplicateCount;
	}
	@Override
	public String toString() {
		return "SolrGxdRnaSeqConsolidatedSample [structure=" + structure + ", structureID=" + structureID
				+ ", theilerStage=" + theilerStage + ", sex=" + sex + ", age=" + age + ", strain=" + strain
				+ ", alleles=" + alleles + ", consolidatedSampleKey=" + consolidatedSampleKey + ", assayMgiID="
				+ assayMgiID + "]";
	}
}
