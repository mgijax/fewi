package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fe.datamodel.GxdLitAssayTypeAgePair;
import org.jax.mgi.fe.datamodel.GxdLitIndexRecord;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.forms.GxdLitQueryForm;
import org.jax.mgi.fewi.util.Highlighter;


/**
 * wrapper around a foo;  represents on row in summary
 */
public class GxdLitReferenceSummaryRow {

	//-------------------
	// instance variables
	//-------------------

	// encapsulated row object
	private GxdLitIndexRecord record;
	
	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

    private List<String> ages = new  ArrayList <String> ();
    private List<String> assayTypes = new  ArrayList <String> ();

    private Highlighter textHL = new Highlighter(null);
    
	//-------------
	// constructors
	//-------------

    public GxdLitReferenceSummaryRow (GxdLitIndexRecord record, GxdLitQueryForm queryForm, Highlighter textHL) {
    	this.textHL = textHL;
    	this.ages = queryForm.getAge();
    	this.assayTypes = queryForm.getAssayType();
    	this.record = record;
    	
    	return;
    }


    
    
    public List<GxdLitAssayTypeAgePair> getValidPairs() {
    	Boolean anyAges = Boolean.FALSE;
    	Boolean anyAssayTypes = Boolean.FALSE;
    	
    	if (!ages.isEmpty()) {
	    	for (String age: ages) {
	    		if (age.endsWith("ANY")) {
	    			anyAges = Boolean.TRUE;
	    		}
	    	}
    	}
    	else {
    		anyAges = Boolean.TRUE;
    	}
    	
    	if (! assayTypes.isEmpty()) {
	    	for (String type: assayTypes) {
	    		if (type.equals("ANY")) {
	    			anyAssayTypes = Boolean.TRUE;
	    		}
	    	}
    	}
    	else {
    		anyAssayTypes = Boolean.TRUE;
    	}
    	
    	// No Restrictions on Ages or AssayTypes, count everything
    	if (anyAssayTypes && anyAges) {
        	return record.getPairs();    		
    	}
    	
    	else if (anyAssayTypes) {
    		List <GxdLitAssayTypeAgePair> outList = new ArrayList<GxdLitAssayTypeAgePair> ();
    		for (GxdLitAssayTypeAgePair pair: record.getPairs()) {
    			for (String age: ages) {
    				if (pair.getAge().equals(age)) {
    					outList.add(pair);
    				}
    			}
    		}
    		return outList;
    	}

    	else if (anyAges) {
    		List <GxdLitAssayTypeAgePair> outList = new ArrayList<GxdLitAssayTypeAgePair> ();
    		for (GxdLitAssayTypeAgePair pair: record.getPairs()) {
    			for (String type: assayTypes) {
    				if (pair.getAssayType().equals(type)) {
    					outList.add(pair);
    				}
    			}
    		}
    		return outList;
    	}
    	
    	else {
    		List <GxdLitAssayTypeAgePair> outList = new ArrayList<GxdLitAssayTypeAgePair> ();
    		for (GxdLitAssayTypeAgePair pair: record.getPairs()) {
    			for (String type: assayTypes) {
    				for (String age: ages) {
    					if (pair.getAssayType().equals(type) && pair.getAge().equals(age)) {
    						outList.add(pair);
    					}
    				}
    			}
    		}
    		return outList;
    	}
    	
    }
    
    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getCount() {
    	
    	Boolean anyAges = Boolean.FALSE;
    	Boolean anyAssayTypes = Boolean.FALSE;
    	int count = 0;
    	
    	for (String age: ages) {
    		if (age.endsWith("ANY")) {
    			anyAges = Boolean.TRUE;
    		}
    	}
    	
    	for (String type: assayTypes) {
    		if (type.equals("ANY")) {
    			anyAssayTypes = Boolean.TRUE;
    		}
    	}
    	
    	// No Restrictions on Ages or AssayTypes, count everything
    	if (anyAssayTypes && anyAges) {
        	count = record.getPairs().size();    		
    	}
    	
    	// There are restrictions on ages only, iterate and count appropriately.
    	
    	else if (anyAssayTypes) {
    		for (GxdLitAssayTypeAgePair pair: record.getPairs()) {
    			for (String age: ages) {
    				if (pair.getAge().equals(age)) {
    					count ++;
    				}
    			}
    		}
    	}
    	
    	// There are restrictions on assayTypes only, iterate and count appropriately.
    	
    	else if (anyAges) {
    		for (GxdLitAssayTypeAgePair pair: record.getPairs()) {
    			for (String type: assayTypes) {
    				if (pair.getAssayType().equals(type)) {
    					count ++;
    				}
    			}
    		}
    	}
    	
    	else {
    		for (GxdLitAssayTypeAgePair pair: record.getPairs()) {
    			for (String type: assayTypes) {
    				for (String age: ages) {
    					if (pair.getAssayType().equals(type) && pair.getAge().equals(age)) {
    						count ++;
    					}
    				}
    			}
    		}
    	}

    	return "" + count;
    	
    }
    
    public String getJnum() {
    	return record.getJnumId();
    }
    
    public String getLongCitation () {
    	
    	// Check to see if a highlighter was created.
    	
    	if (textHL == null) {
    		return record.getLongCitation();
    	}
    	
    	// Otherwise return a highlighted version of long citation
    	
    	return textHL.highLight(record.getLongCitation());
    }
    
    public Boolean getIsFullyCoded () {
    	return record.isFullyCoded();
    }
    
    public Integer getFullyCodedAssayCount() {
    	return record.getFullCodedAssayCount();
    }
    
    public Integer getFullyCodedResultCount() {
    	return record.getFullCodedResultCount();
    }
    
    public String getIndexKey() {
    	return "" + record.getIndexKey();
    }
    
    public String getComments() {
    	return record.getComments();
    }
    
	public void setTextHL(Highlighter highlighter) {
		this.textHL = highlighter;
	}
    
}
