package org.jax.mgi.fewi.summary;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleSystem;
import mgi.frontend.datamodel.RecombinaseInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import javax.persistence.Column;
import mgi.frontend.datamodel.AlleleSynonym;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.IDGenerator;
import org.jax.mgi.fewi.config.ContextLoader;

/** wrapper around an allele, to expose only certain data for a recombinase
 * summary page.  This will aid in efficient conversion to JSON notation and
 * transportation of data across the wire.  (We won't serialize more data
 * than needed.
 * @author jsb
 */
public class RecombinaseSummary {
	//-------------------
	// instance variables
	//-------------------

	// primary object for a RecombinaseSummary is an Allele
	private Allele allele;

	// for convenience, this is the allele's RecombinaseInfo object
	private RecombinaseInfo recombinaseInfo;

	// generates unique IDs for hidden/shown DIVs
	private static IDGenerator divIdGenerator = new IDGenerator("div");

	// generates unique IDs for elements with tooltips
	private static IDGenerator tooltipIdGenerator = new IDGenerator("tt");

	// maps from anatomical system abbreviation to its system key
	private static HashMap systemKeys = new HashMap();

	// expand and collapse arrows for Recombinase Data field
	private static String rightArrow = "<img src='"
		+ ContextLoader.getConfigBean().getProperty("WEBSHARE_URL")
		+ "images/rightArrow.gif' alt='right arrow'>";
	private static String downArrow = "<img src='"
		+ ContextLoader.getConfigBean().getProperty("WEBSHARE_URL")
		+ "images/downArrow.gif' alt='down arrow'>";

	String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

	//-------------
	// constructors
	//-------------

	// hide the default constructor
    private RecombinaseSummary () {}

    public RecombinaseSummary (Allele allele) {
    	this.allele = allele;
    	this.recombinaseInfo = allele.getRecombinaseInfo();
    	return;
    }

    //------------------------
    // public instance methods
    //------------------------

    public String getCountOfReferences() {
    	return "<a href='"+fewiUrl+"/reference/allele/" + this.allele.getPrimaryID()
    		+ "' target='_blank'>" + this.allele.getCountOfReferences().toString() + "</a>";
    }

    public String getDetectedCount() {

    	List<AlleleSystem> affectedSystems =
    		this.recombinaseInfo.getAffectedSystems();

    	// system keys for systems which were affected
    	HashSet<Integer> affectedKeys = new HashSet<Integer>();

    	StringBuffer linkedSystems = new StringBuffer();

    	AlleleSystem myAS = null;
    	String system = null;

    	boolean hasData = false;

    	if (affectedSystems.size() > 0) {
    		String divID = divIdGenerator.nextID();

    		linkedSystems.append("<div id='" + divID + "' class='small' ");
    		linkedSystems.append("style='color:blue; cursor:pointer;' >");

    		Iterator<AlleleSystem> affectedIterator = affectedSystems.iterator();
    		while (affectedIterator.hasNext()) {
    			myAS = affectedIterator.next();
    			system = myAS.getSystem();
    			linkedSystems.append(specificityLink(myAS.getAlleleID(), myAS.getSystemKey(),
    				myAS.getSystem(), null));
    			if (affectedIterator.hasNext()) {
    				linkedSystems.append (", ");
    			}

    			// if we have not already cached this system/key pair, do so
    			if (myAS.getSystemKey() != null) {
    				if (!isCachedSystem(system)) {
   				    cacheSystem(system, myAS.getSystemKey());
    				}
    				hasData = true;
    			}

    			// remember that this system was on the affected list
    			affectedKeys.add(myAS.getSystemKey());
    		}
    		linkedSystems.append("</div>");
    	}

    	affectedKeys = new HashSet<Integer>();

    	return linkedSystems.toString();
	}


    public String getNotDetectedCount() {

    	List<AlleleSystem> unaffectedSystems =
    		this.recombinaseInfo.getUnaffectedSystems();

    	StringBuffer linkedSystems = new StringBuffer();

    	AlleleSystem myAS = null;
    	String system = null;

    	boolean hasData = false;

   		String divID = divIdGenerator.nextID();
   		linkedSystems.append("<div id='" + divID + "' class='small' ");
   		linkedSystems.append("style='color:blue; cursor:pointer;' >");
    	System.out.println("-----!-----> In getActivityNotDetected " + unaffectedSystems.size());
    	if (unaffectedSystems.size() > 0) {

    		Iterator<AlleleSystem> unaffectedIterator = unaffectedSystems.iterator();
    		while (unaffectedIterator.hasNext()) {
    			myAS = unaffectedIterator.next();
    			system = myAS.getSystem();
    			linkedSystems.append(specificityLink(myAS.getAlleleID(), myAS.getSystemKey(),
    				myAS.getSystem(), " "));
    			if (unaffectedIterator.hasNext()) {
    				linkedSystems.append (", ");
    			}

    			// if we have not already cached this system/key pair, do so
    			if (myAS.getSystemKey() != null) {
    				if (!isCachedSystem(system)) {
   				    cacheSystem(system, myAS.getSystemKey());
    				}
    				hasData = true;
    			}

    		}
    	}
   		linkedSystems.append("</div>");

    	return linkedSystems.toString();
	}

