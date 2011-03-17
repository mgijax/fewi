package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.AlleleSystemAssayResult;
import mgi.frontend.datamodel.AlleleSystemAssayResultImagePane;
import mgi.frontend.datamodel.Image;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.util.FormatHelper;
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

    // structure
    public String getStructure() {
        return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getStructure() + "</span>";
    }

    // age
    public String getAssayedAge() {
        return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getAge() + "</span>";
    }

    // level
    public String getLevel() {
        return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getLevel() + "</span>";
    }

    // pattern
    public String getPattern() {
        return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getPattern() + "</span>";
    }

    // source
    public String getSource() {

        String refLink = "<a href='" + fewiUrl
          + "reference/" + alleleSystemAssayResult.getJnumID()
          + "'>" + alleleSystemAssayResult.getJnumID() + "</a>";

        StringBuffer figureLables = new StringBuffer();
        StringBuffer imageTags = new StringBuffer();
        AlleleSystemAssayResultImagePane thisImagePane;
        Image thisImage;

        List<AlleleSystemAssayResultImagePane> imagePanes
          = alleleSystemAssayResult.getPanes();

        Iterator imagePanesIter = imagePanes.iterator();
        while (imagePanesIter.hasNext() ){
            thisImagePane = (AlleleSystemAssayResultImagePane)imagePanesIter.next();

            thisImage = thisImagePane.getImage();

            // figure lables
            figureLables.append("Fig. ");
            figureLables.append(thisImage.getFigureLabel());
            figureLables.append(" ");

            // image tags
            imageTags.append("<img src='http://www.informatics.jax.org/pixeldb/fetch_pixels.cgi?id=");
            imageTags.append(thisImage.getPixeldbNumericID());
            imageTags.append("' width='30' style='border:3px; cursor: pointer;'> ");
		}

        return "<span class='summaryDataCell'>" + refLink + " "
          + figureLables.toString() + "</span><br/>" + imageTags.toString();

    }

    // result notes
    public String getResultNotes() {
        if (alleleSystemAssayResult.getResultNote() != null) {
          return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getResultNote() + "</span>";
	    }
	    else {
			return "";
		}
    }

    // allelic comp
    public String getAllelicComp() {
        String convertedAllComp = new String();
        try {
          NotesTagConverter ntc = new NotesTagConverter();
          convertedAllComp = ntc.convertNotes(alleleSystemAssayResult.getAllelicComposition(), '|');
        }catch (Exception e) {}

        convertedAllComp = FormatHelper.newline2HTMLBR(convertedAllComp);
        return "<div style='padding-top:6px;'></div><span class='summaryDataCell'>"
          + convertedAllComp + "</span>";
    }

    // sex
    public String getSex() {
        return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getSex() + "</span>";
    }

    // specimem note
    public String getSpecimenNote() {
        if (alleleSystemAssayResult.getSpecimenNote() != null) {
          return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getSpecimenNote() + "</span>";
	    }
	    else {
			return "";
		}
    }

    // assay type
    public String getAssayType() {
        return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getAssayType() + "</span>";
    }


    // report gene
    public String getReporterGene() {
        return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getReporterGene() + "</span>";
    }

    // detection method
    public String getDetectionMethod() {
        return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getDetectionMethod() + "</span>";
    }

    // assay note
    public String getAssayNote() {
        if (alleleSystemAssayResult.getAssayNote() != null) {
            return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getAssayNote() + "</span>";
	    }
	    else {
			return "";
		}
    }

}
