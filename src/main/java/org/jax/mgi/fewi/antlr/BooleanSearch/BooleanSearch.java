package org.jax.mgi.fewi.antlr.BooleanSearch;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.util.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BooleanSearch  {

	/**
	 * Uses ANTLR SolrBooleanSearch grammar to parse user input with boolean logic to create nested Filter objects.
	 * 	Grammar supports AND, OR, NOT, (), and "double quoted text"
	 */
	private String errorLine1 = "";
	private String errorLine2 = "";
	private String htmlErrorString = "";
	private final Logger logger = LoggerFactory.getLogger(BooleanSearch.class);

	private SolrBooleanSearchParser parser;

	public Filter buildSolrFilter(String searchConstant,String s) {
		// sanitize the user input to remove grammar abnormalities
		//logger.info("String before: " + s);
		s = sanitizeInput(s);
		//logger.info("String after: " + s);

		ANTLRStringStream input = new ANTLRStringStream(s);
		CommonTokenStream tokens = new CommonTokenStream(new SolrBooleanSearchLexer(input));
		parser = new SolrBooleanSearchParser(tokens);

		try {
			Filter qFilter = parser.query();
			setSearchConstant(searchConstant,qFilter);
			//logger.info("Parsed filter: " + qFilter);
			return qFilter;
		} catch (RecognitionException r) {
			parseErrorMessage(parser, r);
			return null;
		}

	}

	/*
	 * remove unmatched double quotes
	 * remove unmatched parens
	 * remove empty double quotes
	 * remove empty parens
	 */
	public String sanitizeInput(String s) {
		s = QueryParser.removeUnmatched(s,'"');
		s = QueryParser.removeUnmatched(s,'(',')');
		s = s.replace("\"\""," ");
		s = s.replace("()"," ");
		return s;
	}

	/*
	 *  need to recursively set the specified searchConstant.
	 *  Also is a good idea to collapse nested filters that only have one item in them.
	 */
	private void setSearchConstant(String searchConstant, Filter f) {
		f.setProperty(searchConstant);
		for(Filter child : f.getNestedFilters()) {
			setSearchConstant(searchConstant,child);
		}
	}

	private void parseErrorMessage(SolrBooleanSearchParser parser, RecognitionException e) {
		String hdr = parser.getErrorHeader(e);
		String msg = parser.getErrorMessage(e, parser.getTokenNames());
		String errorMessage = "";
		for (int i = 0; i < e.charPositionInLine; i++) errorMessage += " ";
		errorMessage += "^";
		errorLine1 = errorMessage;
		errorLine2 = "Parser: An error occured on: " + hdr + " " + msg;

		String firstpart = parser.input.toString().substring(0, e.charPositionInLine);
		String secondpart = parser.input.toString().substring(e.charPositionInLine + e.token.getText().length(), parser.input.toString().length());
		htmlErrorString = firstpart + "<span style=\"color: #FF0000; font-weight: bold\">" + e.token.getText() + "</span>" + secondpart;

	}

	public String getErrorMessage() {
		return errorLine1 + "\n" + errorLine2 + "\n";
	}

	public String getErrorLine1() {
		return errorLine1;
	}
	public String getErrorLine2() {
		return errorLine2;
	}
	public String getHtmlErrorString() {
		return htmlErrorString;
	}
}
