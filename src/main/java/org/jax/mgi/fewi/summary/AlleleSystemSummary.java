package org.jax.mgi.fewi.summary;

import org.jax.mgi.fe.datamodel.AlleleSystem;

/** wrapper around an AlleleSystem object to expose only certain data
 * needed for a recombinase summary page.  This will aid in efficient
 * conversion to JSON notation and transportation of data across the
 * wire.  (We won't serialize more data than needed by the page.)
 * @author jsb
 */
public class AlleleSystemSummary {
	//-------------------
	// instance variables
	//-------------------

	private AlleleSystem alleleSystem;

    public AlleleSystemSummary (AlleleSystem alleleSystem) {
    	this.alleleSystem = alleleSystem;
    	return;
    }

    public int getAlleleSystemKey() {
    	return this.alleleSystem.getAlleleSystemKey();
    }
    
    public String getSystem() {
    	return this.alleleSystem.getSystem();
    }
}
