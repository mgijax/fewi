package org.jax.mgi.fewi.test;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.controller.AutoCompleteController;
import org.jax.mgi.fewi.util.ImageUtils;
import org.jax.mgi.fewi.util.QueryParser;
import org.jax.mgi.shr.fe.query.SolrLocationTranslator;
//import org.jax.mgi.fewi.util.NotesTagConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

//@ContextConfiguration({"classpath:applicationContext-ci.xml"})
public class NTCTest {

//	@Autowired
//	AutoCompleteController acController;
//	
	/**
	 * @param args
	 */
//	public static void main(String[] args) throws Exception
//	{
//		Thread.sleep(10000);
//		String s = "blah \\Allele(MGI:12345||) \\Ref(MGI!12345678||) \\Acc(MGI:123456||) blah & blah \\Link(http://compbio.dfci.harvard.edu/tgi/cgi-bin/tgi/tc_report.pl?tc=TC1619255&species=mouse%()|Link Name|)";
//		String s2 = "";
//		for(int i=0;i<10000;i++)
//		{
//			NotesTagConverter ntc = new NotesTagConverter();
//			String sc = ntc.convertNotes(FormatHelper.formatVerbatim(s), '|');
//		}
//		System.out.println("Done");
//	}

	public static void main(String[] args) throws Exception
	{
//		testQueryParser( "I am \"parkinsons 4\"",Arrays.asList("i","am","parkinsons 4"));
//		testQueryParser( "\"parkinsons syndrome\"",Arrays.asList("parkinsons syndrome"));
////		testQueryParser( "\"parkinsons 4\" am I",Arrays.asList("i","am","parkinsons 4"));
//		System.out.println(SolrLocationTranslator.getQueryValue("chr1:11000-1949194"));
//		System.out.println(SolrLocationTranslator.getQueryValue("-chr1:11000-1949194"));
//		System.out.println(SolrLocationTranslator.getQueryValue("+chr3:11-194919444"));
//		System.out.println(SolrLocationTranslator.getQueryValue("chr1:1000-10000"));
//		System.out.println(SolrLocationTranslator.getQueryValue("chrx"));
//		System.out.println(SolrLocationTranslator.getQueryValue("-chrx"));
//		System.out.println(SolrLocationTranslator.getQueryValue("-chr1"));
		//Thread.sleep(20000);
		long avg = 0;
		List<String> textsInit = Arrays.asList("roapjapdjada","afhw0fhw0fh032h290-h9-2hg-92hg-92hg-92hg",
				"d","d","f2fh2f02","252525225252525","0","g28hg2g02hg02h02hg02hg082g","ffff22222");
		List<String> texts = new ArrayList<String>(textsInit);
		for(int mult=0;mult<5;mult++)
		{
			texts.addAll(texts);
		}
		System.out.println("running "+texts.size()+" number of tests");
		for(String text : texts)
		{
			startTime();
			String t2 = ImageUtils.getRotatedTextImageTagAbbreviated(text,300.00,200);
			long dur = stopTime();
			//System.out.println("dur for "+text+" = "+dur);
			avg += dur;
		}

		System.out.println("total run for "+texts.size()+" tests = "+avg);
		avg = avg / texts.size();
		System.out.println("average run = "+avg);
	}
	
	public static void testQueryParser(String query,List<String> expectedTokens) throws Exception
	{
		List<String> tokens = QueryParser.parseNomenclatureSearch(query,false,"\"");
		
		Set<String> tokenSet = new HashSet<String>(tokens);
		tokenSet.removeAll(expectedTokens);
		
		Assert.assertTrue("tokens remaining="+StringUtils.join(tokenSet,","),tokenSet.size()==0);
	}
	
	
	
    private static long startTime = 0;
    public static void startTime()
    {
    	startTime = System.nanoTime();
    }
    public static long stopTime()
    {
    	long endTime = System.nanoTime();
    	return (endTime - startTime)/1000000;
    	
    }
}
