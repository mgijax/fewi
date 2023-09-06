package org.jax.mgi.fewi.propertyMapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;

/**
 * The SolrPropertyMapper class handles the mapping of operators being passed in from the 
 * hunter, and mapping them specifically to Solr. 
 * 
 * It also handles the cases where we map 1 -> N column relationships between the hunters and
 * the underlying technology.
 * 
 * In theory this class should handle all possible cases for a normal filter, the only exception 
 * is where we might have to do some sort of special text manipulation.  For that we will need to 
 * have extend this class and override the appropriate function.
 * 
 * @author mhall
 * 
 * - refactored by kstone on 2013-05-02 to actually use objects properly. What a complete WTF this code was.
 */

public class ESPropertyMapper {
	List <String> fieldList = new ArrayList<String>();
	// The default operand is equals.
	int operand = 0;
	protected String singleField = "";
	protected String joinClause = "";

	/**
	 * The constructor that allows us to have an entire fieldlist.
	 * @param fieldList
	 * @param joinClause
	 */

	public ESPropertyMapper(List<String> fieldList, String joinClause) {
		this.fieldList = fieldList;
		this.joinClause = joinClause;
	}

	/**
	 * A single property.
	 * @param field
	 */

	public ESPropertyMapper(String field) {
		this.singleField = field;
	}

	public String getField() {
		return singleField;
	}

	public List<String> getFieldList() {
		return fieldList;
	}

	public String getJoinClause() {
		return joinClause;
	}

	/**
	 * This is the standard api, which returns a string that will be passed 
	 * to the underlying technology.
	 */

	public String getClause(String value,Operator operator) {
		return getClause(value,operator,false, 0);
	}
	
	public String getClause(Filter filter) {
		if(filter.getOperator() == Filter.Operator.OP_RANGE) {
			return getClause(filter.getValues(),filter.getOperator(),filter.isNegate());
		} else {
			return getClause(filter.getValue(),filter.getOperator(),filter.isNegate(), filter.getProximity());
		}
	}
	
	public String getClause(String value, Operator operator, boolean negate, int proximity)  {
		if (!singleField.equals("")) {
			return handleOperand(operator, value, singleField, negate, proximity);
		}
		// else handle multiple fields
		List<String> outClauses = new ArrayList<String>();
		for (String field: fieldList) {
			outClauses.add(handleOperand(operator, value, field));
		}
		String outClause = "("+StringUtils.join(outClauses," "+joinClause+" ")+")";
		if(negate) return "(*:* -"+outClause+")";
		return outClause;
	}
	
	public String getClause(List<String> values,Operator operator,boolean negate) {
		if (!singleField.equals("")) {
			return handleOperand(operator, values, singleField, negate);
		}
		// else handle multiple fields
		List<String> outClauses = new ArrayList<String>();
		for (String field: fieldList) {
			outClauses.add(handleOperand(operator, values, field));
		}
		String outClause = "("+StringUtils.join(outClauses," "+joinClause+" ")+")";
		if(negate) return "(*:* -"+outClause+")";
		return outClause;
	}

	/***
	 * This function handles the mappings from the hunters to the respective solr operands.
	 * It basically functions as a mapping from the front end to the back end.
	 * @param operand
	 * @param value
	 * @param field
	 * @return
	 */
	protected String handleOperand(Operator operand, String value, String field)
	{
		return handleOperand(operand,value,field,false, 0);
	}
	protected String handleOperand(Operator operand, String value, String field,boolean negate, int proximity) 
	{
		String val = "";
		if (operand == Filter.Operator.OP_EQUAL) {
			val =  field + ":\"" + value + "\"";
		}
		else if (operand == Filter.Operator.OP_EQUAL_WILDCARD_ALLOWED) {
			if (value.indexOf("*") >= 0) {
				val =  field + ":" + value;
			} else {
				val =  field + ":\"" + value + "\"";
			}
		}
		else if (operand == Filter.Operator.OP_PROXIMITY) {
			if(proximity > 0) {
				val =  field + ":\"" + value + "\"~" + proximity;
			} else {
				val =  field + ":\"" + value + "\"";
			}
		}
		else if (operand == Filter.Operator.OP_GREATER_THAN) {
			Integer newValue = new Integer(value);
			newValue++;
			val =  field + ":[" + newValue + " TO *]";
		}
		else if (operand == Filter.Operator.OP_LESS_THAN) {
			Integer newValue = new Integer(value);
			newValue--;
			val =  field + ":[* TO "+newValue+"]";
		}
		else if (operand == Filter.Operator.OP_WORD_BEGINS || operand == Filter.Operator.OP_HAS_WORD) {
			val =  field + ":" + value;
		}
		else if (operand == Filter.Operator.OP_GREATER_OR_EQUAL) {
			val =  field + ":[" + value + " TO *]";
		}
		else if (operand == Filter.Operator.OP_LESS_OR_EQUAL) {
			val =  field + ":[* TO "+value+"]";
		}
		else if (operand == Filter.Operator.OP_NOT_EQUAL) {
			val =  "*:* -" + field + ":" + value;
		}
		else if (operand == Filter.Operator.OP_NOT_HAS) {
			val =  "-" + field + ":\"" + value + "\"";
		}
		else if (operand == Filter.Operator.OP_BEGINS) {
			val =  field + ":" + value + "*";
		}
		else if (operand == Filter.Operator.OP_ENDS) {
			val =  field + ":" + "*" + value;
		}
		else if (operand == Filter.Operator.OP_CONTAINS) {
			val =  field + ":" + "(" + value + ")";
		}
		else if (operand == Filter.Operator.OP_CONTAINS_WITH_COLON) {
			val =  field + ":(\"" + value + "\")";
		}
		else if (operand == Filter.Operator.OP_STRING_CONTAINS) {
			val =  field + ":" + "*" + ClientUtils.escapeQueryChars(value) + "*";
		}
		else if (operand == Filter.Operator.OP_GREEDY_BEGINS) {
			val = "(" + field + ":" + value + " OR " + field + ":" + value + "*)";
		}

		if(negate) val = "-("+val+")";
		return val;
	}
	
	protected String handleOperand(Operator operand, List<String> values, String field) {
		return handleOperand(operand,values,field,false);
	}
	
	protected String handleOperand(Operator operand, List<String> values, String field,boolean negate)  {
		String val = "";
		if (operand == Filter.Operator.OP_RANGE) {
			val =  field + ":[" + values.get(0) + " TO " + values.get(1) + "]";
		}
		
		if(negate) val = "-("+val+")";
		return val;
	}
}
