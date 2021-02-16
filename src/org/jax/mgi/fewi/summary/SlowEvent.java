package org.jax.mgi.fewi.summary;

import java.util.Date;

// Wrapper over a monitored result object.
public class SlowEvent {
	Date startDate = null;
	Long elapsedTime = null;
	String eventType = null;
	String identifier = null;
	
	public SlowEvent(Long time, Long elapsedTime, String eventType, String identifier) {
		this.startDate = new Date(time);
		this.eventType = eventType;
		this.identifier = identifier;
		this.elapsedTime = elapsedTime;
	}

	public String getStartDate() {
		return startDate.toString();
	}

	public String getEventType() {
		return eventType;
	}

	public String getIdentifier() {
		return identifier;
	}
	
	public String getElapsedTime() {
		return elapsedTime.toString();
	}
}
