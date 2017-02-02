package org.jax.mgi.fewi.util.html;

public class LinkCreator extends ElementCreator {

	private String url = "";

	private boolean targetBlank = false;

	public LinkCreator(String id, String url) {
		super(id);
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean isTargetBlank() {
		return targetBlank;
	}
	public void setTargetBlank(boolean targetBlank) {
		this.targetBlank = targetBlank;
	}
	
	public String toString() {
		String ret = "<a";

		ret += super.toString();

		if(url != null && url.length() > 0) {
			ret += " href=\"" + url + "\"";
		}
		if(targetBlank) {
			ret += " target=\"_blank\"";
		}
		
		ret += ">";
		
		if(text != null && text.length() > 0) {
			ret += text;
		}
		ret += "</a>";
		return new String(ret);
	}

}
