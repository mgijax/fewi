package org.jax.mgi.fewi.forms;
import org.owasp.encoder.Encode;


/*-------*/
/* class */
/*-------*/

public class AntibodyQueryForm
{
    //--------------------//
    // instance variables
    //--------------------//
    private String markerID;
    private String referenceID;

    //--------------------//
    // constructor
    //--------------------//
    public AntibodyQueryForm() {}

    //--------------------//
    // accessors
    //--------------------//

	public String getMarkerID() {
		return markerID;
	}

	public void setMarkerID(String markerID) {
		this.markerID = markerID;
	}

	public String getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(String referenceID) {
		this.referenceID = referenceID;
	}

	public String toQueryString() {
		StringBuffer sb = new StringBuffer();
		if (markerID != null) {
			sb.append("markerID=");
			sb.append(Encode.forHtml(markerID));
		}
		if (referenceID != null) {
			if (sb.length() > 0) { sb.append("&"); }
			sb.append("referenceID=");
			sb.append(Encode.forHtml(referenceID));
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return "AntibodyQueryForm [markerID=" + markerID
				+ ", referenceID=" + referenceID + "]";
	}
}
