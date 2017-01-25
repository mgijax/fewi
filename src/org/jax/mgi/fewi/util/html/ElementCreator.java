package org.jax.mgi.fewi.util.html;

import java.util.HashMap;

public class ElementCreator {

	private String id = "";
	private HashMap<String, String> styles = new HashMap<String, String>();
	private String onMouseOver = "";
	private String onMouseOut = "";
	protected String text = "";
	private String elementClass = "";

	public ElementCreator(String id) {
		this.id = id;
	}

	public String getOnMouseOver() {
		return onMouseOver;
	}
	public void setOnMouseOver(String onMouseOver) {
		this.onMouseOver = onMouseOver;
	}
	public String getOnMouseOut() {
		return onMouseOut;
	}
	public void setOnMouseOut(String onMouseOut) {
		this.onMouseOut = onMouseOut;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getLinkClass() {
		return elementClass;
	}
	public void setElementClass(String elementClass) {
		this.elementClass = elementClass;
	}
	
	public void addStyle(String key, String value) {
		styles.put(key, value);
	}

	public String toString() {
		String ret = "";
		if(id != null && id.length() > 0) {
			ret += " id=\"" + id + "\"";
		}
		if(elementClass != null && elementClass.length() > 0) {
			ret += " class=\"" + elementClass + "\"";
		}
		if(onMouseOver != null && onMouseOver.length() > 0) {
			ret += " onMouseOver=\"" + onMouseOver + "\"";
		}
		if(onMouseOut != null && onMouseOut.length() > 0) {
			ret += " onMouseOut=\"" + onMouseOut + "\"";
		}
		if(styles.size() > 0) {
			ret += " style=\"";
			for(String key: styles.keySet()) {
				ret += key + ":" + styles.get(key) + ";";
			}
			ret += "\"";
		}
		return ret;
	}

}
