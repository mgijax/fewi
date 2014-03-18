package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private String interpro;
    private List<String> featureType;
    private List<String> mcv;
    private List<String> chromosome;
    private String coordinate;
    private String coordUnit;
    private String cm;
    
    private String startMarker;
    private String endMarker;
    
    // Constants
    public static String CHROMOSOME_ANY="any";
    public static String COORD_UNIT_MBP="mbp";
    
    private List<String> coordUnitDisplay = new ArrayList<String>();

    public MarkerQueryForm()
    {
    	// any defaults
    	coordUnitDisplay.add("bp");
    	coordUnitDisplay.add("Mbp");
    }
	
	public static void setMarkerTypeKeyToDisplayMap(Map<String,String> markerTypeKeyToDisplayMap)
	{
		MarkerQueryForm.markerTypeKeyToDisplayMap=markerTypeKeyToDisplayMap;
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
	
    //--------------------//
    // accessors
    //--------------------//
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
	public void setInterpro(String interpro) {
		this.interpro = interpro;
	}
	public String getPhenotype() {
		return phenotype;
	}
	public void setPhenotype(String phenotype) {
		this.phenotype = phenotype;
	}
	@Override
	public String toString() {
		return "MarkerQueryForm [nomen=" + nomen + ", featureType="
				+ featureType + ", chromosome=" + chromosome + ", coordinate="
				+ coordinate + ", coordUnit=" + coordUnit + ", cm=" + cm + "]";
	}
}
