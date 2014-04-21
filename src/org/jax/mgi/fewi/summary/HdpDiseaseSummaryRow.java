package org.jax.mgi.fewi.summary;

//public class GxdMarkerSummary {
//
//}

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.jax.mgi.fewi.util.FormatHelper;


/**
 * wrapper around a marker;  represents on row in summary
 */
public class HdpDiseaseSummaryRow {
	//-------------------
		// instance variables
		//-------------------

		// encapsulated row object
		private final SolrVocTerm term;
		
		private List<String> highlightedFields;

		// config values
	  String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

		private String score;

		//-------------
		// constructors
		//-------------

	  public HdpDiseaseSummaryRow (SolrVocTerm term) {
	  	this.term = term;
	  }


	  //------------------------------------------------------------------------
	  // public instance methods;  JSON serializer will call all public methods
	  //------------------------------------------------------------------------
		public void setScore(String score)
		{
			this.score = score;
		}
		
		public String getScore() 
		{
			return score;
		}	
		
		public void setHighlightedFields(List<String> highlightedFields)
		{
			this.highlightedFields = highlightedFields;
		}
		
		public String getHighlightedFields()
		{
			if(highlightedFields==null) return "";
			return "<span class=\"hl\">"+StringUtils.join(this.highlightedFields,"<br/>")+"</span>";
		}

		public String getDisease() 
		{
			// disease links to disease detail
			String url = fewiUrl + "disease/" + term.getPrimaryId();
			return "<a href=\""+url+"\">"+term.getTerm()+"</a>";
		}
		  
		public String getDiseaseId() 
		{
		      return term.getPrimaryId();
		}
		
		public String getDiseaseModels()
		{
			if(term.getDiseaseModelCount()==null || term.getDiseaseModelCount()<1) return "";
			
			// links to disease models page
			String url = fewiUrl + "disease/models/" + term.getPrimaryId();
			return "<a href=\""+url+"\">"+term.getDiseaseModelCount()+"</a>";
		}
		
		public String getMouseMarkers(){
	        List<String> markers = term.getDiseaseMouseMarkers();
	        String markersToDisplay =  "";
	        if (markers != null) {
	        	markersToDisplay = FormatHelper.commaDelimit(markers);
		    }
	        return FormatHelper.superscript(markersToDisplay);
	    }
		
		public String getHumanMarkers(){
	        List<String> markers = term.getDiseaseHumanMarkers();
	        String markersToDisplay =  "";
	        if (markers != null) {
	        	markersToDisplay = FormatHelper.commaDelimit(markers);
		    }
	        return FormatHelper.superscript(markersToDisplay);
	    }
		
		public String getRefCount(){
			if(term.getDiseaseRefCount()!=null && term.getDiseaseRefCount()>0)
			{
				return "<a href=\""+fewiUrl+"reference/disease/"+term.getPrimaryId()+"\">"+term.getDiseaseRefCount()+"</a>";
			}
			return "";
		}
}
