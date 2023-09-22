package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*-------*/
/* class */
/*-------*/

public class AlleleQueryForm
{
    //--------------------//
    // instance variables
    //--------------------//
    private String allKey;
    private String allIds;
    private String phenotype;
    private String nomen;
    private String markerId;
    private String refKey;
    private String jnumId;
    private String hasDO;
    private String isCellLine;
    private List<String> alleleType;
    private List<String> alleleSubType;
    private List<String> collection;
    private List<String> chromosome;
    private List<String> mutation;
    private String coordinate;
    private String coordUnit;
    private String cm;
    private String cyto;
    private String mutationInvolves;

    // Form Widget Values
    // collection will be set once dynamically and cached
    private static List<String> collectionValues=null;

    // molecular mutation types -- collection will be set once dynamically and cached
    private static List<String> mutationValues=null;

    // Constants
    public static String COORDINATE_ANY="any";
    public static String COORD_UNIT_MBP="mbp";

    private List<String> coordUnitDisplay = new ArrayList<String>();
    private Map<String,String> chromosomeDisplay = new LinkedHashMap<String,String>();
    private Map<String,String> alleleTypeDisplay = new LinkedHashMap<String,String>();
    // the allele subtypes are displayed in 3 separate groups
    private Map<String,String> subTypeGroup1 = new LinkedHashMap<String,String>();
    private Map<String,String> subTypeGroup2 = new LinkedHashMap<String,String>();
    private Map<String,String> subTypeGroup3 = new LinkedHashMap<String,String>();

    public AlleleQueryForm()
    {
    	// any defaults
    	coordUnitDisplay.add("bp");
    	coordUnitDisplay.add("Mbp");

    	chromosomeDisplay.put(COORDINATE_ANY,"ANY");
    	chromosomeDisplay.put("1","1");
    	chromosomeDisplay.put("2","2");
    	chromosomeDisplay.put("3","3");
    	chromosomeDisplay.put("4","4");
    	chromosomeDisplay.put("5","5");
    	chromosomeDisplay.put("6","6");
    	chromosomeDisplay.put("7","7");
    	chromosomeDisplay.put("8","8");
    	chromosomeDisplay.put("9","9");
    	chromosomeDisplay.put("10","10");
    	chromosomeDisplay.put("11","11");
    	chromosomeDisplay.put("12","12");
    	chromosomeDisplay.put("13","13");
    	chromosomeDisplay.put("14","14");
    	chromosomeDisplay.put("15","15");
    	chromosomeDisplay.put("16","16");
    	chromosomeDisplay.put("17","17");
    	chromosomeDisplay.put("18","18");
    	chromosomeDisplay.put("19","19");
    	chromosomeDisplay.put("X","X");
    	chromosomeDisplay.put("Y","Y");
    	chromosomeDisplay.put("MT","Mitochondrial");
    	chromosomeDisplay.put("XY","XY(pseudoautosomal)");
    	chromosomeDisplay.put("UN","Unknown");

    	chromosome = new ArrayList<String>(Arrays.asList(COORDINATE_ANY));


    	alleleTypeDisplay.put("Chemically induced (ENU)","Chemically induced (ENU)");
    	alleleTypeDisplay.put("Chemically induced (other)","Chemically induced (other)");
    	alleleTypeDisplay.put("Chemically and radiation induced","Chemically and radiation induced");
    	alleleTypeDisplay.put("Endonuclease-mediated","Endonuclease-mediated");
    	alleleTypeDisplay.put("Gene trapped","Gene trapped");
    	alleleTypeDisplay.put("QTL","QTL");
    	alleleTypeDisplay.put("Radiation induced","Radiation induced");
    	alleleTypeDisplay.put("Spontaneous","Spontaneous");
    	alleleTypeDisplay.put("Targeted","Targeted");
    	alleleTypeDisplay.put("Transgenic","Transgenic");
    	alleleTypeDisplay.put("Transposon induced","Transposon induced");

    	subTypeGroup1.put("Conditional ready","Conditional ready");
    	subTypeGroup1.put("Recombinase","Recombinase");
    	subTypeGroup1.put("RMCE-ready","RMCE-ready");

    	subTypeGroup2.put("Endonuclease","Endonuclease");
    	subTypeGroup2.put("Epitope tag","Epitope tag");
    	subTypeGroup2.put("Inserted expressed sequence","Inserted expressed sequence");
    	subTypeGroup2.put("Humanized sequence","Humanized sequence");
    	subTypeGroup2.put("Reporter","Reporter");
    	subTypeGroup2.put("Transposase","Transposase");
    	subTypeGroup2.put("Transposon concatemer","Transposon concatemer");
    	subTypeGroup2.put("Transactivator","Transactivator");

    	subTypeGroup3.put("Constitutively active","Constitutively active");
    	subTypeGroup3.put("Dominant negative","Dominant negative");
    	subTypeGroup3.put("Hypomorph","Hypomorph");
    	subTypeGroup3.put("Inducible","Inducible");
    	subTypeGroup3.put("Knockdown","Knockdown");
    	subTypeGroup3.put("Modified isoform(s)","Modified isoform(s)");
    	subTypeGroup3.put("Modified regulatory region","Modified regulatory region");
    	subTypeGroup3.put("Null/knockout","Null/knockout");
    	subTypeGroup3.put("No functional change","No functional change");
    }

