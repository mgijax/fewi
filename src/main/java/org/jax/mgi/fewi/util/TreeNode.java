package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fewi.config.ContextLoader;

public class TreeNode {

	/*--- instance variables ---*/

	private static String FEWI_URL = null;

	/*--- instance variables ---*/

	private String nodeType = "text";
	private String label = null;
	private boolean expanded = false;
	private int key = 0;
	private String head = null;
	private String help = null;
	private List<TreeNode> children = null;
	private boolean highlighted = false;
	private String accID = null;
	private boolean leaf = false;
	private boolean defaultPath = false;
	private boolean selected = false;
	
	/*--- constructors ---*/

	public TreeNode() {
		return;
	}

	public TreeNode(VocabTerm term) {
		this.setLabel(term.getTerm());
		this.setKey(term.getTermKey());
		this.setAccID(term.getPrimaryId());
		this.setHead(term.getTerm());
		this.setIsLeaf(term.getIsLeaf());
		return;
	}

	/*--- getters and setters ---*/

	public String getAccID() { return this.accID; }

	public void setAccID(String accID) { this.accID = accID; }

	public String getNodeType() { return this.nodeType; }

	// assumes nodeType is not null
	public void setNodeType(String nodeType) { this.nodeType = nodeType; }

	public boolean isOnDefaultPath() { return this.defaultPath; }

	public void setIsOnDefaultPath(boolean defaultPath) {
		this.defaultPath = defaultPath; 
	}

	public boolean isLeaf() { return this.leaf; }

	public void setIsLeaf(int leaf) {
		this.leaf = (leaf == 1);
	}

	public boolean isHighlighted() { return this.highlighted; }

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted; 
	}

	public String getLabel() { return this.label; }

	public void setLabel(String label) { this.label = label; }

	public boolean isSelected() { return this.selected; }

	public void setSelected(boolean selected) { this.selected = selected; }

	public boolean isExpanded() { return this.expanded; }

	public void setExpanded(boolean expanded) { this.expanded = expanded; }

	public int getKey() { return this.key; }
	
	public void setKey(int key) { this.key = key; }

	public String getHead() { return this.head; }

	public void setHead(String head) { this.head = head; }

	public String getHelp() { return this.help; }

	public void setHelp(String help) { this.help = help; }

	public void addChild(TreeNode child) {
		if (this.children == null) {
			this.children = new ArrayList<TreeNode>();
		}
		this.children.add(child);
		return;
	}

	public List<TreeNode> getChildren() { return this.children; }

	public String getJson() {
		if (FEWI_URL == null) {
			FEWI_URL = ContextLoader.getConfigBean().getProperty(
				"FEWI_URL");
		}

		StringBuffer sb = new StringBuffer();
		
		sb.append ("{");

		sb.append ("type:\"");
		sb.append (this.nodeType);
		sb.append ("\"");

		if (this.label != null) {
			sb.append(",label:\"");
			if (this.highlighted) {
			    sb.append (
				"<span style='background-color: yellow'><b>");
			}
			sb.append(this.label);
			if (this.highlighted) {
				sb.append ("</b></span>");
			}
			sb.append("\"");
		}

		if ((FEWI_URL != null) && (this.accID != null)) {
//			sb.append(",href:\"");
//			sb.append (FEWI_URL);
//			sb.append ("vocab/gxd/anatomy/");
//			sb.append (this.accID);
//			sb.append("\"");
			sb.append(",href:\"javascript:resetPanes('");
			sb.append (this.accID);
			sb.append("');\"");
		}

		sb.append(",enable_highlight:false");
		sb.append(",nowrap:true");

		if (this.selected) {
			sb.append(",selectedTerm:true");
		}

		sb.append(",expanded:");
		if (this.expanded) {
			sb.append("true");
		} else {
			sb.append("false");
		}

		if (this.accID != null) {
			sb.append(",accID:\"");
			sb.append(this.accID);
			sb.append("\"");
		}

		if (this.key > 0) {
			sb.append(",key:");
			sb.append(this.key);
		}

		if (this.head != null) {
			sb.append(",head:\"");
			sb.append(this.head);
			sb.append("\"");
		}

		if (this.help != null) {
			sb.append(",help:\"");
			sb.append(this.help);
			sb.append("\"");
		}

		if (this.leaf == true) {
			sb.append(",isLeaf:true");
		} else {
			sb.append(",isLeaf:false");
		}

		if (this.defaultPath == true) {
			sb.append(",defaultPath:true");
		}

		if (this.children != null) {
			sb.append(",children: [");
			for (TreeNode child : this.children) {
				sb.append(child.getJson());
			}
			sb.append("]");
		} else if (this.leaf == false) {
			sb.append(",dynamicLoadComplete:false");
		}

		sb.append ("}");

		return sb.toString().replace("}{", "},{");
	}
}
