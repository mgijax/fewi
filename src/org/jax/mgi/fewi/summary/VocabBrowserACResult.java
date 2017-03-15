package org.jax.mgi.fewi.summary;

/**
 * wrapper around a VocabBrowserSearchResult, hiding unneeded info that we don't need to waste time
 * transferring for every autocomplete keystroke
 */
public class VocabBrowserACResult {

	//-------------------
	// instance variables
	//-------------------

	private VocabBrowserSearchResult result;

	//-------------
	// constructors
	//-------------

    public VocabBrowserACResult (VocabBrowserSearchResult result) {
    	this.result = result;
    	return;
    }

    //------------------------------------------------------------------------
    // public instance methods
    //------------------------------------------------------------------------

    // primary ID of the term
    public String getAccID() {
    	return this.result.getAccID();
    }

    // text of the term itself
    public String getTerm() {
    	return this.result.getTerm();
    }
    
    // display string for autocomplete
    public String getAutocompleteDisplay() {
    	if (this.result.getMatchedTerm()) {
    		return this.result.getHighlightedTerm();
    	}
    	return this.result.getTerm() + " [" + this.result.getHighlightedSynonym() + "]";
    }
}
