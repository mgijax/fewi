package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.searchUtil.SearchConstants;

/*-------*/
/* class */
/*-------*/

public class MarkerQueryForm 
{
	public static boolean initialized=false;
	public static Map<String,String> markerTypeKeyToDisplayMap;
	
	
    //--------------------//
    // instance variables
    //--------------------//
    private String nomen;
    private String phenotype;
    private String go;
    private List<String> goVocab;
    private String interpro;
    private List<String> featureType;
    private List<String> mcv;
    private List<String> chromosome;
    private String coordinate;
    private String coordUnit;
    private String cm;
    private String startMarker;
    private String endMarker;
    private String markerID;
    
    // for links
    private String refKey;
    private String initialSort;
    private String initialDir;
    
    // Constants
    public static String CHROMOSOME_ANY="any";
    public static String COORD_UNIT_MBP="mbp";
    
    private final List<String> coordUnitDisplay = new ArrayList<String>();
    private final Map<String,String> goVocabDisplay = new LinkedHashMap<String,String>();
    
    public MarkerQueryForm()
    {
    	// any defaults
    	coordUnitDisplay.add("bp");
    	coordUnitDisplay.add("Mbp");
    	
    	goVocabDisplay.put(SearchConstants.GO_FUNCTION_TERM,"Molecular Function");
    	goVocabDisplay.put(SearchConstants.GO_PROCESS_TERM,"Biological Process");
    	goVocabDisplay.put(SearchConstants.GO_COMPONENT_TERM,"Cellular Component");
    	
    	goVocab = new ArrayList<String>(goVocabDisplay.keySet());
    }
	
	public static void setMarkerTypeKeyToDisplayMap(Map<String,String> markerTypeKeyToDisplayMap)
	{
		MarkerQueryForm.markerTypeKeyToDisplayMap=markerTypeKeyToDisplayMap;
	}
	
    // for the pheno system popup values
    public Map<String,String> getPhenoSystemWidgetValues()
    {
    	return FormWidgetValues. getPhenoSystemWidgetValuesAll();
    }
	
	// converts mcv keys submitted to displayable text values
	public List<String> getMcvDisplay()
	{
		List<String> displayValues = new ArrayList<String>();
		for(String key : this.getMcv())
		{
			if(MarkerQueryForm.markerTypeKeyToDisplayMap.containsKey(key))
			{
				displayValues.add(MarkerQueryForm.markerTypeKeyToDisplayMap.get(key));
			}
		}
		return displayValues;
	}
	
    public List<String> getCoordUnitDisplay()
    {
    	return coordUnitDisplay;
    }
    
    public Map<String,String> getGoVocabDisplay()
    {
    	return goVocabDisplay;
    }
    
    public String getGoVocabYSF()
    {
    	if(goVocab!=null && goVocab.size()>0 && goVocab.size()<3)
    	{
    		List<String> matchingVocabs = new ArrayList<String>();
    		for(String key : goVocab)
    		{
    			if(this.goVocabDisplay.containsKey(key))
    			{
    				matchingVocabs.add(this.goVocabDisplay.get(key));
    			}
    		}
    		return StringUtils.join(matchingVocabs," and ");
    	}
    	return "";
    }
	
    //--------------------//
    // accessors
    //--------------------//
	public String getMarkerID() {
		return markerID;
	}
	public String getNomen() {
		return nomen;
	}
	public List<String> getFeatureType() {
		return featureType;
	}
	public List<String> getChromosome() {
		return chromosome;
	}
	public String getCoordinate() {
		return coordinate;
	}
	public String getCoordUnit() {
		return coordUnit;
	}
	public String getCm() {
		return cm;
	}
	public void setMarkerID(String markerID) {
		this.markerID = markerID;
	}
	public void setNomen(String nomen) {
		this.nomen = nomen;
	}
	public void setFeatureType(List<String> featureType) {
		this.featureType = featureType;
	}
	
	public List<String> getMcv() {
		return mcv;
	}
	public void setMcv(List<String> mcv) {
		this.mcv = mcv;
	}
	public void setChromosome(List<String> chromosome) {
		this.chromosome = chromosome;
	}
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
	public void setCoordUnit(String coordUnit) {
		this.coordUnit = coordUnit;
	}
	public void setCm(String cm) {
		this.cm = cm;
	}
	public String getStartMarker() {
		return startMarker;
	}
	public String getEndMarker() {
		return endMarker;
	}
	public void setStartMarker(String startMarker) {
		this.startMarker = startMarker;
	}
	public void setEndMarker(String endMarker) {
		this.endMarker = endMarker;
	}
	
	public String getGo() {
		return go;
	}
	public String getInterpro() {
		return interpro;
	}
	public void setGo(String go) {
		this.go = go;
	}
	public List<String> getGoVocab() {
		return goVocab;
	}
	public void setGoVocab(List<String> goVocab) {
		this.goVocab = goVocab;
	}
	public void setInterpro(String interpro) {
		this.interpro = interpro;
	}
	public String getPhenotype() {
		return phenotype;
	}
	public void setPhenotype(String phenotype) {
		this.phenotype = phenotype;
	}
	
	public String getRefKey() {
		return refKey;
	}

	public void setRefKey(String refKey) {
		this.refKey = refKey;
	}

	public String getInitialSort() {
		return initialSort;
	}

	public void setInitialSort(String initialSort) {
		this.initialSort = initialSort;
	}

	public String getInitialDir() {
		return initialDir;
	}

	public void setInitialDir(String initialDir) {
		this.initialDir = initialDir;
	}

	// boolean accessors
	public boolean getHasChromosome()
	{
		// has values, but not if there is one, and it's "any"
		return chromosome!=null && chromosome.size()>0 
				&& !(chromosome.size()==1 && chromosome.contains(MarkerQueryForm.CHROMOSOME_ANY));
	}
	
	@Override
	public String toString() {
		return "MarkerQueryForm [nomen=" + nomen + ", featureType="
				+ featureType + ", chromosome=" + chromosome + ", coordinate="
				+ coordinate + ", coordUnit=" + coordUnit + ", cm=" + cm + "]";
	}
}
