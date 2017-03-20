package org.jax.mgi.fewi.summary;

/**
 * an autocomplete result for the shared vocabulary browser, including the matching term/synonym,
 * the term/synonym as it should be displayed, and the term's ID (used to update the term detail 
 * pane, if this result is chosen by the user).  There can be more than one of these objects for a
 * given vocabulary term, as the term itself could match, and zero or more synonyms could also match,
 * with each match yielding one of these objects.
 */
public class VocabBrowserACResult {

	//-------------------
	// instance variables
	//-------------------

	private String term;				// term or synonym matched by the user's search string
	private String autocompleteDisplay;	// how the term or synonym should be displayed in the A/C
	private String accID;				// primary ID of the term

	//-------------
	// constructors
	//-------------

    public VocabBrowserACResult () {
    	return;
    }

    public VocabBrowserACResult (String term, String autocompleteDisplay, String accID) {
    	this.term = term;
    	this.autocompleteDisplay = autocompleteDisplay;
    	this.accID = accID;
    	return;
    }

    //------------------------------------------------------------------------
    // public instance methods
    //------------------------------------------------------------------------

    // primary ID of the term
    public String getAccID() {
    	return this.accID;
    }

    // text of the term itself
    public String getTerm() {
    	return this.term;
    }
    
    // display string for autocomplete
    public String getAutocompleteDisplay() {
    	return this.autocompleteDisplay;
    }
    
    public void setAccID(String accID) {
    	this.accID = accID;
    }
    
    public void setTerm(String term) {
    	this.term = term;
    }
    
    public void setAutocompleteDisplay(String autocompleteDisplay) {
    	this.autocompleteDisplay = autocompleteDisplay;
    }
}
