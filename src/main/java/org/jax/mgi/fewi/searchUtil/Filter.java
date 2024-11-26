package org.jax.mgi.fewi.searchUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Filter is used to restrict a result set to be returned
 */
public class Filter {

    //////////////////////////////////////////////////////////////////////////
    //  INTERNAL FIELDS
    //////////////////////////////////////////////////////////////////////////

	// The name of the property
	protected String property;

	// The value to compare the property with
	protected List<String> values = new ArrayList<String>();

	// The type of comparison
	protected Operator operator = Operator.OP_EQUAL;

	// List of filters, indicating this is a filter containing nested filters
	protected List<Filter> nestedFilters = new ArrayList<Filter>();

	// The type of comparison to use for joining nested filters
	//
	// Beware that "join" has two meanings in this file.
	// (1) joining nested filters together with "AND" or "OR" operators, and
	// (2) A Solr join query between two indexes
	// 
	protected JoinClause filterJoinClause = JoinClause.FC_OR;
	
	protected boolean negate=false;

	// This is mostly for display purposes
	protected boolean quoted=false;
	
	// Used for proximity searches in multi valued fields
	protected int proximity = 0;
	
	// Used when operator == JOIN
	protected Filter joinQuery = null;
	protected String fromIndex = null;
	protected String fromField = null;
	protected String toField = null;


    //////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////

	public Filter() {}

	public Filter(String property, String value) {
		this.property = property;
		values.add(value);
		this.operator = Operator.OP_EQUAL;
	}

	/**
	 * Creates a basic filter; supply property/value/operator
	 */
	public Filter(String property, String value, Operator operator) {
		this.property = property;
		values.add(value);
		this.operator = operator;
	}
	
	/**
	 * Creates a basic filter; supply property/value/operator
	 */
	public Filter(String property,Integer value, Operator operator)
	{
		this.property = property;
		values.add(value.toString());
		this.operator = operator;
	}
	
	/**
	 * Creates a basic filter; supply property, valueList, and operator
	 */
	public Filter(String property, List<String> values, Operator operator) {
		this.property = property;
		this.values = values;
		this.operator = operator;
	}

	public Filter(String property, String value, int proximity) {
		this.property = property;
		values.add(value);
		this.operator = Operator.OP_PROXIMITY;
		this.proximity = proximity;
	}


	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		if (values.size() > 0){
			return values.get(0);
		} 
		return null;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	public List<String> getValues() {
		return values;
	}
	public void setValue(String value) {
		values.add(value);
	}
	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	// negate this filter?
	public boolean isNegate() {
		return negate;
	}
	public void setNegate(boolean negate) {
		this.negate = negate;
	}
	public void negate() {
		negate = !negate;
	}
	
	public int getProximity() {
		return proximity;
	}
	public void setProximity(int proximity) {
		this.proximity = proximity;
	}
	
	// filters
	public List<Filter> getNestedFilters() {
		return nestedFilters;
	}
	public void addNestedFilter(Filter nestedFilter) {
		this.nestedFilters.add(nestedFilter);
	}
	public void setNestedFilters(List<Filter> nestedFilters) {
		this.nestedFilters = nestedFilters;
	}
	
	public void setNestedFilters(List<Filter> nestedFilters, JoinClause filterJoinClause)
	{
		this.nestedFilters = nestedFilters;
		this.filterJoinClause = filterJoinClause;
	}

	// filter join clause (AND or OR)
	public JoinClause getFilterJoinClause() {
		return filterJoinClause;
	}
	public void setFilterJoinClause(JoinClause filterJoinClause) {
		this.filterJoinClause = filterJoinClause;
	}

	// JOIN operation properties
	public Filter getJoinQuery () {
		return joinQuery;
	}
	public void setJoinQuery (Filter jq) {
		joinQuery = jq;
	}
	public String getFromIndex () {
		return fromIndex;
	}
	public void setFromIndex (String fi) {
		fromIndex = fi;
	}
	public String getFromField () {
		return fromField;
	}
	public void setFromField (String ff) {
		fromField = ff;
	}
	public String getToField () {
		return toField;
	}
	public void setToField (String tf) {
		toField = tf;
	}
	// ------------

	public boolean isQuoted() {
		return quoted;
	}
	public void setQuoted(boolean quoted) {
		this.quoted = quoted;
	}


    //////////////////////////////////////////////////////////////////////////
    //  PROPERTY DETECTION FOR RECURSION
    //////////////////////////////////////////////////////////////////////////

	public boolean hasNestedFilters() {

		boolean hasNested = false;
		if (nestedFilters!=null && !nestedFilters.isEmpty()) {
			hasNested = true;
		}
		return hasNested;
	}

