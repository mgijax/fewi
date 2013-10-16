package org.jax.mgi.fewi.forms;

import java.util.LinkedHashMap;
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
    private String mpHeader;
    private String termId;
    
    // Filter queries
    private String fGene;
    private String fHeader;

    private Map<String, String> organismOptions = new LinkedHashMap<String, String>();

    // Constants
    public static final String HUMAN = "human";
    public static final String MOUSE = "mouse";

    public DiseasePortalQueryForm()
    {
    	organismOptions.put(MOUSE, "Mouse");
		organismOptions.put(HUMAN, "Human");
    }

    //--------------------//
    // accessors
    //--------------------//

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
	public String getMpHeader() {
		return mpHeader;
	}
	public void setMpHeader(String mpHeader) {
		this.mpHeader = mpHeader;
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

	@Override
	public String toString() {
		return "DiseasePortalQueryForm [phenotypes=" + phenotypes + ", genes="
				+ genes + ", locations=" + locations + ", organism=" + organism
				+ ", gridClusterKey=" + gridClusterKey + ", mpHeader="
				+ mpHeader + ", termId=" + termId + ", fGene=" + fGene
				+ ", fHeader=" + fHeader + ", organismOptions="
				+ organismOptions + "]";
	}
	
	
}
