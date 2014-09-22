grammar SolrBooleanSearch;

options {
  language=Java;
}


tokens {
	NOT = 'NOT';
	AND = 'AND';
	OR = 'OR';
	RPAREN = ')';
	LPAREN = '(';
}

@header {
package org.jax.mgi.fewi.antlr.BooleanSearch;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.apache.solr.client.solrj.util.ClientUtils;
}

@rulecatch {
  catch (RecognitionException re) {
    //Custom handling of an exception. Any java code is allowed.
    throw re;
  }
}

@lexer::header {package org.jax.mgi.fewi.antlr.BooleanSearch;}

@members {
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
}


query	returns[Filter queryNode]:	
	(AND|OR)* andExpr=and_expr { queryNode = andExpr; } (AND|OR|NOT)*
	;
	
and_expr	returns[Filter andExpression] @init { andExpression = new Filter(); andExpression.setFilterJoinClause(Filter.JoinClause.FC_AND); }:	
	oe1=or_expr { if(oe1!=null) andExpression.addNestedFilter(oe1); } (AND oe2=or_expr { andExpression.addNestedFilter(oe2); })* 
	;
	
or_expr	returns[Filter orExpression] @init { orExpression = new Filter(); orExpression.setFilterJoinClause(Filter.JoinClause.FC_OR); }:	
	t1=term { if(notEmpty(t1)) orExpression.addNestedFilter(t1); } ((OR)? t2=term { if(notEmpty(t2)) orExpression.addNestedFilter(t2); })*
	;

term	returns[Filter termNode] @init { termNode = new Filter(); boolean neg=false; }:	
	(NOT { neg=true; })? s=str { termNode=s; if(neg) termNode.negate(); }
	| (NOT { neg=true; })? LPAREN e=and_expr RPAREN { termNode=e; if(neg) termNode.negate(); } 
	;

str returns[Filter stringFilter] @init { stringFilter = new Filter(); } :
        q=QUOTED { stringFilter.setQuoted(true); stringFilter.setValue(sanitizeQuoted($q.text)); stringFilter.setOperator(Filter.Operator.OP_EQUAL); }
        | q2=WORD { setWordFilter(stringFilter,sanitizeQuoted($q2.text)); }
        ;

WORD :  (~( ' ' | '\t' | '\r' | '\n' | '(' | ')' | '"' ))+;

QUOTED: '"'(~'"')*'"';

WS  : (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;};