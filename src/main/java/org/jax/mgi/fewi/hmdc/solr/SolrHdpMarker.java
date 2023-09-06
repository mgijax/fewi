package org.jax.mgi.fewi.hmdc.solr;

import java.util.ArrayList;
import java.util.List;

public class SolrHdpMarker implements SolrHdpEntityInterface
{
	String markerKey;
	String symbol = new String();
	String mgiId = new String();
	String name = new String();
	String type = new String();
	String organism = new String();
	String homologeneId = new String();
	String homologySource = new String();
	Integer homologyClusterKey = 0;
	String location = new String();
	String coordinate = new String();
	String coordinateBuild = new String();
	String markerId = new String();
	Integer gridClusterKey = 0;
	Integer allRefCount = 0;
	Integer diseaseRefCount = 0;
	Integer imsrCount = 0;
	List<String> disease;
	List<String> mouseSystem = new ArrayList<String>();
	List<String> humanSystem = new ArrayList<String>();
	List<String> filterableFeatureType = new ArrayList<String>();

	public String getHomologySource() { return homologySource; }
	public void setHomologySource(String homologySource) {
		this.homologySource = homologySource;
	}
	public String getMarkerKey() {return markerKey;}
	public void setMarkerKey(String markerKey) {
		this.markerKey = markerKey;
	}
	public String getSymbol() {return symbol;}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	// homology cluster key is distinct from grid cluster key, as it is
	// used to link to homology cluster pages.  We cannot use the grid
	// cluster key for this purpose, as there are markers we want to link
	// from the Genes tab which do not appear on the grid.
	public Integer getHomologyClusterKey() { return homologyClusterKey; }
	public void setHomologyClusterKey(Integer homologyClusterKey) {
		this.homologyClusterKey = homologyClusterKey;
	}
	public Integer getGridClusterKey() { return gridClusterKey; }
	public void setGridClusterKey(Integer gridClusterKey) {
		this.gridClusterKey = gridClusterKey;
	}
	public String getMgiId() {
		return mgiId;
	}
	public void setMgiId(String mgiId) {
		this.mgiId = mgiId;
	}
	public String getMarkerId() {
		return markerId;
	}
	public void setMarkerId(String markerId) {
		this.mgiId = markerId;
	}
	public List<String> getFilterableFeatureType() {
		return filterableFeatureType;
	}
	public void setFilterableFeatureType(List<String> filterableFeatureType) {
		this.filterableFeatureType = filterableFeatureType;
	}
	public String getType() {return type;}
	public void   setType(String type) {
		this.type = type;
	}
	public String getOrganism() {return organism;}
	public void   setOrganism(String organism) {
		this.organism = organism;
	}
	public String getHomologeneId() {return homologeneId;}
	public void   setHomologeneId(String homologeneId) {
		this.homologeneId = homologeneId;
	}
	public String getLocation() {return location;}
	public void   setLocation(String location) {
		this.location = location;
	}
	public String getName() {return name;}
	public void   setName(String name) {
		this.name = name;
	}
	public String getCoordinate() {return coordinate;}
	public void   setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
	public String getCoordinateBuild() {return coordinateBuild;}
	public void   setCoordinateBuild(String coordinateBuild) {
		this.coordinateBuild = coordinateBuild;
	}
	public List<String> getDisease() {return disease;}
	public void   setDisease(List<String> disease) {
		this.disease = disease;
	}
	public Integer getAllRefCount() {
		return allRefCount;
	}
	public void setAllRefCount(Integer allRefCount) {
		this.allRefCount = allRefCount;
	}
	public Integer getDiseaseRefCount() {
		return diseaseRefCount;
	}
	public void setDiseaseRefCount(Integer diseaseRefCount) {
		this.diseaseRefCount = diseaseRefCount;
	}
	public Integer getImsrCount() {
		return imsrCount;
	}
	public void setImsrCount(Integer imsrCount) {
		this.imsrCount = imsrCount;
	}
	public List<String> getMouseSystem() {
		return mouseSystem;
	}
	public void setMouseSystem(List<String> mouseSystem) {
		this.mouseSystem = mouseSystem;
	}
	public List<String> getHumanSystem() {
		return humanSystem;
	}
	public void setHumanSystem(List<String> humanSystem) {
		this.humanSystem = humanSystem;
	}
}
