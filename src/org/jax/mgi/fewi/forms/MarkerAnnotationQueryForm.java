package org.jax.mgi.fewi.forms;

/*----------------------------------------------------------------------------------*/
/* class: represents the query parameters that can be used with a marker annotation */
/*----------------------------------------------------------------------------------*/

public class MarkerAnnotationQueryForm {


    //--------------------//
    // instance variables
    //--------------------//

    private String mrkKey;
    private String vocab;
    private String restriction;

    //--------------------//
    // accessors
    //--------------------//

    public String getMrkKey() {
        return mrkKey;
    }
    public void setMrkKey(String mrkKey) {
        this.mrkKey = mrkKey;
    }
    public String getVocab() {
        return vocab;
    }
    public void setVocab(String vocab) {
        this.vocab = vocab;
    }
    public String getRestriction() {
        return restriction;
    }
    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }
    //--------------------//
    // toString
    //--------------------//
    @Override
    public String toString() {
        return "MarkerAnnotationQueryForm ["
            + (mrkKey != null ? "mrkKey=" + mrkKey + ", " : "")
            + (vocab != null ? "vocab=" + vocab + ", " : "")
            + "]";
    }
}
