package org.jax.mgi.fewi.test;

import org.jax.mgi.fewi.hunter.SolrSequenceSummaryHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;

public class SequenceTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        System.out.println("Test!!!!!");
        
        SolrSequenceSummaryHunter sssh = new SolrSequenceSummaryHunter();
        
        SearchParams dummySearch = new SearchParams();
        SearchResults dummyResults = new SearchResults();
        
        sssh.hunt(dummySearch, dummyResults);
        
    }

}
