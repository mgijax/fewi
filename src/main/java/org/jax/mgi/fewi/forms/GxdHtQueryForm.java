package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* container for parameters from the GXD High-Throughput experiments' query form
 */
public class GxdHtQueryForm {


    //--------------------//
    // instance variables
    //--------------------//

	private List<String> age = new ArrayList<String>();
	private List<Integer> theilerStage = new ArrayList<Integer>();
	private List<String> variable = new ArrayList<String>();
	private List<String> variableFilter = new ArrayList<String>();
	private List<String> studyTypeFilter = new ArrayList<String>();
	private List<String> rnaseqType = new ArrayList<String>();
	private List<String> method =  new ArrayList<String>();
	private String mutatedIn = "";
	private String mutantAlleleId = "";
	private String relevancy = "";
	private String sex = "";
	private String structure = "";
	private String structureID = "";
	private String text = "";
	private String arrayExpressID = "";
	private String experimentKey = "";
	private String strain = "";
	private List<String> textScope = new ArrayList<String>();
	private Map<String,String> sexOptions = new LinkedHashMap<String,String>();
	private Map<String,String> textScopeOptions = new LinkedHashMap<String,String>();
	private Map<String,String> methodOptions = new LinkedHashMap<String,String>();
	private Map<String,String> rnaseqTypeOptions = new LinkedHashMap<String,String>();
	private Map<Integer, String> theilerStages = new LinkedHashMap<Integer, String>();
	public static Integer ANY_STAGE = 0;

	private Map<String, String> ages = new LinkedHashMap<String, String>();
	public final static String ANY_AGE = "ANY";
	public final static String EMBRYONIC = "Embryonic";
	public final static String POSTNATAL = "Postnatal";

    //--------------------//
    // constructors
    //--------------------//

	public GxdHtQueryForm() {
		this.populateAges();
		this.populateStages();

		// set default values
		age.add(ANY_AGE);
		theilerStage.add(ANY_STAGE);

		sexOptions.put("Female", "Female");
		sexOptions.put("Male", "Male");
		sexOptions.put("Pooled", "Pooled");
		sexOptions.put("", "All");

		textScopeOptions.put("Title", "In Title");
		textScopeOptions.put("Description", "In Description");
		
		methodOptions.put("", "All");
		methodOptions.put("transcription profiling by array", "transcription profiling by array");
		methodOptions.put("RNA-seq", "RNA-seq");

		rnaseqTypeOptions.put("bulk RNA-seq", "bulk RNA-seq");
		rnaseqTypeOptions.put("single cell RNA-seq", "single cell RNA-seq");
		rnaseqTypeOptions.put("spatial RNA-seq", "spatial RNA-seq");
	}
	
    //--------------------//
    // accessors
    //--------------------//

	public Map<String,String> getSexOptions() {
		return sexOptions;
	}

	public List<String> getVariableFilter() {
		return variableFilter;
	}

	public List<String> getStudyTypeFilter() {
		return studyTypeFilter;
	}

	public Map<Integer, String> getTheilerStages() {
		return theilerStages;
	}

	public void setTheilerStages(Map<Integer, String> theilerStages) {
		this.theilerStages = theilerStages;
	}

	public String getStrain() {
		return strain;
	}

	public void setStrain(String strain) {
		this.strain = strain;
	}

	public Map<String, String> getAges() {
		return ages;
	}

	public void setAges(Map<String, String> ages) {
		this.ages = ages;
	}

	public Map<String,String> getMethodOptions() {
		return methodOptions;
	}

	public Map<String,String> getRnaseqTypeOptions() {
		return rnaseqTypeOptions;
	}

	public Map<String,String> getTextScopeOptions() {
		return textScopeOptions;
	}

