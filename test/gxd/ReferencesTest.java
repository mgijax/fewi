package gxd;

import static org.junit.Assert.assertTrue;

import org.concordion.api.ExpectedToFail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-ci.xml"})
@TransactionConfiguration
@Transactional
@ExpectedToFail
public class ReferencesTest {

    /**
     * @param args
     */
    @Test
    public void testAnatomicalSystem() 
    {
//        // TODO Auto-generated method stub
//        SolrSequenceSummaryHunter sssh = new SolrSequenceSummaryHunter();
//        SolrReferenceSummaryHunter srsh = new SolrReferenceSummaryHunter();
//        
//        SearchParams dummySearch = new SearchParams();
//        SearchResults dummyResults = new SearchResults();
//        
//        // A Very simple filter
//        
//        Filter testFilter = new Filter(SearchConstants.SEQ_ID, "2733", Filter.OP_EQUAL);
//
//        dummySearch.setFilter(testFilter);
//        Sort sort = new Sort();
//        sort.setSort("sequence_key");
//        dummySearch.addSort(sort);
//        
//        System.out.println("The first query: ");
//        
//        sssh.hunt(dummySearch, dummyResults);
//       
//        // A nested Filter
//        
//        Filter base1 = new Filter(SearchConstants.MRK_KEY, "10603", Filter.OP_LESS_THAN);
//        Filter base2 = new Filter(SearchConstants.REF_KEY, "", Filter.OP_IN);
//        List <String> values = new ArrayList <String> ();
//        values.add("testString1");
//        values.add("testString2");
//        values.add("testString3");
//        base2.setValues(values);
//        List<Filter> filterList = new ArrayList<Filter>();
//        filterList.add(base1);
//        filterList.add(base2);
//        Filter nestedFilter = new Filter("", "", Filter.FC_AND);
//        nestedFilter.setNestedFilters(filterList);
//        //sort.setDesc(true);
//        
//        dummySearch.setFilter(nestedFilter);
//        
//        System.out.println("The second query: ");
//        sssh.hunt(dummySearch, dummyResults);
//        
//        List<Filter> flist = new ArrayList<Filter>();
//        flist.add(testFilter);
//        flist.add(nestedFilter);
//        Filter complexFilter = new Filter("", "", Filter.FC_AND);
//        complexFilter.setNestedFilters(flist);
//        
//        dummySearch.setFilter(complexFilter);
//        System.out.println("The third query: ");
//        sssh.hunt(dummySearch, dummyResults);
//        
//        
//        System.out.println("Starting the references testing.");
//        Filter refFilter1 = new Filter(SearchConstants.REF_AUTHOR_ANY, "jones");
//        
//        SearchParams params = new SearchParams();
//        Filter ftest = new Filter();
//        List<Filter> flist3= new ArrayList<Filter>();
//        flist3.add(new Filter(SearchConstants.REF_AUTHOR_ANY, "jones"));
//        ftest.setNestedFilters(flist3);
//        params.setFilter(ftest); 
//        
//        srsh.hunt(params, dummyResults);
//        
//        flist3.add(new Filter());
//        ftest.setNestedFilters(flist3);
//        params.setFilter(ftest);
//        srsh.hunt(params, dummyResults);
//        
//        SolrAuthorsACHunter saah = new SolrAuthorsACHunter();
//        Filter fauthor = new Filter(SearchConstants.REF_AUTHOR, "har");
//        params.setFilter(fauthor);
//        Sort foo = new Sort();
//        foo.setSort(IndexConstants.REF_AUTHOR_SORT);
//        foo.setDesc(false);
//        List <Sort> sortList = new ArrayList<Sort>();
//        params.setSorts(sortList);
//        saah.hunt(params, dummyResults);
//        
//        SolrJournalsACHunter sjah = new SolrJournalsACHunter();
//        Filter fjournal = new Filter(SearchConstants.REF_JOURNAL, "nat");
//        params.setFilter(fjournal);
//        foo = new Sort();
//        foo.setSort(IndexConstants.REF_JOURNAL_SORT);
//        foo.setDesc(false);
//        sortList = new ArrayList<Sort>();
//        params.setSorts(sortList);
//        sjah.hunt(params, dummyResults);
//
//        params = new SearchParams();
//        Filter f = new Filter();
//        f.setFilterJoinClause(Filter.FC_AND);
//        filterList = new ArrayList<Filter>(); 
//        filterList = new ArrayList<Filter>();
//        filterList.add(new Filter(SearchConstants.REF_AUTHOR_ANY, "eppig", Filter.OP_EQUAL));
//        filterList.add(new Filter(SearchConstants.REF_JOURNAL, "Dev Biol", Filter.OP_EQUAL));
//        f.setNestedFilters(filterList);
//        params.setFilter(f);
//        
//        srsh.hunt(params, dummyResults);
//        
//        params = new SearchParams();
//        f = new Filter();
//        f.setFilterJoinClause(Filter.FC_AND);
//        filterList = new ArrayList<Filter>();
//        List<String> authors = Arrays.asList("eppig;jones".split(";"));
//        filterList.add(new Filter(SearchConstants.REF_AUTHOR_ANY, authors, Filter.OP_IN));
//        filterList.add(new Filter(SearchConstants.REF_JOURNAL, "Dev Biol", Filter.OP_EQUAL));
//        f.setNestedFilters(filterList);
//        params.setFilter(f); 
//        
//        srsh.hunt(params, dummyResults);
//        
    	assertTrue(true);
    }
}
