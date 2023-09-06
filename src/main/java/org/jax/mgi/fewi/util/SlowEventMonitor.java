package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jax.mgi.fewi.summary.SlowEvent;

// TODO: methods for count of current open events, list of n most recent timed-out events, display in JSP

/* Is: a watchdog for identify slow-running pieces of code, especially useful for tracking slow queries.
 * Has: a set of open events and a list of the longest running events seen so far, with each event
 * 	having a type
 */
public class SlowEventMonitor {
	/*------------------------*/
	/*--- static variables ---*/
	/*------------------------*/
	
	// shared monitor used by static methods
	private static SlowEventMonitor sharedMonitor = null;
	
	/*--------------------------*/
	/*--- instance variables ---*/
	/*--------------------------*/
	
	// maps from key to its corresponding MonitoredEvent (for events begun but not yet finished)
	private Map<String, MonitoredEvent> openEvents = new HashMap<String, MonitoredEvent>();

	// maps from an eventType to its slowest events seen so far (finished events only)
	private Map<String, MonitoredEventSet> slowestEvents = new HashMap<String, MonitoredEventSet>();
	
	// maps from an eventType to a limited-size list of events that have failed
	private Map<String, List<SlowEvent>> failedEvents = new HashMap<String, List<SlowEvent>>();
	
	// characters to choose from when making a key, random number generator to use in choosing from them, and
	// the minimum number of characters to make up a key
	private char[] keyCharacters = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ:;,.!?[]{}()".toCharArray();
	private Random randNum = new Random();
	private int keyLength = 3;

	// number of events we want to keep for each event type
	private int eventsToTrack = 20;
	
	// number of milliseconds to wait between cleanup steps, looking for aged-out events
	private int timeToCleanup = 1000 * 10;		// 10 minutes
	
	// time in milliseconds of the last cleanup step
	private long lastCleanupTime = 0;
	
	// number of milliseconds to wait before giving up on an event found during cleanup
	private int maxWaitTime = 1000 * 60;		// 1 hour
	
	/*-------------------------------*/
	/*--- public instance methods ---*/
	/*-------------------------------*/

	// default constructor
	public SlowEventMonitor() {}

	// returns a list of event types currently logged, sorted alphabetically
	public List<String> getEventTypes() {
		List<String> eventTypes = new ArrayList<String>();
		for (String eventType : slowestEvents.keySet()) {
			eventTypes.add(eventType);
		}
		Collections.sort(eventTypes);
		return eventTypes;
	}
	
	// returns true if the given eventType has any events logged so far, false if not
	public boolean contains(String eventType) {
		return this.slowestEvents.containsKey(eventType);
	}
	
	// returns true if the given eventType has any failures logged so far, false if not
	public boolean hasFailures(String eventType) {
		return this.failedEvents.containsKey(eventType);
	}
	
	// Create an event for the given parameters and begin tracking it.
	public String startEvent(String eventType, String identifier) {
		MonitoredEvent event = new MonitoredEvent(eventType, identifier);
		String key = this.getNextKey();
		openEvents.put(key, event);
			
		// If it's time for another cleanup step, go through the open events and remove any that have aged out.
		if (lastCleanupTime < (event.startTime - timeToCleanup)) {
			this.cullExpiredEvents(event.startTime);
		}

		return key;
	}

	// Note that the event identified by the given key has finished, and add it to the list of slowest
	// events if necessary.
	public void endEvent(String key) {
		if (openEvents.containsKey(key)) {
			MonitoredEvent event = openEvents.get(key);
			event.finish();
			if (!slowestEvents.containsKey(event.eventType)) {
				slowestEvents.put(event.eventType, new MonitoredEventSet(key));
			}
			slowestEvents.get(event.eventType).addEvent(event);
			openEvents.remove(key);
		}
	}
	
	// Get the list of the slowest events recorded for the given event type.
	public List<SlowEvent> getSlowEvents(String eventType) {
		if (this.contains(eventType)) {
			return this.slowestEvents.get(eventType).getSlowEvents();
		}
		return null;
	}
	
	// Note that the event identified by the given key has failed, with the given reason
	public void failEvent(String key, String reason) {
		if (!openEvents.containsKey(key)) {
			return;
		}
		
		long now = (new Date()).getTime();
		MonitoredEvent event = openEvents.get(key);
		openEvents.remove(key);

		SlowEvent failedEvent = new SlowEvent(event.startTime, now - event.startTime, event.eventType, event.identifier, reason);
		
		// First failure for this event type?  If so, need an array.
		if (!this.failedEvents.containsKey(event.eventType)) {
			this.failedEvents.put(event.eventType, new ArrayList<SlowEvent>());
		}
		this.failedEvents.get(event.eventType).add(failedEvent);
		
		// If we're past the number to track, remove the oldest ones. (should be only 1 at a time)
		while (this.failedEvents.get(event.eventType).size() > eventsToTrack) {
			this.failedEvents.get(event.eventType).remove(0);
		}
	}
	
	// Get the list of the most recent 20 failed events for the given event type.
	public List<SlowEvent> getFailedEvents(String eventType) {
		if (this.failedEvents.containsKey(eventType)) {
			return this.failedEvents.get(eventType);
		}
		return null;
	}
	
