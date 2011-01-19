package org.jax.mgi.fewi.forms;

/*-------*/
/* class */
/*-------*/

public class MarkerAnnotationQueryForm {


    //--------------------//
    // instance variables
    //--------------------//
    private String param1;
    private String param2;
    private String param3;
    private String mrkKey;
    private String vocab;


    //--------------------//
    // accessors
    //--------------------//

    // parameter 1
    public String getParam1() {
        return param1;
    }
    public void setParam1(String param1) {
        this.param1 = param1;
    }

    // parameter 2
    public String getParam2() {
        return param2;
    }
    public void setParam2(String param2) {
        this.param2 = param2;
    }

    // parameter 3
    public String getParam3() {
        return param3;
    }
    public void setParam3(String param3) {
        this.param3 = param3;
    }


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
