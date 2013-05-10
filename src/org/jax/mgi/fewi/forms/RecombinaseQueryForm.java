package org.jax.mgi.fewi.forms;


public class RecombinaseQueryForm {

	private String driver;
	private String id;
	private String alleleKey;
	private String system;
	private String systemKey;
	private String structure;

	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public String getSystemKey() {
		return systemKey;
	}
	public void setSystemKey(String systemKey) {
		this.systemKey = systemKey;
	}
	public String getAlleleKey() {
		return alleleKey;
	}
	public void setAlleleKey(String alleleKey) {
		this.alleleKey = alleleKey;
	}
	
	public void setStructure(String structure) {
		this.structure = structure;
	}
	public String getStructure() {
		return structure;
	}
	@Override
	public String toString() {
		return "RecombinaseQueryForm [driver=" + driver + ", id=" + id
				+ ", alleleKey=" + alleleKey + ", system=" + system
				+ ", structure=" + structure 
				+ ", systemKey=" + systemKey + "]";
	}
	
}
