package org.jax.mgi.fewi.searchUtil.entities;

import java.text.NumberFormat;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.snpdatamodel.document.BaseESDocument;

public class SolrAssayResult extends BaseESDocument implements SolrGxdEntity
{
	private String jNum;
	private String pubmedId;

	private String markerSymbol;
	private String markerMgiid;
	private String markerName;
	private String printname;
	private List<String> anatomicalSystems;
	private Integer theilerStage;
	private List<String> figures;
	private List<String> figuresPlain;
	private String cellType;
	
	private String genotype;
	private String age;

	private String assayMgiid;
	private String assayKey;
	private String assayType;

	private String detectionLevel;
	private String shortCitation;
	
	// fields related to RNA-Seq data
	private String tpmLevel;
	private String avgQnTpmLevel;
	private String biologicalReplicates;
	private String strain;
	private String sex;
	private String notes;

	//*** static variables ***//
	private static NumberFormat fmt = null;

	//*** methods ***//

	public String getJNum() {
		return jNum;
	}
	public void setJNum(String jNum) {
		this.jNum = jNum;
	}
	public String getMarkerSymbol() {
		return markerSymbol;
	}
	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}
	public String getPrintname() {
		return printname;
	}
	public void setPrintname(String printname) {
		this.printname = printname;
	}
	public List<String> getAnatomicalSystems() {
		return anatomicalSystems;
	}
	public void setAnatomicalSystems(List<String> anatomicalSystems) {
		this.anatomicalSystems = anatomicalSystems;
	}
	public Integer getTheilerStage() {
		return theilerStage;
	}
	public void setTheilerStage(Integer theilerStage) {
		this.theilerStage = theilerStage;
	}
	public List<String> getFigures() {
		return figures;
	}
	public void setFigures(List<String> figures) {
		this.figures = figures;
	}
	public String getGenotype() {
		return genotype;
	}
	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getAssayMgiid() {
		return assayMgiid;
	}
	public void setAssayMgiid(String assayMgiid) {
		this.assayMgiid = assayMgiid;
	}
	public String getAssayKey() {
		return assayKey;
	}
	public void setAssayKey(String assayKey) {
		this.assayKey = assayKey;
	}
	public String getAssayType() {
		return assayType;
	}
	public void setAssayType(String assayType) {
		this.assayType = assayType;
	}
	public String getDetectionLevel() {
		return detectionLevel;
	}
	public void setDetectionLevel(String detectionLevel) {
		this.detectionLevel = detectionLevel;
	}
	public String getShortCitation() {
		return shortCitation;
	}
	public void setShortCitation(String shortCitation) {
		this.shortCitation = shortCitation;
	}
	public String getMarkerMgiid() {
		return markerMgiid;
	}
	public void setMarkerMgiid(String markerMgiid) {
		this.markerMgiid = markerMgiid;
	}
	public String getMarkerName() {
		return markerName;
	}
	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}
	public List<String> getFiguresPlain() {
		return figuresPlain;
	}
	public void setFiguresPlain(List<String> figuresPlain) {
		this.figuresPlain = figuresPlain;
	}

	public String getPubmedId() {
		return pubmedId;
	}
	public void setPubmedId(String pubmedId) {
		this.pubmedId = pubmedId;
	}
	public String getTpmLevel() {
		// returns High, Medium, Low, or Below Cutoff
		return tpmLevel;
	}
	public void setTpmLevel(String tpmLevel) {
		this.tpmLevel = tpmLevel;
	}
	public String getAvgQnTpmLevel() {
		// The indexer already trims digits appropriately for this
		// value and stores the result as a string.
		return avgQnTpmLevel;
	}
	public void setAvgQnTpmLevel(String avgQnTpmLevel) {
		this.avgQnTpmLevel = avgQnTpmLevel;
	}
	public String getBiologicalReplicates() {
		return biologicalReplicates;
	}
	public void setBiologicalReplicates(String biologicalReplicates) {
		this.biologicalReplicates = biologicalReplicates;
	}
	public String getStrain() {
		// not just a getter -- also convert Not Specified to a space
		if ("Not Specified".equals(strain)) {
			return "";
		}
		return strain;
	}
	public void setStrain(String strain) {
		this.strain = strain;
	}
	public String getSex() {
		// not just a getter -- also convert Not Specified to a space
		if ("Not Specified".equals(sex)) {
			return "";
		}
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getCellType() {
		return cellType;
	}
	public void setCellType(String cellType) {
		this.cellType = cellType;
	}
	public void setjNum(String jNum) {
		this.jNum = jNum;
	}
}
