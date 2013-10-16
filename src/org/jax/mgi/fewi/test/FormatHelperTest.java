package org.jax.mgi.fewi.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import mgi.frontend.datamodel.AlleleSystem;
import mgi.frontend.datamodel.BatchMarkerId;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.test.TestStats;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.controller.AutoCompleteController;
import org.jax.mgi.fewi.controller.BatchController;
import org.jax.mgi.fewi.controller.DiseasePortalController;
import org.jax.mgi.fewi.controller.MarkerController;
import org.jax.mgi.fewi.finder.GxdBatchFinder;
import org.jax.mgi.fewi.finder.GxdFinder;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.hunter.HibernateBatchSummaryHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.test.mock.MockRequest;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.Highlighter;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.view.TextBatchSummary;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-ci.xml"})
@TransactionConfiguration
@Transactional
public class FormatHelperTest {
    // the gatherer for pulling out dynamic test data
    @Autowired
    protected MarkerController markerController;
	@Autowired
	protected BatchController bc;
	@Autowired
    protected RequestMappingHandlerAdapter handler;
    @Autowired
    protected RequestMappingHandlerMapping handlerMapping;
    @Autowired
    protected AutoCompleteController acController;
    @Autowired 
    protected GxdFinder gxdFinder;
    @Autowired
    DiseasePortalController hdpController;
    //@Autowired 
    //protected DispatcherServlet servlet;
//
//	@Test
//	public void verbatimTest()
//	{
//		String s = "()()||\\//\\:::;;\"\"##";
//		String sv = FormatHelper.formatVerbatim(s);
//		System.out.println(sv);
//		Assert.assertEquals(s, sv);
//	}
//	@Test
//	public void superscriptTest() {
//		String s = "H>2<O";
//		String ss = FormatHelper.superscript(s);
//		System.out.println(ss);
//		Assert.assertEquals(s, ss);
//		s = "H<2>O";
//		ss = FormatHelper.superscript(s);
//		System.out.println(ss);
//		Assert.assertEquals("H<sup>2</sup>O", ss);
//		s = "H<2><O";
//		ss = FormatHelper.superscript(s);
//		System.out.println(ss);
//		Assert.assertEquals("H<sup>2</sup><O", ss);
//
//	}
	
//	@Test
//	public void blahTest()
//	{
//		String s= "RNA in situ||Fst||8B";
//		String[] sar = s.split("\\|\\|");
//		for(String si : sar)
//		{
//			System.out.println(si);
//		}
//	}
//	@Test
//	public void ntcGenericLinkTest() throws Exception{
//		NotesTagConverter ntc = new NotesTagConverter("classA");
//		String s = "blah \\Acc(MGI:11225234||) \\Ref(reffff|dsgdg|1) blah & blah \\Link(http://compbio.dfci.harvard.edu/tgi/cgi-bin/tgi/tc_report.pl?tc=TC1619255&species=mouse%()|Link Name|)";
//		for(int i=0;i<10;i++)
//		{
//			String sc = ntc.convertNotes(FormatHelper.formatVerbatim(s), '|');
//
//			System.out.println(sc);
//		}
//		//Assert.assertEquals("blah blah & blah <a class=\"\" href=\"http://compbio.dfci.harvard.edu/tgi/cgi-bin/tgi/tc_report.pl?tc=TC1619255&species=mouse\" >Link Name</a>", sc);
//	}
//    @Test
//    public void autocompleteTest() throws Exception
//    {
//    	System.out.println("ho");
//		Thread.sleep(20000);
//		System.out.println("hi");
//		String url = "/autocomplete/vocabTerm";
//		if(handler==null || handlerMapping==null) System.out.println("spring failed to autowire handlers");
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.setRequestURI(url);
//    	request.setMethod("GET");
//    	request.addParameter("query","o");
//    	
//    	MockHttpServletResponse response = new MockHttpServletResponse();
//    	HandlerExecutionChain chain = handlerMapping.getHandler(request);
//    	if(chain==null) System.out.println("chain is null");
//    	
//    	startTime();
//    	ModelAndView mav = handler.handle(request, response, chain.getHandler());
//    	System.out.println("Time took "+stopTime()+"ms");
//    	
//    }
//    @Test
//    public void acTest() throws Exception
//    {
//    	String resolvedIds = acController.resolveVocabTermId("114480, breast cancer, 114480 hi bro, pax4 pax4 pax6 pax5");
//    	System.out.println(resolvedIds);
//    	Assert.assertEquals("114480(Breast Cancer), breast cancer, 114480(Breast Cancer) hi bro, pax4 pax4 pax6 pax5",resolvedIds);
//    	
//    	
//    }
    
