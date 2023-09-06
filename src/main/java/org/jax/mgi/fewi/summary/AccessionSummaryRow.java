package org.jax.mgi.fewi.summary;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.ObjectTypes;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.link.FewiLinker;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a foo;  represents on row in summary
 */
public class AccessionSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private final Logger logger = LoggerFactory.getLogger(AccessionSummaryRow.class);

	// encapsulated row object
	private final Accession acc;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

    private final IDLinker  linker = IDLinker.getInstance();
    
    private final FewiLinker  feLinker = FewiLinker.getInstance();
    
    private Boolean useKeyUrl = Boolean.FALSE;

	//-------------
	// constructors
	//-------------

    public AccessionSummaryRow (Accession acc) {
    	this.acc = acc;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getAccId() {
    	return acc.getDisplayID();
    }
    public String getLogicalDb() {
    	if (acc.getLogicalDB().equals("MGI") || acc.getLogicalDB().equals("BayGenomics") || acc.getLogicalDB().equals("Mouse Genome Project")) {
    		return acc.getLogicalDB();
    	}
    	else  {
    		return linker.getLinks(acc.getLogicalDB(), acc.getDisplayID());
    	}
    }
    public String getMgiLink() {
    	
    	String url = "";

    	// Figure out which object type to pass to the linker, so we can 
    	// reduce our overall if complexity.
    	String objectType = acc.getObjectType();
    	
    	if (objectType.equals("Vocabulary Term")) { 
    		objectType = acc.getDisplayType();
    		url = feLinker.getFewiIDLink(objectType, acc.getDisplayID());
    	}
    	
    	// Handle the old wi cases, but with ID
    	
    	else if (objectType.equals(ObjectTypes.DISEASE)) {
    		url = feLinker.getFewiIDLink(objectType, acc.getDisplayID());
    	}
    	else if (objectType.equals(ObjectTypes.HOMOLOGY)) {
    		url = feLinker.getFewiIDLink(objectType, acc.getDisplayID());
    	}
    	else if (objectType.equals(ObjectTypes.MARKER_CLUSTER)) {
    		url = feLinker.getFewiIDLink(objectType, acc.getDisplayID());
    	}
    	
    	// Handle the old wi cases.
    	
    	else if (objectType.equals(ObjectTypes.ASSAY) ||
    			objectType.equals(ObjectTypes.GO_CC) ||
    			objectType.equals(ObjectTypes.GO_BP) ||
    			objectType.equals(ObjectTypes.GO_MF) ||
    			objectType.equals(ObjectTypes.PROBECLONE) ||
    			objectType.equals(ObjectTypes.ANTIBODY) ||
    			objectType.equals(ObjectTypes.ANTIGEN)) {
    		url = feLinker.getFewiKeyLink(objectType, "" + acc.getObjectKey());
    	}
    	else {
    		logger.debug("Base case.");
    		if (! this.useKeyUrl) {
    			url = feLinker.getFewiIDLink(objectType, acc.getDisplayID());
    		}
    		else {
    			url = feLinker.getFewiKeyLink(objectType, "" + acc.getObjectKey());
    		}
    	}
    	if (! objectType.equals(ObjectTypes.INTERPRO)) {
	    if (objectType.equals(ObjectTypes.MARKER_CLUSTER)) {
		objectType = ObjectTypes.HOMOLOGY;
	    }
    		return "<a href=\"" + url + "\">" + "MGI " + objectType + " Detail" +  "</a>";
    	}
    	else {
    		return "";
    	}
    }
    public String getDisplayType() {
    	return acc.getDisplayType();
    }
    public String getDescription() {
    	return FormatHelper.superscript(acc.getDescription());
    }

    public void setUseKey() {
    	this.useKeyUrl = Boolean.TRUE; 
    }
    
    public String getObjectType() {
    	return acc.getObjectType();
    }

}
