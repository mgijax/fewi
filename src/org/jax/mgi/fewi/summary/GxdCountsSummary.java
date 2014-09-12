package org.jax.mgi.fewi.summary;

/**
 * Hold counts for all three (or more) tabs on the gxd query summary
 * 
 * @author kstone
 * 
 */
public class GxdCountsSummary {

	private int resultsCount;
	private int assaysCount;
	private int genesCount;

	public int getResultsCount() {
		return resultsCount;
	}

	public int getAssaysCount() {
		return assaysCount;
	}

	public int getGenesCount() {
		return genesCount;
	}

	public void setResultsCount(int resultsCount) {
		this.resultsCount = resultsCount;
	}

	public void setAssaysCount(int assaysCount) {
		this.assaysCount = assaysCount;
	}

	public void setGenesCount(int genesCount) {
		this.genesCount = genesCount;
	}

}
