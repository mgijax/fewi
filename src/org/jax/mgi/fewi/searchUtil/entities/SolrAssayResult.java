package org.jax.mgi.fewi.searchUtil.entities;

import java.util.List;

public class SolrAssayResult 
{
	private String jNum;
	private String pubmedId;

	private String markerSymbol;
	private String markerMgiid;
	private String markerName;
	private String printname;
	private String anatomicalSystem;
	private Integer theilerStage;
	private List<String> figures;
	private List<String> figuresPlain;
	
	private String genotype;
	private String age;

	private String assayMgiid;
	private String assayKey;
	private String assayType;
	//private String pattern;

	private String detectionLevel;
	private String shortCitation;
	
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
	public String getAnatomicalSystem() {
		return anatomicalSystem;
	}
	public void setAnatomicalSystem(String anatomicalSystem) {
		this.anatomicalSystem = anatomicalSystem;
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

//	public String getPattern() {
//		return pattern;
//	}
//	public void setPattern(String pattern) {
//		this.pattern = pattern;
//	}
	public String getPubmedId() {
		return pubmedId;
	}
	public void setPubmedId(String pubmedId) {
		this.pubmedId = pubmedId;
	}
}
