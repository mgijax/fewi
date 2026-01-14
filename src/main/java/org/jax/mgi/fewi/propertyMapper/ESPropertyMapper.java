package org.jax.mgi.fewi.propertyMapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;

import co.elastic.clients.elasticsearch._types.query_dsl.NumberRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

/**
 * The SolrPropertyMapper class handles the mapping of operators being passed in
 * from the hunter, and mapping them specifically to Solr.
 * 
 * It also handles the cases where we map 1 -> N column relationships between
 * the hunters and the underlying technology.
 * 
 * In theory this class should handle all possible cases for a normal filter,
 * the only exception is where we might have to do some sort of special text
 * manipulation. For that we will need to have extend this class and override
 * the appropriate function.
 * 
 * @author mhall
 * 
 *         - refactored by kstone on 2013-05-02 to actually use objects
 *         properly. What a complete WTF this code was.
 */

public class ESPropertyMapper {
	List<String> fieldList = new ArrayList<String>();
	// The default operand is equals.
	int operand = 0;
	protected String singleField = "";
	protected String joinClause = "";

	/**
	 * The constructor that allows us to have an entire fieldlist.
	 * 
	 * @param fieldList
	 * @param joinClause
	 */

	public ESPropertyMapper(List<String> fieldList, String joinClause) {
		this.fieldList = fieldList;
		this.joinClause = joinClause;
	}

	/**
	 * A single property.
	 * 
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
	 * This is the standard api, which returns a string that will be passed to the
	 * underlying technology.
	 */

	public String getClause(String value, Operator operator) {
		return getClause(value, operator, false, 0);
	}

	public String getClause(Filter filter) {
		if (filter.getOperator() == Filter.Operator.OP_RANGE) {
			return getClause(filter.getValues(), filter.getOperator(), filter.isNegate());
		} else {
			return getClause(filter.getValue(), filter.getOperator(), filter.isNegate(), filter.getProximity());
		}
	}

	public String getClause(String value, Operator operator, boolean negate, int proximity) {
		if (!singleField.equals("")) {
			return handleOperand(operator, value, singleField, negate, proximity);
		}
		// else handle multiple fields
		List<String> outClauses = new ArrayList<String>();
		for (String field : fieldList) {
			outClauses.add(handleOperand(operator, value, field));
		}
		String outClause = "(" + StringUtils.join(outClauses, " " + joinClause + " ") + ")";
		if (negate)
			return "(*:* -" + outClause + ")";
		return outClause;
	}

	public String getClause(List<String> values, Operator operator, boolean negate) {
		if (!singleField.equals("")) {
			return handleOperand(operator, values, singleField, negate);
		}
		// else handle multiple fields
		List<String> outClauses = new ArrayList<String>();
		for (String field : fieldList) {
			outClauses.add(handleOperand(operator, values, field));
		}
		String outClause = "(" + StringUtils.join(outClauses, " " + joinClause + " ") + ")";
		if (negate)
			return "(*:* -" + outClause + ")";
		return outClause;
	}

	/***
	 * This function handles the mappings from the hunters to the respective solr
	 * operands. It basically functions as a mapping from the front end to the back
	 * end.
	 * 
	 * @param operand
	 * @param value
	 * @param field
	 * @return
	 */
	protected String handleOperand(Operator operand, String value, String field) {
		return handleOperand(operand, value, field, false, 0);
	}

