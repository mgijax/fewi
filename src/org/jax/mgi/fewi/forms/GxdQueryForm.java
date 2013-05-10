package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/*-------*/
/* class */
/*-------*/

public class GxdQueryForm 
{
    private Map<Integer, String> theilerStages = new LinkedHashMap<Integer, String>();
	private List<Integer> theilerStage = new ArrayList<Integer>();
	public static Integer ANY_STAGE = 0;

    private Map<String, String> ages = new LinkedHashMap<String, String>();
	private List<String> age = new ArrayList<String>();
	public final static String ANY_AGE = "ANY";
	public final static String EMBRYONIC = "Embryonic";
	public final static String POSTNATAL = "Postnatal";
	public final static String ANY_DETECTED = "either";

	private Map<String, String> detectedOptions = new LinkedHashMap<String, String>();

	private List<String> assayTypes = new ArrayList<String>();
	private List<String> assayType = new ArrayList<String>();

	private String nomenclature = "";
    private String vocabTerm = "";
    private String structure = "";
    private String structureID = "";
    private String structureKey = "";
    private String annotatedStructureKey = "";
	private String location = "";
    private String detected = ANY_DETECTED;
    private String annotationId = "";
    private String isWildType = "";
    private String mutatedIn = "";
    private String markerMgiId = "";
    private String jnum = "";
    private String alleleId = "";
    private String probeKey = "";
    private String antibodyKey = "";


	// differential specific fields
    private String difStructure = "";
    
    private Map<Integer, String> theilerStagesRibbon2 = new LinkedHashMap<Integer, String>();
    private Map<Integer, String> difTheilerStages = new LinkedHashMap<Integer, String>();
	private List<Integer> difTheilerStage = new ArrayList<Integer>();
	public static Integer ANY_STAGE_NOT_ABOVE=-1;
	
    private Map<String, String> difAges = new LinkedHashMap<String, String>();
	private List<String> difAge = new ArrayList<String>();
	public final static String ANY_AGE_NOT_ABOVE = "NOT_ABOVE";
    
