package org.jax.mgi.fewi.util.link;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mgi.frontend.datamodel.AccessionID;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerID;

import org.jax.mgi.fewi.config.ContextLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** An IDLinker is an object devoted to making links for accession IDs, using
 * a properties file (externalurls.properties) to map from a logical 
 * database to its various actual databases.
 * 
 * Previously, the properties file was augmented with entries from the actual_database tables
 * (which came from ACC_ActualDB in production).
 * As of 13 August 2022, the externalUrls.properties 
 * is the fewi's _sole_ source of truth concerning linking to external resources.
 *
 * NOTICE: Going forward, if it should happen the something is added to ACC_ActualDB that 
 * should be reflected in the front end, the same change must be made to externalUrls.properties.
 */
@Repository
public class IDLinker {

	//--- Class Variables ---

	// shared instance of an IDLinker
	private static IDLinker instance = null;


	private static Map<String,List<ActualDB>> logicalDbMapCache = null;

	//--- Instance Variables ---


	@Autowired
	private ContextLoader contextLoader;

	private Map<String,List<ActualDB>> logicalDbMap = null;


	/**
	 * externalUrls properties object
	 */
	private Properties externalUrlsProperties = null;

	/**
	 * All other global properties object
	 */
	private Properties globalProperties = null;

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());


	/**
	 * Exposed for testing
	 */
	public void setExternalUrlsProperties(Properties config) {
		this.externalUrlsProperties = config;
	}
	private Properties getExternalUrlsProperties() {
		if (this.externalUrlsProperties == null) {
			this.externalUrlsProperties = ContextLoader.getExternalUrls();
		}
		return this.externalUrlsProperties;
	}

	/**
	 * Exposed for testing
	 */
	public void setGlobalProperties(Properties config) {
		this.globalProperties = config;
	}

	private Properties getGlobalProperties() {
		if (this.globalProperties == null) {
			this.globalProperties = ContextLoader.getConfigBean();
		}
		return this.globalProperties;
	}

	/**
	 * Exposed for testing
	 */
	public void setlogicalDbMap(Map<String,List<ActualDB>> logicalDbMap) {
		this.logicalDbMap = logicalDbMap;
		logicalDbMapCache = this.logicalDbMap;
	}

	private Map<String,List<ActualDB>> getLogicalDbMap() {
		// initialize static cache if needed
		if (logicalDbMapCache == null) {
			this.logicalDbMap = initLogicalDbMapCache();
			logicalDbMapCache = this.logicalDbMap;
		}

		if (this.logicalDbMap == null) {
			// pull from the cache if this is not set manually
			this.logicalDbMap = logicalDbMapCache;
		}

		return this.logicalDbMap;
	}

	//--- Private Instance Methods ---

	/**
	 * Initialize map of logical DBs to actual DBs
	 */
	private Map<String,List<ActualDB>> initLogicalDbMapCache() {

		HashMap<String,List<ActualDB>> ldbToAdb = new HashMap<String,List<ActualDB>>();

		List<ActualDB> adbs;	// list of ActualDBs for this logical db

		// used to cache our ActualDB objects by actual db name
		HashMap<String,ActualDB> allAdbs = new HashMap<String,ActualDB>();

		// For each logical database, collect and populate a List of ActualDB objects

		// look for ${myName} notation for config values that need to be inserted
		Pattern hasName = Pattern.compile("\\$\\{([A-Za-z0-9_]+)\\}");
		Properties configBean = ContextLoader.getConfigBean();

		Properties externalUrlsConfig = getExternalUrlsProperties();

		for (Object key: externalUrlsConfig.keySet())
		{
			String name = (String)key;
			String value = externalUrlsConfig.getProperty(name);

			Matcher hasNameMatcher = hasName.matcher(value);
			// not sure what this section does - kstone
			if (hasNameMatcher.find()) 
			{
				String configValue = configBean.getProperty(hasNameMatcher.group(1));
				if (configValue != null) 
				{
					value = value.replaceFirst("\\$\\{([A-Za-z0-9_]+)\\}",configValue);
				}
			}

			String prefix = name.replaceAll("\\..*", "");

			// get the matching ActualDB object for this prefix
			if (!allAdbs.containsKey(prefix)) allAdbs.put(prefix,new ActualDB(prefix)); 
			ActualDB adb = allAdbs.get(prefix);

			// set various portions of the actualdb object, depending on the type of config suffix
			if (name.endsWith(".ldb")) 
			{
				// add this actualdb to this appropriate logicaldb mapping
				if (!ldbToAdb.containsKey(value)) 
				{
					adbs = new ArrayList<ActualDB>();
					ldbToAdb.put(value, adbs);
					ldbToAdb.put(prefix, adbs);
				} 
				ldbToAdb.get(value).add(adb);
			} 
			else if (name.endsWith(".name")) 
			{
				adb.setDisplayName(value);
			} 
			else if (name.endsWith(".order")) 
			{
				adb.setOrderVal(value);
			} 
			else 
			{
				adb.setUrl(value);
			}
		}

                // Here we used to get the actual_database entries from the fe database and add any that
                // are not already included in the file.
                // The following piece is a holdover from the previous practice. 
                // I don't know if anyone downstream is depending on having lowercased options,
                // so I left it in.
                //
		// Now go through and duplicate each logical database's entry with a
		// lowercase version of itself to make this more resilient to db changes
		HashMap<String,List<ActualDB>> lowerMap = new HashMap<String,List<ActualDB>> ();

		for (String ldb : ldbToAdb.keySet()) 
		{
			adbs = ldbToAdb.get(ldb);
			Collections.sort(adbs);
			lowerMap.put(ldb.toLowerCase(), ldbToAdb.get(ldb));
		}
		ldbToAdb.putAll(lowerMap);


		return ldbToAdb;
	}

	/** get the List of ActualDBs for the given logical database
	 */
	private List<ActualDB> getActualDBs (String ldb) {
		Map<String,List<ActualDB>> ldbToAdb = getLogicalDbMap();
		if (ldbToAdb.containsKey(ldb)) {
			return ldbToAdb.get(ldb);
		} else if (ldbToAdb.containsKey(ldb.toLowerCase())) {
			return ldbToAdb.get(ldb.toLowerCase());
		}
		return null;
	}

	/** get the first ActualDB for the given logical database
	 */
	private ActualDB getActualDB (String ldb) {
		List<ActualDB> adbs = getActualDBs(ldb);

		if (adbs == null) {
			return null;
		}
		return adbs.get(0);
	}

	/** return the <a href>...</a> tag for the given parameters
	 */
	private String makeLink (String url, String id, String linkText, String className) {
		return makeLink(url, id, linkText, className, true);
	}
	/** return the <a href>...</a> tag for the given parameters
	 */
	private String makeLink (String url, String id, String linkText, String className, boolean targetBlank) {
		StringBuffer sb = new StringBuffer();
		sb.append("<a ");
		if(className != null && className.length() > 0) {
			sb.append("class=\"" + className + "\" ");
		}
		sb.append("href='");
		if ((id != null) && (id.startsWith("PR:"))) {
			sb.append(url.replace("@@@@", id.replace("PR:", "PR_")));
		} else {
			sb.append(url.replace("@@@@", id));
		}
		sb.append("'");
		if (targetBlank) {
			sb.append(" target='_blank'");
		}
		sb.append(">");
		sb.append(linkText);
		sb.append("</a>");
		return sb.toString();
	}

	//-- Specific object related link methods

	/**
	 * Returns the default marker link by Symbol
	 * 	depending on 
	 * 	organism and other attributes
	 * 
	 */
	public String getDefaultMarkerLink(Marker marker) {

		Properties globalConfig = getGlobalProperties();

		if (marker.getOrganism().equalsIgnoreCase("mouse")) {
			String url = globalConfig.getProperty("FEWI_URL") + "marker/" + marker.getPrimaryID();
			return makeLink(url, "", marker.getSymbol(), "", false);
		}

		MarkerID entrezGeneID = marker.getEntrezGeneID();

		if (entrezGeneID != null 
				&& entrezGeneID.getAccID() != null 
				&& !entrezGeneID.getAccID().equals("")) {
			return getLink("Entrez Gene", entrezGeneID.getAccID(), marker.getSymbol());
		}

		// no link can be made
		return marker.getSymbol();
	}



	public String getLink (AccessionID id) {
		String accID = id.getAccID();
		return getLink(id.getLogicalDB(), accID);
	}

	public String getLink (AccessionID id, String linkText) {
		return getLink(id.getLogicalDB(), id.getAccID(), linkText, "");
	}

	public String getLinkWithClass (AccessionID id, String linkText, String className) {
		return getLink(id.getLogicalDB(), id.getAccID(), linkText, className);
	}

	public String getLink (String logicalDB, String id) {
                String linkId = id;
                // Special case for VISTA elements. An id looks like "mm472".
                // The link only wants the numeric part "442". Link text should
                // be the whole id.
                if ("VISTA Enhancer Element".equals(logicalDB)) {
                    linkId = id.replaceAll("^[^0-9]*", "");
                }
		return getLink(logicalDB, linkId, id, "");
	}

	public String getLink (String logicalDB, String id, String linkText) {
		return getLink(logicalDB, id, linkText, "");
	}

	public String getLink (String logicalDB, String id, String linkText, String className) {
		ActualDB adb = getActualDB(logicalDB);
		if (adb == null) {
			return id;
		}
		id = fixIds(logicalDB, id);
		return makeLink(adb.getUrl(), id, linkText, className);
	}

	/* generates the set of links for the given 'id', but just returns the first one
	 */
	public String getFirstLink (AccessionID id) {
		String links = getLinks (id.getLogicalDB(), id.getAccID(), " ||| ");
		return links.replaceAll(" [|]{3}.*", "");
	}

	public String getLinks (AccessionID id) {
		return getLinks (id.getLogicalDB(), id.getAccID(), " | ");
	}
	public String getLinks (AccessionID id, String separator) {
		return getLinks (id.getLogicalDB(), id.getAccID(), separator, "");
	}
	public String getLinks (AccessionID id, String separator, String className) {
		return getLinks (id.getLogicalDB(), id.getAccID(), separator, className);
	}

	public String getLinks (String logicalDB, String id) {
		return getLinks (logicalDB, id, " | ");
	}
	public String getLinks (String logicalDB, String id, String separator) {
		return getLinks (logicalDB, id, separator, "");
	}
	public String getLinks (String logicalDB, String id, String separator, String className) {
		List<ActualDB> adbs = getActualDBs(logicalDB);
		if (adbs == null) {
			return id;
		}
		StringBuffer sb = new StringBuffer();
		String href;
		boolean isFirst = true;
		HashMap<String,String> done = new HashMap<String,String>();

		for (ActualDB adb : adbs) {
			if (!done.containsKey(adb.getName())) {
				id = fixIds(adb.getName(), id);
				href = makeLink (adb.getUrl(), id, adb.getDisplayName().replace("_", " "), className);
				if (!isFirst) {
					sb.append (separator);
				}
				sb.append(href);
				done.put(adb.getName(),"");
			}
			isFirst = false;
		}
		return sb.toString();
	}

	private String fixIds(String dbName, String id) {
		if(dbName.equals("OMIM") || dbName.equals("OMIM:PS")) {
			id = id.replaceAll("OMIM:", "");
		}
		/* Prepare for DOID to apply proper prefixes for OMIM Phenotype series terms
		 * See: https://github.com/DiseaseOntology/HumanDiseaseOntology/pull/968
		 */
		if(dbName.equals("OMIMPS")) {
			id = id.replaceAll("OMIMPS:", "");
		}
		if(dbName.equals("NCI")) {
			id = id.replaceAll("NCI:", "");
		}
		if(dbName.equals("ORDO")) {
			id = id.replaceAll("ORDO:", "");
		}
		if(dbName.equals("EFO")) {
			id = id.replaceAll("EFO:", "EFO_");
		}
		if(dbName.equals("KEGG")) {
			id = id.replaceAll("KEGG:", "");
		}
		if(dbName.equals("MESH")) {
			id = id.replaceAll("MESH:", "");
		}
		return id;
	}
	/** get the URLs as a Properties object
	 */
	public Properties getUrlsAsProperties() {
		List<ActualDB> adbs;	// list of ActualDBs for this
		// ...logical db

		Properties props = new Properties();

		Map<String,List<ActualDB>> ldbToAdb = getLogicalDbMap();

		for (String ldb : ldbToAdb.keySet()) {
			adbs = ldbToAdb.get(ldb);
			for (ActualDB adb: adbs) {
				props.setProperty (ldb, adb.getUrl());
			}
		}
		return props;
	}

	//--- Public Class Methods ---

	/** get a reference to the shared IDLinker; this instance is shared to
	 * avoid having to slice and dice the properties each time we want to
	 * instantiate one
	 */
	public static IDLinker getInstance () {
		if (instance == null) {
			instance = new IDLinker();
		}
		return instance;
	}

	//--- Inner Classes ---

	public class ActualDB implements Comparable<ActualDB> {
		private String url;
		private int orderVal = 9999;
		private final String name;
		private String displayName;

		public ActualDB(String name) {
			this.name = name;
		}

		public String getDisplayName() {
			if (this.displayName == null) {
				return this.name;
			}
			return this.displayName;
		}

		public String getName() {
			return this.name;
		}

		public String getUrl () {
			return this.url;
		}

		public void setDisplayName (String displayName) {
			this.displayName = displayName;
		}

		public void setUrl (String url) {
			this.url = url;
		}

		public void setOrderVal (int orderVal) {
			this.orderVal = orderVal;
		}

		public void setOrderVal (String orderVal) {
			try {
				this.orderVal = Integer.parseInt(orderVal);
			} catch (Exception e) {
				this.orderVal = 999;
			}
		}

		public int compareTo(ActualDB b) {
			if (this.orderVal < b.orderVal) {
				return -1;
			} else if (this.orderVal > b.orderVal){
				return 1;
			}
			return this.name.compareTo(b.name);
		}
	}
}