	public List<Integer> getTheilerStage() {
		return theilerStage;
	}
	public void setTheilerStage(List<Integer> theilerStage) {
		this.theilerStage = theilerStage;
	}
	public void setTheilerStage(Integer theilerStage) {
		if (theilerStage != null) {
			List<Integer> stages = new ArrayList<Integer>();
			stages.add(theilerStage);
			this.theilerStage = stages;
		}
	}

	//
	public List<String> getRnaseqType() {
		return rnaseqType;
	}
	public void setRnaseqType(List<String> rnaseqType) {
		this.rnaseqType = rnaseqType;
	}
	public void setRnaseqType(String rnaseqType) {
		if ((rnaseqType != null) && (rnaseqType.length() > 0)) {
			List<String> t = new ArrayList<String>();
			t.add(rnaseqType);
			this.rnaseqType = t;
		}
	}
	//

	public List<String> getAge() {
		return age;
	}
	public void setAge(List<String> age) {
		this.age = age;
	}
	public void setAge(String age) {
		if ((age != null) && (age.length() > 0)) {
			List<String> t = new ArrayList<String>();
			t.add(age);
			this.age = t;
		}
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

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getMutatedIn() {
		return mutatedIn;
	}
	public void setMutatedIn(String mutatedIn) {
		this.mutatedIn = mutatedIn;
	}

	public String getMutantAlleleId() {
		return mutantAlleleId;
	}

	public void setMutantAlleleId(String mutantAlleleId) {
		this.mutantAlleleId = mutantAlleleId;
	}

	public List<String> getMethod() {
		return method;
	}
	public void setMethod(List<String> method) {
		this.method = method;
	}
	public void setMethod(String method) {
		if ((method != null) && (method.length() > 0)) {
			List<String> t = new ArrayList<String>();
			t.add(method);
			this.method = t;
		}
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

    // convenience method to see if we need to do a text search against title 
    public boolean searchTitle() {
    	if ((text != null) && (text.length() > 0) && (textScope != null)) {
    		return textScope.contains("Title");
    	}
    	return false;
    }
    // convenience method to see if we need to do a text search against description
    public boolean searchDescription() {
    	if ((text != null) && (text.length() > 0) && (textScope != null)) {
    		return textScope.contains("Description");
    	}
    	return false;
    }

    public List<String> getTextScope() {
        return textScope;
    }
    public void setTextScope(List<String> textScope) {
        this.textScope = textScope;
    }
    public void setTextScopeDefault() {
    	this.textScope.addAll(this.textScopeOptions.keySet());
    }

	public List<String> getVariable() {
		return variable;
	}
	public void setVariable(List<String> variable) {
		this.variable = variable;
	}

    public String getArrayExpressID() {
		return arrayExpressID;
	}

	public void setArrayExpressID(String arrayExpressID) {
		this.arrayExpressID = arrayExpressID;
	}

	public String getExperimentKey() {
		return experimentKey;
	}

	public void setExperimentKey(String experimentKey) {
		this.experimentKey = experimentKey;
	}

	public String getRelevancy() {
		return relevancy;
	}

	public void setRelevancy(String relevancy) {
		this.relevancy = relevancy;
	}

	public void setVariableFilter(List<String> variableFilter) {
		this.variableFilter = variableFilter;
	}

	public void setStudyTypeFilter(List<String> studyTypeFilter) {
		this.studyTypeFilter = studyTypeFilter;
	}

	@Override
	public String toString() {
		return "GxdHtQueryForm [age=" + age + ", theilerStage=" + theilerStage + ", method=" + method + ", rnaseqType=" + rnaseqType + ", mutatedIn="
				+ mutatedIn + ", mutantAlleleId=" + mutantAlleleId + ", sex=" + sex + ", structure=" + structure + ", structureID=" + structureID + ", text="
				+ text + ", textScope=" + textScope + "]";
	}

    //--- private methods ---//
    
	private void populateAges() {
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
	}

	private void populateStages() {
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
		theilerStages.put(27, "TS 27 (newborn)");
		theilerStages.put(28, "TS 28 (postnatal)");
	}
}
