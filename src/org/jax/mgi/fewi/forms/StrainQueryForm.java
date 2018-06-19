package org.jax.mgi.fewi.forms;

import java.util.List;

public class StrainQueryForm {
	/***--- class variables ---***/
	
	// set of strain attributes we want to present to the user for choices
	// (to be drawn from the database by the StrainController and passed into this object)
	private static List<String> attributeChoices = null;
	public static List<String> getAttributeChoices() {
		return StrainQueryForm.attributeChoices;
	}
	public static void setAttributeChoices(List<String> attributeChoices) {
		StrainQueryForm.attributeChoices = attributeChoices;
	}
	
	/***--- instance variables ---***/
	
	private String strainName;				// user's choice of strain name
	private Integer	isSequenced;			// has the strain been sequenced (1) or not (0)?
	private String group;					// user's selected strain grouping
	private List<String> attributes;		// user's selected strain attributes (chosen from attributeChoices)
	private List<String> attributeFilter;	// user's selections when filtering a result set by strain attribute

	private String referenceID;

	/***--- constructors ---***/
	
	public StrainQueryForm() {}

	/***--- getters and setters ---***/

	public List<String> getAttributeFilter() {
		return attributeFilter;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public String getGroup() {
		return group;
	}

	public Integer getIsSequenced() {
		return isSequenced;
	}
	
	public String getReferenceID() {
		return referenceID;
	}
	public String getStrainName() {
		return strainName;
	}
	public void setAttributeFilter(List<String> attributeFilter) {
		this.attributeFilter = attributeFilter;
	}
	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}
	public void setGroup(String group) {
		this.group = group;
	}

	public void setIsSequenced(Integer isSequenced) {
		this.isSequenced = isSequenced;
	}

	public void setReferenceID(String referenceID) {
		this.referenceID = referenceID;
	}

	public void setStrainName(String strainName) {
		this.strainName = strainName;
	}

	/***--- toString ---***/

	@Override
	public String toString() {
		return "StrainQueryForm [strainName=" + strainName + ", attributes=" + attributes + ", attributeFilter="
				+ attributeFilter + ", referenceID=" + referenceID + "]";
	}
}