	public GxdQueryForm() {

		detectedOptions.put("Yes", "detected in ");
		detectedOptions.put("No", "not detected in ");
		detectedOptions.put(ANY_DETECTED, "either");

    	ages.put(ANY_AGE, "Any");
    	ages.put(EMBRYONIC, "Embryonic");
    	ages.put(POSTNATAL, "Postnatal");
    	ages.put("0.5", "E0.5");
    	ages.put("1", "E1.0");
    	ages.put("1.5", "E1.5");
    	ages.put("2", "E2.0");
    	ages.put("2.5", "E2.5");
    	ages.put("3", "E3.0");
    	ages.put("3.5", "E3.5");
    	ages.put("4", "E4.0");
    	ages.put("4.5", "E4.5");
    	ages.put("5", "E5.0");
    	ages.put("5.5", "E5.5");
    	ages.put("6", "E6.0");
    	ages.put("6.5", "E6.5");
    	ages.put("7", "E7.0");
    	ages.put("7.5", "E7.5");
    	ages.put("8", "E8.0");
    	ages.put("8.5", "E8.5");
    	ages.put("9", "E9.0");
    	ages.put("9.5", "E9.5");
    	ages.put("10", "E10.0");
    	ages.put("10.5", "E10.5");
    	ages.put("11", "E11.0");
    	ages.put("11.5", "E11.5");
    	ages.put("12", "E12.0");
    	ages.put("12.5", "E12.5");
    	ages.put("13", "E13.0");
    	ages.put("13.5", "E13.5");
    	ages.put("14", "E14.0");
    	ages.put("14.5", "E14.5");
    	ages.put("15", "E15.0");
    	ages.put("15.5", "E15.5");
    	ages.put("16", "E16.0");
    	ages.put("16.5", "E16.5");
    	ages.put("17", "E17.0");
    	ages.put("17.5", "E17.5");
    	ages.put("18", "E18.0");
    	ages.put("18.5", "E18.5");
    	ages.put("19", "E19.0");
    	ages.put("19.5", "E19.5");
    	ages.put("20", "E20.0");
    	ages.put("20.5", "E20.5");

    	age.add(ANY_AGE);

    	theilerStages.put(ANY_STAGE, "Any developmental stage");
    	theilerStages.put(1, "TS 1 (0.0-2.5 dpc)");
    	theilerStages.put(2, "TS 2 (1.0-2.5 dpc)");
    	theilerStages.put(3, "TS 3 (1.0-3.5 dpc)");
    	theilerStages.put(4, "TS 4 (2.0-4.0 dpc)");
    	theilerStages.put(5, "TS 5 (3.0-5.5 dpc)");
    	theilerStages.put(6, "TS 6 (4.0-5.5 dpc)");
    	theilerStages.put(7, "TS 7 (4.5-6.0 dpc)");
    	theilerStages.put(8, "TS 8 (5.0-6.5 dpc)");
    	theilerStages.put(9, "TS 9 (6.25-7.25 dpc)");
    	theilerStages.put(10, "TS 10 (6.5-7.75 dpc)");
    	theilerStages.put(11, "TS 11 (7.25-8.0 dpc)");
    	theilerStages.put(12, "TS 12 (7.5-8.75 dpc)");
    	theilerStages.put(13, "TS 13 (8.0-9.25 dpc)");
    	theilerStages.put(14, "TS 14 (8.5-9.75 dpc)");
    	theilerStages.put(15, "TS 15 (9.0-10.25 dpc)");
    	theilerStages.put(16, "TS 16 (9.5-10.75 dpc)");
    	theilerStages.put(17, "TS 17 (10.0-11.25 dpc)");
    	theilerStages.put(18, "TS 18 (10.5-11.25 dpc)");
    	theilerStages.put(19, "TS 19 (11.0-12.25 dpc)");
    	theilerStages.put(20, "TS 20 (11.5-13.0 dpc)");
    	theilerStages.put(21, "TS 21 (12.5-14.0 dpc)");
    	theilerStages.put(22, "TS 22 (13.5-15.0 dpc)");
    	theilerStages.put(23, "TS 23 (15 dpc)");
    	theilerStages.put(24, "TS 24 (16 dpc)");
    	theilerStages.put(25, "TS 25 (17 dpc)");
    	theilerStages.put(26, "TS 26 (18 dpc)");
    	theilerStages.put(28, "TS 28 (postnatal)");

    	theilerStage.add(ANY_STAGE);

    	assayTypes.add("Immunohistochemistry");
    	assayTypes.add("In situ reporter (knock in)");
    	assayTypes.add("Northern blot");
    	assayTypes.add("Nuclease S1");
    	assayTypes.add("RNA in situ");
    	assayTypes.add("RNase protection");
    	assayTypes.add("RT-PCR");
    	assayTypes.add("Western blot");

    	
    	// init differential fields
    	for(Integer key : theilerStages.keySet())
    	{
    		if(key != ANY_STAGE) theilerStagesRibbon2.put(key,theilerStages.get(key));
    	}
    	difTheilerStages.put(ANY_STAGE_NOT_ABOVE, "Any stage not selected above");
    	for(Integer key : theilerStages.keySet())
    	{
    		if(key != ANY_STAGE) difTheilerStages.put(key,theilerStages.get(key));
    	}
    	//difTheilerStage.add(ANY_STAGE_NOT_ABOVE);
    	
    	difAges.put(ANY_AGE_NOT_ABOVE, "Any age not selected above");
    	for(String key : ages.keySet())
    	{
    		if(key != ANY_AGE) difAges.put(key,ages.get(key));
    	}
    	//difAge.add(ANY_AGE_NOT_ABOVE);
    }
    public List<String> getAssayType() {
        return assayType;
    }
    public void setAssayType(List<String> assayType) {
        this.assayType = assayType;
    }

	public List<String> getAge() {
        return age;
    }

    public Map<String, String> getAges() {
		return ages;
	}
    public String getAgesSelected () {
    	if (! age.contains(ANY_AGE)) {
    		return StringUtils.join(age, " or ");
    	}
    	return null;
    }

    public List<Integer> getTheilerStage() {
        return theilerStage;
    }
    public Map<Integer,String> getTheilerStages(){
    	return theilerStages;
    }

    public List<String> getAssayTypes() {
		return assayTypes;
	}
    public String getAssayTypesSelected() {
    	if (!assayType.contains("ANY")) {
    		return StringUtils.join(assayType, " or ");
    	}
    	return null;
    }

    public String getNomenclature() {
        return nomenclature;
    }

    public void setAge(List <String> age) {
        this.age = age;
    }
    public void setAges(Map<String, String> ages) {
		this.ages = ages;
	}