	protected String handleOperand(Operator operand, String value, String field, boolean negate, int proximity) {
		String val = "";
		if (operand == Filter.Operator.OP_EQUAL) {
			val = field + ":\"" + value + "\"";
		} else if (operand == Filter.Operator.OP_EQUAL_WILDCARD_ALLOWED) {
			if (value.indexOf("*") >= 0) {
				val = field + ":" + value;
			} else {
				val = field + ":\"" + value + "\"";
			}
		} else if (operand == Filter.Operator.OP_PROXIMITY) {
			if (proximity > 0) {
				val = field + ":\"" + value + "\"~" + proximity;
			} else {
				val = field + ":\"" + value + "\"";
			}
		} else if (operand == Filter.Operator.OP_GREATER_THAN) {
			Integer newValue = Integer.parseInt(value);
			newValue++;
			val = field + ":[" + newValue + " TO *]";
		} else if (operand == Filter.Operator.OP_LESS_THAN) {
			Integer newValue = Integer.parseInt(value);
			newValue--;
			val = field + ":[* TO " + newValue + "]";
		} else if (operand == Filter.Operator.OP_WORD_BEGINS || operand == Filter.Operator.OP_HAS_WORD) {
			val = field + ":" + value;
		} else if (operand == Filter.Operator.OP_GREATER_OR_EQUAL) {
			val = field + ":[" + value + " TO *]";
		} else if (operand == Filter.Operator.OP_LESS_OR_EQUAL) {
			val = field + ":[* TO " + value + "]";
		} else if (operand == Filter.Operator.OP_NOT_EQUAL) {
			val = "*:* -" + field + ":" + value;
		} else if (operand == Filter.Operator.OP_NOT_HAS) {
			val = "-" + field + ":\"" + value + "\"";
		} else if (operand == Filter.Operator.OP_BEGINS) {
			val = field + ":" + value + "*";
		} else if (operand == Filter.Operator.OP_ENDS) {
			val = field + ":" + "*" + value;
		} else if (operand == Filter.Operator.OP_CONTAINS) {
			val = field + ":" + "(" + value + ")";
		} else if (operand == Filter.Operator.OP_CONTAINS_WITH_COLON) {
			val = field + ":(\"" + value + "\")";
		} else if (operand == Filter.Operator.OP_STRING_CONTAINS) {
			val = field + ":" + "*" + ClientUtils.escapeQueryChars(value) + "*";
		} else if (operand == Filter.Operator.OP_GREEDY_BEGINS) {
			val = "(" + field + ":" + value + " OR " + field + ":" + value + "*)";
		}

		if (negate)
			val = "-(" + val + ")";
		return val;
	}

	protected String handleOperand(Operator operand, List<String> values, String field) {
		return handleOperand(operand, values, field, false);
	}

	protected String handleOperand(Operator operand, List<String> values, String field, boolean negate) {
		String val = "";
		if (operand == Filter.Operator.OP_RANGE) {
			val = field + ":[" + values.get(0) + " TO " + values.get(1) + "]";
		}

		if (negate)
			val = "-(" + val + ")";
		return val;
	}
	
    /* ==============================
    Public API
    ============================== */

	 public Query getClauseQuery(Filter filter) {
	     if (filter.getOperator() == Operator.OP_RANGE) {
	         return getClauseQuery(filter.getValues(), filter.getOperator(), filter.isNegate());
	     }
	     return getClauseQuery(
	         filter.getValue(),
	         filter.getOperator(),
	         filter.isNegate(),
	         filter.getProximity()
	     );
	 }
	
	 public Query getClauseQuery(String value, Operator operator, boolean negate, int proximity) {
	     Query base;
	
	     if (!singleField.isEmpty()) {
	         base = handleOperandQuery(operator, value, singleField, proximity);
	     } else {
	         List<Query> clauses = new ArrayList<>();
	         for (String field : fieldList) {
	             clauses.add(handleOperandQuery(operator, value, field, proximity));
	         }
	         base = orJoin(clauses);
	     }
	
	     return negate ? negate(base) : base;
	 }
	
	 public Query getClauseQuery(List<String> values, Operator operator, boolean negate) {
	     Query base;
	
	     if (!singleField.isEmpty()) {
	         base = handleOperandQuery(operator, values, singleField);
	     } else {
	         List<Query> clauses = new ArrayList<>();
	         for (String field : fieldList) {
	             clauses.add(handleOperandQuery(operator, values, field));
	         }
	         base = orJoin(clauses);
	     }
	
	     return negate ? negate(base) : base;
	 }
	
