package org.jax.mgi.fewi.forms;


/*-------*/
/* class */
/*-------*/

public class HomologyQueryForm
{
    //--------------------//
    // instance variables
    //--------------------//
    private String symbol;
    private String clusterKey;
    private String organism;

    //--------------------//
    // constructor
    //--------------------//
    public HomologyQueryForm() {}

    //--------------------//
    // accessors
    //--------------------//

    public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

    public String getClusterKey() {
		return clusterKey;
	}

	public void setClusterKey(String clusterKey) {
		this.clusterKey = clusterKey;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	@Override
	public String toString() {
		return "HomologyQueryForm [symbol=" + symbol + ", clusterKey=" + clusterKey + ", organism=" + organism + "]";
	}
}
