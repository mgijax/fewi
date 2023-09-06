package org.jax.mgi.fewi.forms;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*-------*/
/* class */
/*-------*/

public class DiseasePortalQueryForm
{
    //--------------------//
    // instance variables
    //--------------------//
    private String phenotypes;
    private String genes;
    private String locations;
    private String organism = MOUSE;
    private String gridClusterKey;
    private String termHeader;
    private String termId;
    private String locationsFileName;
    private String enableVcfFilter;
    private String geneFileName;
    private List<String> featureTypeFilter;
    private int numDCol=100;
    
    // Filter queries
    private String fGene;
    private String fHeader;
    private String referseF;

    private Map<String, String> organismOptions = new LinkedHashMap<String, String>();

    // Constants
    public static final String HUMAN = "human";
    public static final String MOUSE = "mouse";
    
    // constants for file processing
    public static final String GENE_FILE_VAR = "geneFile";
    public static final String GENE_FILE_VAR_NAME = "geneFileName";
    public static final String LOCATIONS_FILE_VAR = "locationsFile";
    public static final String LOCATIONS_FILE_VAR_NAME = "locationsFileName";
    public static final String LOCATIONS_FILE_VAR_MOUSE_KEYS = "locationsFileMouseKeys";
    public static final String LOCATIONS_FILE_VAR_HUMAN_KEYS = "locationsFileHumanKeys";

    public static List<String> ACCEPTABLE_FILE_VARS;
    static
    {
    	ACCEPTABLE_FILE_VARS = Arrays.asList(GENE_FILE_VAR,LOCATIONS_FILE_VAR);
    }
    public static final String VCF_FILE_TYPE = "vcf";
    public static final String SINGLE_COL_TYPE = "singleCol";


    public DiseasePortalQueryForm()
    {
    	organismOptions.put(MOUSE, "Mouse");
		organismOptions.put(HUMAN, "Human");
    }

    //--------------------//
    // accessors
    //--------------------//

    public List<String> getFeatureTypeFilter() {
	return featureTypeFilter;
    }
    public void setFeatureTypeFilter(List<String> featureTypeFilter) {
	this.featureTypeFilter = featureTypeFilter;
    }

    public String getPhenotypes() {
		return phenotypes;
	}
	public void setPhenotypes(String phenotypes) {
		this.phenotypes = phenotypes;
	}
    public String getGenes() {
		return genes;
	}
	public void setGenes(String genes) {
		this.genes = genes;
	}

    public String getLocations() {
		return locations;
	}
	public void setLocations(String locations) {
		this.locations = locations;
	}

	public String getOrganism() {
		return organism;
	}
	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public String getGridClusterKey() {
		return gridClusterKey;
	}
	public void setGridClusterKey(String gridClusterKey) {
		this.gridClusterKey = gridClusterKey;
	}
	public String getTermHeader() {
		return termHeader;
	}
	public void setTermHeader(String termHeader) {
		this.termHeader = termHeader;
	}

	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}

	public Map<String, String> getOrganismOptions() {
		return organismOptions;
	}

	public void setOrganismOptions(Map<String, String> organismOptions) {
		this.organismOptions = organismOptions;
	}
	
	public String getLocationsFileName() {
		return locationsFileName;
	}

	public void setLocationsFileName(String locationsFileName) {
		this.locationsFileName = locationsFileName;
	}

	public String getGeneFileName() {
		return geneFileName;
	}

	public void setGeneFileName(String geneFileName) {
		this.geneFileName = geneFileName;
	}
	
	
	public boolean getHasLocationsQuery()
	{
		return (locations!=null && !locations.equals("")) || (locationsFileName!=null && !locationsFileName.equals(""));
	}

	public String getFGene() {
		return fGene;
	}

	public String getFHeader() {
		return fHeader;
	}
	public void setFGene(String fGene) {
		this.fGene = fGene;
	}

	public void setFHeader(String fHeader) {
		this.fHeader = fHeader;
	}
	
	/* convenience method for extracting an integer from a String param
	 */
	public void setNumDCol(String num) {
		try {
			if(num == null || num.equals("")) num = "0";
			int i = Integer.parseInt(num);
			this.numDCol = i;
		} catch (NumberFormatException e) {
			// just leave the default, if we don't have an integer
			// in the parameter string
		}
	}
	
	public void setNumDCol(int num) {
		this.numDCol = num;
	}
	
	public int getNumDCol() {
		return this.numDCol;
	}
	
	public String getReferseF() {
		return referseF;
	}

	public void setReferseF(String referseF) {
		this.referseF = referseF;
	}

	public String getEnableVcfFilter() {
		return enableVcfFilter;
	}

	public void setEnableVcfFilter(String enableVcfFilter) {
		this.enableVcfFilter = enableVcfFilter;
	}
	
	@Override
	public String toString() {
		return "DiseasePortalQueryForm [phenotypes=" + phenotypes + ", genes="
				+ genes + ", locations=" + locations + ", organism=" + organism
				+ ", gridClusterKey=" + gridClusterKey + ", termHeader="
				+ termHeader + ", termId=" + termId + ", fGene=" + fGene
				+ ", fHeader=" + fHeader + ", organismOptions="
				+ organismOptions + "]";
	}
}
