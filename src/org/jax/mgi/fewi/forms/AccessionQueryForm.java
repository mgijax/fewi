package org.jax.mgi.fewi.forms;

/*-------*/
/* class */
/*-------*/

public class AccessionQueryForm {

    //--------------------//
    // instance variables
    //--------------------//
    private String id;
    
    // accessors
    //--------------------//
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

	@Override
	public String toString() {
		return "AccessionQueryForm [id=" + id + "]";
	}
}
