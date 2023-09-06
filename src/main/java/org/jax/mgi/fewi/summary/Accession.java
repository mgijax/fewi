package org.jax.mgi.fewi.summary;

import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fewi.searchUtil.ObjectTypes;

/* Is: a representation of an accessionable object
 * Does: collects fields for use through the AccessionController
 */
public class Accession {
	private String objectType;
	private String displayType;
	private String displayID;
	private String logicalDB;
	private Integer objectKey;
	private String description;

	/*** constructors ***/
	
	public Accession() {}
	
	public Accession(String objectType, String displayType, String displayID, String logicalDB, Integer objectKey, String description) {
		this.objectType = objectType;
		this.displayType = displayType;
		this.displayID = displayID;
		this.logicalDB = logicalDB;
		this.objectKey = objectKey;
		this.description = description;
	}

	public Accession(String objectType, String displayType, String displayID, String logicalDB, int objectKey, String description) {
		this.objectType = objectType;
		this.displayType = displayType;
		this.displayID = displayID;
		this.logicalDB = logicalDB;
		this.objectKey = objectKey;
		this.description = description;
	}

	/*** getters ***/
	
	public String getDescription() {
		return description;
	}
	public String getDisplayID() {
		return displayID;
	}
	public String getDisplayType() {
		return displayType;
	}
	public String getLogicalDB() {
		return logicalDB;
	}
	public Integer getObjectKey() {
		return objectKey;
	}
	public String getObjectType() {
		return objectType;
	}

	/*** setters ***/

	public void setDescription(String description) {
		this.description = description;
	}
	public void setDisplayID(String displayID) {
		this.displayID = displayID;
	}
	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}
	public void setLogicalDB(String logicalDB) {
		this.logicalDB = logicalDB;
	}
	public void setObjectKey(Integer objectKey) {
		this.objectKey = objectKey;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	
	@Override
	public String toString() {
		return "Accession [displayID=" + displayID + ", logicalDB=" + logicalDB + ", objectType=" + objectType
				+ ", displayType=" + displayType + ", objectKey=" + objectKey + ", description=" + description + "]";
	}
}
