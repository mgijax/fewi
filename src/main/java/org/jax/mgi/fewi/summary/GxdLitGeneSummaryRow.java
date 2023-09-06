package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fe.datamodel.GxdLitIndexRecord;
import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.MarkerSynonym;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.forms.GxdLitQueryForm;
import org.jax.mgi.fewi.util.Highlighter;


/**
 * wrapper around a foo;  represents on row in summary
 */
public class GxdLitGeneSummaryRow {

	//-------------------
	// instance variables
	//-------------------

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
    
    public List<String> getSynonyms() {
	Marker marker = record.getMarker();
	List<MarkerSynonym> markerSynonyms = marker.getSynonyms();
	List<String> synonyms = new ArrayList<String>();

	for (MarkerSynonym markerSynonym: markerSynonyms) {
		synonyms.add(markerSynonym.getSynonym());
	}

	return synonyms;
    }
}
