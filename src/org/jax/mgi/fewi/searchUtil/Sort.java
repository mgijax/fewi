package org.jax.mgi.fewi.searchUtil;


/**
 * A Sort is used to signify the sorting parameters of a result set
 */
public class Sort {


    /////////////////////////////////////////////////////////////////////////
    //  INTERNAL FIELDS
    /////////////////////////////////////////////////////////////////////////

	// The name of the property
	protected String sort;
	
	public static final String
		DIR_ASC = "asc",
		DIR_DESC = "desc";

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
	public Sort(String sort) {
		this.sort = sort;
	}

	/**
	 * Create a sort for a given property and direction
	 */
	public Sort(String sort, boolean desc) {
		this.sort = sort;
		this.desc = desc;
	}


    /////////////////////////////////////////////////////////////////////////
    //  BASIC ACCESSORS
    /////////////////////////////////////////////////////////////////////////

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
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

	@Override
	public String toString() {
		return "Sort [desc=" + desc + ", sort=" + sort + "]";
	}

	/* make and return a copy of Sort 'a', such that changes to the object
	 * returned will not affect 'a'.
	 */
	public static Sort copy(Sort a) {
		return new Sort(a.sort, a.desc);
	}
}
