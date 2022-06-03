package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleSynonym;
import mgi.frontend.datamodel.AlleleSystem;
import mgi.frontend.datamodel.Marker;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.FormatHelper;

/** wrapper around an allele, to expose only certain data for a recombinase
 * summary page.  This will aid in efficient conversion to JSON notation and
 * transportation of data across the wire.  (We won't serialize more data
 * than needed.
 * @author jsb, kstone
 */
public class RecombinaseSummary {
	//-------------------
	// instance variables
	//-------------------
	
	// primary object for a RecombinaseSummary is an Allele
	private final Allele allele;

	private Set<String> detectedHighlights;
	private Set<String> notDetectedHighlights;

	private final String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

	// to be used in creating custom css ids
	private int cssIdCounter=0;

	//-------------
	// constructors
	//-------------

    public RecombinaseSummary (Allele allele,Set<String> detectedHighlights, Set<String> notDetectedHighlights) {
    	this.allele = allele;

    	this.detectedHighlights=detectedHighlights;
    	this.notDetectedHighlights=notDetectedHighlights;

    	if(this.detectedHighlights==null) this.detectedHighlights = new HashSet<String>();
    	if(this.notDetectedHighlights==null) this.notDetectedHighlights = new HashSet<String>();
    }

    //------------------------
    // public instance methods
    //------------------------

    public String getCountOfReferences() {
    	String refCounts = "<a href='"+fewiUrl+"/reference/allele/" + this.allele.getPrimaryID()
        		+ "?typeFilter=Literature' target='_blank'>" + this.allele.getCountOfReferences().toString() + "</a>";
    	return refCounts;
    }

    public String getDetectedSystems()
    {

    	List<AlleleSystem> affectedSystems = this.allele.getAffectedSystems();
    	
    	StringBuffer linkedSystems = new StringBuffer();

    	if (affectedSystems.size() > 0)
    	{
    		String divID = getNextCssId("div");

    		linkedSystems.append("<div id='" + divID + "' class='systemWrap' >");

       		List<String> links = new ArrayList<String>();
    		for(AlleleSystem aSystem : affectedSystems)
    		{
    			String systemText = aSystem.getSystem();
    			if(isSystemHighlighted(systemText,this.detectedHighlights)) systemText = "<span class=\"systemHl exact\">"+systemText+"</span>";
    			if(isSystemHighlighted(systemText,this.notDetectedHighlights)) systemText = "<span class=\"systemHl\">"+systemText+"</span>";
    			links.add(specificityLink(aSystem.getAlleleID(), aSystem.getAlleleSystemKey(),systemText));
    		}
    		linkedSystems.append(StringUtils.join(links,", "));

    		linkedSystems.append("</div>");
    	}

    	return linkedSystems.toString();
	}


    public String getNotDetectedSystems()
    {
    	
    	List<AlleleSystem> unaffectedSystems = this.allele.getUnaffectedSystems();

    	StringBuffer linkedSystems = new StringBuffer();

   		String divID = getNextCssId("div");
   		linkedSystems.append("<div id='" + divID + "' class='systemWrap' >");

    	if (unaffectedSystems.size() > 0)
    	{
    		List<String> links = new ArrayList<String>();
    		for(AlleleSystem aSystem : unaffectedSystems)
    		{
    			String systemText = aSystem.getSystem();
    			if(isSystemHighlighted(systemText,this.notDetectedHighlights)) systemText = "<span class=\"systemHl exact\">"+systemText+"</span>";
    			if(isSystemHighlighted(systemText,this.detectedHighlights)) systemText = "<span class=\"systemHl\">"+systemText+"</span>";
    			links.add(specificityLink(aSystem.getAlleleID(), aSystem.getAlleleSystemKey(),systemText));
    		}
    		linkedSystems.append(StringUtils.join(links,", "));
    	}
   		linkedSystems.append("</div>");

    	return linkedSystems.toString();
	}

    public static boolean isSystemHighlighted(String systemText,Set<String> highlights) {

    	return highlights.contains(systemText);
	}

	public String getDriver() {
		String driver = this.allele.getDriver();
    	return driver;
    }
	
