package org.jax.mgi.fewi.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.ObjectTypes;

public class FewiLinker {

	public static FewiLinker linker;
	public Map<String, String> idUrlMap = new HashMap<String, String> ();
	public Map<String, String> keyUrlMap = new HashMap<String, String> ();

	private Properties config = null;

	private FewiLinker () {

		try {
			config = ContextLoader.getConfigBean();
		} catch (Exception e) {};

		String baseUrl = config.getProperty("FEWI_URL");
		String javaWiUrl = config.getProperty("JAVAWI_URL");
		String pythonWiUrl = config.getProperty("WI_URL");

		idUrlMap.put(ObjectTypes.MARKER,  baseUrl +"marker/%s");
		idUrlMap.put(ObjectTypes.REFERENCE, baseUrl +"reference/%s");
		idUrlMap.put(ObjectTypes.ALLELE, javaWiUrl +"WIFetch?page=alleleDetail&id=%s");
		idUrlMap.put(ObjectTypes.SEQUENCE, baseUrl +"sequence/%s");
		idUrlMap.put(ObjectTypes.IMAGE, baseUrl +"image/%s");
		idUrlMap.put(ObjectTypes.HOMOLOGY, baseUrl +"homology/%s");
		idUrlMap.put(ObjectTypes.MARKER_CLUSTER, baseUrl +"homology/%s");
		idUrlMap.put(ObjectTypes.DISEASE, baseUrl +"disease/%s");
		idUrlMap.put(ObjectTypes.GENE, javaWiUrl +"?page=markerDetail&id=%s");
		idUrlMap.put(ObjectTypes.GO_BP, pythonWiUrl +"searches/GO.cgi?id=%s");
		idUrlMap.put(ObjectTypes.GO_MF, pythonWiUrl +"searches/GO.cgi?id=%s");
		idUrlMap.put(ObjectTypes.GO_CC, pythonWiUrl +"searches/GO.cgi?id=%s");
		idUrlMap.put(ObjectTypes.MP, pythonWiUrl +"searches/Phat.cgi?id=%s");
		idUrlMap.put(ObjectTypes.ASSAY, baseUrl +"assay/%s");

		// Mapping that is key based

		keyUrlMap.put(ObjectTypes.MARKER,  baseUrl +"marker/key/%s");
		keyUrlMap.put(ObjectTypes.REFERENCE, baseUrl +"reference/key/%s");
		keyUrlMap.put(ObjectTypes.ALLELE, javaWiUrl +"WIFetch?page=alleleDetail&key=%s");
		keyUrlMap.put(ObjectTypes.SEQUENCE, baseUrl +"sequence/key/%s");
		keyUrlMap.put(ObjectTypes.PROBECLONE, pythonWiUrl +"searches/probe.cgi?%s");
		keyUrlMap.put(ObjectTypes.IMAGE, baseUrl +"image/key/%s");
		keyUrlMap.put(ObjectTypes.ASSAY, baseUrl +"assay/key/%s");
		keyUrlMap.put(ObjectTypes.ANTIBODY, pythonWiUrl +"searches/antibody.cgi?%s");
		keyUrlMap.put(ObjectTypes.ANTIGEN, pythonWiUrl +"searches/antigen.cgi?%s");
		keyUrlMap.put(ObjectTypes.MAPPING, baseUrl +"mapping/key/%s");
		keyUrlMap.put(ObjectTypes.DISEASE, baseUrl +"disease/key/%s");
		keyUrlMap.put(ObjectTypes.HOMOLOGY, baseUrl +"homology/key/%s");
		keyUrlMap.put(ObjectTypes.MARKER_CLUSTER, baseUrl +"homology/key/%s");

	}

	public String getFewiIDLink(String objectType, String id) {

		if (idUrlMap.containsKey(objectType)) {
			return String.format(idUrlMap.get(objectType), id);
		}

		return null;
	}

	public String getFewiKeyLink(String objectType, String key) {

		if (keyUrlMap.containsKey(objectType)) {
			return String.format(keyUrlMap.get(objectType), key);
		}

		return  "Link Type " + objectType + " not available yet";
	}

	public static FewiLinker getInstance() {
		if (linker == null) {
			linker = new FewiLinker();
			return linker;
		}
		return linker;
	}

}
