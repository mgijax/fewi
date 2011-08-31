package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import mgi.frontend.datamodel.AccessionID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.summary.GOSummaryRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** An IDLinker is an object devoted to making links for accession IDs, using
 * a properties file to map from a logical database to its various actual
 * databases.
 */
public class IDLinker {

	/*--- Class Variables ---*/

	// maps from a given externalUrls.properties file to a cached,
	// pre-generated IDLinker
	private static HashMap<String,IDLinker> instances = new HashMap();


	/*--- Instance Variables ---*/

	// maps from a given logical database to a List of ActualDB objects
	private HashMap<String,List<ActualDB>> ldbToAdb = new HashMap<String,List<ActualDB>> ();

	private Configuration config = null;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());


	/*--- Constructors ---*/

	/** construct our IDLinker using data from externalUrls.properties file
	 */
	private IDLinker () {

		try {
			config = new PropertiesConfiguration("../properties/externalUrls.properties");
			config.addProperty("FEWI_URL", "http://faramir.jax.org/");
		} catch (Exception e) {};

		// used to iterate through properties in externalUrls:
		String name;	// property name
		String value;	// property value
		String prefix;	// property name minus any ".*" suffix
		ActualDB adb;	// the ActualDB object for given 'name'

		List<ActualDB> adbs;	// list of ActualDBs for this logical db

		// used to cache our ActualDB objects by actual db name
		HashMap<String,ActualDB> allAdbs = new HashMap<String,ActualDB>();

		// For each logical database, collect and populate a List of ActualDB
		// objects:
		for (Iterator iter =  config.getKeys(); iter.hasNext();) {
			name = (String) iter.next();
			value = config.getString(name);

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

		// Now go through and duplicate each logical database's entry with a
		// lowercase version of itself to make this more resilient to db
		// changes:

		String ldbLower;
		String ldb;
		HashMap<String,List<ActualDB>> lowerMap =
			new HashMap<String,List<ActualDB>> ();

		Iterator<String> it = this.ldbToAdb.keySet().iterator();
		while (it.hasNext()) {
			ldb = it.next();
			adbs = this.ldbToAdb.get(ldb);
			Collections.sort(adbs);
			lowerMap.put(ldb.toLowerCase(), this.ldbToAdb.get(ldb));
		}
		this.ldbToAdb.putAll(lowerMap);
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
		sb.append("'>");
		sb.append(label);
		sb.append("</a>");
		return sb.toString();
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

	//--- Public Class Methods ---

	/** get an IDLinker for the given externalUrls.properties file; these
	 * instances are shared to avoid having to slice and dice the properties
	 * each time we want to instantiate one
	 */
	public static IDLinker getInstance (Properties externalUrls) {
		String key = Integer.toString(externalUrls.hashCode());
		if (instances.containsKey(key)) {
			return instances.get(key);
		}
		IDLinker linker = new IDLinker();
		instances.put(key, linker);
		return linker;
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
