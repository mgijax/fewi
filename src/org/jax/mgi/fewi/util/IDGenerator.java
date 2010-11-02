package org.jax.mgi.fewi.util;

public class IDGenerator {

	private int counter = 0;
	private Integer counterLock = new Integer(0);
	private String prefix = null;
	
	public IDGenerator() {
		this.prefix = "id";
		return;
	}
	
	public IDGenerator(String prefix) {
		this.prefix = prefix;
		return;
	}
	
	public String nextID() {
		int myCount = 0;
		synchronized (this.counterLock){
			this.counter++;
			
			if (this.counter < 0) {
				this.counter = 1;
			}
			
			myCount = this.counter;
		}
		return this.prefix + myCount;
	}
}
