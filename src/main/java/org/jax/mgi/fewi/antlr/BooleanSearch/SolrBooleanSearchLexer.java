// $ANTLR 3.5.2 /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g 2023-08-14 14:06:38
package org.jax.mgi.fewi.antlr.BooleanSearch;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

@SuppressWarnings("all")
public class SolrBooleanSearchLexer extends Lexer {
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
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public SolrBooleanSearchLexer() {} 
	public SolrBooleanSearchLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public SolrBooleanSearchLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "/Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g"; }

	// $ANTLR start "AND"
	public final void mAND() throws RecognitionException {
		try {
			int _type = AND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:9:5: ( 'AND' )
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:9:7: 'AND'
			{
			match("AND"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "AND"

	// $ANTLR start "LPAREN"
	public final void mLPAREN() throws RecognitionException {
		try {
			int _type = LPAREN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:10:8: ( '(' )
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:10:10: '('
			{
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LPAREN"

	// $ANTLR start "NOT"
	public final void mNOT() throws RecognitionException {
		try {
			int _type = NOT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:11:5: ( 'NOT' )
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:11:7: 'NOT'
			{
			match("NOT"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NOT"

	// $ANTLR start "OR"
	public final void mOR() throws RecognitionException {
		try {
			int _type = OR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:12:4: ( 'OR' )
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:12:6: 'OR'
			{
			match("OR"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OR"

	// $ANTLR start "RPAREN"
	public final void mRPAREN() throws RecognitionException {
		try {
			int _type = RPAREN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:13:8: ( ')' )
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:13:10: ')'
			{
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RPAREN"

	// $ANTLR start "WORD"
	public final void mWORD() throws RecognitionException {
		try {
			int _type = WORD;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:106:6: ( (~ ( ' ' | '\\t' | '\\r' | '\\n' | '(' | ')' | '\"' ) )+ )
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:106:9: (~ ( ' ' | '\\t' | '\\r' | '\\n' | '(' | ')' | '\"' ) )+
			{
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:106:9: (~ ( ' ' | '\\t' | '\\r' | '\\n' | '(' | ')' | '\"' ) )+
			int cnt1=0;
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( ((LA1_0 >= '\u0000' && LA1_0 <= '\b')||(LA1_0 >= '\u000B' && LA1_0 <= '\f')||(LA1_0 >= '\u000E' && LA1_0 <= '\u001F')||LA1_0=='!'||(LA1_0 >= '#' && LA1_0 <= '\'')||(LA1_0 >= '*' && LA1_0 <= '\uFFFF')) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\b')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\u001F')||input.LA(1)=='!'||(input.LA(1) >= '#' && input.LA(1) <= '\'')||(input.LA(1) >= '*' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt1 >= 1 ) break loop1;
					EarlyExitException eee = new EarlyExitException(1, input);
					throw eee;
				}
				cnt1++;
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WORD"

	// $ANTLR start "QUOTED"
	public final void mQUOTED() throws RecognitionException {
		try {
			int _type = QUOTED;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:108:7: ( '\"' (~ '\"' )* '\"' )
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:108:9: '\"' (~ '\"' )* '\"'
			{
			match('\"'); 
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:108:12: (~ '\"' )*
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( ((LA2_0 >= '\u0000' && LA2_0 <= '!')||(LA2_0 >= '#' && LA2_0 <= '\uFFFF')) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop2;
				}
			}

			match('\"'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "QUOTED"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:110:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
			// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:110:7: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
			{
			if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	@Override
	public void mTokens() throws RecognitionException {
		// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:1:8: ( AND | LPAREN | NOT | OR | RPAREN | WORD | QUOTED | WS )
		int alt3=8;
		int LA3_0 = input.LA(1);
		if ( (LA3_0=='A') ) {
			int LA3_1 = input.LA(2);
			if ( (LA3_1=='N') ) {
				int LA3_10 = input.LA(3);
				if ( (LA3_10=='D') ) {
					int LA3_13 = input.LA(4);
					if ( ((LA3_13 >= '\u0000' && LA3_13 <= '\b')||(LA3_13 >= '\u000B' && LA3_13 <= '\f')||(LA3_13 >= '\u000E' && LA3_13 <= '\u001F')||LA3_13=='!'||(LA3_13 >= '#' && LA3_13 <= '\'')||(LA3_13 >= '*' && LA3_13 <= '\uFFFF')) ) {
						alt3=6;
					}

					else {
						alt3=1;
					}

				}

				else {
					alt3=6;
				}

			}

			else {
				alt3=6;
			}

		}
		else if ( (LA3_0=='(') ) {
			alt3=2;
		}
		else if ( (LA3_0=='N') ) {
			int LA3_3 = input.LA(2);
			if ( (LA3_3=='O') ) {
				int LA3_11 = input.LA(3);
				if ( (LA3_11=='T') ) {
					int LA3_14 = input.LA(4);
					if ( ((LA3_14 >= '\u0000' && LA3_14 <= '\b')||(LA3_14 >= '\u000B' && LA3_14 <= '\f')||(LA3_14 >= '\u000E' && LA3_14 <= '\u001F')||LA3_14=='!'||(LA3_14 >= '#' && LA3_14 <= '\'')||(LA3_14 >= '*' && LA3_14 <= '\uFFFF')) ) {
						alt3=6;
					}

					else {
						alt3=3;
					}

				}

				else {
					alt3=6;
				}

			}

			else {
				alt3=6;
			}

		}
		else if ( (LA3_0=='O') ) {
			int LA3_4 = input.LA(2);
			if ( (LA3_4=='R') ) {
				int LA3_12 = input.LA(3);
				if ( ((LA3_12 >= '\u0000' && LA3_12 <= '\b')||(LA3_12 >= '\u000B' && LA3_12 <= '\f')||(LA3_12 >= '\u000E' && LA3_12 <= '\u001F')||LA3_12=='!'||(LA3_12 >= '#' && LA3_12 <= '\'')||(LA3_12 >= '*' && LA3_12 <= '\uFFFF')) ) {
					alt3=6;
				}

				else {
					alt3=4;
				}

			}

			else {
				alt3=6;
			}

		}
		else if ( (LA3_0==')') ) {
			alt3=5;
		}
		else if ( (LA3_0=='\f') ) {
			alt3=6;
		}
		else if ( (LA3_0=='\"') ) {
			alt3=7;
		}
		else if ( ((LA3_0 >= '\u0000' && LA3_0 <= '\b')||LA3_0=='\u000B'||(LA3_0 >= '\u000E' && LA3_0 <= '\u001F')||LA3_0=='!'||(LA3_0 >= '#' && LA3_0 <= '\'')||(LA3_0 >= '*' && LA3_0 <= '@')||(LA3_0 >= 'B' && LA3_0 <= 'M')||(LA3_0 >= 'P' && LA3_0 <= '\uFFFF')) ) {
			alt3=6;
		}
		else if ( ((LA3_0 >= '\t' && LA3_0 <= '\n')||LA3_0=='\r'||LA3_0==' ') ) {
			alt3=8;
		}

		else {
			NoViableAltException nvae =
				new NoViableAltException("", 3, 0, input);
			throw nvae;
		}

		switch (alt3) {
			case 1 :
				// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:1:10: AND
				{
				mAND(); 

				}
				break;
			case 2 :
				// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:1:14: LPAREN
				{
				mLPAREN(); 

				}
				break;
			case 3 :
				// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:1:21: NOT
				{
				mNOT(); 

				}
				break;
			case 4 :
				// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:1:25: OR
				{
				mOR(); 

				}
				break;
			case 5 :
				// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:1:28: RPAREN
				{
				mRPAREN(); 

				}
				break;
			case 6 :
				// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:1:35: WORD
				{
				mWORD(); 

				}
				break;
			case 7 :
				// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:1:40: QUOTED
				{
				mQUOTED(); 

				}
				break;
			case 8 :
				// /Users/olinblodgett/git/fewi/src/org/jax/mgi/fewi/antlr/BooleanSearch/SolrBooleanSearch.g:1:47: WS
				{
				mWS(); 

				}
				break;

		}
	}



}
