package org.jax.mgi.fewi.finder;

/*-------------------------------*/
/* to be changed for each Finder */
/*-------------------------------*/

import mgi.frontend.datamodel.Allele;
import org.jax.mgi.fewi.hunter.SolrCreSummaryHunter;

/*----------------------------------------*/
/* standard classes, used for all Finders */
/*----------------------------------------*/

import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*-------*/
/* class */
/*-------*/

@Repository
public class RecombinaseFinder {

	/*--------------------*/
	/* instance variables */
	/*--------------------*/

	private Logger logger = LoggerFactory.getLogger (
		RecombinaseFinder.class);

	@Autowired
	private SolrCreSummaryHunter hunter;
	
	@Autowired
	private HibernateObjectGatherer<Allele> gatherer;

	/*-------------------------*/
	/* public instance methods */
	/*-------------------------*/

	public SearchResults<Allele> searchRecombinases(SearchParams params) {
		logger.debug ("searchReferences");
		SearchResults<Allele> results = new SearchResults<Allele>();

		logger.debug ("hunt");
		hunter.hunt (params, results);

		logger.debug ("gather");
		gatherer.setType (Allele.class);
		
		results.setResultObjects (gatherer.get (results.getResultKeys()));
		return results;
	}
}
