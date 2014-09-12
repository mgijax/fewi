package org.jax.mgi.fewi.forms;


/*-------*/
/* class */
/*-------*/

public class MutationInvolvesQueryForm
{
    //--------------------//
    // instance variables
    //--------------------//
    private String alleleID;

    public MutationInvolvesQueryForm() {}

    //--------------------//
    // accessors
    //--------------------//
    public String getAlleleID() {
	return alleleID;
    }
    public void setAlleleID(String alleleID) {
	this.alleleID = alleleID;
    }

    @Override
    public String toString() {
	return "MutationInvolvesQueryForm [alleleID=" + alleleID + "]";
    }
}
