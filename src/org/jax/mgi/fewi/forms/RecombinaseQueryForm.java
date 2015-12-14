package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.List;

public class RecombinaseQueryForm {

	private String driver;
	private String id;
	private String alleleKey;
	private String system;
	private String systemKey;
	private String structure;

	private String detected;
	private String notDetected;

	private List<String> inducer = new ArrayList<String>();
	private List<String> systemDetected = new ArrayList<String>();
	private List<String> systemNotDetected = new ArrayList<String>();

	// driver
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}

	// ID
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	// System
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}

	// System Key
	public String getSystemKey() {
		return systemKey;
	}
	public void setSystemKey(String systemKey) {
		this.systemKey = systemKey;
	}

	// Allele Key
	public String getAlleleKey() {
		return alleleKey;
	}
	public void setAlleleKey(String alleleKey) {
		this.alleleKey = alleleKey;
	}

	// Structure
	public void setStructure(String structure) {
		this.structure = structure;
	}
	public String getStructure() {
		return structure;
	}

	// Detected
	public void setDetected(String detected) {
		this.detected = detected;
	}
	public String getDetected() {
		return detected;
	}

	// Not Detected
	public void setNotDetected(String notDetected) {
		this.notDetected = notDetected;
	}
	public String getNotDetected() {
		return notDetected;
	}

	// Inducer
	public List<String> getInducer() {
		return inducer;
	}
	public void setInducer(List<String> inducer) {
		this.inducer = inducer;
	}

	// detected systems
	public List<String> getSystemDetected() {
		return systemDetected;
	}
	public void setSystemDetected(List<String> systemDetected) {
		this.systemDetected = systemDetected;
	}
	public List<String> getSystemNotDetected() {
		return systemNotDetected;
	}
	public void setSystemNotDetected(List<String> systemNotDetected) {
		this.systemNotDetected = systemNotDetected;
	}

	@Override
	public String toString() {
		return "RecombinaseQueryForm [driver=" + driver
				+ ", id=" + id
				+ ", alleleKey=" + alleleKey
				+ ", system=" + system
				+ ", structure=" + structure
				+ ", inducer=" + inducer
				+ ", detected=" + detected
				+ ", notDetected=" + notDetected
				+ ", systemDetected=" + systemDetected
				+ ", systemNotDetected=" + systemNotDetected
				+ ", systemKey=" + systemKey + "]";
	}

}
