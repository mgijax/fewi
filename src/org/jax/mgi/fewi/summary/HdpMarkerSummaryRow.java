package org.jax.mgi.fewi.summary;

//public class GxdMarkerSummary {
//
//}

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.config.ContextLoader;

import javax.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a marker;  represents on row in summary
 */
public class HdpMarkerSummaryRow {
    //-------------------
    // instance variables
    //-------------------

    private Logger logger = LoggerFactory.getLogger(HdpMarkerSummaryRow.class);

    // encapsulated row object
    private SolrDiseasePortalMarker solrDiseasePortalMarker;

    // config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
    String imsrUrl = ContextLoader.getConfigBean().getProperty("IMSRURL");

    private String score;

    private List<String> highlightedFields;

    //-------------
    // constructors
    //-------------

    public HdpMarkerSummaryRow (SolrDiseasePortalMarker solrDiseasePortalMarker) {
        this.solrDiseasePortalMarker = solrDiseasePortalMarker;
        return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------
    public void setScore(String score){
        this.score = score;
    }

    public String getScore(){
        return score;
    }

    public void setHighlightedFields(List<String> highlightedFields)
	{
		this.highlightedFields = highlightedFields;
	}

	public String getHighlightedFields()
	{
		if(highlightedFields==null) return "";
		return  "<span class=\"hl\">"+StringUtils.join(this.highlightedFields,"<br/>")+"</span>";
	}

    public String getSymbol(){
    	String symbol = FormatHelper.superscript(solrDiseasePortalMarker.getSymbol());
    	// mouse symbol links to marker detail
    	// human symbol links to homology summary
    	String url = fewiUrl + "marker/" + solrDiseasePortalMarker.getMgiId();
    	if("human".equalsIgnoreCase(solrDiseasePortalMarker.getOrganism()))
    	{
    		if(solrDiseasePortalMarker.getHomologeneId() == null || solrDiseasePortalMarker.getHomologeneId().equals(""))
    		{
    			// simply return the unlinked symbol if there is no homologene ID for the human marker
    			return symbol;
    		}
    		url = fewiUrl + "homology/key/" + solrDiseasePortalMarker.getMarkerKey();
    	}

    	String displayText = "<a href=\""+url+"/\">"+
        		symbol+"</a>";
        return displayText;
    }

    public String getOrganism(){
        return solrDiseasePortalMarker.getOrganism();
    }

//    public String getHomologeneId(){
//        return solrDiseasePortalMarker.getHomologeneId();
//    }

//    public String getType(){
//        return solrDiseasePortalMarker.getType();
//    }

    public String getLocation(){
        return solrDiseasePortalMarker.getLocation();
    }

    public String getCoordinate(){
        StringBuffer coordDisplay = new StringBuffer();
        String coords = solrDiseasePortalMarker.getCoordinate();
        String build = solrDiseasePortalMarker.getCoordinateBuild();

        if (coords != null) {
          coordDisplay.append(coords.replace(" ","&nbsp;"));
	    }
        if (build != null) {
          coordDisplay.append("<br/><span class='example'>");
          coordDisplay.append(build);
          coordDisplay.append("</span>");
	    }


        return coordDisplay.toString();
    }

    public String getDisease(){
        StringBuffer diseaseDisplay = new StringBuffer();
        List<String> diseases = solrDiseasePortalMarker.getDisease();
        if (diseases != null) {
        	diseaseDisplay.append("<ul class=\"diseaseList\">");
            for(String disease : diseases)
            {
            	diseaseDisplay.append("<li>");
                diseaseDisplay.append(disease);
                diseaseDisplay.append("</li>");
            }
            diseaseDisplay.append("</ul>");
        }
        return diseaseDisplay.toString();
    }

    public String getSystem(){
        List<String> systems = solrDiseasePortalMarker.getSystem();
        String systemsToDisplay =  "";
        if (systems != null) {
          systemsToDisplay = FormatHelper.commaDelimit(systems);
	    }
        return systemsToDisplay;
    }

    public String getAllRefCount(){
    	if("human".equalsIgnoreCase(solrDiseasePortalMarker.getOrganism())) return "";

    	StringBuffer refDisplay = new StringBuffer();
        refDisplay.append("All Mouse: <a href=\"").append(fewiUrl).append("reference/marker/");
        refDisplay.append(solrDiseasePortalMarker.getMgiId()).append("\">");
        refDisplay.append(solrDiseasePortalMarker.getAllRefCount());
        refDisplay.append("</a>");
        if(solrDiseasePortalMarker.getDiseaseRefCount()!=null && solrDiseasePortalMarker.getDiseaseRefCount() > 0)
        {
            refDisplay.append("<br/><span style=\"white-space: nowrap;\">Disease Relevant: <a href=\"")
            	.append(fewiUrl).append("reference/diseaseRelevantMarker/")
            	.append(solrDiseasePortalMarker.getMgiId()).append("\">")
            	.append(solrDiseasePortalMarker.getDiseaseRefCount())
            	.append("</a></span>");
        }
    	return refDisplay.toString();
    }

    public String getImsrCount(){
    	if("human".equalsIgnoreCase(solrDiseasePortalMarker.getOrganism())) return "";

    	StringBuffer countDisplay = new StringBuffer();
        if(solrDiseasePortalMarker.getImsrCount()!=null && solrDiseasePortalMarker.getImsrCount() > 0)
        {
            countDisplay.append("<a target=\"_blank\" href=\"")
            		.append(imsrUrl)
            		.append("summary?states=ES+Cell&states=embryo&states=live&states=ovaries&states=sperm&gaccid=")
            		.append(solrDiseasePortalMarker.getMgiId()).append("\">")
            	.append(solrDiseasePortalMarker.getImsrCount())
            	.append("</a>");
        }
    	return countDisplay.toString();
    }
}
