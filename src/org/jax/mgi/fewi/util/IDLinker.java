package org.jax.mgi.fewi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import mgi.frontend.datamodel.AccessionID;
import mgi.frontend.datamodel.ActualDatabase;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.ActualDatabaseFinder;
import org.jax.mgi.fewi.summary.GOSummaryRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

/** An IDLinker is an object devoted to making links for accession IDs, using
 * a properties file to map from a logical database to its various actual
 * databases.
 */
@Component
public class IDLinker {
	
	//--- Class Variables ---
	
	@Autowired
	private SessionFactory sessionFactory;
	
	// shared instance of an IDLinker
	private static IDLinker instance = null;
	
	private static HashMap<String,List<ActualDB>> cachedLdbToAdb = new HashMap<String,List<ActualDB>> ();
	//--- Instance Variables ---
	
	// maps from a given logical database to a List of ActualDB objects
	private HashMap<String,List<ActualDB>> ldbToAdb = new HashMap<String,List<ActualDB>> ();
	
	private Configuration config = null;
		
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private ActualDatabaseFinder actualDatabaseFinder;
	
	private boolean initialized = false;

	//--- Constructors ---
		
	/** construct our IDLinker using data from externalUrls.properties file
	 */
	public IDLinker() {}

	public void setup() {

		if (this.initialized) { return; }

		this.initialized = true;
		if (cachedLdbToAdb.size() > 0) {
			this.ldbToAdb = cachedLdbToAdb;
			return;
		}

		try {
			config = new PropertiesConfiguration("../properties/externalUrls.properties");
		} catch (Exception e) {
			logger.debug ("Failed to read externalUrls.properties");
logger.debug (e.toString());
		};
		

		// used to iterate through properties in externalUrls:
		String name;	// property name
		String value;	// property value
		String prefix;	// property name minus any ".*" suffix
		ActualDB adb;	// the ActualDB object for given 'name'

		List<ActualDB> adbs;	// list of ActualDBs for this
					// ...logical db
		
		// used to cache our ActualDB objects by actual db name
		HashMap<String,ActualDB> allAdbs = 
			new HashMap<String,ActualDB>();

		// For precedence, we want the URLs in the database to be
		// preferred over those URLs from the properties file.  So, we
		// initialize our ActualDB objects with data from the file and
		// then go through and update with URLs from the database (and
		// add any actual databases that weren't in the properties
		// file).

		// For each logical database, collect and populate a List of
		// ActualDB objects:

		// look for ${myName} notation for config values that need
		// to be inserted
		Pattern hasName = Pattern.compile("\\$\\{([A-Za-z0-9_]+)\\}");
		Matcher hasNameMatcher = null;
		String configValue = null;

		Properties configBean = ContextLoader.getConfigBean();

		for (Iterator iter =  config.getKeys(); iter.hasNext();) {
			name = (String) iter.next();
			value = config.getString(name);

			hasNameMatcher = hasName.matcher(value);
			if (hasNameMatcher.find()) {
				configValue = configBean.getProperty(
					hasNameMatcher.group(1));
				if (configValue != null) {
					value = value.replaceFirst(
						"\\$\\{([A-Za-z0-9_]+)\\}",
						configValue);
				}
			}

			prefix = name.replaceAll("\\..*", "");

			if (allAdbs.containsKey(prefix)) {
				adb = allAdbs.get(prefix);
			} else {
				adb = new ActualDB(prefix);
				allAdbs.put(prefix, adb);
			}
			
			if (name.endsWith(".ldb")) {
				if (this.ldbToAdb.containsKey(value)) {
					adbs = this.ldbToAdb.get(value);
				} else {
					adbs = new ArrayList<ActualDB>();
					this.ldbToAdb.put(value, adbs);
				}
				adbs.add(adb);
			} else if (name.endsWith(".name")) {
				adb.setDisplayName(value);
			} else if (name.endsWith(".order")) {
				adb.setOrderVal(value);
			} else {
				adb.setUrl(value);
			}			
		}

		// Now go through those from the database.  If the actual db
		// wasn't in the file, add an entry for it.  If it was in the
		// file, then update the URL specified in the file.

		// get the logical/actual database info from the database
		Map<String,List> fromDb = getActualDbsFromDatabase();

		Set<String> ldbs = fromDb.keySet();
		Iterator<String> it = ldbs.iterator();
		String ldb = null;
		List<ActualDatabase> adbs1 = null;
		String adbName = null;
		String ldbName = null;

		while (it.hasNext()) {
			ldb = it.next();
			adbs1 = fromDb.get(ldb);

			for (ActualDatabase adb1: adbs1) {
				adbName = adb1.getActualDb().replaceAll(
					" ", "_");

				if (allAdbs.containsKey(adbName)) {
					// update URL for an actual database
					// that was in the properties file

					adb = allAdbs.get(adbName);
					adb.setUrl(adb1.getUrl());
				} else {
					// add an actual database that was not
					// in the properties file

					adb = new ActualDB(adbName);
					adb.setUrl (adb1.getUrl());
					adb.setDisplayName (adbName);
					adb.setOrderVal (
						adb1.getSequenceNum());

					ldbName = adb1.getLogicalDb();

					if (this.ldbToAdb.containsKey (
					    ldbName)) {
						adbs = this.ldbToAdb.get(ldbName);
					} else {
					    adbs = new ArrayList<ActualDB>();
					    this.ldbToAdb.put(ldbName, adbs);
					}
					adbs.add(adb);
				}
			}
		}

		// Now go through and duplicate each logical database's entry with a
		// lowercase version of itself to make this more resilient to db 
		// changes:
		
		String ldbLower;
		ldb = null;
		HashMap<String,List<ActualDB>> lowerMap = 
			new HashMap<String,List<ActualDB>> ();
		
		it = this.ldbToAdb.keySet().iterator();
		while (it.hasNext()) {
			ldb = it.next();
			adbs = this.ldbToAdb.get(ldb);
			Collections.sort(adbs);
			lowerMap.put(ldb.toLowerCase(), this.ldbToAdb.get(ldb));
		}
		this.ldbToAdb.putAll(lowerMap);
		cachedLdbToAdb = this.ldbToAdb;
	}
	
