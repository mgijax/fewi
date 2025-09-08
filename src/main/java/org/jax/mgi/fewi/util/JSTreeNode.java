package org.jax.mgi.fewi.util;

import java.util.List;

import org.jax.mgi.shr.jsonmodel.BrowserChild;
import org.jax.mgi.shr.jsonmodel.BrowserTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Is: one node in a jsTree for the shared vocabulary browser
 */
public class JSTreeNode {

	/*--- instance variables ---*/

    private final Logger logger = LoggerFactory.getLogger(JSTreeNode.class);
	private static int nextID = 1;

	/*--- instance variables ---*/

	private int id = 0;
	private String edgeType;
	private boolean opened = false;
	private boolean selected = false;
	private String text;
	private String primaryID;
	private boolean hasChildren = false;
	private List<BrowserChild> children;
	private JSTreeNode chosenChild;
	private String annotationLinkText;
	private String annotationLinkUrl;
	private String hasAnnotations;
	
	/*--- constructors ---*/

	private JSTreeNode() {}			// hide default constructor

	/* basic constructor, using a BrowserTerm
	 */
	public JSTreeNode(BrowserTerm term) {
		this.id = getNewID();
		this.setBrowserTermProperties(term);
	}

	/* convenience constructor, so we can pass in other attributes at the same time
	 */
	public JSTreeNode(BrowserTerm term, String edgeType, boolean opened, boolean selected) {
		this.edgeType = edgeType;
		this.opened = opened;
		this.selected = selected;
		this.id = getNewID();
		this.setBrowserTermProperties(term);
	}

	/* basic constructor, using a BrowserChild
	 */
	public JSTreeNode(BrowserChild child) {
		this.id = getNewID();
		this.setBrowserChildProperties(child);
	}
	
	/* convenience constructor, so we can pass in other attributes at the same time
	 */
	public JSTreeNode(BrowserChild child, String edgeType, boolean opened, boolean selected) {
		this.edgeType = edgeType;
		this.opened = opened;
		this.selected = selected;
		this.id = getNewID();
		this.setBrowserChildProperties(child);
	}

	/*--- getters and setters ---*/

	public JSTreeNode setNodeID(String nodeID) {
		try {
			this.id = Integer.parseInt(nodeID);
		} catch (Exception e) {};
		return this;
	}
	
	/* When we are building the tree upward from a child, we need to tell the parent node about which
	 * one of its children is being used on the default path.  This method indicates that chosenChild.
	 */
	public void setChosenChild(JSTreeNode child) {
		this.chosenChild = child;
	}

	/* take the values we need from the BrowserTerm to initialize this object
	 */
	private void setBrowserTermProperties(BrowserTerm term) {
		this.text = term.getTerm();
		this.primaryID = term.getPrimaryID().getAccID();
		this.children = term.getChildren();
		this.hasChildren = ((this.children != null) && (this.children.size() > 0));
		this.annotationLinkText = term.getAnnotationLabel();
		this.annotationLinkUrl = term.getAnnotationUrl();
		this.hasAnnotations = term.getHasAnnotations();
	}
	
	/* take the values we need from the BrowserChild to initialize this object
	 */
	private void setBrowserChildProperties(BrowserChild child) {
		this.text = child.getTerm();
		this.primaryID = child.getPrimaryID();
		this.hasChildren = child.getHasChildren() > 0;
		this.annotationLinkText = child.getAnnotationLabel();
		this.annotationLinkUrl = child.getAnnotationUrl();
		this.hasAnnotations = child.getHasAnnotations();
	}
	
	/* get the next available ID, wrapping the nextID back to 1 if we go past the max int
	 */
	private static int getNewID() {
		int newID = nextID;
		nextID++;
		if (nextID < 0) {
			nextID = 1;
		}
		return newID;
	}

	/* set the type of edge between the parent and this term (This can differ, depending on which parent
	 * we're adding this term for.)
	 */
	public void setEdgeType(String edgeType) {
		this.edgeType = edgeType;
	}
	
	/* set this node to be opened (true) or not (false)
	 */
	public void setOpened(boolean opened) {
		this.opened = opened;
	}
	
	/* set this node to be selected (true) or not (false)
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/* get a JSON string for this node, with just a boolean flag to indicate whether the node has
	 * children (true) or not (false).
	 */
	public String toString() {
		return this.getJson(false);
	}
	
	/* get a JSON string for this node, also including any child nodes in a list.  Those child nodes
	 * will have a boolean flag to indicate whether then have children themselves (true) or not (false).
	 */
	public String toStringWithChildren() {
		return this.getJson(true);
	}
	
	/* generate the JSON string for this node, optionally including either the children or a flag to 
	 * indicate if there are children or not.
	 */
	private String getJson(boolean includeChildren) {
		StringBuffer sb = new StringBuffer();
		sb.append ("{");
		
		sb.append("\"id\" : \"");		// id field
		sb.append(this.id);
		sb.append("\"");
		
		sb.append(", \"text\" : \"");		// text field
		sb.append(this.text);
		sb.append("\"");
		
		sb.append(", \"state\" : {");	// state.opened and state.selected fields
		sb.append("\"opened\" : ");
		sb.append(this.opened);
		sb.append(", \"selected\" : ");
		sb.append(this.selected);
		sb.append("}");
		
		sb.append(", \"data\" : {\"accID\" : \"");	// term ID for the node, bundled into an accID field
		sb.append(this.primaryID);
		
		if (this.annotationLinkUrl != null) {
			sb.append("\", \"annotationUrl\" : \"");
			sb.append(this.annotationLinkUrl);
			sb.append("\"");
		} else {
			sb.append("\", \"annotationUrl\" : null");
		}

		if (this.annotationLinkText != null) {
			sb.append(", \"annotationLabel\" : \"");
			sb.append(this.annotationLinkText);
			sb.append("\"");
		}

		sb.append(", \"edgeType\" : \"");
		sb.append(this.edgeType);
		sb.append("\"");
		
		sb.append(", \"hasAnnotations\" : \"");
		sb.append(this.hasAnnotations);
		sb.append("\"}");

		// attribute for the <li> tag, used to help find like terms in tree
		sb.append(", \"li_attr\" : {\"accID\" : \"");
		sb.append(this.primaryID.replaceAll(":", "_"));
		sb.append("\"}");
		
		if (this.edgeType != null) {	// edge type becomes the icon to the left of the node
			sb.append(", \"icon\" : \"/assets/images/");
			sb.append(this.edgeType.replaceAll("-", "_") + ".gif");			// part_of.gif, is_a.gif, etc.
			sb.append("\"");
		}
		
		// children of this term; if we are not including their details, we at least need to include a
		// flag to indicate whether the node has children (true) or not (false)
		sb.append(", \"children\" : ");
		if (!includeChildren) {
			sb.append(this.hasChildren);
		} else {
			if (this.hasChildren && (this.children != null)) {
				int childCount = this.children.size();

				sb.append("[");
				for (BrowserChild bc : this.children) {
					if ((this.chosenChild == null) || !bc.getPrimaryID().equals(this.chosenChild.primaryID)) {
						JSTreeNode childNode = new JSTreeNode(bc, bc.getEdgeType(), false, false);
						sb.append(childNode.toString());
					} else {
						sb.append(this.chosenChild.toStringWithChildren());
					}
					if (--childCount > 0) {
						sb.append(",");
					}
				}
				sb.append("]");
			} else {
				sb.append(this.hasChildren);			// no children
			}
		}
		sb.append("}");
		return sb.toString();
	}
}
