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
	protected int operator;

	// List of filters, indicating this is a filter containing nested filters
	protected List<Filter> nestedFilters = new ArrayList<Filter>();

	// The type of comparison to use for joining nested filters
	protected int filterJoinClause;
	
	protected boolean negate=false;

    //////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////

	/**
	 * Create an empty filter
	 */
	public Filter() {}

	/**
	 * Creates a basic filter;  default operator 'equals'
	 */
	public Filter(String property, String value) {
		this.property = property;
		values.add(value);
		this.operator = OP_EQUAL;
	}

	/**
	 * Creates a basic filter; supply property/value/operator
	 */
	public Filter(String property, String value, int operator) {
		this.property = property;
		values.add(value);
		this.operator = operator;
	}
	
	/**
	 * Creates a basic filter; supply property/value/operator
	 */
	public Filter(String property,Integer value, int operator)
	{
		this.property = property;
		values.add(value.toString());
		this.operator = operator;
	}
	
	/**
	 * Creates a basic filter; supply property, valueList, and operator
	 */
	public Filter(String property, List<String> values, int operator) {
		this.property = property;
		this.values = values;
		this.operator = operator;
	}


    //////////////////////////////////////////////////////////////////////////
    //  CONSTANTS
    //////////////////////////////////////////////////////////////////////////

	// basic operators
	public static final int
		OP_EQUAL = 0,
		OP_NOT_EQUAL = 1,
		OP_LESS_THAN = 2,
		OP_GREATER_THAN = 3,
		OP_LESS_OR_EQUAL = 4,
		OP_GREATER_OR_EQUAL = 5,
		OP_LIKE = 6,
		OP_NULL = 7,
		OP_NOT_NULL = 8,
	    OP_BEGINS = 9,
	    OP_ENDS = 10,
	    OP_CONTAINS = 11,
	    OP_HAS_WORD = 12,
	    OP_WORD_BEGINS = 13,
	    OP_NOT_HAS = 14;

	/*
	 *  advanced operators
	 *  
	 *  Some of these are only subtley different from those above.
	 *  	When in doubt, use one of the basic operators
	 */
	public static final int
		OP_GREEDY_BEGINS = 99,
		OP_IN = 100,
		OP_NOT_IN = 101,
		OP_STRING_CONTAINS = 102;

	// operators for nested filters
	public static final int
		FC_AND = 1000,
		FC_OR = 1001;


    //////////////////////////////////////////////////////////////////////////
    //  BASIC ACCESSORS
    //////////////////////////////////////////////////////////////////////////

	// property
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}

	// value
	public String getValue() {
		if (values.size() > 0){
			return values.get(0);
		} 
		return null;
	}
	public List<String> getValues() {
		return values;
	}
	public void setValue(String value) {
		values.add(value);
	}
	public void setValues(List<String> values) {
		this.values = values;
	}

	// operator
	public int getOperator() {
		return operator;
	}
	public void setOperator(int operator) {
		this.operator = operator;
	}
	
	// negate this filter?
	public void negate() { this.negate=true; }
	public boolean doNegation() { return this.negate; }

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
	
	public void setNestedFilters(List<Filter> nestedFilters, int filterJoinClause)
	{
		this.nestedFilters = nestedFilters;
		this.filterJoinClause = filterJoinClause;
	}

	// filter join clause
	public int getFilterJoinClause() {
		return filterJoinClause;
	}
	public void setFilterJoinClause(int filterJoinClause) {
		this.filterJoinClause = filterJoinClause;
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

		boolean isBasic = true;
		if (nestedFilters!=null && !nestedFilters.isEmpty()) {
			isBasic = false;
		}
		return isBasic;
	}

    //////////////////////////////////////////////////////////////////////////
    //  STATIC METHODS - CREATING NEW FILTERS
    //////////////////////////////////////////////////////////////////////////


	/**
	 * Create a new Filter using the == operator.
	 */
	public static Filter equal(String property, String value) {
		return new Filter(property, value, OP_EQUAL);
	}

	/**
	 * Create a new Filter using the != operator.
	 */
	public static Filter notEqual(String property, String value) {
		return new Filter(property, value, OP_NOT_EQUAL);
	}

	/**
	 * Create a new Filter using the < operator.
	 */
	public static Filter lessThan(String property, String value) {
		return new Filter(property, value, OP_LESS_THAN);
	}

	/**
	 * Create a new Filter using the > operator.
	 */
	public static Filter greaterThan(String property, String value) {
		return new Filter(property, value, OP_GREATER_THAN);
	}

	/**
	 * Create a new Filter using the <= operator.
	 */
	public static Filter lessOrEqual(String property, String value) {
		return new Filter(property, value, OP_LESS_OR_EQUAL);
	}

	/**
	 * Create a new Filter using the >= operator.
	 */
	public static Filter greaterOrEqual(String property, String value) {
		return new Filter(property, value, OP_GREATER_OR_EQUAL);
	}

	/**
	 * Create a new Filter using the IN operator.
	 */
	public static Filter in(String property, List<String> values) {
		return new Filter(property, values, OP_IN);
	}

	/**
	 * Create a new Filter using the NOT IN operator.
	 */
	public static Filter notIn(String property, List<String> values) {
		return new Filter(property, values, OP_NOT_IN);
	}

	/**
	 * Create a new Filter using the LIKE operator.
	 */
	public static Filter like(String property, String value) {
		return new Filter(property, value, OP_LIKE);
	}

	/**
	 * Create a new container Filter (contains other filters) using
	 * the AND operator
	 */
	public static Filter and(List<Filter> filters) {
		Filter filter = new Filter();
		filter.setFilterJoinClause(FC_AND);
		filter.setNestedFilters(filters);
		return filter;
	}

	/**
	 * Create a new container Filter (contains other filters) using
	 * the OR operator
	 */
	public static Filter or(List<Filter> filters) {
		Filter filter = new Filter();
		filter.setFilterJoinClause(FC_OR);
		filter.setNestedFilters(filters);
		return filter;
	}



    // overriding toString() method to display property values
	@Override
	public String toString() {

        String returnString = new String();

        if (this.isBasicFilter()) {

            StringBuffer valueStrings = new StringBuffer();
            Iterator<String> valueIter = values.iterator();

            while (valueIter.hasNext()) {
              valueStrings.append(valueIter.next());
            }

            returnString = "Filter-["
				+ "property=" + property + " : "
				+ "values=" + valueStrings + "] ";
        }
        else {

            StringBuffer filterStrings = new StringBuffer();
            Iterator<Filter> filterIter = nestedFilters.iterator();

            filterStrings.append("[[NestedFilters=" + nestedFilters.size() + " -- ");
            while (filterIter.hasNext()) {
              filterStrings.append(filterIter.next().toString());
            }
            filterStrings.append("]] " );


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
	private void replaceProperty(Filter f,String propertyToReplace,String replacement)
	{
		if(f.property != null && f.property.equals(propertyToReplace)) f.setProperty(replacement);
		if(f.getNestedFilters()!=null)
		{
			for(Filter nestedF : f.getNestedFilters())
			{
				replaceProperty(nestedF,propertyToReplace,replacement);
			}
		}
	}


}
