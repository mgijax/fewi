package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mgi.frontend.datamodel.AccessionID;
import mgi.frontend.datamodel.ActualDatabase;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.ActualDatabaseFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** An IDLinker is an object devoted to making links for accession IDs, using
 * a properties file (and hibernate actual_database table) to map from a logical 
 * database to its various actual databases.
 * 
 * NOTE: externalurls.properties file is intended to override any actual_database values in fe.
 * 	Please do not change this.
 */
@Component
public class IDLinker {

	//--- Class Variables ---


	// shared instance of an IDLinker
	private static IDLinker instance = null;

	private static HashMap<String,List<ActualDB>> cachedLdbToAdb = new HashMap<String,List<ActualDB>> ();
	//--- Instance Variables ---

	// maps from a given logical database to a List of ActualDB objects
	private HashMap<String,List<ActualDB>> ldbToAdb = new HashMap<String,List<ActualDB>> ();

	private Properties config = null;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private ActualDatabaseFinder actualDatabaseFinder;

	private boolean initialized = false;

	public void setup() 
	{
		if (this.initialized) { return; }

		if (cachedLdbToAdb.size() > 0) {
			this.ldbToAdb = cachedLdbToAdb;
			return;
		}

		try {
			config = ContextLoader.getExternalUrls();
		} catch (Exception e) {
			logger.error("Failed to read externalUrls.properties",e);
		};


		List<ActualDB> adbs;	// list of ActualDBs for this logical db

		// used to cache our ActualDB objects by actual db name
		HashMap<String,ActualDB> allAdbs = new HashMap<String,ActualDB>();

		// For each logical database, collect and populate a List of ActualDB objects

		// look for ${myName} notation for config values that need to be inserted
		Pattern hasName = Pattern.compile("\\$\\{([A-Za-z0-9_]+)\\}");
		Properties configBean = ContextLoader.getConfigBean();

		for (Object key: config.keySet())
		{
			String name = (String)key;
			String value = config.getProperty(name);

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
				if (!this.ldbToAdb.containsKey(value)) 
				{
					adbs = new ArrayList<ActualDB>();
					this.ldbToAdb.put(value, adbs);
					this.ldbToAdb.put(prefix, adbs);
				} 
				this.ldbToAdb.get(value).add(adb);
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

		// Now go through those from the database.  If the actual db
		// wasn't in the file, add an entry for it.  

		// get the logical/actual database info from the database
		Map<String,List<ActualDatabase>> fromDb = getActualDbsFromDatabase();

		boolean gotSomeFromDb = fromDb.size() > 0;

		for (String ldb : fromDb.keySet()) 
		{	
			ActualDB adb=null;	// the ActualDB object for given 'name'
			List<ActualDatabase> adbs1 = fromDb.get(ldb);

			for (ActualDatabase adb1: adbs1) 
			{
				String adbName = adb1.getActualDb().replaceAll(" ", "_");
				String ldbName = adb1.getLogicalDb();

				// skip updating MGI from the database, as
				// those entries are set for production
				if (adbName.equals("MGI"))  continue; // skip it
				if (ldbName.startsWith("MGI")) continue; // skip it
				
				if (allAdbs.containsKey(adbName)) 
				{
					continue;
					// As of 2014/02/05 we use the properties file as the definitive resource for external urls in the fewi. - kstone+pf
					// update URL for an actual database that was in the properties file
					//adb = allAdbs.get(adbName);
					//adb.setUrl(adb1.getUrl());
				} 
				else 
				{
					// add an actual database that was not in the properties file
					adb = new ActualDB(adbName);
					adb.setUrl (adb1.getUrl());
					adb.setDisplayName (adbName);
					adb.setOrderVal (
						adb1.getSequenceNum());

					if (this.ldbToAdb.containsKey(ldbName)) 
					{
						adbs = this.ldbToAdb.get(ldbName);
					} 
					else 
					{
					    adbs = new ArrayList<ActualDB>();
					    this.ldbToAdb.put(ldbName, adbs);
					}
					boolean seenIt = false;
					for (ActualDB a: adbs) 
					{
					    if (a.getName().equals(adbName))
					    {
					    	seenIt = true;
					    }
					    logger.debug ("Compare: *" + adbName + "* and *" + a.getName() + "*");
					}
					if (!seenIt) 
					{
					    adbs.add(adb);
					}
				}
			}
		}

		// Now go through and duplicate each logical database's entry with a
		// lowercase version of itself to make this more resilient to db changes

		HashMap<String,List<ActualDB>> lowerMap = new HashMap<String,List<ActualDB>> ();

		for (String ldb : this.ldbToAdb.keySet()) 
		{
			adbs = this.ldbToAdb.get(ldb);
			Collections.sort(adbs);
			lowerMap.put(ldb.toLowerCase(), this.ldbToAdb.get(ldb));
		}
		this.ldbToAdb.putAll(lowerMap);

		if (gotSomeFromDb) 
		{
			cachedLdbToAdb = this.ldbToAdb;
			this.initialized = true;
		}
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
	private Map<String,List<ActualDatabase>> getActualDbsFromDatabase() {
		// get all the ActualDatabase objects from the database

		if (this.actualDatabaseFinder == null) {
			this.actualDatabaseFinder = new ActualDatabaseFinder();
		}

		List<ActualDatabase> results = actualDatabaseFinder.getAll();

		String ldbName = null;
		String adbName = null;
		HashMap<String,List<ActualDatabase>> byLdb = new HashMap<String,List<ActualDatabase>>();
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

				boolean seenIt = false;
				for (ActualDatabase a: adbs) {
				    if (a.getActualDb().equals(adb.getActualDb() )) {
					seenIt = true;
				    }
				}
				if (!seenIt) {
				    adbs.add(adb);
				}
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
		StringBuffer sb = new StringBuffer();
		String href;
		boolean isFirst = true;
		HashMap<String,String> done = new HashMap<String,String>();

		for (ActualDB adb : adbs) 
		{
			if (!done.containsKey(adb.getName())) {
			href = this.makeLink (adb.getUrl(), id, adb.getDisplayName());
			if (!isFirst) {
				sb.append (separator);
			}
			sb.append(href);
			done.put(adb.getName(),"");
			}
			isFirst = false;
		}
		done = new HashMap<String,String>();
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
