package org.jax.mgi.fewi.forms;

/*-------*/
/* class */
/*-------*/

public class QuickSearchQueryForm {

    //--------------------//
    // instance variables
    //--------------------//
    private String query;

    //--------------------//
    // accessors
    //--------------------//
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }

    @Override
	public String toString() {
		return "QuickSearchQueryForm [query=" + query + "]";
	}
}
