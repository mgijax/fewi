package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.Image;
import mgi.frontend.datamodel.Genotype;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.util.FormatHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around an image, for the recombinase detail image gallery
 */
public class ImageSummaryRow {

    //-------------------
    // instance variables
    //-------------------

    // this is the expected width of images in this summary
    public static final int summaryImageWidth = 150;

    // logger & config values
    private Logger logger = LoggerFactory.getLogger(ImageSummaryRow.class);
    private String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

    // encapsulated row object and object data
    private Image image;
    private List<Genotype> genotypes;


    //-------------
    // constructors
    //-------------

    // hide the default constructor - we NEED an image to wrap
    private ImageSummaryRow () {}
    public  ImageSummaryRow (Image image) {
        this.image = image;
        this.genotypes = image.getGenotypes();
System.out.println("--ImageThumbKey--" + image.getThumbnailImageKey());
        return;
    }


    //------------------------------------------------------------------------
    // public instance methods
    //------------------------------------------------------------------------

    public String getPixeldbNumericID() {
        return image.getPixeldbNumericID();
    }

    public String getMgiID() {
        return image.getMgiID();
    }

    public int getRowSpanCount() {
        return genotypes.size();
    }







    // allelic comp
    public List<String> getAllelicCompStrings() {

        // collection of formatted display strings to be returned
        List<String> allelicCompStrings = new ArrayList<String>();

//        String convertedAllComp = alleleSystemAssayResult.getAllelicComposition();

        // html-ize with superscript and new lines
//        convertedAllComp = convertedAllComp.replaceAll("[\\r\\n]", "<br/>");
//        convertedAllComp = convertedAllComp.replaceAll("<br/><br/>", "<br/>");
//        convertedAllComp = convertedAllComp.replace("<+>", "<sup>+</sup>");

        // convert predefined tags within the notes
//        try {
//          NotesTagConverter ntc = new NotesTagConverter();
//          convertedAllComp = ntc.convertNotes(convertedAllComp, '|');
//        }catch (Exception e) {}

        return allelicCompStrings;
    }













    public String getCaption() {
        String cleanCaption = image.getCaption();
        if (cleanCaption==null) {return "";}
        return image.getCaption().trim().replaceAll("[\\r\\n]", "").replaceAll("[']", "\'");
    }




    // scaled height, keeping aspect ratio for image with 150px width
    public int getModifiedHeight() {
        double width = image.getWidth().doubleValue();
        double height = image.getHeight().doubleValue();
        int modifiedHeight = (int)((height * summaryImageWidth) / width);
        return modifiedHeight;
    }

}