	public boolean isBasicFilter() {
		return (nestedFilters==null || nestedFilters.isEmpty()) && joinQuery == null;
	}

	/* retrieve the first-found Filter which has the given property name,
	 * beginning with this one and traversing into nested Filters, or
	 * null if none are found.
	 */
	public Filter getFirstFilterFor(String property) {
		if (isBasicFilter()) {
			if (property.equals(this.property)) {
				return this;
			}
			return null;
		}

		if (hasNestedFilters()) {
			for (Filter f : this.nestedFilters) {
				if (f.property != null && f.property.equals(property)) {
					return f;
				}
				Filter g = f.getFirstFilterFor(property);
				if (g != null) {
					return g;
				}
			}
		}
		return null;
	}

    //////////////////////////////////////////////////////////////////////////
    //  STATIC METHODS - CREATING NEW FILTERS
    //////////////////////////////////////////////////////////////////////////


	/**
	 * Create a new Filter using the == operator.
	 */
	public static Filter equal(String property, String value) {
		return new Filter(property, value, Operator.OP_EQUAL);
	}

	/**
	 * Create a new Filter using the != operator.
	 */
	public static Filter notEqual(String property, String value) {
		return new Filter(property, value, Operator.OP_NOT_EQUAL);
	}

	/**
	 * Create a new Filter using the < operator.
	 */
	public static Filter lessThan(String property, String value) {
		return new Filter(property, value, Operator.OP_LESS_THAN);
	}

	/**
	 * Create a new Filter using the > operator.
	 */
	public static Filter greaterThan(String property, String value) {
		return new Filter(property, value, Operator.OP_GREATER_THAN);
	}

	/**
	 * Create a new Filter using the <= operator.
	 */
	public static Filter lessOrEqual(String property, String value) {
		return new Filter(property, value, Operator.OP_LESS_OR_EQUAL);
	}

	/**
	 * Create a new Filter using the >= operator.
	 */
	public static Filter greaterOrEqual(String property, String value) {
		return new Filter(property, value, Operator.OP_GREATER_OR_EQUAL);
	}

	/**
	 * Create a new Filter using the IN operator.
	 */
	public static Filter in(String property, List<String> values) {
		return new Filter(property, values, Operator.OP_IN);
	}

	/**
	 * Create a new Filter using the NOT IN operator.
	 */
	public static Filter notIn(String property, List<String> values) {
		return new Filter(property, values, Operator.OP_NOT_IN);
	}
	
	/**
	 * Create a new Filter using the RANGE operator.
	 */
	public static Filter range(String property, String value1, String value2) {
		List<String> values = new ArrayList<String>();
		values.add(value1);
		values.add(value2);
		return new Filter(property, values, Operator.OP_RANGE);
	}

	/**
	 * Create a new Filter using the LIKE operator.
	 */
	public static Filter like(String property, String value) {
		return new Filter(property, value, Operator.OP_LIKE);
	}

	/**
	 * Create a new container Filter (contains other filters) using
	 * the AND operator
	 */
	public static Filter and(List<Filter> filters) {
		Filter filter = new Filter();
		filter.setFilterJoinClause(JoinClause.FC_AND);
		filter.setNestedFilters(filters);
		return filter;
	}

	/**
	 * Create a new container Filter (contains other filters) using
	 * the OR operator
	 */
	public static Filter or(List<Filter> filters) {
		Filter filter = new Filter();
		filter.setFilterJoinClause(JoinClause.FC_OR);
		filter.setNestedFilters(filters);
		return filter;
	}

	/**
	 */
	public static Filter join(String fromIndex, String fromField, String toField, Filter joinQuery) {
		Filter filter = new Filter();
		filter.setOperator( Operator.OP_JOIN );
		filter.setJoinQuery( joinQuery );
		filter.setFromIndex( fromIndex );
		filter.setFromField( fromField );
		filter.setToField( toField );
		return filter;
	}

	public static Filter extractTermsForNestedFilter(Filter filter) {
		Filter nestedFilter = new Filter();
		extractTermsForNestedFilter(filter, nestedFilter);
		return nestedFilter;
	}

	public static void extractTermsForNestedFilter(Filter filter, Filter nestedFilter) {
		if(filter.getNestedFilters().size() > 0) {
			for(Filter f: filter.getNestedFilters()) {
				extractTermsForNestedFilter(f, nestedFilter);
			}
		} else {
			if(filter.getProperty().equals(SearchConstants.VOC_TERM)) {
				nestedFilter.addNestedFilter(new Filter(filter.getProperty(), filter.getValue(), filter.getOperator()));
			}
		}
	}