	/* if we have a mouse driver with expression data and having at least one allele with recombinase
	 * activity data, then we need to return a link to the recombinase grid
	 */
	public String getGridLink() {
		String gridLink = "";
		Marker driver = this.allele.getDriverMarker();
                String driverId = driver.getPrimaryID();
                if (!"mouse".equals(driver.getOrganism())) {
                    driverId = driver.getMouseMarkerId();
                    if (driverId == null) {
                        return "";
                    }
                }
		if (this.allele.getCountOfRecombinaseResults() > 0 ) {
			String link = fewiUrl + "gxd/recombinasegrid/" + driverId + "?alleleID=" + this.allele.getPrimaryID();
			String image = fewiUrl + "assets/images/gxd_matrix_icon.png";
			gridLink = "<a href='" + link + "' target='_blank'><img src='" + image
				+ "' class='matrixIcon' title='See a matrix showing expression data for mouse "
				+ driver.getSymbol() + " with its recombinase activity data.'/></a>";
		}
		return gridLink;
	}

    public String getImsrCount()
    {
    	Integer count = this.allele.getImsrStrainCount();
    	if ((count == null) || count.equals(0)) { return null; }

    	StringBuffer sb = new StringBuffer();
    	sb.append("<a href='");
    	sb.append(ContextLoader.getConfigBean().getProperty("IMSRURL"));
    	sb.append("summary?gaccid=");
    	sb.append(this.allele.getPrimaryID());
    	sb.append("&states=archived&states=embryo&states=live&states=ovaries&states=sperm' target='_blank'>");
    	sb.append(this.allele.getImsrStrainCount());
    	sb.append("</a>");
    	
    	return sb.toString();
    }

    public String getInducibleNote()
    {
		String inducibleNote = this.allele.getInducibleNote();
		if (inducibleNote != null) {
			inducibleNote= inducibleNote.replace("-", "-&#8203;");
		}
    	return inducibleNote;
    }

	public String getNomenclature()
	{
        StringBuffer sb = new StringBuffer();

        sb.append("<a class=\"alleleSymbol\" href='" + fewiUrl + "allele/"
          + this.allele.getPrimaryID()
          + "?recomRibbon=open' target='_blank'>"
          + FormatHelper.superscript(this.allele.getSymbol()) + "</a>");
		sb.append("<br/><span class='small'>");
		sb.append(FormatHelper.superscript(this.allele.getGeneName()));
		if (!this.allele.getGeneName().equals(this.allele.getName())) {
			sb.append("; ");
			sb.append(FormatHelper.superscript(this.allele.getName()));
		}
		sb.append("</span>");
		return sb.toString();
	}

	public String getAlleleSymbol()
	{ return this.allele.getSymbol(); }

	public String getSynonyms()
	{
		List<AlleleSynonym> alleleSynonyms = this.allele.getSynonyms();
		ArrayList<String> synonyms = new ArrayList<String>();

		for(AlleleSynonym alleleSynonym : alleleSynonyms)
		{
			synonyms.add("<span class=\"synonym\">"+FormatHelper.superscript(alleleSynonym.getSynonym()) + "</span>");
		}
		Collections.sort(synonyms);

		return StringUtils.join(synonyms,", ");
	}


    //-------------------------
    // private instance methods
    //-------------------------

	/*
	 * For some reason we need a unique css id for this summary row.
	 * Allele key seems safe enough
	 */
	private String getNextCssId(String suffix)
	{
		this.cssIdCounter+=1;
		return this.allele.getAlleleKey()+"_"+this.cssIdCounter+"_"+suffix;
	}

    /** return a link to a recombinase specificity detail page for the
     * given alleleID and systemKey, and with the given label as the text
     * of the link
     *
     * 		AS OF 11/24/2015 these links are disabled
     */
    private  String specificityLink (String alleleID, Integer systemKey,
    		String label)
    {
    	if (systemKey == null) {
	    	if (label == null) {return "";}
			return label;
		}
    	StringBuffer sb = new StringBuffer();

    	//sb.append("<a href='"+fewiUrl+"/recombinase/specificity?id=");
    	//sb.append(alleleID);
    	//sb.append("&systemKey=");
    	//sb.append(systemKey.toString());


    	//sb.append("' target='_blank'>");
    	sb.append(label);
    	//sb.append("</a>");
    	return sb.toString();
    }
}