    public String getDriver() {
    	return this.allele.getDriver();
    }

    public String getImsrCount() {
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

    public String getInducibleNote() {
    	String note = this.allele.getInducibleNote();
    	if (note == null) {
    		return note;
    	}
    	StringBuffer sb = new StringBuffer();
    	sb.append("<span id='");
    	sb.append(tooltipIdGenerator.nextID());
    	sb.append("' title='");
    	sb.append(note);
    	sb.append("'>Yes</span>");
    	return sb.toString();
    }

	public String getNomenclature() {
        String javawiUrl = ContextLoader.getConfigBean().getProperty("JAVAWI_URL");
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

	public String getSynonyms() {
		List<AlleleSynonym> alleleSynonyms = this.allele.getSynonyms();
		ArrayList<String> synonyms = new ArrayList<String>();
		Iterator<AlleleSynonym> it = alleleSynonyms.iterator();
		AlleleSynonym alleleSynonym = null;

		while (it.hasNext()) {
			alleleSynonym = it.next();
			synonyms.add(alleleSynonym.getSynonym());
		}
		Collections.sort(synonyms);

		Iterator<String> si = synonyms.iterator();
		StringBuffer sb = new StringBuffer();
		while (si.hasNext()) {
			sb.append (FormatHelper.superscript(si.next()));
			if (si.hasNext()) {
				sb.append (", ");
			}
		}
		return sb.toString();
	}


    //-------------------------
    // private instance methods
    //-------------------------

    private String flagToString (Integer isDetected, String system) {
    	if (isDetected == null) {
    		return "";
    	}

    	String label;
    	String tooltip;
    	if (isDetected.intValue() == 1) {
    		label = "Detected";
    		tooltip = "Recombinase activity was assayed and detected in these tissues.";
    	}
    	else {
    		label = "Not Detected";
    		tooltip = "Recombinase activity was assayed but not detected in these tissues.";
    	}

    	Integer systemKey = lookupSystem(getSystemCode(system));
    	if (systemKey == null) {
    		// if the key for this sysetm has not been cached, call
    		// getDetectedCount() to ensure that the cache has been updated,
    		// then try again (should happen rarely, if ever)
    		String ignore = this.getDetectedCount();
    		systemKey = lookupSystem(getSystemCode(system));
    		if (systemKey == null) {
    			systemKey = new Integer(0);
    		}
    	}

    	return specificityLink (this.allele.getPrimaryID(), systemKey, label, tooltip);
    }

    private List<AlleleSystemSummary> wrapSystems (List<AlleleSystem> systems) {
    	ArrayList<AlleleSystemSummary> summaries = new ArrayList<AlleleSystemSummary> ();
    	Iterator<AlleleSystem> it = systems.iterator();
    	while (it.hasNext()) {
    		summaries.add(new AlleleSystemSummary(it.next()));
    	}
    	return summaries;
    }

    //-----------------------
    // private static methods
    //-----------------------

    /** return the unique 4-letter lowercase prefix that will uniquely
    * identify an anatomical system (this lets us be more flexible in
    * terms of capitalization and exact term contents)
    */
    private static String getSystemCode (String s) {
    	if (s == null) {
    		return null;
    	}
    	return s.substring(0,4).toLowerCase();
    }

    /** determine whether this anatomical system has already had its
     * system key cached globally
     */
    private static boolean isCachedSystem (String system) {
    	return systemKeys.containsKey(getSystemCode(system));
    }

    /** cache the given system key for this anatomical system
     */
    private static void cacheSystem (String system, int systemKey) {
    	synchronized (systemKeys) {
    		systemKeys.put(getSystemCode(system), new Integer(systemKey));
    	}
    	return;
    }

    /** find the system key for the given anatomical system
     */
    private static Integer lookupSystem (String system) {
    	return (Integer) systemKeys.get(getSystemCode(system));
    }

    /** return a link to a recombinase specificity detail page for the
     * given alleleID and systemKey, and with the given label as the text
     * of the link
     */
    private  String specificityLink (String alleleID, Integer systemKey,
    		String label, String tooltip) {
    	if (systemKey == null) {
	    	if (label == null) {return "";}
			return label;
		}
    	StringBuffer sb = new StringBuffer();
    	sb.append("<a href='"+fewiUrl+"/recombinase/specificity?id=");
    	sb.append(alleleID);
    	sb.append("&systemKey=");
    	sb.append(systemKey.toString());

    	if (tooltip != null) {
    		String id = tooltipIdGenerator.nextID();
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

    // kstone: COMMENTED OUT. P.S. What is so much more convenient about int over Integer? Java does automatic conversions.
//    /** wrapper for convenience of using an int systemKey
//     */
//    private  String specificityLink (String alleleID, int systemKey,
//    		String label, String tooltip) {
//    	return specificityLink (alleleID, new Integer(systemKey), label, tooltip);
//    }
}
