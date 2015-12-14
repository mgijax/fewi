package org.jax.mgi.fewi.searchUtil.entities;


/**
 * Represents a result from the driver autocomplete hunter.
 */
public class SolrDriverACResult
{
	private String driver;
	private String driverDisplay;

	public SolrDriverACResult(){}
	public SolrDriverACResult (String driver, String driverDisplay)
	{
		this.driver=driver;
		this.driverDisplay=driverDisplay;
	}

	public String getDriver() { return this.driver; }
	public void setDriver(String driver) { this.driver = driver; }

	public String getDriverDisplay() { return this.driverDisplay; }
	public void setDriverDisplay(String driverDisplay) { this.driverDisplay = driverDisplay; }

}
