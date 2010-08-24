package org.jax.mgi.fewi.test;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.hunter.SolrSequenceSummaryHunter;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;

public class SolrSequenceHunterTestHarness {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SolrSequenceSummaryHunter sssh = new SolrSequenceSummaryHunter();
        
        
        SearchParams dummySearch = new SearchParams();
        SearchResults dummyResults = new SearchResults();
        
        // A Very simple filter
        
        Filter testFilter = new Filter(SearchConstants.SEQUENCE_ID, "100", Filter.OP_GREATER_THAN);

        dummySearch.setFilter(testFilter);
        Sort sort = new Sort();
        sort.setProperty("sequence_key");
        dummySearch.addSort(sort);
        
        System.out.println("The first query: ");
        
        sssh.hunt(dummySearch, dummyResults);
       
        // A nested Filter
        
        Filter base1 = new Filter(SearchConstants.MARKER_KEY, "10603", Filter.OP_LESS_THAN);
        Filter base2 = new Filter(SearchConstants.REFERENCE_KEY, "", Filter.OP_IN);
        List <String> values = new ArrayList <String> ();
        values.add("testString1");
        values.add("testString2");
        values.add("testString3");
        base2.setValues(values);
        List<Filter> filterList = new ArrayList<Filter>();
        filterList.add(base1);
        filterList.add(base2);
        Filter nestedFilter = new Filter("", "", Filter.FC_AND);
        nestedFilter.setNestedFilters(filterList);
        //sort.setDesc(true);
        
        dummySearch.setFilter(nestedFilter);
        
        System.out.println("The second query: ");
        sssh.hunt(dummySearch, dummyResults);
        
        List<Filter> flist = new ArrayList<Filter>();
        flist.add(testFilter);
        flist.add(nestedFilter);
        Filter complexFilter = new Filter("", "", Filter.FC_AND);
        complexFilter.setNestedFilters(flist);
        
        dummySearch.setFilter(complexFilter);
        System.out.println("The third query: ");
        sssh.hunt(dummySearch, dummyResults);
        
        Filter base3 = new Filter("field", "multiSplitValue", Filter.OP_BEGINS);
        dummySearch.setFilter(base3);
        System.out.println("The fourth query: ");
        sssh.hunt(dummySearch, dummyResults);
        
        List<Filter> flist2 = new ArrayList<Filter>();
        flist2.add(complexFilter);
        flist2.add(base3);
        
        Filter veryComplexFilter = new Filter("", "", Filter.FC_AND);
        veryComplexFilter.setNestedFilters(flist2);
        
        dummySearch.setFilter(veryComplexFilter);
        System.out.println("The fifth query: ");
        sssh.hunt(dummySearch, dummyResults);
        
    }

}
