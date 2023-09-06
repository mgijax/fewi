package org.jax.mgi.fewi.forms;

/*-------*/
/* class */
/*-------*/

public class FooQueryForm {


    //--------------------//
    // instance variables
    //--------------------//
    private String param1;
    private String param2;
    private String param3;


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


    //--------------------//
    // toString
    //--------------------//
    @Override
    public String toString() {
        return "FooQueryForm ["
            + (param1 != null ? "param1=" + param1 + ", " : "")
            + (param2 != null ? "param2=" + param2 + ", " : "")
            + (param3 != null ? "param3=" + param3 + ", " : "")
            + "]";
    }
}
