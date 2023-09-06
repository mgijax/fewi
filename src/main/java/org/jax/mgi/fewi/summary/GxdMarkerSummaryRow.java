package org.jax.mgi.fewi.summary;

//public class GxdMarkerSummary {
//
//}

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.util.FormatHelper;


/**
 * wrapper around a marker;  represents on row in summary
 */
public class GxdMarkerSummaryRow {
	//-------------------
		// instance variables
		//-------------------
		// encapsulated row object
		private SolrGxdMarker marker;

		// config values
	  String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

		private String score;

		//-------------
		// constructors
		//-------------

	  public GxdMarkerSummaryRow (SolrGxdMarker marker) {
	  	this.marker = marker;
	  	return;
	  }


	  //------------------------------------------------------------------------
	  // public instance methods;  JSON serializer will call all public methods
	  //------------------------------------------------------------------------
		public void setScore(String score){
			this.score = score;
		}

		public String getScore() {
			return score;
		}	

	  public String getSymbol() {
	      return "<a href='" + fewiUrl + "marker/" + marker.getMgiid() + "'>" + marker.getSymbol() +"</a>";
	  }
	  
	  public String getName() {
	      return marker.getName();
	  }
	  
	  public String getChr() {        
	      return marker.getChr();
	  }    
	  
	  public String getLocation() {
		  if(marker.getStartCoord() !=null && !marker.getStartCoord().equals("") 
				&& marker.getEndCoord()!=null && !marker.getEndCoord().equals(""))
		  {
			  return FormatHelper.formatCoordinates(Double.parseDouble(marker.getStartCoord()),
					  Double.parseDouble(marker.getEndCoord()));
		  }
		  else if(marker.getCytoband() !=null && !marker.getCytoband().equals(""))
		  {
			  return "cytoband "+marker.getCytoband();
		  }
		  else if(marker.getChr()!=null && !marker.getChr().equals("UN"))
		  {
			  return "syntenic";
		  }
	      return "";
	  }

	  public String getCM() {        
		  String cm = marker.getCm();
		  if((cm != null) && !cm.equals(""))
		  {
			  Float centimorgans = Float.parseFloat(cm);
		      // don't display negative cM values
		      if (centimorgans >=0) {
		      	return "" + centimorgans;
		      } 
		      else 
		      {
		      	return "";
		      }
		  }
		  return "";
	  }
	  
	  public String getStrand() {
	      return marker.getStrand();
	  }
	  
	  public String getType() {
	      return marker.getType();
	  }
	  
	  public String getPrimaryID() {
	      return marker.getMgiid();
	  }
    
}
