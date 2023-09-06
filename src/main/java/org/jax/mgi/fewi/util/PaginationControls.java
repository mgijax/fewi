package org.jax.mgi.fewi.util;

// standard java
import org.jax.mgi.fewi.searchUtil.Paginator;


/**
* A PaginationControls object encapsulates the logic for MGI's internal
* pagination, assisting in the display of pagination droplists and
* display-time boolean logic.  Wraps Spring-loaded Paginator object.
*/
public class PaginationControls
{

    //---------------------------------------------------------------
    // instance variables
    //---------------------------------------------------------------

	// pagination data
	private Integer resultsTotal = 0;
	private Integer startIndex = 0;
	private Integer results = 0;


    //---------------------------------------------------------------
    // Constructors
    //---------------------------------------------------------------

    /**
    * uses spring-filled paginator object to retrieve pagination values
    */
    public PaginationControls (Paginator paginator){
        startIndex  = paginator.getStartIndex();
        results     = paginator.getResults();

        // ensure start index isn't negative
        if (startIndex < 0) {startIndex=0;}

        return;
    }


    //---------------------------------------------------------------
    // Public access methods
    //---------------------------------------------------------------

	public Integer getStartIndex() {
		return startIndex;
	}
	public Integer getStartIndexDisplay() {
		return startIndex + 1;
	}
	public Integer getResults() {
		return results;
	}

    // total # of results
	public Integer getResultsTotal() {
		return resultsTotal;
	}
	public void setResultsTotal(Integer resultsTotal) {
		this.resultsTotal = resultsTotal;
	}


    // total being displayed
	public Integer getLastResultDisplayed() {

        // derive ending
        Integer remainingResults = resultsTotal - startIndex;
        if (remainingResults < results) {
          return this.getResultsTotal();
	    }
	    return startIndex + results;
	}

    // drop list
	public String getDropList() {

        String rangeDropList = "<select name='results' " +
            "onchange='this.form.submit();'> " +
            "<OPTION VALUE='10'>10 " +
            "<OPTION VALUE='25'>25 " +
            "<OPTION VALUE='50'>50 " +
            "<OPTION VALUE='1000'>All " +
            "</select>";
        if (results == 25){
            rangeDropList = "<select name='results' " +
            "onchange='this.form.submit();'> " +
            "<OPTION VALUE='10'>10 " +
            "<OPTION VALUE='25' selected>25 " +
            "<OPTION VALUE='50'>50 " +
            "<OPTION VALUE='1000'>All " +
            "</select>";
        }
        if (results == 50){
            rangeDropList = "<select name='results' " +
            "onchange='this.form.submit();'> " +
            "<OPTION VALUE='10'>10 " +
            "<OPTION VALUE='25'>25 " +
            "<OPTION VALUE='50' selected>50 " +
            "<OPTION VALUE='1000'>All " +
            "</select>";
        }
        if (results == 1000){
            rangeDropList = "<select name='results' " +
            "onchange='this.form.submit();'> " +
            "<OPTION VALUE='10'>10 " +
            "<OPTION VALUE='25'>25 " +
            "<OPTION VALUE='50'>50 " +
            "<OPTION VALUE='1000' selected>All " +
            "</select>";
        }
	    return rangeDropList;
	}


    //----------------------------------
    // boolean tests for anchor display
    //----------------------------------

	public boolean getShowStartLink() {
        if (startIndex == 0) {return false;}
	    return true;
	}
	public boolean getShowPreviousLink() {
        if (startIndex == 0) {return false;}
	    return true;
	}
	public boolean getShowNextLink() {
        if (startIndex + results >= resultsTotal) {return false;}
	    return true;
	}
	public boolean getShowLastLink() {
        if (startIndex + results >= resultsTotal) {return false;}
	    return true;
	}

}
