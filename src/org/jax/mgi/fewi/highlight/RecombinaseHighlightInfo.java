package org.jax.mgi.fewi.highlight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jax.mgi.fewi.searchUtil.entities.SolrCreSystemHighlight;

/**
 * Maps and stores all the information for highlighting Solr results
 * for the Recombinase Allele summary
 */
public class RecombinaseHighlightInfo {

	// alleleKey to list of detected system highlights
	private final Map<String, Set<String>> detectedSystemsMap = new HashMap<String, Set<String>>();
	// alleleKey to list of not detected system highlights
	private final Map<String, Set<String>> notDetectedSystemsMap = new HashMap<String, Set<String>>();
	
	
	/*
	 * Maps a list of system group highlights to the 
	 * 	detected and not detected systems maps by alleleKey
	 */
	public void addSystemHighlights(List<SolrCreSystemHighlight> systemHighlights) {
		
		for (SolrCreSystemHighlight systemHighlight : systemHighlights) {
			
			String alleleKey = systemHighlight.getAlleleKey();
			
			if (systemHighlight.getDetected()) {
				
				if ( !detectedSystemsMap.containsKey(alleleKey) ) {
					detectedSystemsMap.put(alleleKey, new HashSet<String>());
				}
				detectedSystemsMap.get(alleleKey).addAll(systemHighlight.getSystems());
				
			}
			else {
				if ( !notDetectedSystemsMap.containsKey(alleleKey) ) {
					notDetectedSystemsMap.put(alleleKey, new HashSet<String>());
				}
				notDetectedSystemsMap.get(alleleKey).addAll(systemHighlight.getSystems());
			}
		}
	}
	
	public Set<String> getDetectedHighlights(String alleleKey) {
		
		if (detectedSystemsMap.containsKey(alleleKey)) {
			return detectedSystemsMap.get(alleleKey);
		}
		return new HashSet<String>();
	}
	
	public Set<String> getNotDetectedHighlights(String alleleKey) {
		
		if (notDetectedSystemsMap.containsKey(alleleKey)) {
			return notDetectedSystemsMap.get(alleleKey);
		}
		return new HashSet<String>();
	}
	
}
