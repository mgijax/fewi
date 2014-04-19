package org.jax.mgi.fewi.summary;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GxdAssaySummaryRow {
	
	// -------------------
	// instance variables
	// -------------------

	private Logger logger = LoggerFactory.getLogger(GxdAssayResultSummaryRow.class);

	// encapsulated row object
	private SolrGxdAssay assay;

	// config values
	String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
	String pywiUrl = ContextLoader.getConfigBean().getProperty("WI_URL");
	String webshareUrl = ContextLoader.getConfigBean().getProperty("WEBSHARE_URL");
	 
	private String score;

	// -------------
	// constructors
	// -------------
	public GxdAssaySummaryRow(SolrGxdAssay assay) {
		this.assay = assay;
	}

	// -------------------------------------------------------------------
	// public instance methods; JSON serializer will call all public methods
	// -------------------------------------------------------------------
	public void setScore(String score) {
		this.score = score;
	}

	public String getScore() {
		return score;
	}

	public String getGene() {
		
//		return "<a href='" + fewiUrl + "marker/" + mrk.getPrimaryID() + "'>"
//				+ mrk.getSymbol() + "</a>";
		
		return assay.getMarkerSymbol();
	}
	
	public String getAssayID()
	{
		if(assay.getHasImage() )
		{
			return "<a href='"+fewiUrl+"assay/"+assay.getAssayMgiid()+"'>data</a><span> ("+assay.getAssayMgiid()+")</span>" + 
					" <img class=\"cameraIcon\" src=\""+webshareUrl+"/images/mgi_camera.gif\" /> ";
		}
		return "<a href='"+fewiUrl+"assay/"+assay.getAssayMgiid()+"'>data</a><span> ("+assay.getAssayMgiid()+")</span>";
	}
	
	public String getAssayType() {
		return assay.getAssayType();
	}


	public String getReference()
	{
		return "<a href='"+fewiUrl+"reference/"+assay.getJNum()+"'>"+assay.getJNum()+"</a> "+assay.getMiniCitation();

	}


}
