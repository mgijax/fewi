package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.List;

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
}