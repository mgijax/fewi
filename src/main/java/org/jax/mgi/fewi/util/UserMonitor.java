package org.jax.mgi.fewi.util;

import org.springframework.web.servlet.ModelAndView;
import org.jax.mgi.fewi.config.ContextLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap; 

/* Is: a monitor to keep an eye on user traffic and return a true/false flag for whether
 * 	he/she is behaving nicely or has exceeded a (generous) reasonable amount of traffic.
 * 	The idea is to be able to catch and deny access to the periodic users who swamp us
 * 	with traffic and end up taking down the site.
 * Has: in-memory caches needed to monitor users based on IP address
 * Notes:
 * 	1. A single UserMonitor is intended to be shared across all Controllers.  As such,
 * 	handling concurrent access efficiently is a primary concern.  (One could use separate
 * 	UserMonitors, but that's not the intent.)
 * 	2. Should be able to disable this mechanism for the robot server (see isEnabled).
 * 	3. Should only check this for page-level requests (not Ajax requests).
 * 	4. Should allow limits to be adjusted easily.
 */
public class UserMonitor {
	//-----------------
	// static variables
	//-----------------
	public static UserMonitor sharedMonitor = new UserMonitor();
	
	private final Logger logger = LoggerFactory.getLogger(UserMonitor.class);

	//-------------------
	// instance variables
	//-------------------

	// maps from IP address to list of hit times (from currentTimeMillis), oldest first
	private HashMap<String, List<Long>> hits = new HashMap<String, List<Long>>();

	// (ordered) list of IP addresses to check to see if they've timed out and can be
	// removed from 'hits'.  Idea is to periodically (every 'm' requests) check the first
	// 'n' entries from this list.  If any have timed out, remove them from this list
	// and from 'hits'.  If they're still active, move them from the start of the list
	// to the end (so they can be rechecked later on).
	private List<String> toCheck = new ArrayList<String>();

	private int cleanupCounter = 0;		// number of hits processed since the last cleanup process
	private boolean isEnabled;			// is monitoring enabled (true) or not (false)
    private int windowSize;				// size of window for tracking users (in milliseconds)
    private int badHitCount;			// number of hits (within the window) at which to cut off access
    private int cleanupFrequency;		// number of hits between periodic internal cleanup processes
    private int cleanupSize;			// number of IP addresses to process for each cleanup process

	//-------------
	// constructors
	//-------------
	public UserMonitor() {
		this.windowSize = Integer.parseInt(ContextLoader.getConfigBean().getProperty("userMonitor.windowSize").trim());
		this.badHitCount = Integer.parseInt(ContextLoader.getConfigBean().getProperty("userMonitor.badHitCount").trim());
		this.cleanupFrequency = Integer.parseInt(ContextLoader.getConfigBean().getProperty("userMonitor.cleanupFrequency").trim());
		this.cleanupSize = Integer.parseInt(ContextLoader.getConfigBean().getProperty("userMonitor.cleanupSize").trim());
		this.isEnabled = Boolean.parseBoolean(ContextLoader.getConfigBean().getProperty("userMonitor.isEnabled").trim());
	}
	
	//------------------------
	// public instance methods
	//------------------------

	// check whether the given ipAddress is okay to keep requesting content (true) or
	// if they've reached their limit and need to have access suspended (false).  This
	// method is specific to a single UserMonitor instance, so could be used directly
	// if a given Controller needed its own separate monitoring.
	public boolean isOkay(String ipAddress) {
		if (!isEnabled) {
			return true; 
		}
		
		long currentTime = System.currentTimeMillis();
		
		synchronized(this) {
			cleanupCounter++;
			
			if (cleanupCounter >= cleanupFrequency) {
				doCleanup(currentTime);
			}
			
			// If this is the first hit for this IP address...
			if (!hits.containsKey(ipAddress)) {
				hits.put(ipAddress, new ArrayList());
				hits.get(ipAddress).add(currentTime);
				toCheck.add(ipAddress);
				
			// We've seen this IP address before, but it hasn't reached its limit.
			} else if (hits.get(ipAddress).size() < badHitCount) {
				hits.get(ipAddress).add(currentTime);

			// This IP address has reached its limit of hits.  We need to prune out old
			// ones (outside the window size), though, to see if they're all recent
			// enough to count against it.  (We do this only once the limit is reached
			// so we don't incur this cost for every hit.)
			} else {
				hits.put(ipAddress, pruneOldHits(hits.get(ipAddress), currentTime - windowSize));
				hits.get(ipAddress).add(currentTime);

				if (hits.get(ipAddress).size() >= badHitCount) {
					logger.info("UserMonitor rejected " + ipAddress + ": " + hits.get(ipAddress).size() + " hits");
					return false;
				}
			}
		}
		
		return true;
	}
	
	// get basic reporting about UserMonitor's current status
	public String toString() {
		return toCheck.size() + " addresses being monitored";
	}
	
	// return the list of IP addresses being monitored (for reporting)
	public List<String> getIPs() {
		return toCheck;
	}
	
	// return the list of times associated with ipAddress (for reporting)
	public List<Long> getHits(String ipAddress) {
		if (hits.containsKey(ipAddress)) {
			return hits.get(ipAddress);
		}
		return new ArrayList();
	}
	
	// get a ModelAndView object for forwarding control to the JSP that notifies the user of 
	// exceeding access limits
	public ModelAndView getLimitedMessage() {
		return new ModelAndView("traffic_limited");
	}

	// get a String naming the JSP that notifies the user of exceeding access limits
	public String getLimitedJSP() {
		return "traffic_limited";
	}

	//----------------------
	// public static methods
	//----------------------
	
	// return the shared UserMonitor
	public static UserMonitor getSharedInstance() {
		return sharedMonitor;
	}
	
	//-------------------------
	// private instance methods
	//-------------------------

	// remove items from 'hitList' up to the point where 'windowStart' is or would be
	private List<Long> pruneOldHits(List<Long> hitList, long windowStart) {
		int index = Collections.binarySearch(hitList, windowStart);
		
		// if 'index' is negative, it indicates that 'windowStart' is not actually in
		// the list (no surprise there).  In that case index is (-position - 1) for the
		// position where it would be inserted.
		if (index < 0) {
			index = 0 - (index + 1);
		}
		
		for (int i = 0; i < index; i++) {
			hitList.remove(0);
		}

		return hitList;
	}
	
	// Go through the first 'cleanupSize' IP addresses in 'toCheck', prune their old hits,
	// and remove any that aren't still active.
	private void doCleanup(long currentTime) {
		// start at the n-th IP address in 'toCheck' and work back to the first one (to
		// avoid index issues by deleting earlier ones before dealing with later ones)
		
		for (int i = Math.min(toCheck.size(), cleanupSize) - 1; i >= 0; i--) {
			// For this IP address: prune out its old hits and see if there are any hits still
			// within the monitoring window.  If so, move it to the end of 'toCheck' to be checked
			// again later on.  If not, just remove it from both 'hits' and 'toCheck'.
			
			String ipAddress = toCheck.get(i);
			if (hits.containsKey(ipAddress)) {
				hits.put(ipAddress, pruneOldHits(hits.get(ipAddress), currentTime));
				if (hits.get(ipAddress).size() == 0) {
					hits.remove(ipAddress);
				} else {
					toCheck.add(ipAddress);
				}
			}
			toCheck.remove(i);
		}
		cleanupCounter = 0;		// reset the counter of hits between calls to cleanup
	}
}
