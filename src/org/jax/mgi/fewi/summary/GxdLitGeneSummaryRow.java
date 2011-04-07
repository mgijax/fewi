package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.GxdLitIndexRecord;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.forms.GxdLitQueryForm;
import org.jax.mgi.fewi.util.Highlighter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a foo;  represents on row in summary
 */
public class GxdLitGeneSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(GxdLitGeneSummaryRow.class);

	// encapsulated row object
	private GxdLitIndexRecord record;

	private List<GxdLitReferenceSummaryRow> referenceRecords = new ArrayList<GxdLitReferenceSummaryRow> ();

	// For these objects we need access to the query form in order to generate counts
	private GxdLitQueryForm queryForm;
	
	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

    Highlighter textHL = null;

	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a foo to wrap
    private GxdLitGeneSummaryRow () {}

    public GxdLitGeneSummaryRow (GxdLitIndexRecord record, GxdLitQueryForm queryForm, Highlighter textHL) {
    	this.record = record;
    	this.textHL = textHL;
    	this.queryForm = queryForm;
    	GxdLitReferenceSummaryRow refRecord = new GxdLitReferenceSummaryRow(record, queryForm, textHL);
    	if (new Integer(refRecord.getCount()) > 0) {
    		referenceRecords.add(refRecord); 
    	}
    	
    	return;
    }


    public void addRecord(GxdLitIndexRecord record) {
    	GxdLitReferenceSummaryRow refRecord = new GxdLitReferenceSummaryRow(record, queryForm, textHL);
    	
    	if (new Integer(refRecord.getCount()) > 0) {
    		referenceRecords.add(refRecord); 
    	}
    }
    
    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getName() {
    	return record.getMarkerName();
    }
    
    public String getSymbol() {
    	return record.getMarkerSymbol();
    }
    
    public List<GxdLitReferenceSummaryRow> getReferenceRecords() {
    	return this.referenceRecords;
    }
    
}