    public void setTheilerStage(List<Integer> theilerStage) {
        this.theilerStage = theilerStage;
    }
    public void setTheilerStages(Map<Integer,String> theilerStages) {
        this.theilerStages = theilerStages;
    }
    public void setNomenclature(String nomenclature) {
        this.nomenclature = nomenclature;
    }
	public String getVocabTerm() {
		return vocabTerm;
	}
	public void setVocabTerm(String vocabTerm) {
		this.vocabTerm = vocabTerm;
	}
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
	public String getStructureKey() {
		return structureKey;
	}
	public void setStructureKey(String structureKey) {
		this.structureKey = structureKey;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDetected() {
		return detected;
	}
	public void setDetected(String detected) {
		this.detected = detected;
	}
    public Map<String, String> getDetectedOptions() {
		return detectedOptions;
	}
	public void setDetectedOptions(Map<String, String> detectedOptions) {
		this.detectedOptions = detectedOptions;
	}

	public String getAnnotationId() {
		return annotationId;
	}
	public void setAnnotationId(String annotationId) {
		this.annotationId = annotationId;
	}
	public String getMutatedIn() {
		return mutatedIn;
	}
	public void setMutatedIn(String mutatedIn) {
		this.mutatedIn = mutatedIn;
	}
	public String getMarkerMgiId() {
		return markerMgiId;
	}
	public void setMarkerMgiId(String markerMgiId) {
		this.markerMgiId = markerMgiId;
	}
	public String getJnum() {
		return jnum;
	}
	public void setJnum(String jnum) {
		this.jnum = jnum;
	}
	public String getIsWildType() {
		return isWildType;
	}
	public void setIsWildType(String isWildType) {
		this.isWildType = isWildType;
	}
    public String getAnnotatedStructureKey() {
		return annotatedStructureKey;
	}
	public void setAnnotatedStructureKey(String annotatedStructureKey) {
		this.annotatedStructureKey = annotatedStructureKey;
	}
	public String getAlleleId() {
		return alleleId;
	}
	public void setAlleleId(String alleleId) {
		this.alleleId = alleleId;
	}
	public String getProbeKey() {
		return probeKey;
	}
	public void setProbeKey(String probeKey) {
		this.probeKey = probeKey;
	}
	public String getAntibodyKey() {
		return antibodyKey;
	}
	public void setAntibodyKey(String antibodyKey) {
		this.antibodyKey = antibodyKey;
	}
	
	/*
	 * Differential getters / setters
	 */
	public String getDifStructure() {
		return difStructure;
	}
	public void setDifStructure(String difStructure) {
		this.difStructure = difStructure;
	}
	public Map<Integer, String> getDifTheilerStages() {
		return difTheilerStages;
	}
	public void setDifTheilerStages(Map<Integer, String> difTheilerStages) {
		this.difTheilerStages = difTheilerStages;
	}
	public List<Integer> getDifTheilerStage() {
		return difTheilerStage;
	}
	public void setDifTheilerStage(List<Integer> difTheilerStage) {
		this.difTheilerStage = difTheilerStage;
	}
	public Map<String, String> getDifAges() {
		return difAges;
	}
	public void setDifAges(Map<String, String> difAges) {
		this.difAges = difAges;
	}
	public List<String> getDifAge() {
		return difAge;
	}
	public void setDifAge(List<String> difAge) {
		this.difAge = difAge;
	}
	
	
	public Map<Integer, String> getTheilerStagesRibbon2() {
		return theilerStagesRibbon2;
	}
	public void setTheilerStagesRibbon2(Map<Integer, String> theilerStagesRibbon2) {
		this.theilerStagesRibbon2 = theilerStagesRibbon2;
	}
	/*
	 *  resolve difTheilerStages by processing "ANY_OTHER_STAGE" option 
	 *  	(assumes this part of the form is filled in)
	 */
	public Collection<Integer> getResolvedDifTheilerStage()
	{
		if(difTheilerStage.contains(ANY_STAGE_NOT_ABOVE))
		{
			// I'm actually not sure what to do in this case. What does "any not selected" from "any" mean?
			if(theilerStage.contains(ANY_STAGE)) return new ArrayList<Integer>();
			
			// populate stagesBelow with the full list of stages
			Set<Integer> stagesBelow = new HashSet<Integer>();
			for(Integer s : theilerStages.keySet())
			{
				// don't include the ANY option
				if(!s.equals(ANY_STAGE))
				{
					stagesBelow.add(s);
				}
			}
			stagesBelow.removeAll(theilerStage);
			return stagesBelow;
		}
		
		return difTheilerStage;
	}
	@Override
	public String toString() {
		return "GxdQueryForm [theilerStages=" + theilerStages
				+ ", theilerStage=" + theilerStage + ", ages=" + ages
				+ ", age=" + age + ", detectedOptions=" + detectedOptions
				+ ", assayTypes=" + assayTypes + ", assayType=" + assayType
				+ ", nomenclature=" + nomenclature + ", vocabTerm=" + vocabTerm
				+ ", structure=" + structure + ", location=" + location
				+ ", structureKey=" + structureKey + ", structureID=" + structureID
				+ ", detected=" + detected + ", annotationId=" + annotationId
				+ ", isWildType=" + isWildType + ", mutatedIn=" + mutatedIn
				+ ", markerMgiId=" + markerMgiId + ", jnum=" + jnum
				+ ", probeKey=" + probeKey
				+ ", antibodyKey=" + antibodyKey
				+ "]";
	}

}
