package org.jax.mgi.fewi.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.StringBuffer;

import org.jax.mgi.fewi.config.ContextLoader;
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

	// logger
	private final Logger logger = LoggerFactory.getLogger(DotInputStrFactory.class);
	
	// cache fewi url, pulled from config
	private String fewiURL = ContextLoader.getConfigBean().getProperty("FEWI_URL");
	
	// storage of edges
	private List<Edge> edges = new ArrayList<Edge>();

	// storage of labels
	Map<String, String> labels = new HashMap<String, String>();
	
	// add an individual edge
	public void addEdge(String parent, String child) {
		if(parent != null && child != null) {
			Edge edge = new Edge();
			edge.setParent(parent);
			edge.setChild(child);
			edges.add(edge);
		}
	}

	// set a label for a given node
	public void addNodeLabel(String node, String label) {
		if(node != null && label != null) {
			labels.put(node, label);
		}
	}
	
	public String getNodeLabel(String node) {
		
		String nodeLabel = node;
		if (labels.containsKey(node)) {
			nodeLabel = labels.get(node);
		}
		return nodeLabel;
	}

	
	/////////////////////////////
	//  Dot String Generation
	/////////////////////////////

	public String getDotEdgesStr() {

		StringBuffer  sb = new StringBuffer(" ");
		for (Edge edge : edges ) {
			sb.append(" \"").append(edge.getParent()).append("\" -> \"").append(edge.getChild()).append("\" ");
		}
		sb.append(" ");
		//logger.info(sb.toString());
		return sb.toString();
	}

	public String getDotNodeLabels() {

		StringBuffer  sb = new StringBuffer(" ");
		for (Map.Entry<String, String> label : labels.entrySet()) {
			
			String node = label.getKey();
			String nodeLabel = label.getValue().replace(" ", "\\n").replace("'", "");
			sb.append(" \"").append(node).append("\" [label=\"").append(nodeLabel).append("\"] ");
		}
		sb.append(" ");
		//logger.info(sb.toString());
		return sb.toString();
	}

	public String getDotInputStr() {
		StringBuffer  sb = new StringBuffer("digraph {");
		sb.append(" graph [bgcolor=\"#ffffff\"] size=15 ratio=compress ");
		sb.append(" node [URL=\"").append(this.fewiURL).append("disease/\\\\N\" fontname=\"Helvetica\" fontsize=\"10\" shape=\"box\" style=\"rounded,filled\" color=\"#54709B\" fillcolor=\"#DFEFFF\" ] ");
		sb.append(" edge [color=\"#54709B\" penwidth=\"1.5\" dir=\"back\"]"); 
		sb.append( getDotNodeLabels() );
		sb.append( getDotEdgesStr() );
		sb.append(" ;} ");
		return sb.toString();
	}

	/////////////////////////////
	//  Inner class definitions
	/////////////////////////////
	
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