	 /* ==============================
	    Operator Handlers (String)
	    ============================== */
	
	 protected Query handleOperandQuery(Operator op, String value, String field, int proximity) {
	
	     switch (op) {
	
	         case OP_EQUAL:
	         case OP_EQUAL_WILDCARD_ALLOWED:
	             return term(field, value);
	
	         case OP_PROXIMITY:
	             return proximity > 0
	                 ? matchPhrase(field, value, proximity)
	                 : term(field, value);
	
	         case OP_GREATER_THAN:
	         	 return numberRange(field, r -> r.gt(Double.parseDouble(value)));
	
	         case OP_GREATER_OR_EQUAL:
	             return numberRange(field, r -> r.gte(Double.parseDouble(value)));
	
	         case OP_LESS_THAN:
	             return numberRange(field, r -> r.lt(Double.parseDouble(value)));
	
	         case OP_LESS_OR_EQUAL:
	             return numberRange(field, r -> r.lte(Double.parseDouble(value)));
	
	         case OP_WORD_BEGINS:
	         case OP_HAS_WORD:
	         case OP_BEGINS:
	             return prefix(field, value);
	
	         case OP_ENDS:
	             return wildcard(field, "*" + value);
	
	         case OP_CONTAINS:
	         case OP_CONTAINS_WITH_COLON:
	         case OP_STRING_CONTAINS:
	             return wildcard(field, "*" + value + "*");
	
	         case OP_NOT_EQUAL:
	         case OP_NOT_HAS:
	             return negate(term(field, value));
	
	         case OP_GREEDY_BEGINS:
	             return orJoin(List.of(
	                 term(field, value),
	                 prefix(field, value)
	             ));
	
	         default:
	             throw new UnsupportedOperationException("Unsupported operator: " + op);
	     }
	 }
	
	 /* ==============================
	    Operator Handlers (Range List)
	    ============================== */
	
	 protected Query handleOperandQuery(Operator op, List<String> values, String field) {
	     if (op == Operator.OP_RANGE) {
	         return Query.of(q -> q.range(r -> r
	             .number(n -> n
	                 .field(field)
	                 .gte(Double.parseDouble(values.get(0)))
	                 .lte(Double.parseDouble(values.get(1)))
	             )
	         ));
	     }
	     throw new UnsupportedOperationException("Unsupported list operator: " + op);
	 }
	
	 /* ==============================
	    Query Builders
	    ============================== */
	
	 private Query term(String field, String value) {
	     return Query.of(q -> q.term(t -> t.field(field).value(value)));
	 }
	
	 private Query prefix(String field, String value) {
	     return Query.of(q -> q.prefix(p -> p.field(field).value(value)));
	 }
	
	 private Query wildcard(String field, String value) {
	     return Query.of(q -> q.wildcard(w -> w.field(field).value(value)));
	 }
	
	 private Query matchPhrase(String field, String value, int slop) {
	     return Query.of(q -> q.matchPhrase(mp -> mp
	         .field(field)
	         .query(value)
	         .slop(slop)
	     ));
	 }
	
	 private Query numberRange(
	 	    String field,
	 	    java.util.function.Function<NumberRangeQuery.Builder, NumberRangeQuery.Builder> fn
	 	) {
	 	    NumberRangeQuery nrq = fn
	 	        .apply(new NumberRangeQuery.Builder().field(field))
	 	        .build();   // ✅ BUILD IT
	
	 	    return Query.of(q -> q.range(r -> r.number(nrq)));
	 	}
	
	
	 /* ==============================
	    Bool Helpers
	    ============================== */
	
	 private Query orJoin(List<Query> clauses) {
	     return Query.of(q -> q.bool(b -> {
	         clauses.forEach(b::should);
	         b.minimumShouldMatch("1");
	         return b;
	     }));
	 }
	
	 private Query negate(Query q) {
	     return Query.of(qb -> qb.bool(b -> b.mustNot(q)));
	 }	
}
