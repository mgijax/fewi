package org.jax.mgi.fewi.summary;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.snpdatamodel.ConsensusCoordinateSNP;
import org.jax.mgi.snpdatamodel.ConsensusMarkerSNP;
import org.jax.mgi.snpdatamodel.ConsensusAlleleSNP;
import org.jax.mgi.snpdatamodel.ConsensusSNP;

public class ConsensusSNPSummaryRow {

	/*--- instance variables ---*/

	private ConsensusSNP consensusSNP;
	
	private String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
	private HashMap<String, String> matchingMarkerIdList = new HashMap<String, String>();
	
	private Map<String,String> alleles = null;
	private Map<String,String> conflicts = null;

	/*--- methods ---*/

	public ConsensusSNPSummaryRow(ConsensusSNP consensusSNP, HashMap<String, String> matchingMarkerIdList) {
		this.consensusSNP = consensusSNP;
		this.matchingMarkerIdList = matchingMarkerIdList;
	}
	
	public ConsensusSNP getConsensusSNP() {
		return this.consensusSNP;
	}

	public String getAccid() {
		
		String ret = consensusSNP.getAccid() + "<br>";
		
		ret += "<font class=\"small\">";
		ret += "<a href=\"" + fewiUrl + "snp/" + consensusSNP.getAccid() + "\" target=\"_blank\">MGI&nbsp;SNP&nbsp;Detail</a></font>";
		
		return ret;
	}
	
	public String getLocation() {
		String ret = "";
		String hr = "";
		for(ConsensusCoordinateSNP c: consensusSNP.getConsensusCoordinates()) {
			ret += hr + "Chr" + c.getChromosome() + ":" + c.getStartCoordinate();
			hr = "<hr>";
		}
		return ret;
	}
	
	public String getAssays() {
		return "<a href=\"" + fewiUrl + "snp/" + consensusSNP.getAccid() + "\" target=\"_blank\">" + consensusSNP.getSubSNPs().size() + "</a>";
	}
	
	public String getVariationClass() {
		return consensusSNP.getVariationClass();
	}
	
	public String getAlleleSummary() {
		return consensusSNP.getAlleleSummary();
	}
	
	/* get a map of allele calls, keyed by strain
	 */
	public Map<String,String> getAlleles() {
		if (alleles != null) { return alleles; }

		alleles = new HashMap<String,String>();
		for (ConsensusAlleleSNP s : consensusSNP.getConsensusAlleles()){
			alleles.put(s.getStrain(), s.getAllele());
		}
		return alleles;
	}

	/* get a mapping from a strain name to a string that indictates
	 * whether there is a conflict or not (this specifies a css class)
	 */
	public Map<String,String> getConflicts() {
		if (conflicts != null) { return conflicts; }

		conflicts = new HashMap<String,String>();
		for (ConsensusAlleleSNP s : consensusSNP.getConsensusAlleles()){
			if (s.isConflict() && !"?".equals(s.getAllele())) {
				conflicts.put(s.getStrain(), " conflict");
			} else {
				conflicts.put(s.getStrain(), "");
			}
		}
		return conflicts;
	}

	public String getFunctionClass() {
		String ret = "";
		
		// <StartCoordinate, Marker Symbol, Function Class> = ConsensusMarkerSNP
		TreeMap<Integer, TreeMap<String, TreeMap<String, ConsensusMarkerSNP>>> map = new TreeMap<Integer, TreeMap<String, TreeMap<String,ConsensusMarkerSNP>>>();
		
		for(ConsensusCoordinateSNP c: consensusSNP.getConsensusCoordinates()) {
			if(!map.containsKey(c.getStartCoordinate())) {
				map.put(c.getStartCoordinate(), new TreeMap<String, TreeMap<String, ConsensusMarkerSNP>>());
			}
			
			for(ConsensusMarkerSNP m: c.getMarkers()) {
				if(!map.get(c.getStartCoordinate()).containsKey(m.getSymbol().toLowerCase())) {
					map.get(c.getStartCoordinate()).put(m.getSymbol().toLowerCase(), new TreeMap<String, ConsensusMarkerSNP>());
				}
				map.get(c.getStartCoordinate()).get(m.getSymbol().toLowerCase()).put(m.getFunctionClass().toLowerCase(), m);
				
			}
		}
		
		boolean outOfSync = ("true".equalsIgnoreCase(ContextLoader.getConfigBean().getProperty("snpsOutOfSync")));

		String hr = "";
		for(Integer sc: map.keySet()) {
			ret += hr;
			for(String symbol: map.get(sc).keySet()) {
				for(String functionClass: map.get(sc).get(symbol.toLowerCase()).keySet()) {
					ConsensusMarkerSNP m = map.get(sc).get(symbol.toLowerCase()).get(functionClass.toLowerCase());
					
					// Rules:
					// For a marker that matches the nomen search, show all function classes/distance relationships for that marker
					// For other markers, Display all relationship/function classes except "distance from" (within distance of)
					// Do not show function class "Contig-Reference"
					
					if((matchingMarkerIdList.containsKey(m.getAccid()) || !m.getFunctionClass().equals("within distance of")) && !m.getFunctionClass().equals("Contig-Reference")) {
						if(m.getFunctionClass().equals("within distance of")) {
							m.setFunctionClass(m.getDistanceFrom() + " bp " + m.getDistanceDirection());
							//16 bp downstream of
						}
						if(!outOfSync && m.getFunctionClass().equals("Locus-Region")) {
							ret += "<a href=\"" + fewiUrl + "marker/" + m.getAccid() + "\" target=\"_blank\">" + m.getSymbol() + "</a> <nobr>" + m.getFunctionClass() + " " + m.getDistanceDirection() + "</nobr><br>";
						} else {
							ret += "<a href=\"" + fewiUrl + "marker/" + m.getAccid() + "\" target=\"_blank\">" + m.getSymbol() + "</a> <nobr>" + m.getFunctionClass() + "</nobr><br>";
						}
					}
					
				}
			}
			hr = "<hr>";
		}
		
		return ret;
	}
	
}
