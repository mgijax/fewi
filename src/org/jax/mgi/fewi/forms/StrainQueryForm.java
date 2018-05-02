package org.jax.mgi.fewi.forms;

import java.util.List;

public class StrainQueryForm {
	/***--- instance variables ---***/
	
	private String strainName;			// user's choice of strain name
	private List<String> attributes;	// user's selected strain attributes (chosen from attributeChoices)
	
	/***--- class variables ---***/
	
	// set of strain attributes we want to present to the user for choices
	// (to be drawn from the database by the StrainController and passed into this object)
	private static List<String> attributeChoices = null;
	
	/***--- constructors ---***/
	
	public StrainQueryForm() {}

	/***--- getters and setters ---***/
	
	public String getStrainName() {
		return strainName;
	}

	public void setStrainName(String strainName) {
		this.strainName = strainName;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public static List<String> getAttributeChoices() {
		return StrainQueryForm.attributeChoices;
	}

	public static void setAttributeChoices(List<String> attributeChoices) {
		StrainQueryForm.attributeChoices = attributeChoices;
	}
	
	/***--- toString ---***/

	@Override
	public String toString() {
		return "StrainQueryForm [strainName=" + strainName + ", attribute=" + attributes + "]";
	}
}
