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

	private String ageUnit = ANY_AGE;
	private String ageRange = "";
	private List<Integer> theilerStage = new ArrayList<Integer>();
	private List<String> variable = new ArrayList<String>();
	private List<String> variableFilter = new ArrayList<String>();
	private List<String> studyTypeFilter = new ArrayList<String>();
	private List<String> method =  new ArrayList<String>();
	private List<String> methodFilter = new ArrayList<String>();
	private String referenceID = "";
	private String mutatedIn = "";
	private String mutantAlleleId = "";
	private String relevancy = "";
	private String sex = "";
	private String structure = "";
	private String structureID = "";
	private String cellType = "";
	private String cellTypeID = "";
	private List<String> cellTypeFilter = new ArrayList<String>();
	private String text = "";
	private String arrayExpressID = "";
	private String experimentKey = "";
	private String strain = "";
	private List<String> textScope = new ArrayList<String>();
	private Map<String,String> sexOptions = new LinkedHashMap<String,String>();
	private Map<String,String> textScopeOptions = new LinkedHashMap<String,String>();
	private Map<String,String> methodOptions = new LinkedHashMap<String,String>();
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
		//age.add(ANY_AGE);
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
		methodOptions.put("bulk RNA-seq", "bulk RNA-seq");
		methodOptions.put("single cell RNA-seq", "single cell RNA-seq");
		methodOptions.put("spatial RNA-seq", "spatial RNA-seq");
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

	public String getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(String referenceID) {
		this.referenceID = referenceID;
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
	//

	public String getAgeUnit() {
		return ageUnit;
	}
	public void setAgeUnit(String ageUnit) {
		this.ageUnit = ageUnit;
	}
	public String getAgeRange() {
		return ageRange;
	}
	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
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

	public String getCellType() {
		return cellType;
	}
	public void setCellType(String cellType) {
		this.cellType = cellType;
	}

	public String getCellTypeID() {
		return cellTypeID;
	}
	public void setCellTypeID(String cellTypeID) {
		this.cellTypeID = cellTypeID;
	}

	public List<String> getCellTypeFilter() {
		return cellTypeFilter;
	}
	public void setCellTypeFilter(List<String> cellTypeFilter) {
		this.cellTypeFilter = cellTypeFilter;
	}
	public void setCellTypeFilter(String cellTypeFilter) {
		if ((cellTypeFilter != null) && (cellTypeFilter.length() > 0)) {
			List<String> t = new ArrayList<String>();
			t.add(cellTypeFilter);
			this.cellTypeFilter = t;
		}
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

	public List<String> getMethodFilter() {
		return methodFilter;
	}
	public void setMethodFilter(List<String> methodFilter) {
		this.methodFilter = methodFilter;
	}
	public void setMethodFilter(String methodFilter) {
		if ((methodFilter != null) && (methodFilter.length() > 0)) {
			List<String> t = new ArrayList<String>();
			t.add(methodFilter);
			this.methodFilter = t;
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
		return "GxdHtQueryForm [ageUnit=" + ageUnit + ", ageRange=" + ageRange
				+ ", theilerStage=" + theilerStage + ", method=" + method + ", mutatedIn="
				+ mutatedIn + ", mutantAlleleId=" + mutantAlleleId + ", sex=" + sex 
				+ ", structure=" + structure + ", structureID=" + structureID 
				+ ", cellType=" + cellType + ", cellTypeID=" + cellTypeID 
				+ ", text=" + text + ", textScope=" + textScope 
				+ ", referenceID=" + referenceID 
				+ "]";
	}

    //--- private methods ---//
    
	private void populateAges() {
		ages.put(ANY_AGE, "Choose age prefix");
		ages.put("Ed", "Embryonic day");
		ages.put("Pd", "Postnatal day");
		ages.put("Pw", "Postnatal week");
		ages.put("Pm", "Postnatal month");
		ages.put("Py", "Postnatal year");
		ages.put("A", "Adult");
		ages.put("N", "Newborn");
		ages.put("E", "Embryonic (all ages)");
		ages.put("P", "Postnatal (all ages)");
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
