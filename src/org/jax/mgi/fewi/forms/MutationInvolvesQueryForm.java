package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
