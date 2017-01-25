package org.jax.mgi.fewi.util.html;

public class SpanCreator extends ElementCreator {

	public SpanCreator() {
		super("");
	}
	public SpanCreator(String id) {
		super(id);
	}

	public String toString() {
		String ret = "<span";
		ret += super.toString();
		ret += ">";
		if(text != null && text.length() > 0) {
			ret += text;
		}
		ret += "</span>";
		return new String(ret);
	}

}
