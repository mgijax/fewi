package org.jax.mgi.fewi.forms;

import java.util.List;

public class StrainQueryForm {
	/***--- instance variables ---***/
	
	private String strainName;			// user's choice of strain name
	private List<String> strainType;	// user's selected strain types (chosen from strainTypeChoices)
	
	/***--- class variables ---***/
	
	// set of strain types that we want to present to the user for choices
	// (to be drawn from the database by the StrainController and passed into this object)
	private static List<String> strainTypeChoices = null;
	
	/***--- constructors ---***/
	
	public StrainQueryForm() {}

	/***--- getters and setters ---***/
	
	public String getStrainName() {
		return strainName;
	}

	public void setStrainName(String strainName) {
		this.strainName = strainName;
	}

	public List<String> getStrainType() {
		return strainType;
	}

	public void setStrainType(List<String> strainType) {
		this.strainType = strainType;
	}

	public static List<String> getStrainTypeChoices() {
		return StrainQueryForm.strainTypeChoices;
	}

	public static void setStrainTypeChoices(List<String> strainTypeChoices) {
		StrainQueryForm.strainTypeChoices = strainTypeChoices;
	}
	
	/***--- toString ---***/

	@Override
	public String toString() {
		return "StrainQueryForm [strainName=" + strainName + ", strainType=" + strainType + "]";
	}
}
