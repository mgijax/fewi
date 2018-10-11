package org.jax.mgi.fewi.util.link;

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
		idUrlMap.put(ObjectTypes.ANTIBODY, baseUrl +"antibody/%s");
		idUrlMap.put(ObjectTypes.ALLELE, baseUrl +"allele/%s");
		idUrlMap.put(ObjectTypes.SEQUENCE, baseUrl +"sequence/%s");
		idUrlMap.put(ObjectTypes.IMAGE, baseUrl +"image/%s");
		idUrlMap.put(ObjectTypes.HOMOLOGY, baseUrl +"homology/%s");
		idUrlMap.put(ObjectTypes.MARKER_CLUSTER, baseUrl +"homology/%s");
		idUrlMap.put(ObjectTypes.DISEASE, baseUrl +"disease/%s");
		idUrlMap.put(ObjectTypes.GENE, javaWiUrl +"?page=markerDetail&id=%s");
		idUrlMap.put(ObjectTypes.GO_BP, baseUrl +"vocab/gene_ontology/%s");
		idUrlMap.put(ObjectTypes.GO_MF, baseUrl +"vocab/gene_ontology/%s");
		idUrlMap.put(ObjectTypes.GO_CC, baseUrl +"vocab/gene_ontology/%s");
		idUrlMap.put(ObjectTypes.MP, baseUrl +"vocab/mp_ontology/%s");
		idUrlMap.put(ObjectTypes.ASSAY, baseUrl +"assay/%s");
		idUrlMap.put(ObjectTypes.GENOTYPE, baseUrl +"allele/genoview/%s");
		idUrlMap.put(ObjectTypes.EMAPA, baseUrl +"vocab/gxd/anatomy/%s");
		idUrlMap.put(ObjectTypes.EMAPS, baseUrl +"vocab/gxd/anatomy/%s");
		idUrlMap.put(ObjectTypes.PROBECLONE, baseUrl +"probe/%s");
		idUrlMap.put(ObjectTypes.MAPPING, baseUrl + "mapping/%s");
		idUrlMap.put(ObjectTypes.STRAIN, baseUrl + "strain/%s");
		idUrlMap.put(ObjectTypes.SNP, baseUrl + "snp/%s");

		// Mapping that is key based

		keyUrlMap.put(ObjectTypes.MARKER,  baseUrl +"marker/key/%s");
		keyUrlMap.put(ObjectTypes.REFERENCE, baseUrl +"reference/key/%s");
		keyUrlMap.put(ObjectTypes.ALLELE, baseUrl +"allele/key/%s");
		keyUrlMap.put(ObjectTypes.SEQUENCE, baseUrl +"sequence/key/%s");
		keyUrlMap.put(ObjectTypes.IMAGE, baseUrl +"image/key/%s");
		keyUrlMap.put(ObjectTypes.ASSAY, baseUrl +"assay/key/%s");
		keyUrlMap.put(ObjectTypes.ANTIBODY, baseUrl +"antibody/key/%s");
		keyUrlMap.put(ObjectTypes.MAPPING, baseUrl +"mapping/key/%s");
		keyUrlMap.put(ObjectTypes.HOMOLOGY, baseUrl +"homology/key/%s");
		keyUrlMap.put(ObjectTypes.MARKER_CLUSTER, baseUrl +"homology/key/%s");
		keyUrlMap.put(ObjectTypes.PROBECLONE, baseUrl +"probe/key/%s");

	}

	public String getFewiIDLink(String objectType, String id) {
		/* Note that we purposely omitted VOCAB_TERM from the idUrlMap,
		 * so we can have custom handling here based on the type of the
		 * particular ID (eg.- EMAPA/EMAPS IDs).
		 */

		if (idUrlMap.containsKey(objectType)) {
			return String.format(idUrlMap.get(objectType), id);
		} else if (objectType.startsWith("EMAPS:")) {
			if (id != null) {
				return config.getProperty("FEWI_URL") + "vocab/gxd/anatomy/" + id;
			}
		} else if (objectType.startsWith("MA:")) {
			if (id != null) {
				return config.getProperty("FEWI_URL") + "vocab/gxd/ma_ontology/" + id;
			}
		} else if (objectType.startsWith("EMAPA:")) {
			if (id != null) {
				return config.getProperty("FEWI_URL") + "vocab/gxd/anatomy/" + id;
			}
		} else if (objectType.startsWith("GO:")) {
			if (id != null) {
				return config.getProperty("FEWI_URL") + "vocab/gene_ontology/" + id;
			}
		} else if (objectType.startsWith("MP:")) {
			if (id != null) {
				return config.getProperty("FEWI_URL") + "vocab/mp_ontology/" + id;
			}
		} else if (objectType.startsWith("DOID:")) {
			if (id != null) {
				return config.getProperty("FEWI_URL") + "disease/" + id;
			}
		} else if (objectType.startsWith("HP:")) {
			if (id != null) {
				return config.getProperty("FEWI_URL") + "vocab/hp_ontology/" + id;
			}
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
