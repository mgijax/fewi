// $ANTLR 3.5.2 /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g 2023-08-14 14:06:38

package org.jax.mgi.fewi.antlr.BooleanSearch;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.apache.solr.client.solrj.util.ClientUtils;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class SolrBooleanSearchParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND", "LPAREN", "NOT", "OR", 
		"QUOTED", "RPAREN", "WORD", "WS"
	};
	public static final int EOF=-1;
	public static final int AND=4;
	public static final int LPAREN=5;
	public static final int NOT=6;
	public static final int OR=7;
	public static final int QUOTED=8;
	public static final int RPAREN=9;
	public static final int WORD=10;
	public static final int WS=11;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public SolrBooleanSearchParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public SolrBooleanSearchParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return SolrBooleanSearchParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g"; }


	@Override
	public void emitErrorMessage(String msg) {
	        //super.emitErrorMessage(msg);
	}

	@Override
	public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
	        super.displayRecognitionError(tokenNames, e);
	        String hdr = getErrorHeader(e);
	        String msg = getErrorMessage(e, tokenNames);
	        String errorMessage = "";
	        for (int i = 0; i < e.charPositionInLine; i++) errorMessage += " ";
	        errorMessage += "^";
	        System.out.println(errorMessage);
	        System.out.println("Parser: An error occured on: " + hdr + " " + msg + "");
	}

	private String sanitizeText(String s)
	{
		if(s==null) s="";
		return s;
	}
	private String sanitizeQuoted(String s)
	{
		s = sanitizeText(s);
		s = s.replace("\"","");
		return s;
	}
	private boolean notEmpty(Filter f)
	{
		if(f==null) return false;
		if(!f.hasNestedFilters() && f.getValue()==null) return false;
		return true;
	}

	private void setWordFilter(Filter f,String text)
	{
		if(text!=null && text.contains("*"))
		{
			// Have solr escape any special characters, but make sure asterisk remains in tact
			f.setValue(text);
			f.setOperator(Filter.Operator.OP_HAS_WORD);
		}
		else
		{
			f.setValue(text); 
			f.setOperator(Filter.Operator.OP_EQUAL);
		}
	}



	// $ANTLR start "query"
	// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:84:1: query returns [Filter queryNode] : ( AND | OR )* andExpr= and_expr ( AND | OR | NOT )* ;
	public final Filter query() throws RecognitionException {
		Filter queryNode = null;


		Filter andExpr =null;

		try {
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:84:32: ( ( AND | OR )* andExpr= and_expr ( AND | OR | NOT )* )
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:85:2: ( AND | OR )* andExpr= and_expr ( AND | OR | NOT )*
			{
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:85:2: ( AND | OR )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==AND||LA1_0==OR) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:
					{
					if ( input.LA(1)==AND||input.LA(1)==OR ) {
						input.consume();
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;

				default :
					break loop1;
				}
			}

			pushFollow(FOLLOW_and_expr_in_query109);
			andExpr=and_expr();
			state._fsp--;

			 queryNode = andExpr; 
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:85:54: ( AND | OR | NOT )*
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( (LA2_0==AND||(LA2_0 >= NOT && LA2_0 <= OR)) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:
					{
					if ( input.LA(1)==AND||(input.LA(1) >= NOT && input.LA(1) <= OR) ) {
						input.consume();
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;

				default :
					break loop2;
				}
			}

			}

		}

		  catch (RecognitionException re) {
		    //Custom handling of an exception. Any java code is allowed.
		    throw re;
		  }

		finally {
			// do for sure before leaving
		}
		return queryNode;
	}
	// $ANTLR end "query"



	// $ANTLR start "and_expr"
	// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:88:1: and_expr returns [Filter andExpression] : oe1= or_expr ( AND oe2= or_expr )* ;
	public final Filter and_expr() throws RecognitionException {
		Filter andExpression = null;


		Filter oe1 =null;
		Filter oe2 =null;

		 andExpression = new Filter(); andExpression.setFilterJoinClause(Filter.JoinClause.FC_AND); 
		try {
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:88:140: (oe1= or_expr ( AND oe2= or_expr )* )
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:89:2: oe1= or_expr ( AND oe2= or_expr )*
			{
			pushFollow(FOLLOW_or_expr_in_and_expr142);
			oe1=or_expr();
			state._fsp--;

			 if(oe1!=null) andExpression.addNestedFilter(oe1); 
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:89:68: ( AND oe2= or_expr )*
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==AND) ) {
					int LA3_1 = input.LA(2);
					if ( (LA3_1==NOT) ) {
						int LA3_3 = input.LA(3);
						if ( (LA3_3==LPAREN||LA3_3==QUOTED||LA3_3==WORD) ) {
							alt3=1;
						}

					}
					else if ( (LA3_1==LPAREN||LA3_1==QUOTED||LA3_1==WORD) ) {
						alt3=1;
					}

				}

				switch (alt3) {
				case 1 :
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:89:69: AND oe2= or_expr
					{
					match(input,AND,FOLLOW_AND_in_and_expr147); 
					pushFollow(FOLLOW_or_expr_in_and_expr151);
					oe2=or_expr();
					state._fsp--;

					 andExpression.addNestedFilter(oe2); 
					}
					break;

				default :
					break loop3;
				}
			}

			}

		}

		  catch (RecognitionException re) {
		    //Custom handling of an exception. Any java code is allowed.
		    throw re;
		  }

		finally {
			// do for sure before leaving
		}
		return andExpression;
	}
	// $ANTLR end "and_expr"



	// $ANTLR start "or_expr"
	// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:92:1: or_expr returns [Filter orExpression] : t1= term ( ( OR )? t2= term )* ;
	public final Filter or_expr() throws RecognitionException {
		Filter orExpression = null;


		Filter t1 =null;
		Filter t2 =null;

		 orExpression = new Filter(); orExpression.setFilterJoinClause(Filter.JoinClause.FC_OR); 
		try {
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:92:135: (t1= term ( ( OR )? t2= term )* )
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:93:2: t1= term ( ( OR )? t2= term )*
			{
			pushFollow(FOLLOW_term_in_or_expr178);
			t1=term();
			state._fsp--;

			 if(notEmpty(t1)) orExpression.addNestedFilter(t1); 
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:93:65: ( ( OR )? t2= term )*
			loop5:
			while (true) {
				int alt5=2;
				switch ( input.LA(1) ) {
				case OR:
					{
					int LA5_2 = input.LA(2);
					if ( (LA5_2==NOT) ) {
						int LA5_3 = input.LA(3);
						if ( (LA5_3==LPAREN||LA5_3==QUOTED||LA5_3==WORD) ) {
							alt5=1;
						}

					}
					else if ( (LA5_2==LPAREN||LA5_2==QUOTED||LA5_2==WORD) ) {
						alt5=1;
					}

					}
					break;
				case NOT:
					{
					int LA5_3 = input.LA(2);
					if ( (LA5_3==LPAREN||LA5_3==QUOTED||LA5_3==WORD) ) {
						alt5=1;
					}

					}
					break;
				case LPAREN:
				case QUOTED:
				case WORD:
					{
					alt5=1;
					}
					break;
				}
				switch (alt5) {
				case 1 :
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:93:66: ( OR )? t2= term
					{
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:93:66: ( OR )?
					int alt4=2;
					int LA4_0 = input.LA(1);
					if ( (LA4_0==OR) ) {
						alt4=1;
					}
					switch (alt4) {
						case 1 :
							// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:93:67: OR
							{
							match(input,OR,FOLLOW_OR_in_or_expr184); 
							}
							break;

					}

					pushFollow(FOLLOW_term_in_or_expr190);
					t2=term();
					state._fsp--;

					 if(notEmpty(t2)) orExpression.addNestedFilter(t2); 
					}
					break;

				default :
					break loop5;
				}
			}

			}

		}

		  catch (RecognitionException re) {
		    //Custom handling of an exception. Any java code is allowed.
		    throw re;
		  }

		finally {
			// do for sure before leaving
		}
		return orExpression;
	}
	// $ANTLR end "or_expr"



	// $ANTLR start "term"
	// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:96:1: term returns [Filter termNode] : ( ( NOT )? s= str | ( NOT )? LPAREN e= and_expr RPAREN );
	public final Filter term() throws RecognitionException {
		Filter termNode = null;


		Filter s =null;
		Filter e =null;

		 termNode = new Filter(); boolean neg=false; 
		try {
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:96:84: ( ( NOT )? s= str | ( NOT )? LPAREN e= and_expr RPAREN )
			int alt8=2;
			switch ( input.LA(1) ) {
			case NOT:
				{
				int LA8_1 = input.LA(2);
				if ( (LA8_1==QUOTED||LA8_1==WORD) ) {
					alt8=1;
				}
				else if ( (LA8_1==LPAREN) ) {
					alt8=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 8, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case QUOTED:
			case WORD:
				{
				alt8=1;
				}
				break;
			case LPAREN:
				{
				alt8=2;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 8, 0, input);
				throw nvae;
			}
			switch (alt8) {
				case 1 :
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:97:2: ( NOT )? s= str
					{
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:97:2: ( NOT )?
					int alt6=2;
					int LA6_0 = input.LA(1);
					if ( (LA6_0==NOT) ) {
						alt6=1;
					}
					switch (alt6) {
						case 1 :
							// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:97:3: NOT
							{
							match(input,NOT,FOLLOW_NOT_in_term214); 
							 neg=true; 
							}
							break;

					}

					pushFollow(FOLLOW_str_in_term222);
					s=str();
					state._fsp--;

					 termNode=s; if(neg) termNode.negate(); 
					}
					break;
				case 2 :
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:98:4: ( NOT )? LPAREN e= and_expr RPAREN
					{
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:98:4: ( NOT )?
					int alt7=2;
					int LA7_0 = input.LA(1);
					if ( (LA7_0==NOT) ) {
						alt7=1;
					}
					switch (alt7) {
						case 1 :
							// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:98:5: NOT
							{
							match(input,NOT,FOLLOW_NOT_in_term230); 
							 neg=true; 
							}
							break;

					}

					match(input,LPAREN,FOLLOW_LPAREN_in_term236); 
					pushFollow(FOLLOW_and_expr_in_term240);
					e=and_expr();
					state._fsp--;

					match(input,RPAREN,FOLLOW_RPAREN_in_term242); 
					 termNode=e; if(neg) termNode.negate(); 
					}
					break;

			}
		}

		  catch (RecognitionException re) {
		    //Custom handling of an exception. Any java code is allowed.
		    throw re;
		  }

		finally {
			// do for sure before leaving
		}
		return termNode;
	}
	// $ANTLR end "term"



	// $ANTLR start "str"
	// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:101:1: str returns [Filter stringFilter] : (q= QUOTED |q2= WORD );
	public final Filter str() throws RecognitionException {
		Filter stringFilter = null;


		Token q=null;
		Token q2=null;

		 stringFilter = new Filter(); 
		try {
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:101:73: (q= QUOTED |q2= WORD )
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0==QUOTED) ) {
				alt9=1;
			}
			else if ( (LA9_0==WORD) ) {
				alt9=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}

			switch (alt9) {
				case 1 :
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:102:9: q= QUOTED
					{
					q=(Token)match(input,QUOTED,FOLLOW_QUOTED_in_str273); 
					 stringFilter.setQuoted(true); stringFilter.setValue(sanitizeQuoted((q!=null?q.getText():null))); stringFilter.setOperator(Filter.Operator.OP_EQUAL); 
					}
					break;
				case 2 :
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:103:11: q2= WORD
					{
					q2=(Token)match(input,WORD,FOLLOW_WORD_in_str289); 
					 setWordFilter(stringFilter,sanitizeQuoted((q2!=null?q2.getText():null))); 
					}
					break;

			}
		}

		  catch (RecognitionException re) {
		    //Custom handling of an exception. Any java code is allowed.
		    throw re;
		  }

		finally {
			// do for sure before leaving
		}
		return stringFilter;
	}
	// $ANTLR end "str"

	// Delegated rules



	public static final BitSet FOLLOW_and_expr_in_query109 = new BitSet(new long[]{0x00000000000000D2L});
	public static final BitSet FOLLOW_or_expr_in_and_expr142 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_AND_in_and_expr147 = new BitSet(new long[]{0x0000000000000560L});
	public static final BitSet FOLLOW_or_expr_in_and_expr151 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_term_in_or_expr178 = new BitSet(new long[]{0x00000000000005E2L});
	public static final BitSet FOLLOW_OR_in_or_expr184 = new BitSet(new long[]{0x0000000000000560L});
	public static final BitSet FOLLOW_term_in_or_expr190 = new BitSet(new long[]{0x00000000000005E2L});
	public static final BitSet FOLLOW_NOT_in_term214 = new BitSet(new long[]{0x0000000000000500L});
	public static final BitSet FOLLOW_str_in_term222 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_term230 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_LPAREN_in_term236 = new BitSet(new long[]{0x0000000000000560L});
	public static final BitSet FOLLOW_and_expr_in_term240 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_RPAREN_in_term242 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_QUOTED_in_str273 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_WORD_in_str289 = new BitSet(new long[]{0x0000000000000002L});
}