    public List<String> getCoordUnitDisplay()
    {
    	return coordUnitDisplay;
    }

    public Map<String,String> getChromosomeDisplay()
    {
    	return chromosomeDisplay;
    }
    public Map<String,String> getAlleleTypeDisplay()
    {
    	return alleleTypeDisplay;
    }
    public Map<String,String> getSubTypeGroup1()
    {
    	return subTypeGroup1;
    }
    public Map<String,String> getSubTypeGroup2()
    {
    	return subTypeGroup2;
    }
    public Map<String,String> getSubTypeGroup3()
    {
    	return subTypeGroup3;
    }

    // for the pheno system popup values
    public Map<String,String> getPhenoSystemWidgetValues()
    {
    	return FormWidgetValues. getPhenoSystemWidgetValuesAll();
    }

    //--------------------//
    // accessors
    //--------------------//
	public String getAllKey() {
		return allKey;
	}
	public String getAllIds() {
		return allIds;
	}
	public List<String> getAlleleType() {
		return alleleType;
	}
	public List<String> getChromosome() {
		return chromosome;
	}
	public void setAllKey(String allKey) {
		this.allKey = allKey;
	}
	public void setAllIds(String allIds) {
		this.allIds = allIds;
	}
	public void setAlleleType(List<String> alleleType) {
		this.alleleType = alleleType;
	}

	public void setChromosome(List<String> chromosome) {
		this.chromosome = chromosome;
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
	public String getCyto() {
		return cyto;
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
	public void setCyto(String cyto) {
		this.cyto = cyto;
	}

	public List<String> getCollection() {
		return collection;
	}
	public void setCollection(List<String> collection) {
		this.collection = collection;
	}

	public List<String> getMutation() {
		return mutation;
	}
	public void setMutation(List<String> mutation) {
		this.mutation = mutation;
	}

	public String getPhenotype() {
		return phenotype;
	}
	public String getMarkerId() {
		return markerId;
	}
	public void setPhenotype(String phenotype) {
		this.phenotype = phenotype;
	}
	public void setMarkerId(String markerId) {
		this.markerId = markerId;
	}

	public String getNomen() {
		return nomen;
	}
	public void setNomen(String nomen) {
		this.nomen = nomen;
	}

	public String getRefKey() {
		return refKey;
	}
	public String getJnumId() {
		return jnumId;
	}
	public String getHasDO() {
		return hasDO;
	}
	public void setRefKey(String refKey) {
		this.refKey = refKey;
	}
	public void setJnumId(String jnumId) {
		this.jnumId = jnumId;
	}
	public void setHasDO(String hasDO) {
		this.hasDO = hasDO;
	}

	public List<String> getAlleleSubType() {
		return alleleSubType;
	}
	public void setAlleleSubType(List<String> alleleSubType) {
		this.alleleSubType = alleleSubType;
	}

	public String getIsCellLine() {
		return isCellLine;
	}
	public void setIsCellLine(String isCellLine) {
		this.isCellLine = isCellLine;
	}

	public String getMutationInvolves() {
		return mutationInvolves;
	}
	public void setMutationInvolves(String mutationInvolves) {
		this.mutationInvolves = mutationInvolves;
	}

	//=== form widget values
	public static List<String> getCollectionValues() {
		return AlleleQueryForm.collectionValues;
	}
	public static void setCollectionValues(List<String> collectionValues) {
		AlleleQueryForm.collectionValues = collectionValues;
	}

	public static List<String> getMutationValues() {
		return AlleleQueryForm.mutationValues;
	}
	public static void setMutationValues(List<String> mutationValues) {
		AlleleQueryForm.mutationValues = mutationValues;
	}

	// boolean accessors
	public boolean getHasChromosome()
	{
		// has values, but not if there is one, and it's "any"
		return chromosome!=null && chromosome.size()>0
				&& !(chromosome.size()==1 && chromosome.contains(AlleleQueryForm.COORDINATE_ANY));
	}

	@Override
	public String toString() {
		return "AlleleQueryForm [allKey=" + allKey + ", allIds=" + allIds
				+ ", phenotype=" + phenotype + ", nomen=" + nomen
				+ ", markerId=" + markerId + ", refKey=" + refKey + ", jnumId="
				+ jnumId + ", hasDO=" + hasDO + ", excludeCellLines="
				+ isCellLine + ", alleleType=" + alleleType
				+ ", alleleSubType=" + alleleSubType + ", collection="
				+ collection + ", chromosome=" + chromosome + ", coordinate="
				+ coordinate + ", coordUnit=" + coordUnit + ", cm=" + cm
				+ ", mutation=" + mutation + ", cyto=" + cyto + "]";
	}
}
