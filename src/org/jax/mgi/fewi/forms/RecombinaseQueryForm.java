package org.jax.mgi.fewi.forms;


public class RecombinaseQueryForm {
	
	private String driver;
	private String system;

	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	
	@Override
	public String toString() {
		return "RecombinaseQueryForm ["
				+ (driver != null ? "driver=" + driver + ", " : "")
				+ (system != null ? "system=" + system : "") + "]";
	}	
}
