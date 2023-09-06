package org.jax.mgi.fewi.forms;

import java.util.List;

/*-------*/
/* class */
/*-------*/

public class SequenceQueryForm {
    private List<String> type;
    private List<String> strain;
    private String refKey;
    private String mrkKey;
    private String provider;

	public String getMrkKey() {
		return mrkKey;
	}
	public String getProvider() {
		return provider;
	}
	public String getRefKey() {
		return refKey;
	}
	public List<String> getStrain() {
		return strain;
	}
	public List<String> getType() {
		return type;
	}
	public void setMrkKey(String mrkKey) {
		this.mrkKey = mrkKey;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public void setRefKey(String refKey) {
		this.refKey = refKey;
	}
	public void setStrain(List<String> strain) {
		this.strain = strain;
	}
	public void setType(List<String> type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "SequenceQueryForm [type=" + type + ", strain=" + strain + ", refKey=" + refKey + ", mrkKey=" + mrkKey
				+ ", provider=" + provider + "]";
	}
}
