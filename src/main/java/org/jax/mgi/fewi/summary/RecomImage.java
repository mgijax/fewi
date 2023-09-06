package org.jax.mgi.fewi.summary;

import org.jax.mgi.fe.datamodel.Image;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around an image, for the recombinase detail image gallery
 */
public class RecomImage {

	private final Logger logger = LoggerFactory.getLogger(RecomImage.class);

	//-------------------
	// instance variables
	//-------------------


	// encapsulated row object
	private Image image;

	// index of image in gallery;  used for z-index and positioning
	private int imageIndex;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------
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

    public String getExternalLink() {
        return image.getExternalLink();
    }

    public String getCaption() {
		String cleanCaption = image.getCaption();
		if (cleanCaption==null) {return "";}
        return image.getCaption().trim().replaceAll("[\\r\\n]", "").replaceAll("[']", "\'");
    }

	public String getCopyright() {
		String cleanCopyright = image.getCopyright();
		if (cleanCopyright==null) {return "";}
        try {
          NotesTagConverter ntc = new NotesTagConverter();
          cleanCopyright = ntc.convertNotes(cleanCopyright, '|');
        }catch (Exception e) {
        	logger.error(e.getMessage(),e);
        }
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

        StringBuffer onMouseUpStr = new StringBuffer();

        onMouseUpStr.append("onMouseUp=\"return overlib('");
        onMouseUpStr.append(this.getCaption());
        onMouseUpStr.append("<br/><br/>");
        onMouseUpStr.append(this.getCopyright());
        onMouseUpStr.append("<br/><br/>");
        onMouseUpStr.append("<em>Drag image to compare to others or to data in the table below. ");
        onMouseUpStr.append("Drag corners to resize image for more detail.</em>', CAPTION, '");
        onMouseUpStr.append(this.getJnumID() + "&nbsp;Fig.&nbsp; ");
        onMouseUpStr.append(this.getFigureLabel() + "<br />");
        onMouseUpStr.append("<em>Drag image, resize at corners.</em>', WIDTH, 350, ANCHOR, ");
        onMouseUpStr.append("'creImg" + this.getPixeldbNumericID());
        onMouseUpStr.append("', ANCHORALIGN, 1.05,0,0,0, STICKY, CLOSECLICK, CLOSETEXT, 'close X');\"");

        return onMouseUpStr.toString();
    }


}
