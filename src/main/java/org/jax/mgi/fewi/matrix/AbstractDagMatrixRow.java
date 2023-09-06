package org.jax.mgi.fewi.matrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jax.mgi.fe.datamodel.VocabTerm;


public abstract class AbstractDagMatrixRow<T extends AbstractDagMatrixRow<T>> implements MatrixRow {
	protected String rid;
	protected String term;
	protected boolean ex = false; // expandable
	protected OpenCloseState oc; // open/close state

	protected final List<T> children = new ArrayList<T>();
	protected final Set<String> uniqueChildIds = new HashSet<String>();

	public String getRid() {
		return rid;
	}

	public String getRowId() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public void setOc(OpenCloseState ocState) {
		this.oc = ocState;
	}

	public String getOc() {
		return this.oc != null ? this.oc.getState() : "";
	}

	public boolean getEx() {
		return this.ex;
	}

	// set expandable
	public void setEx(boolean ex) {
		this.ex = ex;
	}

	public void addChild(T row) {
		if (uniqueChildIds.contains(row.getRid()))
			return;
		uniqueChildIds.add(row.getRid());
		children.add(row);
	}

	public List<T> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		return printTree();
	}

	public String printTree() {
		return printTree(0);
	}

	public String printTree(int depth) {
		String output = "row(rid=" + rid + ",term=" + term;

		if (children.size() > 0) {

			output += ",children=";

			for (T child : children) {
				output += "\n";
				for (int i = 0; i < depth; i++) {
					output += "\t";
				}
				output += child.printTree(depth + 1);
			}
		}
		output += ")";
		return output;
	}
	
	/* must implement */
	public abstract T makeMatrixRow(VocabTerm term);
}
