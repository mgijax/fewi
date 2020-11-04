package org.jax.mgi.fewi.forms;

/*-------*/
/* class */
/*-------*/

public class AccessionQueryForm {

    //--------------------//
    // instance variables
    //--------------------//
    private String id;
    
    private String flag;		// flag for special handling (optional)

    //--------------------//
    // accessors
    //--------------------//
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "AccessionQueryForm [id=" + id + "]";
	}
}
