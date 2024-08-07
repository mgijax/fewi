package org.jax.mgi.fewi.searchUtil.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

import org.jax.mgi.fewi.util.FormatHelper;

/* Is: a high-throughput expression experiment
 * Notes: is populated using data from the gxdHtExperiment Solr index
 */
public class GxdHtExperiment
{
	private Integer experimentKey;
	private String arrayExpressID;
	private String geoID;
	private String title;
	private Integer sampleCount;
	private List<String> experimentalVariables;
	private String studyType;
	private String method;
	private String description;
	private String note;
	private Integer byDefault;
	private Integer matchingSampleCount = 0;
	private List<String> pubmedIDs;
	private Integer isInAtlas;
	private Integer isLoaded;

	public Integer getExperimentKey() {
		return experimentKey;
	}
	public void setExperimentKey(Integer experimentKey) {
		this.experimentKey = experimentKey;
	}
	public String getArrayExpressID() {
		return arrayExpressID;
	}
	public void setArrayExpressID(String arrayExpressID) {
		this.arrayExpressID = arrayExpressID;
	}
	public String getGeoID() {
		return geoID;
	}
	public void setGeoID(String geoID) {
		this.geoID = geoID;
	}
	public Integer getIsInAtlas() {
		return isInAtlas;
	}
	public void setIsInAtlas(Integer isInAtlas) {
		this.isInAtlas = isInAtlas;
	}
	public Integer getIsLoaded() {
		return isLoaded;
	}
	public void setIsLoaded(Integer isLoaded) {
		this.isLoaded = isLoaded;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getSampleCount() {
		return sampleCount;
	}
	public void setSampleCount(Integer sampleCount) {
		this.sampleCount = sampleCount;
	}
	public List<String> getFilteredExperimentalVariables() {
		List<String> displayable = new ArrayList<String>();
		for (String ev : this.getExperimentalVariables()) {
			if (!ev.equals("Not Applicable") && !ev.equals("Not Curated")) {
				displayable.add(ev);
			}
		}
		return displayable;
	}
	/*
	 * WTS2-1515 Custom comparator for ExperimentalVariables facet. Push RNA-seq terms to the bottom.
	 */
	public static class SortExperimentalVariables implements Comparator<String> 
	{ 
		public int compare(String a, String b) 
                { 
			String rnaseq = "RNA-seq";
			if (a.contains(rnaseq)) {
				if (b.contains(rnaseq)) {
					return a.compareTo(b);
				} else {
					return 1;
				}
			} else if (b.contains(rnaseq)) {
				return -1;
			} else {
				return a.compareTo(b);
			}
		} 
	} 
	
	public List<String> getExperimentalVariables() {
		Collections.sort(experimentalVariables, new SortExperimentalVariables());
		return experimentalVariables;
	}
	public void setExperimentalVariables(List<String> experimentalVariables) {
		this.experimentalVariables = experimentalVariables;
	}
	public String getStudyType() {
		return studyType;
	}
	public void setStudyType(String studyType) {
		this.studyType = studyType;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getLinkedDescription() {
		return FormatHelper.makeUrlsIntoLinks(description, true, null);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Integer getByDefault() {
		return byDefault;
	}
	public void setByDefault(Integer byDefault) {
		this.byDefault = byDefault;
	}
	public Integer getMatchingSampleCount() {
		return matchingSampleCount;
	}
	public void setMatchingSampleCount(Integer matchingSampleCount) {
		this.matchingSampleCount = matchingSampleCount;
	}
	public List<String> getPubmedIDs() {
		return pubmedIDs;
	}
	public void setPubmedIDs(List<String> pubmedIDs) {
		this.pubmedIDs = pubmedIDs;
	}
	@Override
	public String toString() {
		return "GxdHtExperiment [experimentKey=" + experimentKey + ", arrayExpressID=" + arrayExpressID + ", geoID="
				+ geoID + ", title=" + title + ", sampleCount=" + sampleCount + ", experimentalVariables="
				+ experimentalVariables + ", studyType=" + studyType + ", method=" + method + ", description="
				+ description + ", note=" + note + ", byDefault=" + byDefault + "]";
	}
}
