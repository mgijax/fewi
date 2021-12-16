package org.jax.mgi.fewi.summary;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import mgi.frontend.datamodel.AlleleSystemAssayResult;
import mgi.frontend.datamodel.AlleleSystemAssayResultImagePane;
import mgi.frontend.datamodel.Image;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;


/**
 * wrapper around a AlleleSystemAssayResult;  represents on row in summary
 */
public class RecomSpecificitySummaryRow {

    //-------------------
    // instance variables
    //-------------------

    // encapsulated row object
    private AlleleSystemAssayResult alleleSystemAssayResult;

    // config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
    String pixUrl  = ContextLoader.getConfigBean().getProperty("PIXELDB_URL");


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

        Iterator<AlleleSystemAssayResultImagePane> imagePanesIter = imagePanes.iterator();

        if (!imagePanesIter.hasNext() ){
            imageTags.append("<span class='small italic'>No figure available<br></span>");
		}

        while (imagePanesIter.hasNext() ){

            thisImagePane = imagePanesIter.next();
            thisImage = thisImagePane.getImage();

            // figure lables
            figureLables.append("Fig. ");
            figureLables.append(thisImage.getFigureLabel());
            if (thisImagePane.getPaneLabel() != null) {
              figureLables.append(thisImagePane.getPaneLabel());
			}
            figureLables.append(" ");
            try {
              NotesTagConverter ntc = new NotesTagConverter();
              figureLables.append(ntc.convertNotes(thisImage.getExternalLink(), '|'));
              figureLables.append(" ");
            }catch (Exception e) {}

            if (thisImage.getPixeldbNumericID() == null) {
              imageTags.append("<span class='small italic' ");
              imageTags.append("onMouseOver='return overlib('This annotation is based on text statements in the cited reference.', WIDTH, 250);");
              imageTags.append("onMouseOut='nd();'>No figure available<br></span>");
            }
            else {
              // image tags
              imageTags.append("<img src='");
              imageTags.append(pixUrl + thisImage.getPixeldbNumericID());
              imageTags.append("' width='30' style='border:3px;'> ");
		    }
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

        String convertedAllComp = alleleSystemAssayResult.getAllelicComposition();

        // html-ize with superscript and new lines
        convertedAllComp = convertedAllComp.replaceAll("[\\r\\n]", "<br/>");
        convertedAllComp = convertedAllComp.replaceAll("<br/><br/>", "<br/>");
        convertedAllComp = convertedAllComp.replace("<+>", "<sup>+</sup>");

        // convert predefined tags within the notes
        try {
          NotesTagConverter ntc = new NotesTagConverter();
          convertedAllComp = ntc.convertNotes(convertedAllComp, '|');
        }catch (Exception e) {}

        return String.format("<div style='padding-top:6px;'></div><span class='summaryDataCell'>%s<br/>%s</span>",
        		convertedAllComp, FormatHelper.superscript(alleleSystemAssayResult.getBackgroundStrain()));
    }

    // sex
    public String getSex() {
        return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getSex() + "</span>";
    }

    // cell types
    public String getCellTypes() {
        if (alleleSystemAssayResult.getCellTypes() != null) {
          return "<span class='summaryDataCell'>" + alleleSystemAssayResult.getCellTypes() + "</span>";
	    }
	    else {
	    	return "";
		}
    }

    // specimen note
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
    public String getAssayNote() throws IOException
    {
        if (alleleSystemAssayResult.getAssayNote() != null) {
        	NotesTagConverter ntc = new NotesTagConverter();
            return "<span class='summaryDataCell'>" + ntc.convertNotes(alleleSystemAssayResult.getAssayNote(),'|') + "</span>";
	    }
	    else {
			return "";
		}
    }

}
