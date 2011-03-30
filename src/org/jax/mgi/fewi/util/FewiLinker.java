package org.jax.mgi.fewi.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jax.mgi.fewi.searchUtil.ObjectTypes;

public class FewiLinker {
	
	public static FewiLinker linker;
	public Map<String, String> idUrlMap = new HashMap<String, String> ();
	public Map<String, String> keyUrlMap = new HashMap<String, String> ();
	
	private Configuration config = null;
	
	private FewiLinker () {
		
		try {
			config = new PropertiesConfiguration("../properties/fewi.properties");
		} catch (Exception e) {};
		
		String baseUrl = config.getString("FEWI_URL");

		idUrlMap.put(ObjectTypes.MARKER,  baseUrl +"marker/%s");
		idUrlMap.put(ObjectTypes.REFERENCE, baseUrl +"reference/%s");
		idUrlMap.put(ObjectTypes.ALLELE, baseUrl +"allele/%s");
		idUrlMap.put(ObjectTypes.SEQUENCE, baseUrl +"sequence/%s");
		idUrlMap.put(ObjectTypes.PROBECLONE, baseUrl +"probeclone/%s");
		idUrlMap.put(ObjectTypes.IMAGE, baseUrl +"image/%s");
		idUrlMap.put(ObjectTypes.ASSAY, baseUrl +"assay/%s");
		idUrlMap.put(ObjectTypes.ORTHOLOGY, baseUrl +"orthology/%s");

		keyUrlMap.put(ObjectTypes.MARKER,  baseUrl +"marker/key/%s");
		keyUrlMap.put(ObjectTypes.REFERENCE, baseUrl +"reference/key/%s");
		keyUrlMap.put(ObjectTypes.ALLELE, baseUrl +"allele/key/%s");
		keyUrlMap.put(ObjectTypes.SEQUENCE, baseUrl +"sequence/key/%s");
		keyUrlMap.put(ObjectTypes.PROBECLONE, baseUrl +"probeclone/key/%s");
		keyUrlMap.put(ObjectTypes.IMAGE, baseUrl +"image/key/%s");
		keyUrlMap.put(ObjectTypes.ASSAY, baseUrl +"assay/key/%s");
		keyUrlMap.put(ObjectTypes.ORTHOLOGY, baseUrl +"orthology/key/%s");
		
	}
	
	public String getFewiIDLink(String objectType, String id) {
		
		if (idUrlMap.containsKey(objectType)) {
			return String.format(idUrlMap.get(objectType), id);
		}
		
		return "Link Type " + objectType + " not available yet";
	}
	
	public String getFewiKeyLink(String objectType, String key) {
		
		if (idUrlMap.containsKey(objectType)) {
			return String.format(keyUrlMap.get(objectType), key);
		}
		
		return "Link Type " + objectType + " not available yet";
	}

	public static FewiLinker getInstance() {
		if (linker == null) {
			linker = new FewiLinker();
			return linker;
		}
		return linker;
	}
	
}
