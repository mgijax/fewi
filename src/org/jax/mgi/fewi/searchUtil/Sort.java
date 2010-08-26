package org.jax.mgi.fewi.searchUtil;

import java.util.*;

/**
 * A Sort is used to signify the sorting parameters of a result set
 */
public class Sort {


    /////////////////////////////////////////////////////////////////////////
    //  INTERNAL FIELDS
    /////////////////////////////////////////////////////////////////////////

	// The name of the property
	protected String property;

	// Accending or Decending order
	protected boolean desc = false;


    /////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    /////////////////////////////////////////////////////////////////////////

	/**
	 * Empty sort
	 */
	public Sort() {	}

	/**
	 * Create a sort for a given property.
	 * Defaults to decending
	 */
	public Sort(String property) {
		this.property = property;
	}

	/**
	 * Create a sort for a given property and direction
	 */
	public Sort(String property, boolean desc) {
		this.property = property;
		this.desc = desc;
	}


    /////////////////////////////////////////////////////////////////////////
    //  BASIC ACCESSORS
    /////////////////////////////////////////////////////////////////////////

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


    /////////////////////////////////////////////////////////////////////////
    //  STATIC METHODS - CREATING NEW SORTS
    /////////////////////////////////////////////////////////////////////////

	public static Sort asc(String property) {
		return new Sort(property);
	}


	public static Sort desc(String property) {
		return new Sort(property, true);
	}


}
