package org.jax.mgi.fewi.forms;


public class RecombinaseQueryForm {

	private String driver;
	private String id;
	private String system;
	private String systemKey;

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

	@Override
	public String toString() {
		return "RecombinaseQueryForm ["
				+ (driver != null ? "driver=" + driver + ", " : "")
				+ (id != null ? "id=" + id + ", " : "")
				+ (systemKey != null ? "systemKey=" + systemKey + ", " : "")
				+ (system != null ? "system=" + system : "") + "]";
	}
}
