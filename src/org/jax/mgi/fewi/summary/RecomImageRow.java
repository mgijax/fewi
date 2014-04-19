package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a row of recombinase images
 */
public class RecomImageRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(RecomImageRow.class);

	// encapsulated row object
	private List<RecomImage> recomImages = new ArrayList<RecomImage>();

	//-------------
	// constructors
	//-------------

    public RecomImageRow () {}


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public void setRecomImages(List<RecomImage> imageList) {
    	recomImages = imageList;
    }

    public List<RecomImage> getRecomImages() {
    	return recomImages;
    }


    // height of the row, taking into account the tallest image in the row
    public int getRowHeight() {

		int rowHeight = 80; // default

		for (RecomImage image: recomImages) {
			if (image.getModifiedHeight() > rowHeight) {
				rowHeight = image.getModifiedHeight();
			}
		}
        rowHeight = rowHeight + 42; //adding space for header

    	return rowHeight;
    }


}