    // overriding toString() method to display property values
	@Override
	public String toString() {

        String returnString = new String();

        if (isBasicFilter()) {

            StringBuffer valueStrings = new StringBuffer();
            Iterator<String> valueIter = values.iterator();

            // handle first iteration up front, so we can include commas in the 'while'
            if (valueIter.hasNext()) {
              valueStrings.append(valueIter.next());
            }

            while (valueIter.hasNext()) {
              valueStrings.append(",");
              valueStrings.append(valueIter.next());
            }

            returnString = "Filter-[" + property + " " + operator.getName() + " " + valueStrings + ", NOT: " + negate + " QUOTE: " + quoted + " Nest: " + (nestedFilters.size() > 0) + "] ";
        }
        else {

	    StringBuffer filterStrings = new StringBuffer();
	    if (joinQuery == null) {
		Iterator<Filter> filterIter = nestedFilters.iterator();

		filterStrings.append("[[NestedFilters=" + nestedFilters.size() + " NOT: " + negate + " -- ");
		while (filterIter.hasNext()) {
		    filterStrings.append(filterIter.next().toString());
		}
		filterStrings.append("]] " );

	    }
	    else {
	        filterStrings.append(String.format("[[Join FromIndex=%s FromField=%s ToField=%s JoinQuery=%s]]",
		    fromIndex, fromField, toField, joinQuery.toString() ));
	    }
	    returnString = filterStrings.toString();

        }

        return returnString;

	}


	/*
	 * A function for recursively replacing every property in a fiter with a different one.
	 *  searches all nested filters
	 */
	public void replaceProperty(String propertyToReplace, String replacement)
	{
		replaceProperty(this,propertyToReplace,replacement);
	}
	// the necessary recursive function
	private void replaceProperty(Filter f,String propertyToReplace,String replacement) {
		if(f.property != null && f.property.equals(propertyToReplace)) f.setProperty(replacement);
		if(f.getNestedFilters()!=null) {
			for(Filter nestedF : f.getNestedFilters()) {
				replaceProperty(nestedF,propertyToReplace,replacement);
			}
		}
	}

	public void Accept(VisitorInterface pi) {
		pi.Visit(this);
	}

	public String getOperatorString() {
		return operator.getName();
	}
	
	public enum JoinClause {
		FC_AND(1000, "AND"),
		FC_OR(1001, "OR")
		;
		
		private int	id = 0;
		private String name = "";

		JoinClause(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public int getId() {
			return id;
		}
		public String getName() {
			return name;
		}
	}
	
	public enum Operator {
		OP_EQUAL(0, "="),
		OP_NOT_EQUAL(1, "!="),
		OP_LESS_THAN(2, "<"),
		OP_GREATER_THAN(3, ">"),
		OP_LESS_OR_EQUAL(4, "<="),
		OP_GREATER_OR_EQUAL(5, ">="),
		OP_LIKE(6, "LIKE"),
		OP_NULL(7, "NULL"),
		OP_NOT_NULL(8, "!NULL"),
		OP_BEGINS(9, "BIGINS"),
		OP_ENDS(10, "ENDS"),
		OP_CONTAINS(11, "CONTAINS"),
		OP_HAS_WORD(12, "HAS WORD"),
		OP_WORD_BEGINS(13, "WORD BEGINS"),
		OP_NOT_HAS(14, "!HAS"),
		OP_CONTAINS_WITH_COLON(15, "CONTAINS WITH COLON"),
		OP_GREEDY_BEGINS(99, "GREEDY BEGIN"),
		OP_IN(100, "IN"),
		OP_NOT_IN(101, "!IN"),
		OP_PROXIMITY(103, "proximity"),
		OP_STRING_CONTAINS(102, "STRING CONTAINS"),
		OP_EQUAL_WILDCARD_ALLOWED(200, "EQUAL WITH WILDCARD"),
		OP_RANGE(201, "RANGE"),
		OP_JOIN(301, "JOIN")
	    ;
		
		private int	id = 0;
		private String name = "";

		Operator(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public int getId() {
			return id;
		}
		public String getName() {
			return name;
		}
	}

	/* make a copy of Filter 'a' and return it, such that any modifications
	 * to the copy will not affect Filter 'a'.
	 */
	public static Filter copy(Filter a) {
		Filter b = new Filter();

		b.property = a.property;
		b.negate = a.negate;
		b.quoted = a.quoted;
		b.proximity = a.proximity;
		b.operator = a.operator;
		b.filterJoinClause = a.filterJoinClause;

		for (String v : a.values) {
			b.values.add(v);
		}

		for (Filter f : a.nestedFilters) {
			b.nestedFilters.add(Filter.copy(f));
		}
		return b;
	}
}
