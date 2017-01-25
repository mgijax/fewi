package org.jax.mgi.fewi.util.html;

public class DivCreator extends ElementCreator {

	public DivCreator() {
		super("");
	}
	public DivCreator(String id) {
		super(id);
	}

	public String toString() {
		String ret = "<div";
		ret += super.toString();
		ret += ">";
		if(text != null && text.length() > 0) {
			ret += text;
		}
		ret += "</div>";
		return new String(ret);
	}

}
