package org.jax.mgi.fewi.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.test.TestStats;

import org.jax.mgi.fewi.controller.MarkerController;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.Highlighter;
import org.jax.mgi.fewi.util.NotesTagConverter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:applicationContext-ci.xml"})
//@TransactionConfiguration
//@Transactional
public class FormatHelperTest {
    // the gatherer for pulling out dynamic test data
    @Autowired
    protected MarkerController markerController;
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
    @Test 
    public void intersectionTest()
    {
    	Set<String> s1 = new HashSet<String>(Arrays.asList("a","b","v","d"));
    	Set<String> s2 = new HashSet<String>(Arrays.asList("a","b","h","g"));
    	Set<String> s3 = new HashSet<String>(Arrays.asList("a","b","k","l"));
    	Set<String> s4 = new HashSet<String>(Arrays.asList("a","b","i","m"));
    	List<Set<String>> sets = Arrays.asList(s1,s2,s3,s4);
    	
    	Set<String> intersect = new HashSet<String>();
    	for(Set<String> s : sets)
		{
			if(intersect.size()==0) intersect = s;
			else intersect.retainAll(s);
		}
    	Assert.assertTrue("size should be 2",intersect.size()==2);
    	Assert.assertTrue("a should exist",intersect.contains("a"));
    	Assert.assertTrue("b should exist",intersect.contains("b"));
    }
    
    private long startTime = 0;
    public void startTime()
    {
    	startTime = System.nanoTime();
    }
    public long stopTime()
    {
    	long endTime = System.nanoTime();
    	return (endTime - startTime)/1000;
    	
    }
}