	/*--------------------------------*/
	/*--- private instance methods ---*/
	/*--------------------------------*/

	// return a new key to be used to uniquely identify an event
	private String getNextKey() {
		String key = "";
		int i = 0;
		
		// If we get to the regular length key and that one is already in use, keep adding characters until
		// we find a unique one.
		while ((i < keyLength) || openEvents.containsKey(key)) {
			key = key + keyCharacters[randNum.nextInt(keyCharacters.length)];
			i++;
		}
		
		return key;
	}
		
	// remove any expired events from the map of open events
	private void cullExpiredEvents(long now) {
		long expiredTime = now - maxWaitTime;
		List<String> toRemove = new ArrayList<String>();
				
		// First need to collect the keys to remove, then remove them in a second step to avoid a
		// concurrent modification exception.
			
		for (String key : openEvents.keySet()) {
			if (openEvents.get(key).startTime <= expiredTime) {
				toRemove.add(key);
			}
		}

		synchronized (this) {
			for (String key : toRemove) {
				failEvent(key, "Timed out, stopped monitoring");
			}
		}
		lastCleanupTime = now;
	}

	/*-----------------------------*/
	/*--- private inner classes ---*/
	/*-----------------------------*/
	
	/* Is: a single event, either being monitored or having finished
	 */
	class MonitoredEvent {
		public String eventType = null;
		public String identifier = null;
		public Long startTime = null;
		public Long endTime = null;
		public Long elapsedTime = null;

		// constructor
		public MonitoredEvent(String eventType, String identifier) {
			this.eventType = eventType;
			this.identifier = identifier;
			this.startTime = (new Date()).getTime();
		}
		
		// note that this event has ended
		public void finish() {
			this.endTime = (new Date()).getTime();
			this.elapsedTime = endTime - startTime;
		}
	}
	
	/* Is: a comparator for ordering MonitoredEvents by elapsed time (slowest first)
	 */
	class MonitoredEventComparator implements Comparator<MonitoredEvent> {
		public int compare (MonitoredEvent a, MonitoredEvent b) {
			// Sort by elapsedTime first, then startTime secondarily (if no elapsed time yet).

			if (a.elapsedTime != null) {
				if (b.elapsedTime != null) {
					// Both events have finished.  Sort highest to lowest.
					// (This branch should always be used, but we include the others for completeness.)
					return Long.compare(b.elapsedTime, a.elapsedTime);
				}

				// b has not finished, so a first
				return -1;

			} else if (b.elapsedTime != null) {
				// a has not finished, so b first.
				return 1;

			} else {
				// Neither has finished, so sort by start time.  (oldest first)
				return Long.compare(a.startTime, b.startTime);
			}
		}
	}
	
	/* Is: a set of events for a single event type
	 */
	class MonitoredEventSet {
		public String eventType = null;
		public List<MonitoredEvent> events = null;
		public Long fastestEventKept = null;
		
		// constructor
		public MonitoredEventSet(String eventType) {
			this.eventType = eventType;
			this.reset();
		}
		
		// reset this object to its initial state
		public void reset() {
			this.events = new ArrayList<MonitoredEvent>();
			this.fastestEventKept = 9999999L;
		}
		
		// add event to the set, if it's going to be kept (performance-wise)
		public void addEvent(MonitoredEvent event) {
			// We should keep this event if either:
			// 1. the cache is not fully populated, or
			// 2. this event is slower than the fastest one we've kept so far
			if ((this.events.size() < eventsToTrack) || (event.elapsedTime > fastestEventKept)) {
				this.events.add(event);
				this.fastestEventKept = Math.min(this.fastestEventKept, event.elapsedTime);
				
				// Let the set of events build up with a little buffer, so we're not sorting and trimming
				// every time we see a slow event.
				if (this.events.size() > (10 + eventsToTrack)) {
					this.cullSlowEvents();
				}
			}
		}
		
		// return an ordered list (slowest to fastest) of events seen so far
		public List<MonitoredEvent> getEvents() {
			this.cullSlowEvents();
			return this.events;
		}
		
		// order our list of MonitoredEvents (slowest to fastest) and trim it to the max length (if over)
		private void cullSlowEvents() {
			if (this.events != null) {
				Collections.sort(this.events, new MonitoredEventComparator());
				this.events = this.events.subList(0, Math.min(eventsToTrack, this.events.size()));
			}
		}
		
		// get a list of the slow events for this event type
		public List<SlowEvent> getSlowEvents() {
			this.cullSlowEvents();
			List<SlowEvent> slowEvents = new ArrayList<SlowEvent>();
			
			if (this.events != null) {
				for (MonitoredEvent event : this.events) {
					slowEvents.add(new SlowEvent(event.startTime, event.elapsedTime, event.eventType, event.identifier, null));
				}
			}
			return slowEvents;
		}
	}

	/*-----------------------------*/
	/*--- public static methods ---*/
	/*-----------------------------*/
	
	// return a shared instance of a SlowEventMonitor
	public static SlowEventMonitor getSharedMonitor() {
		if (sharedMonitor == null) { sharedMonitor = new SlowEventMonitor(); }
		return sharedMonitor;
	}
}
