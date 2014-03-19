package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleSynonym;
import mgi.frontend.datamodel.AlleleSystem;
import mgi.frontend.datamodel.RecombinaseInfo;

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
	private Allele allele;

	// for convenience, this is the allele's RecombinaseInfo object
	private RecombinaseInfo recombinaseInfo;

	private String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
	
	// to be used in creating custom css ids
	private int cssIdCounter=0;

	//-------------
	// constructors
	//-------------

    public RecombinaseSummary (Allele allele) {
    	this.allele = allele;
    	this.recombinaseInfo = allele.getRecombinaseInfo();
    }

    //------------------------
    // public instance methods
    //------------------------

    public String getCountOfReferences() {
    	return "<a href='"+fewiUrl+"/reference/allele/" + this.allele.getPrimaryID()
    		+ "' target='_blank'>" + this.allele.getCountOfReferences().toString() + "</a>";
    }

    public String getDetectedCount() 
    {
    	List<AlleleSystem> affectedSystems = this.recombinaseInfo.getAffectedSystems();

    	StringBuffer linkedSystems = new StringBuffer();

    	if (affectedSystems.size() > 0) 
    	{
    		String divID = getNextCssId("div");

    		linkedSystems.append("<div id='" + divID + "' class='small' ");
    		linkedSystems.append("style='color:blue; cursor:pointer;' >");
    		
       		List<String> links = new ArrayList<String>();
    		for(AlleleSystem aSystem : affectedSystems)
    		{
    			links.add(specificityLink(aSystem.getAlleleID(), aSystem.getSystemKey(),aSystem.getSystem(), null));
    		}
    		linkedSystems.append(StringUtils.join(links,", "));

    		linkedSystems.append("</div>");
    	}

    	return linkedSystems.toString();
	}


    public String getNotDetectedCount() 
    {
    	List<AlleleSystem> unaffectedSystems = this.recombinaseInfo.getUnaffectedSystems();

    	StringBuffer linkedSystems = new StringBuffer();

   		String divID = getNextCssId("div");
   		linkedSystems.append("<div id='" + divID + "' class='small' ");
   		linkedSystems.append("style='color:blue; cursor:pointer;' >");
    	
    	if (unaffectedSystems.size() > 0) 
    	{
    		List<String> links = new ArrayList<String>();
    		for(AlleleSystem aSystem : unaffectedSystems)
    		{
    			links.add(specificityLink(aSystem.getAlleleID(), aSystem.getSystemKey(),aSystem.getSystem(), null));
    		}
    		linkedSystems.append(StringUtils.join(links,", "));
    	}
   		linkedSystems.append("</div>");

    	return linkedSystems.toString();
	}

    public String getDriver() {
    	return this.allele.getDriver();
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
    	sb.append("&states=embryo&states=live&states=ovaries&states=sperm' target='_blank'>");
    	sb.append(this.allele.getImsrStrainCount());
    	sb.append("</a>");
    	return sb.toString();
    }

    public String getInducibleNote() 
    {
    	String note = this.allele.getInducibleNote();
    	if (note == null) {
    		return note;
    	}
    	StringBuffer sb = new StringBuffer();
    	sb.append("<span id='");
    	sb.append(getNextCssId("tt"));
    	sb.append("' title='");
    	sb.append(note);
    	sb.append("'>Yes</span>");
    	return sb.toString();
    }

	public String getNomenclature() 
	{
        StringBuffer sb = new StringBuffer();

        sb.append("<a href='" + fewiUrl + "allele/"
          + this.allele.getPrimaryID()
          + "?recomRibbon=open' target='_blank'>"
          + FormatHelper.superscript(this.allele.getSymbol()) + "</A>");
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
			synonyms.add(FormatHelper.superscript(alleleSynonym.getSynonym()));
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
     */
    private  String specificityLink (String alleleID, Integer systemKey,
    		String label, String tooltip) 
    {
    	if (systemKey == null) {
	    	if (label == null) {return "";}
			return label;
		}
    	StringBuffer sb = new StringBuffer();
    	sb.append("<a href='"+fewiUrl+"/recombinase/specificity?id=");
    	sb.append(alleleID);
    	sb.append("&systemKey=");
    	sb.append(systemKey.toString());

    	if (tooltip != null) 
    	{
    		String id = getNextCssId("tt");
    		sb.append("' id='");
    		sb.append(id);
    		sb.append("' title='");
    		sb.append(tooltip);
    	}

    	sb.append("' target='_blank'>");
    	sb.append(label);
    	sb.append("</a>");
    	return sb.toString();
    }
}
