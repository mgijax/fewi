package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.Image;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.util.FormatHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around an image, for the recombinase detail image gallery
 */
public class RecomImage {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(RecomImage.class);

	// encapsulated row object
	private Image image;

	// index of image in gallery;  used for z-index and positioning
	private int imageIndex;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a foo to wrap
    private RecomImage () {}

    public RecomImage (Image image, int imageIndex) {
    	this.image = image;
    	this.imageIndex = imageIndex;
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods
    //------------------------------------------------------------------------

    public Integer getHeight() {
    	return image.getHeight();
    }

    public Integer getWidth() {
    	return image.getWidth();
    }

    public Integer getFullsizeImageKey() {
        return image.getFullsizeImageKey();
    }

    public int getImageKey() {
        return image.getImageKey();
    }

    public String getPixeldbNumericID() {
        return image.getPixeldbNumericID();
    }

    public String getJnumID() {
		return image.getJnumID();
	}

    public String getFigureLabel() {
        return image.getFigureLabel();
    }

    public String getCaption() {
        return image.getCaption().trim().replaceAll("[\\r\\n]", "").replaceAll("[']", "\'");
    }

	public String getCopyright() {
		String cleanCopyright = new String();
        try {
          NotesTagConverter ntc = new NotesTagConverter();
          cleanCopyright = ntc.convertNotes(image.getCopyright(), '|');
        }catch (Exception e) {}

        return cleanCopyright.trim().replaceAll("[\\r\\n]", "<br/>").replaceAll("[\"]", "\\\\'");
    }

    public int getIndexZ() {
        return 1000 - imageIndex;
    }

    // scaled height, keeping aspect ratio for image with 80px width
    public int getModifiedHeight() {
		double width = image.getWidth().doubleValue();
		double height = image.getHeight().doubleValue();
		int modifiedHeight = (int)((height * 80) / width);
        return modifiedHeight;
    }

    public String getMouseUp() {
        return "onMouseUp=\"return overlib('"
          + this.getCaption() + "<br/><br/>"
          + this.getCopyright() + "<br/><br/>"
          + "<em>Drag image to compare to others or to data in the table below. "
          + "Drag corners to resize image for more detail.</em>', CAPTION, '"
          + this.getJnumID() + "&nbsp;Fig.&nbsp; "
          + this.getFigureLabel() + "<br />"
          + "<em>Drag image, resize at corners.</em>', WIDTH, 350, ANCHOR, "
          + "'creImg" + this.getPixeldbNumericID()
          + "', ANCHORALIGN, 1.05,0,0,0, STICKY, CLOSECLICK, CLOSETEXT, 'close X');\"";

    }


}
