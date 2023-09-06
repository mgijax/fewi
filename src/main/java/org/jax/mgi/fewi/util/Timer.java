package org.jax.mgi.fewi.util;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Timer {
	
	private Date time;
	private String message = null;
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public Timer() {
		time = new Date();
		time();
	}
	
	public Timer(String in) {
		message = in;
		time = new Date();
		time();
	}
	
	public Timer(Logger log, String in) {
		this.log = log;
		message = in;
		time = new Date();
		time();
	}
	
	public void time() {
		time(null);
	}
	
	public void time(String in) {
		Date dt = time;
		time = new Date();
		if(in != null && message != null) {
			log.info("Time Elapsed: " + message + ": " + in + ": " + (time.getTime() - dt.getTime()));
		} else if(message != null) {
			log.info("Time Elapsed: " + message + ": " + (time.getTime() - dt.getTime()));
		} else if(in != null) {
			log.info("Time Elapsed: " + in + ": " + (time.getTime() - dt.getTime()));
		} else {
			log.info("Time Elapsed: " + (time.getTime() - dt.getTime()));
		}
	}
}