	//--- Private Instance Methods ---

	/** get the List of ActualDBs for the given logical database
	 */
	private List<ActualDB> getActualDBs (String ldb) {
		if (this.ldbToAdb.containsKey(ldb)) {
			return this.ldbToAdb.get(ldb);
		} else if (this.ldbToAdb.containsKey(ldb.toLowerCase())) {
			return this.ldbToAdb.get(ldb.toLowerCase());
		}
		return null;
	}
	
	/** get the first ActualDB for the given logical database
	 */
	private ActualDB getActualDB (String ldb) {
		List<ActualDB> adbs = this.getActualDBs(ldb);
		if (adbs == null) {
			return null;
		}
		return adbs.get(0);
	}

	/** return the <a href>...</a> tag for the given parameters
	 */
	private String makeLink (String url, String id, String label) {
		StringBuffer sb = new StringBuffer();
		sb.append("<a href='");
		sb.append(url.replace("@@@@", id));
		sb.append("' target='_blank'>");
		sb.append(label);
		sb.append("</a>");
		return sb.toString();
	}
	
	/** get a mapping from the database where:
	 * 	{ logical db : [ ActualDatabase objects ] }
	 */
	private Map<String,List> getActualDbsFromDatabase() {
		// get all the ActualDatabase objects from the database

		if (this.actualDatabaseFinder == null) {
			this.actualDatabaseFinder = new ActualDatabaseFinder();
		}

		List<ActualDatabase> results = actualDatabaseFinder.getAll();

		String ldbName = null;
		String adbName = null;
		HashMap<String,List> byLdb = new HashMap<String,List>();
		List<ActualDatabase> adbs = null;

		// bring them into a HashMap
		
		for (ActualDatabase adb: results) {
			ldbName = adb.getLogicalDb();

			if (!byLdb.containsKey(ldbName)) {
				adbs = new ArrayList<ActualDatabase>();
				adbs.add (adb);
				byLdb.put (ldbName, adbs);
			} else {
				adbs = byLdb.get(ldbName);
				adbs.add (adb);
			}
		}
		logger.debug ("Got info for " + byLdb.size() + " ldbs");
		return byLdb;
	}

