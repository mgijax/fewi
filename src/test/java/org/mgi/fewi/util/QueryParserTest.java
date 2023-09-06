package org.mgi.fewi.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.util.QueryParser;
import org.junit.Assert;
import org.junit.Test;

public class QueryParserTest {

	/*
	 * Test the removeUnmatched() method(s)
	 */
	@Test
	public void testRemoveUnmatchedNoChar() {
		Assert.assertEquals("original",QueryParser.removeUnmatched("original",'*'));
		Assert.assertEquals("original",QueryParser.removeUnmatched("original",'(',')'));
	}
	@Test
	public void testRemoveUnmatchedOneChar() {
		Assert.assertEquals("original",QueryParser.removeUnmatched("original*",'*'));
		Assert.assertEquals("original",QueryParser.removeUnmatched("*original",'*'));
	}
	@Test
	public void testRemoveUnmatchedTwoChars() {
		Assert.assertEquals("origi?nal?",QueryParser.removeUnmatched("origi?nal?",'?'));
	}
	@Test
	public void testRemoveUnmatchedManyChars() {
		Assert.assertEquals("or$igi$nal",QueryParser.removeUnmatched("or$igi$nal$",'$'));
		Assert.assertEquals("$ori$gi$nal$",QueryParser.removeUnmatched("$ori$gi$nal$",'$'));
	}
	
	@Test
	public void testRemoveUnmatchedDifferentChars() {
		Assert.assertEquals("<original>",QueryParser.removeUnmatched("<original>",'<','>'));
		Assert.assertEquals("<original><><>",QueryParser.removeUnmatched("<original><><>",'<','>'));
		Assert.assertEquals("<ori>ginal",QueryParser.removeUnmatched("<ori>gina<l",'<','>'));
	}

	
	/*
	 * Test tokenizeOnWhitespaceAndComma() method
	 */
	@Test
	public void testTokenizeWhitespaceAndCommaBlank() {
		Assert.assertEquals("",listJoin(QueryParser.tokeniseOnWhitespaceAndComma(""),","));
	}
	@Test
	public void testTokenizeWhitespaceAndCommaOneWord() {
		Assert.assertEquals("word",listJoin(QueryParser.tokeniseOnWhitespaceAndComma("word"),","));
	}
	@Test
	public void testTokenizeWhitespaceAndCommaManyWords() {
		Assert.assertEquals("word1,word2,word3",listJoin(QueryParser.tokeniseOnWhitespaceAndComma("word1 word2 word3"),","));
		Assert.assertEquals("word1,word2,word3",listJoin(QueryParser.tokeniseOnWhitespaceAndComma("word1,word2,word3"),","));
	}
	@Test
	public void testTokenizeWhitespaceAndCommaMixedDelim() {
		Assert.assertEquals("word1,word2,word3,word4",listJoin(QueryParser.tokeniseOnWhitespaceAndComma("word1 word2,,word3  , word4"),","));
	}
	@Test
	public void testTokenizeWhitespaceAndCommaSpecialChars() {
		Assert.assertEquals("\"word1[]{},word2@$%,word3)~(`",listJoin(QueryParser.tokeniseOnWhitespaceAndComma("\"word1[]{} word2@$%,word3)~(`"),","));
	}
	
	/*
	 * Test tokenizeOnWhitespace() method
	 */
	@Test
	public void testTokenizeWhitespaceBlank() {
		Assert.assertEquals("",listJoin(QueryParser.tokeniseOnWhitespace(""),","));
	}
	@Test
	public void testTokenizeWhitespaceOneWord() {
		Assert.assertEquals("word",listJoin(QueryParser.tokeniseOnWhitespace("word"),","));
	}
	@Test
	public void testTokenizeWhitespaceManyWords() {
		Assert.assertEquals("word1,word2,word3",listJoin(QueryParser.tokeniseOnWhitespace("word1 word2   word3"),","));
	}
	@Test
	public void testTokenizeWhitespaceSpecialChars() {
		Assert.assertEquals("\"word1[]{} word2@$%,word3)~(`",listJoin(QueryParser.tokeniseOnWhitespace("\"word1[]{} word2@$%,word3)~(`")," "));
	}
	
	/**
	 * private helper methods
	 */
	private String listJoin(List<String> list, String delim) {
		return StringUtils.join(list,delim);
	}
}