    @Test 
    public void hdpTest() throws Exception
    {
    	DispatcherServlet servlet = new DispatcherServlet();
    	// now start the test proper
    	System.out.println("ready to hook");
    	Thread.sleep(20000);
    	System.out.println("begin execution");
    	String url = "/diseasePortal/grid";
    	MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI(url);
    	request.setMethod("GET");
    	request.addParameter("genes","a");
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	HandlerExecutionChain chain = handlerMapping.getHandler(request);
    	if(chain==null) System.out.println("chain is null");
    	
    	startTime();
    	ModelAndView mav = handler.handle(request, response, chain.getHandler());
    	System.out.println("time to render MAV = "+stopTime());

    	System.out.println("view = "+mav.getViewName());
    }
//	@Test
//	public void batchControllerTest() throws Exception
//	{
//		System.out.println("ho");
//		Thread.sleep(20000);
//		System.out.println("hi");
//		String url = "/batch/report.txt";
//		//?idType=auto&ids=Pax6+kit+KIT+kiT+ttn+1001&fileType=tab&idColumn=1&attributes=Nomenclature&_attributes=on&association=None";
//
//		if(handler==null || handlerMapping==null) System.out.println("spring failed to autowire handlers");
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.setRequestURI(url);
//    	request.setMethod("GET");
//    	
//    	SearchParams params = new SearchParams();
//		params.setFilter(new Filter(SearchConstants.PRIMARY_KEY,"[* TO *]",Filter.OP_HAS_WORD));
//
//		GxdBatchFinder gxdBatchFinder = new GxdBatchFinder(gxdFinder,params);
//		gxdBatchFinder.batchSize = 5000;
//		
//		// gather marker id strings in batches
//		startTime();
//		StringBuffer ids = new StringBuffer();
//		int count=0;
//		while(gxdBatchFinder.hasNextMarkers())
//		{
//			SearchResults<SolrGxdMarker> searchResults = gxdBatchFinder.getNextMarkers();
//			//add each marker ID to the ids list
//	        for (SolrGxdMarker solrMarker : searchResults.getResultObjects()) {
//	        	count++;
//				ids.append(solrMarker.getMgiid() + ", ");
//
//		        if(count>7000) break;
//			}
//	        if(count>7000) break;
//		}
//		// setup batch query object
//		request.addParameter("ids",ids.toString());
//		request.addParameter("association","RefSNP");
//		System.out.println("time to wrap marker ids = "+stopTime());
//		
//
//		
//    	//request.addParameter("nomenclature","*");
//    	//String[] ids = {"Pax6","1001","ttn","KiT","kit","KIT"};
//    	//request.addParameter("ids",StringUtils.join(ids," "));
//    	MockHttpServletResponse response = new MockHttpServletResponse();
//    	HandlerExecutionChain chain = handlerMapping.getHandler(request);
//    	if(chain==null) System.out.println("chain is null");
//    	
//    	startTime();
//    	ModelAndView mav = handler.handle(request, response, chain.getHandler());
//    	TextBatchSummary tbs = new TextBatchSummary();
//    	tbs.render(mav.getModel(),request,response);
////    	Map<String,Object> model = mav.getModel();
//    	
//    	System.out.println("Time took "+stopTime()+"ms");
////    	System.out.println("id count = "+model.get("inputIdCount"));
////    	System.out.println("querystring = "+model.get("queryString"));
//	}
	
//    @Test 
//    public void highlight1Test() throws Exception
//    {
//    	long avgTime = 0;
//    	String hs1="";
//    	for(int i=0;i<100;i++)
//    	{
//	    	startTime();
//	    	Highlighter h = new Highlighter(Arrays.asList("europhenome"));
//	    	hs1 = h.highLight("aodhaodhaohaohdad europhenome aoidhaoidhaod Europhenome <a href=\"europhenome\" >Europhenome</a>+");
//	    	avgTime += stopTime();
//    	}
//    	avgTime = avgTime / 100;
//    	System.out.println("Took: "+avgTime);
//    	
//    	avgTime = 0;
//    	String hs2="1";
//    	for(int i=0;i<100;i++)
//    	{
//	    	startTime();
//	    	Highlighter h2 = new Highlighter(Arrays.asList("europhenome"),true);
//	    	hs2 = h2.highLightRegex("aodhaodhaohaohdad europhenome aoidhaoidhaod Europhenome <a href=\"europhenome\" >Europhenome</a>+");
//	    	avgTime += stopTime();
//    	}
//    	avgTime = avgTime / 100;
//    	System.out.println("Took: "+avgTime);
//    	
//    	System.out.println(hs1);
//    	System.out.println(hs2);
//    	Assert.assertEquals(hs1,hs2);
//    }
//    @Test 
//    public void intersectionTest()
//    {
//    	Set<String> s1 = new HashSet<String>(Arrays.asList("a","b","v","d"));
//    	Set<String> s2 = new HashSet<String>(Arrays.asList("a","b","h","g"));
//    	Set<String> s3 = new HashSet<String>(Arrays.asList("a","b","k","l"));
//    	Set<String> s4 = new HashSet<String>(Arrays.asList("a","b","i","m"));
//    	List<Set<String>> sets = Arrays.asList(s1,s2,s3,s4);
//    	
//    	Set<String> intersect = new HashSet<String>();
//    	for(Set<String> s : sets)
//		{
//			if(intersect.size()==0) intersect = s;
//			else intersect.retainAll(s);
//		}
//    	Assert.assertTrue("size should be 2",intersect.size()==2);
//    	Assert.assertTrue("a should exist",intersect.contains("a"));
//    	Assert.assertTrue("b should exist",intersect.contains("b"));
//    }
    
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
