package org.jax.mgi.fewi.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.finder.GxdFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-ci.xml"})
@TransactionConfiguration
@Transactional
public class GxdTest {

	@Autowired
	GXDController gxdController;
	
	@Autowired
	GxdFinder gxdFinder;
	
	@Test
	public void test() throws InterruptedException 
	{
		
		List<Filter> queryFilters = new ArrayList<Filter>();
		Filter sFilter = gxdController.makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,"nervous system");
		Filter dsFilter = gxdController.makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,"cardiovascular system");
		dsFilter.negate();
		queryFilters.add(sFilter);
		queryFilters.add(dsFilter);
		Filter difFilter = new Filter();
		difFilter.setNestedFilters(queryFilters,Filter.FC_AND);
		SearchParams sp = new SearchParams();
		sp.setFilter(difFilter);
		
		System.out.println("time to hook profiler");
		//Thread.sleep(30000);
		
		startTime();
		
		List<String> mKeys = gxdFinder.searchDifferential(sp);
		
		System.out.println("time to fetch "+mKeys.size()+" diff markers = "+stopTime());
		
		Filter gxdFilter = new Filter(SearchConstants.MRK_KEY,mKeys,Filter.OP_IN);
		SearchParams gxdSP = new SearchParams();
		gxdSP.setFilter(gxdFilter);
		gxdSP.setPageSize(500);
		startTime();
		
		int rCount = gxdFinder.getAssayResultCount(gxdSP);
		System.out.println("time to fetch "+rCount+" results = "+stopTime());
		
		startTime();
		int aCount = gxdFinder.getAssayCount(gxdSP);
		System.out.println("time to fetch "+aCount+" assays = "+stopTime());
		
		startTime();
		int mCount = gxdFinder.getMarkerCount(gxdSP);
		System.out.println("time to fetch "+mCount+" markers = "+stopTime());
		
		startTime();
		int iCount = gxdFinder.getImageCount(gxdSP);
		System.out.println("time to fetch "+iCount+" images = "+stopTime());
		
	}

    private long startTime = 0;
    public void startTime()
    {
    	startTime = System.nanoTime();
    }
    public long stopTime()
    {
    	long endTime = System.nanoTime();
    	return (endTime - startTime)/1000000;
    	
    }
}
