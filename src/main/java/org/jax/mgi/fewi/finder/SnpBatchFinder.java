package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.summary.ConsensusSNPSummaryRow;

/**
 * Modified from the gxdBatchFinder - This finder allows us to easily retrieve
 * SNPs in batches for generating reports.
 */
public class SnpBatchFinder {
	//--- instance variables ---//

	private SnpFinder finder;
	public SearchParams searchParams = null;
	public Integer batchSize = 10000;
	public Integer currentOffset = 0;
	private Integer totalCount = null;
	

	//--- constructors ---//

	public SnpBatchFinder(SnpFinder finder, SearchParams searchParams) {
		this.finder = finder;
		this.searchParams = searchParams;
	}

	//--- setter methods ---//

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	//--- accessor methods ---//

	// are there more results to process? NOTE that this test does NOT account for max_result_window limits.
	public boolean hasNextResults() {
		if(totalCount != null && totalCount <= currentOffset) return false;
		return true;
	}

	// get the next batch of results
	public SearchResults<ConsensusSNPSummaryRow> getNextResults() {
		if (currentOffset.equals(0)) { totalCount = null; }

		Paginator p = new Paginator();
		p.setResults(batchSize);
		p.setStartIndex(currentOffset);
		searchParams.setPaginator(p);
		List<String> matchedMarkerIds = new ArrayList<String>();

		SearchResults<ConsensusSNPSummaryRow> results = finder.getSummarySnps(searchParams, matchedMarkerIds);

		currentOffset += batchSize;
		totalCount = results.getTotalCount();
		return results;
	}
}
