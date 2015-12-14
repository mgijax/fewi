package org.jax.mgi.fewi.hunter;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrDriverACResult;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.indexconstants.CreFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrDriverACHunter extends SolrHunter<SolrDriverACResult>
{
    /***
     * the constructor sets up this hunter to retreive a list of drivers
     * matching a given input string.
     */
    public SolrDriverACHunter() {

        // property map -- maps search value to corresponding field in Solr
        propertyMap.put(SearchConstants.CRE_DRIVER, new SolrPropertyMapper(CreFields.DRIVER));

        // sort auto-complete results in a case-insensitive manner
        sortMap.put(SortConstants.CRE_DRIVER,
          new SolrSortMapper(CreFields.DRIVER));
    }

	/**
	 * packInformation
	 *
	 * @return List of keys This overrides the typical behavior of
	 *         packInformation method. For this autocomplete hunter, we don't
	 *         need highlighting or metadata, but only need a list of
	 *         DriverACResult objects.
	 */
	@Override
	protected void packInformation(QueryResponse rsp,
			SearchResults<SolrDriverACResult> sr, SearchParams sp) {

		// Iterate response documents, extracting the information
		SolrDocumentList sdl = rsp.getResults();
		for (SolrDocument doc : sdl) {

			String driver = (String) doc
					.getFieldValue(CreFields.DRIVER);
			String driverDisplay = (String) doc
					.getFieldValue(CreFields.DRIVER_DISPLAY);

			// create AC result and add to search results
			SolrDriverACResult resultObject = new SolrDriverACResult(driver, driverDisplay);
			sr.addResultObjects(resultObject);
		}
	}

	@Value("${solr.driver_ac.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
