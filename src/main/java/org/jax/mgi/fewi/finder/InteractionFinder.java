package org.jax.mgi.fewi.finder;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.hunter.SolrInteractionDataSourceFacetHunter;
import org.jax.mgi.fewi.hunter.SolrInteractionHunter;
import org.jax.mgi.fewi.hunter.SolrInteractionTermFacetHunter;
import org.jax.mgi.fewi.hunter.SolrInteractionValidationFacetHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrInteraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding interaction relationships between
 * markers.
 */

@Repository
public class InteractionFinder 
{
	private final Logger logger = LoggerFactory.getLogger(InteractionFinder.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private SolrInteractionHunter interactionHunter;

	@Autowired
	private SolrInteractionTermFacetHunter intTermFacetHunter;

	@Autowired
	private SolrInteractionValidationFacetHunter intValidationFacetHunter;

	@Autowired
	private SolrInteractionDataSourceFacetHunter intDataSourceFacetHunter;


	/** get the interaction relationships which match the given search
	 * parameters (to support the new interaction explorer)
	 */
	public SearchResults<SolrInteraction> getInteraction (
			SearchParams searchParams) {

		logger.debug("->InteractionFinder.getInteraction()");

		// result object to be returned
		SearchResults<SolrInteraction> searchResults =
				new SearchResults<SolrInteraction>();

		// ask the hunter to identify which objects to return
		interactionHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found "
				+ searchResults.getResultObjects().size() + " interaction relationships");

		return searchResults;
	}

	public SearchResults<SolrInteraction> getValidationFacet(SearchParams params) {
		SearchResults<SolrInteraction> results = new SearchResults<SolrInteraction>();
		intValidationFacetHunter.hunt (params, results);
		return results;
	}

	public SearchResults<SolrInteraction> getInteractionFacet(SearchParams params) {
		SearchResults<SolrInteraction> results = new SearchResults<SolrInteraction>();
		intTermFacetHunter.hunt (params, results);
		return results;
	}

	public SearchResults<SolrInteraction> getDataSourceFacet(SearchParams params) {
		SearchResults<SolrInteraction> results = new SearchResults<SolrInteraction>();
		intDataSourceFacetHunter.hunt (params, results);
		return results;
	}

	/** special method to truncate the 'score' to a three decimal places, with
	 * a forced round 'up' or down (if not 'up')
	 */
	private String abbreviateScore (String score, boolean up) {
		int decimalPos = score.indexOf('.');

		// no decimal point, no truncation needed
		if (decimalPos < 0) {
			return score;
		}

		int truncationPoint = decimalPos + 3;

		// too few digits, no truncation needed
		if ((score.length() - 1) <= truncationPoint) {
			return score;
		}

		DecimalFormat fmt = new DecimalFormat("#.###");
		if (up) {
			fmt.setRoundingMode (RoundingMode.CEILING);
		} else {
			fmt.setRoundingMode (RoundingMode.FLOOR);
		}

		try {
			Float fScoreObj = Float.parseFloat(score);
			float fScore = fScoreObj.floatValue();
			return fmt.format(fScore);

		} catch (NumberFormatException e) {
			return score;
		}
	}

	public SearchResults<SolrInteraction> getScoreFacet(SearchParams params) {
		List<Sort> sorts = new ArrayList<Sort>();

		logger.debug("Entering getScoreFacet()");
		SearchResults<SolrInteraction> results = new SearchResults<SolrInteraction>();

		// We just want one record returned from each end of the data set.

		params.setPageSize(1);

		// find record with the lowest score in this set

		sorts.add(new Sort(SortConstants.BY_SCORE, true));
		params.setSorts(sorts);

		SearchResults<SolrInteraction> searchResults1 =
				new SearchResults<SolrInteraction>();

		interactionHunter.hunt(params, searchResults1);

		String first = "0";
		if (searchResults1.getResultObjects().size() >= 1) {
			first = searchResults1.getResultObjects().get(0).getScore();
			first = abbreviateScore(first, false);
		}
		logger.debug("Found first: " + first);

		// find record with the highest score in this set

		sorts.remove(0);
		sorts.add(new Sort(SortConstants.BY_SCORE, false));
		params.setSorts(sorts);

		SearchResults<SolrInteraction> searchResults2 = new SearchResults<SolrInteraction>();

		interactionHunter.hunt(params, searchResults2);

		String last = "1";
		if (searchResults2.getResultObjects().size() >= 1) {
			last = searchResults2.getResultObjects().get(0).getScore();
			last = abbreviateScore(last, false);
		}
		logger.debug("Found last: " + last);

		// collect into a single list and add as the facets to be returned

		List<String> resultObjects = new ArrayList<String>();
		resultObjects.add(first);
		resultObjects.add(last);

		results.setResultFacets(resultObjects); 
		results.setTotalCount(resultObjects.size()); 

		logger.debug("Exiting getScoreFacet()");
		return results;
	}
}
