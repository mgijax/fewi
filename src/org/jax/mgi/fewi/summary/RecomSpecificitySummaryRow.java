package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.AlleleSystemAssayResult;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.config.ContextLoader;

import javax.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a AlleleSystemAssayResult;  represents on row in summary
 */
public class RecomSpecificitySummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger
      = LoggerFactory.getLogger(RecomSpecificitySummaryRow.class);

	// encapsulated row object
	private AlleleSystemAssayResult alleleSystemAssayResult;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

    public RecomSpecificitySummaryRow () {}

    public RecomSpecificitySummaryRow (AlleleSystemAssayResult asay) {
    	this.alleleSystemAssayResult = asay;
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getStructure() {
    	return alleleSystemAssayResult.getStructure();
    }
    public String getAssayedAge() {
    	return alleleSystemAssayResult.getAge();
    }
    public String getLevel() {
    	return alleleSystemAssayResult.getLevel();
    }
    public String getPattern() {
    	return alleleSystemAssayResult.getPattern();
    }
    public String getSource() {
    	return "<a href='" + fewiUrl + "reference/"
          + alleleSystemAssayResult.getJnumID()
          + "'>" + alleleSystemAssayResult.getJnumID() + "</a>";
    }
    public String getResultNotes() {
    	return alleleSystemAssayResult.getResultNote();
    }
    public String getAllelicComp() {
        String convertedAllComp = new String();
        try {
          NotesTagConverter ntc = new NotesTagConverter();
          convertedAllComp = ntc.convertNotes(alleleSystemAssayResult.getAllelicComposition(), '|');
		}catch (Exception e) {}
        return convertedAllComp;
    }
    public String getSex() {
    	return alleleSystemAssayResult.getSex();
    }
    public String getSpecimenNote() {
    	return alleleSystemAssayResult.getSpecimenNote();
    }
    public String getAssayType() {
    	return alleleSystemAssayResult.getAssayType();
    }
    public String getReporterGene() {
    	return alleleSystemAssayResult.getReporterGene();
    }
    public String getDetectionMethod() {
    	return alleleSystemAssayResult.getDetectionMethod();
    }
    public String getAssayNote() {
    	return alleleSystemAssayResult.getAssayNote();
    }



}
