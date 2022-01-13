package org.jax.mgi.fewi.summary;

// Is: as small an object as we can use for transferring vital data when the Quick Search needs it
// for forwarding to other resources.  Field is called "item", often used for IDs or symbols.
public class QSTinyResult {
	private String item;
	
	public QSTinyResult(String item) {
		this.item = item;
	}
	
	public String getItem() { return item; }
}
