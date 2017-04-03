package org.jax.mgi.fewi.util;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.StringBuffer;

import org.jax.mgi.fewi.controller.DiseaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mgi.frontend.datamodel.VocabChild;

/*
 * Example Usage
 
	GraphvizCreator graph = new GraphvizCreator("Graph Name");

digraph {
	graph [bgcolor="#ffffff"] 
	node [fontname="Helvetica" fontsize="10" shape="box" style="rounded,filled" color="black" fillcolor="#0099cc" URL="http://www.informatics.jax.org/disease/\N"] 
	edge [color="black" penwidth="2.0" ] "DOID:4" [label="disease"] "DOID:7" [label="disease of anatomical entity"] 
	"DOID:4" -> "DOID:7" "DOID:7" -> "DOID:10"
;}


digraph {graph [bgcolor="#ffffff"] node [fontname="Helvetica" fontsize="10" shape="box" style="rounded,filled" color="black" fillcolor="#0099cc" URL="http://www.informatics.jax.org/disease/\N"] edge [color="black" penwidth="2.0" ] "DOID:4" [label="disease"] "DOID:7" [label="disease of anatomical entity"] "DOID:4" -> "DOID:7" "DOID:7" -> "DOID:10";}


 */

public class DotInputStrFactory {

	private final Logger logger = LoggerFactory.getLogger(DotInputStrFactory.class);
	
	private List<Edge> edges = new ArrayList<Edge>();
	
	public void addEdge(String parent, String child) {
		if(parent != null && child != null) {
			Edge edge = new Edge();
			edge.setParent(parent);
			edge.setChild(child);
			edges.add(edge);
		}
	}

	public String getDotEdgesStr() {
		StringBuffer  sb = new StringBuffer(" ");
		sb.append(" ");
		for (Edge edge : edges ) {
			sb.append(" \"").append(edge.getParent()).append("\" -> \"").append(edge.getChild()).append("\" ");
		}
		sb.append(" ");
		logger.info(sb.toString());
		return sb.toString();
	}

	public String getDotInputStr() {
		StringBuffer  sb = new StringBuffer("digraph {");
		sb.append(" graph [bgcolor=\"#ffffff\"] ");
		sb.append(" node [fontname=\"Helvetica\" fontsize=\"10\" shape=\"box\" style=\"rounded,filled\" color=\"black\" fillcolor=\"#DFEFFF\" ] ");
		sb.append(" edge [color=\"black\" penwidth=\"2.0\" ]"); 
		sb.append( getDotEdgesStr() );
		sb.append(" ;} ");
		return sb.toString();
	}

	/*
	 * Inner class definitions
	 */
	
	class Edge {
		
		private String parent;
		private String child;
		   
		//parent
		public String getParent() {
			return parent.replace("'", "");
		}
		public void setParent(String parent) {
			this.parent = parent;
		}

		//child
		public String getChild() {
			return child.replace("'", "");
		}
		public void setChild(String child) {
			this.child = child;
		}

	}


}