	//--- Public Instance Methods ---
	
	/** get basic link using ID as link text, using first actual db for
	 * the relevant logical db
	 */
	public String getLink (AccessionID id) {
		String accID = id.getAccID();
		return this.getLink(id.getLogicalDB(), accID, accID);
	}
	
	/** get basic link using given label as link text, using first actual db 
	 * for the relevant logical db
	 */
	public String getLink (AccessionID id, String label) {
		return this.getLink(id.getLogicalDB(), id.getAccID(), label);
	}
	
	/** get basic link using given ID as link text, using first actual db 
	 * for the specified logical db
	 */
	public String getLink (String logicalDB, String id) {
		return this.getLink(logicalDB, id, id);
	}
	
	/** get basic link using given label as link text, using first actual db 
	 * for the specified logical db
	 */
	public String getLink (String logicalDB, String id, String label) {
		this.setup();
		ActualDB adb = this.getActualDB(logicalDB);
		if (adb == null) {
			return id;
		}
		return this.makeLink(adb.getUrl(), id, label);
	}

	/** get a string with links for each available actual database for the
	 * given id, separated by a pipe symbol
	 */
	public String getLinks (AccessionID id) {
		return this.getLinks (id.getLogicalDB(), id.getAccID(), " | ");
	}
	
	/** get a string with links for each available actual database for the
	 * given id, separated by the given separator
	 */
	public String getLinks (AccessionID id, String separator) {
		return this.getLinks (id.getLogicalDB(), id.getAccID(), " | ");
	}
	
	/** get a string with links for each available actual database for the
	 * given logical database, separated by a pipe symbol
	 */
	public String getLinks (String logicalDB, String id) {
		return this.getLinks (logicalDB, id, " | ");
	}

	/** get a string with links for each available actual database for the
	 * given logical database, separated by the given separator
	 */
	public String getLinks (String logicalDB, String id, String separator) {
		this.setup();
		List<ActualDB> adbs = this.getActualDBs(logicalDB);
		if (adbs == null) {
			return id;
		}
		Iterator<ActualDB> it = adbs.iterator();
		StringBuffer sb = new StringBuffer();
		ActualDB adb;
		String href;
		
		while (it.hasNext()) {
			adb = it.next();
			href = this.makeLink (adb.getUrl(), id, adb.getDisplayName());
			sb.append(href);
			if (it.hasNext()) {
				sb.append (separator);
			}
		}
		return sb.toString();
	}

	/** get the URLs as a Properties object
	 */
	public Properties getUrlsAsProperties() {
		List<ActualDB> adbs;	// list of ActualDBs for this
					// ...logical db

		Properties props = new Properties();

		for (String ldb : this.ldbToAdb.keySet()) {
			adbs = (List<ActualDB>) this.ldbToAdb.get(ldb);
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
			instance.setup();
		}
		return instance;
	}

	//--- Inner Classes ---

	private class ActualDB implements Comparable {
		private String url;
		private int orderVal = 9999;
		private String name;
		private String displayName;
		
		private ActualDB() {}
		
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
		
		public int compareTo(Object b) {
			if (!b.getClass().getName().endsWith("ActualDB")) {
				return 0;
			}
			ActualDB b2 = (ActualDB) b;
			if (this.orderVal < b2.orderVal) {
				return -1;
			} else if (this.orderVal > b2.orderVal){
				return 1;
			}
			return this.name.compareTo(b2.name);
		}
	}
}
