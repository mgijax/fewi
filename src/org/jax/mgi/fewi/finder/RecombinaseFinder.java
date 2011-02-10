package org.jax.mgi.fewi.finder;

/*-------------------------------*/
/* to be changed for each Finder */
/*-------------------------------*/

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleSystem;
import mgi.frontend.datamodel.AlleleSystemAssayResult;
import org.jax.mgi.fewi.hunter.SolrCreSummaryHunter;
import org.jax.mgi.fewi.hunter.SolrCreAlleleSystemKeyHunter;
import org.jax.mgi.fewi.hunter.SolrCreAssayResultSummaryHunter;

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
import org.jax.mgi.fewi.summary.RecombinaseSummary;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private SolrCreSummaryHunter creSummaryHunter;

	@Autowired
	private SolrCreAlleleSystemKeyHunter creAlleleSystemKeyHunter;

	@Autowired
	private SolrCreAssayResultSummaryHunter creAssayHunter;

	@Autowired
	private HibernateObjectGatherer<Allele> summaryGatherer;

	@Autowired
	private HibernateObjectGatherer<AlleleSystem> alleleSystemGatherer;

	@Autowired
	private HibernateObjectGatherer<AlleleSystemAssayResult> assayResultGatherer;

	/*-------------------------*/
	/* public instance methods */
	/*-------------------------*/

	// Recombinase Summary
	public SearchResults<Allele> searchRecombinases(SearchParams params) {

		logger.debug ("searchRecombinases");

		SearchResults<Allele> results = new SearchResults<Allele>();

		logger.debug ("hunt");
		creSummaryHunter.hunt (params, results);

		logger.debug ("gather");
		summaryGatherer.setType (Allele.class);

		List<Allele> alleles = summaryGatherer.get (results.getResultKeys());

		results.setResultObjects (alleles);
		return results;
	}


	// Recombinase Specificity
	public SearchResults<AlleleSystem> getAlleleSystem(SearchParams params) {

		logger.debug ("getAlleleSystem");

		SearchResults<AlleleSystem> results = new SearchResults<AlleleSystem>();

		logger.debug ("hunt");
		creAlleleSystemKeyHunter.hunt (params, results);

		logger.debug ("gather");
		alleleSystemGatherer.setType (AlleleSystem.class);

		List<AlleleSystem> alleleSystem
		  = alleleSystemGatherer.get (results.getResultKeys());

		results.setResultObjects (alleleSystem);
		return results;
	}




	// Recombinase Specificity Assay Summary
	public SearchResults<AlleleSystemAssayResult> getAssaySummary(SearchParams params) {

		logger.debug ("getAssaySummary");

		SearchResults<AlleleSystemAssayResult> results
		  = new SearchResults<AlleleSystemAssayResult>();

		logger.debug ("hunt");
		creAssayHunter.hunt (params, results);

System.out.println("-->" + results.getTotalCount());

		logger.debug ("gather");
		assayResultGatherer.setType (AlleleSystemAssayResult.class);

		List<AlleleSystemAssayResult> alleleSystem
		  = assayResultGatherer.get (results.getResultKeys());

		results.setResultObjects (alleleSystem);
		return results;
	}


}
