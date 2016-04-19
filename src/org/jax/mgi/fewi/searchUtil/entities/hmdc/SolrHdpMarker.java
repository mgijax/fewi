package org.jax.mgi.fewi.searchUtil.entities.hmdc;

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
	List<String> system;

	public String getHomologySource() { return homologySource; }
	public void setHomologySource(String homologySource) {
		this.homologySource = homologySource;
	}

	public String getMarkerKey() {return markerKey;}
	public void setMarkerKey(String markerKey) {
		this.markerKey = markerKey;
	}

	// symbol
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

	// marker MGI ID
	public String getMgiId() {
		return mgiId;
	}
	public void setMgiId(String mgiId) {
		this.mgiId = mgiId;
	}

	// marker ID
	public String getMarkerId() {
		return markerId;
	}
	public void setMarkerId(String markerId) {
		this.mgiId = markerId;
	}

	// type
	public String getType() {return type;}
	public void   setType(String type) {
		this.type = type;
	}

	// organism
	public String getOrganism() {return organism;}
	public void   setOrganism(String organism) {
		this.organism = organism;
	}

	// homologeneID
	public String getHomologeneId() {return homologeneId;}
	public void   setHomologeneId(String homologeneId) {
		this.homologeneId = homologeneId;
	}

	// location
	public String getLocation() {return location;}
	public void   setLocation(String location) {
		this.location = location;
	}

	// name
	public String getName() {return name;}
	public void   setName(String name) {
		this.name = name;
	}

	// coordinates
	public String getCoordinate() {return coordinate;}
	public void   setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	// coordinate build
	public String getCoordinateBuild() {return coordinateBuild;}
	public void   setCoordinateBuild(String coordinateBuild) {
		this.coordinateBuild = coordinateBuild;
	}

	// disease
	public List<String> getDisease() {return disease;}
	public void   setDisease(List<String> disease) {
		this.disease = disease;
	}

	// system
	public List<String> getSystem() {return system;}
	public void   setSystem(List<String> system) {
		if (system == null) {
			system = new ArrayList<String>(); 
		}
		this.system = system;
	}

	// all ref count
	public Integer getAllRefCount() {
		return allRefCount;
	}
	public void setAllRefCount(Integer allRefCount) {
		this.allRefCount = allRefCount;
	}

	// disease ref count
	public Integer getDiseaseRefCount() {
		return diseaseRefCount;
	}
	public void setDiseaseRefCount(Integer diseaseRefCount) {
		this.diseaseRefCount = diseaseRefCount;
	}

	// IMSR count
	public Integer getImsrCount() {
		return imsrCount;
	}
	public void setImsrCount(Integer imsrCount) {
		this.imsrCount = imsrCount;
	}
}
