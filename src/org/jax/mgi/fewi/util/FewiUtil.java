package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.AccessionID;

/**
 * General purpose utility methods
 * 	 (for very basic things that don't fit a specific purpose)
 */
public class FewiUtil {

	/*
	 * Returns a list of the batches of the original list based on batch size
	 */
	public static <T> List<List<T>> getBatches(List<T> collection,int batchSize){
	    int i = 0;
	    List<List<T>> batches = new ArrayList<List<T>>();
	    while(i<collection.size()){
	        int nextInc = Math.min(collection.size()-i,batchSize);
	        List<T> batch = collection.subList(i,i+nextInc);
	        batches.add(batch);
	        i = i + nextInc;
	    }

	    return batches;
	}
	
	/* Returns the logical database of the first accession ID that matches the given ID string
	 * or null if no match.
	 */
	public static String getLogicalDB(List<AccessionID> ids, String desiredID) {
		for (AccessionID id : ids) {
			if (desiredID.equalsIgnoreCase(id.getAccID())) {
				return id.getLogicalDB();
			}
		}
		return null;
	}
	
	/* cleanses the given 'accID' of malicious Javascript (from a reflected cross-site scripting attack),
	 * returning a sanitized version of accID.  Removes quotes (double and single), angle brackets, spaces,
	 * equals signs, parentheses, etc.
	 */
	public String sanitizeID(String accID) {
		if (accID == null) { return accID; }

		// Allow all letters and numbers, plus underscore, colon, hyphen, and period.
		// Strip out everything else.
		return accID.replaceAll("[^A-Za-z0-9_:\\.\\-]", "");
	}
}
