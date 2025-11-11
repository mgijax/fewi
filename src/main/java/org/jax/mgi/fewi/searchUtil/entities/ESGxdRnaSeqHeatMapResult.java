package org.jax.mgi.fewi.searchUtil.entities;

import java.util.List;

import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.snpdatamodel.document.ESEntity;


public class ESGxdRnaSeqHeatMapResult extends ESEntity implements SolrGxdEntity {
	protected String structureExact;
	protected String printname;
	protected String structureId;
	protected Integer theilerStage;
	protected String sex;
	protected String age;
	protected String strain;
	protected String genotype;
	protected String markerSymbol;
	protected String markerMgiid;
	protected String ensemblGeneModelID;
	protected String avgQnTpmLevel;
	protected String consolidatedSampleKey;
	protected String assayMgiid;
	protected String resultKey;
	
	public static final List<String> RETURN_FIELDS = List.of(GxdResultFields.STRUCTURE_EXACT, GxdResultFields.STRUCTURE_PRINTNAME,
			GxdResultFields.THEILER_STAGE, GxdResultFields.SEX, GxdResultFields.STRAIN, GxdResultFields.GENOTYPE,
			GxdResultFields.ASSAY_MGIID, GxdResultFields.AGE, GxdResultFields.STRUCTURE_PRINTNAME,
			GxdResultFields.CONSOLIDATED_SAMPLE_KEY, GxdResultFields.MARKER_SYMBOL, GxdResultFields.MARKER_MGIID,
			GxdResultFields.ENSEMBL_GMID, GxdResultFields.AVG_QN_TPM_LEVEL, GxdResultFields.RESULT_KEY);
	
	@Override
	public String toString() {
		return "SolrGxdRnaSeqHeatMapResult [structure=" + structureExact + ", structureID=" + structureId + ", theilerStage="
				+ theilerStage + ", sex=" + sex + ", age=" + age + ", strain=" + strain + ", genotype=" + genotype
				+ ", markerSymbol=" + markerSymbol + ", markerMgiID=" + markerMgiid + ", markerEnsemblGeneModelID="
				+ ensemblGeneModelID + ", avergageQNTPM=" + avgQnTpmLevel + ", consolidatedSampleKey="
				+ consolidatedSampleKey + ", assayMgiID=" + assayMgiid + ", resultKey=" + resultKey + "]";
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
	public String getMarkerSymbol() {
		return markerSymbol;
	}
	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}
	public String getConsolidatedSampleKey() {
		return consolidatedSampleKey;
	}
	public void setConsolidatedSampleKey(String consolidatedSampleKey) {
		this.consolidatedSampleKey = consolidatedSampleKey;
	}
	public String getResultKey() {
		return resultKey;
	}
	public void setResultKey(String resultKey) {
		this.resultKey = resultKey;
	}
	public String getStructureExact() {
		return structureExact;
	}
	public void setStructureExact(String structureExact) {
		this.structureExact = structureExact;
	}
	public String getStructureId() {
		return structureId;
	}
	public void setStructureId(String structureId) {
		this.structureId = structureId;
	}
	public String getMarkerMgiid() {
		return markerMgiid;
	}
	public void setMarkerMgiid(String markerMgiid) {
		this.markerMgiid = markerMgiid;
	}
	public String getEnsemblGeneModelID() {
		return ensemblGeneModelID;
	}
	public void setEnsemblGeneModelID(String ensemblGeneModelID) {
		this.ensemblGeneModelID = ensemblGeneModelID;
	}
	public String getAvgQnTpmLevel() {
		return avgQnTpmLevel;
	}
	public void setAvgQnTpmLevel(String avgQnTpmLevel) {
		this.avgQnTpmLevel = avgQnTpmLevel;
	}
	public String getAssayMgiid() {
		return assayMgiid;
	}
	public void setAssayMgiid(String assayMgiid) {
		this.assayMgiid = assayMgiid;
	}

	public String getPrintname() {
		return printname;
	}

	public void setPrintname(String printname) {
		this.printname = printname;
	}

	public String getGenotype() {
		return genotype;
	}

	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}
}
