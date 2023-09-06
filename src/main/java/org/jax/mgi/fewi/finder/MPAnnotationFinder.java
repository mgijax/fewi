package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.hunter.SolrMPAnnotationHunter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrMPAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding MP annotations
 */
@Repository
public class MPAnnotationFinder {

	/*--------------------*/
	/* instance variables */
	/*--------------------*/

    private Logger logger = LoggerFactory.getLogger(MPAnnotationFinder.class);

    @Autowired
    private SolrMPAnnotationHunter mpAnnotationHunter;

	/*---------------------------------*/
	/* Retrieval of multiple annotations
	/*---------------------------------*/

    public SearchResults<SolrMPAnnotation> getAnnotations(
	SearchParams searchParams) {

        logger.debug("->MPAnnotationFinder.getAnnotations()");

        // result object to be returned
        SearchResults<SolrMPAnnotation> searchResults =
	    new SearchResults<SolrMPAnnotation>();

        // ask the hunter to identify which objects to return
        mpAnnotationHunter.hunt(searchParams, searchResults,
		SearchConstants.ANNOTATION_KEY);
        logger.debug("->hunter found "
		+ searchResults.getResultObjects().size() + " annotations");

/*
*        List<SolrMPAnnotation> annotationList = annotationGatherer.get(
*	    Annotation.class, searchResults.getResultKeys() );
*
*	logger.debug("->annotationGatherer got " +
*		annotationList.size() + " Annotation objects");
*
*        searchResults.setResultObjects(annotationList);
*/
        return searchResults;
    }



}
