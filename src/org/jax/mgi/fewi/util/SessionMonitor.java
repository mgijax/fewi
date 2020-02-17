package org.jax.mgi.fewi.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Is: a container for tracking user sessions, both so the fewi can determine if a user is still
 *	waiting for results (or has quit) and so status updates can be delivered to the user.
 */
public class SessionMonitor {
	//--- static (class variables) ---
	private static SessionMonitor sharedMonitor = new SessionMonitor();
	private final Logger logger = LoggerFactory.getLogger(SessionMonitor.class);
	
	// maximum number of milliseconds since the last recorded event to still consider that session open
	private static long maxAge = 10 * 60 * 1000;
	
	//--- instance variables ---
	
	private Map<String,SessionInfo> sessions = new HashMap<String,SessionInfo>();
	
	//--- constructors ---
	
	// default constructor is okay -- no need to define one at present

	//--- public instance methods ---
	
	// returns true if the given session is still active, false if not
	public boolean isActive(String sessionID) {
		if (this.sessions.containsKey(sessionID)) {
			this.sessions.get(sessionID).timestamp();
			logger.debug("Found sessionID: " + sessionID);
			return true;
		}
		logger.debug("Unknown sessionID: " + sessionID);
		return false;
	}
	
	// register a new session with the given ID
	public void startSession(String sessionID) {
		// If we're getting crowded with old sessions, make sure to remove any defunct ones.
		if (this.sessions.size() >= 100) {
			this.cleanup();
		}
		SessionInfo info = new SessionInfo();
		this.sessions.put(sessionID, info);
	}
	
	// unregister the session with the given ID
	public void endSession(String sessionID) {
		if (this.sessions.containsKey(sessionID)) {
			this.sessions.remove(sessionID);
		}
	}
	
	// get the current status message associated with the given session ID
	public String getStatus(String sessionID) {
		if (!this.sessions.containsKey(sessionID)) {
			logger.debug("Missing ID: " + sessionID);
			return "Waiting...";
		} 
		logger.debug("Returning status for ID: " + sessionID + ", " + this.sessions.get(sessionID).status);
		this.sessions.get(sessionID).timestamp();
		return this.sessions.get(sessionID).status;
	}
	
	// set a new status message for the given session ID (if it's still active)
	public void setStatus(String sessionID, String message) {
		if (this.isActive(sessionID)) {
			logger.debug("Set message: " + message);
			this.sessions.get(sessionID).status = message;
		}
	}
	
	//--- private instance methods ---
	
	// clean up and remove any defunct sessions (those with no status updates and no client requests for n minutes)
	private void cleanup() {
		long cutoff = System.currentTimeMillis() - maxAge;
		Set<String> sessionIDs = this.sessions.keySet();
		for (String sessionID : sessionIDs) {
			if (this.sessions.get(sessionID).lastActive < cutoff) {
				this.sessions.remove(sessionID);
			}
		}
	}
	
	//--- class methods ---
	
	// get the shared SessionMonitor
	public static SessionMonitor getSharedMonitor() {
		return sharedMonitor;
	}
	
	//--- private inner class ---
	private class SessionInfo {
		long lastActive = System.currentTimeMillis();
		String status = "Registered";
		
		public void timestamp() {
			this.lastActive = System.currentTimeMillis();
		}
	}
}
