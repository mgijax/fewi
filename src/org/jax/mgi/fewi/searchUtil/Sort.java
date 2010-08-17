package org.jax.mgi.fewi.searchUtil;

import java.util.*;

/**
 * A Sort is used to signify the sorting parameters of a result set
 */
public class Sort {


    //////////////////////////////////////////////////////////////////////////
    //  INTERNAL FIELDS
    //////////////////////////////////////////////////////////////////////////

	/**
	 * The name of the property
	 */
	protected String property;

	/**
	 * Accending or Decending order
	 */
	protected boolean desc = false;

	/**
	 * Request that string case be ignored
	 */
	protected boolean ignoreCase = false;



    //////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////

	/**
	 * Create an empty sort
	 */
	public Sort() {	}

	/**
	 * Create a sort for a given property.
	 * Defaults to decending, not ignoring case
	 */
	public Sort(String property) {
		this.property = property;
	}

	/**
	 * Create a sort for a given property and direction
	 * Defaults to ignoring case
	 */
	public Sort(String property, boolean desc) {
		this.property = property;
		this.desc = desc;
	}

	/**
	 * Create a sort for a given property, direction, case sensativity
	 * Defaults to ignoring case
	 */
	public Sort(String property, boolean desc, boolean ignoreCase) {
		this.property = property;
		this.desc = desc;
		this.ignoreCase = ignoreCase;
	}


    //////////////////////////////////////////////////////////////////////////
    //  BASIC ACCESSORS
    //////////////////////////////////////////////////////////////////////////

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean isDesc() {
		return desc;
	}

	public void setDesc(boolean desc) {
		this.desc = desc;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}



    //////////////////////////////////////////////////////////////////////////
    //  STATIC METHODS - CREATING NEW SORTS
    //////////////////////////////////////////////////////////////////////////

	public static Sort asc(String property) {
		return new Sort(property);
	}

	public static Sort asc(String property, boolean ignoreCase) {
		return new Sort(property, ignoreCase);
	}

	public static Sort desc(String property) {
		return new Sort(property, true);
	}

	public static Sort desc(String property, boolean ignoreCase) {
		return new Sort(property, true, ignoreCase);
	}

}
