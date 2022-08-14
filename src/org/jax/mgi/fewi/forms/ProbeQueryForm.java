package org.jax.mgi.fewi.forms;
import org.owasp.encoder.Encode;


/*-------*/
/* class */
/*-------*/

public class ProbeQueryForm
{
    //--------------------//
    // instance variables
    //--------------------//
    private String segmentType;
    private String markerID;
    private String referenceID;

    //--------------------//
    // constructor
    //--------------------//
    public ProbeQueryForm() {}

    //--------------------//
    // accessors
    //--------------------//

    public String getSegmentType() {
		return segmentType;
	}

	public void setSegmentType(String segmentType) {
		this.segmentType = segmentType;
	}

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
		if (segmentType != null) {
			if (sb.length() > 0) { sb.append("&"); }
			sb.append("segmentType=");
			sb.append(Encode.forHtml(segmentType));
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return "ProbeQueryForm [segmentType=" + segmentType + ", markerID=" + markerID
				+ ", referenceID=" + referenceID + "]";
	}
}
