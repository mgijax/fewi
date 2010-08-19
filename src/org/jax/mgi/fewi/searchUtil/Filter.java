package org.jax.mgi.fewi.searchUtil;

import java.util.*;

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
		OP_NOT_NULL = 8;

	// advanced operators
	public static final int
		OP_IN = 100,
		OP_NOT_IN = 101;

	// operators for nested filters
	public static final int
		OP_AND = 1000,
		OP_OR = 1001,
		OP_NOT = 1002;



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
		return values.get(0);
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

	// filters
	public List<Filter> getNestedFilters() {
		return nestedFilters;
	}
	public void setNestedFilters(List<Filter> nestedFilters) {
		this.nestedFilters = nestedFilters;
	}


    //////////////////////////////////////////////////////////////////////////
    //  PROPERTY DETECTION FOR RECURSION
    //////////////////////////////////////////////////////////////////////////

	public boolean hasNestedFilters() {

		boolean hasNested = false;
		if (!nestedFilters.isEmpty() && nestedFilters.size() > 1) {
			hasNested = true;
		}
		return hasNested;
	}

	public boolean isBasicFilter() {

		boolean isBasic = true;
		if (!nestedFilters.isEmpty() && nestedFilters.size() > 1) {
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
		filter.setOperator(OP_AND);
		filter.setNestedFilters(filters);
		return filter;
	}

	/**
	 * Create a new container Filter (contains other filters) using
	 * the OR operator
	 */
	public static Filter or(List<Filter> filters) {
		Filter filter = new Filter();
		filter.setOperator(OP_OR);
		filter.setNestedFilters(filters);
		return filter;
	}
}
