package org.jax.mgi.fewi.summary;

import java.util.*;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.config.ContextLoader;

import javax.persistence.Column;
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

}
