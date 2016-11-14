package org.jax.mgi.fewi.test.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fewi.util.HmdcAnnotationGroup;
import org.springframework.web.servlet.ModelAndView;

/*
 * Test data gathering methods for HMDC popups
 */
public class HmdcPopupTestApi {

	// ModelAndView returned from DiseasePortalController.popup()
	ModelAndView mav;
	
	public HmdcPopupTestApi(ModelAndView mav) {
		this.mav = mav;
	}
	
	public List<String> getPhenoGenotypeClusterStrings() {
		List<String> genotypeDisplays = new ArrayList<String>();
		
		HmdcAnnotationGroup mpGroup = (HmdcAnnotationGroup) mav.getModel().get("mpGroup");
		Map<Integer, String> allelePairMap = mpGroup.getAllelePairMap();
		
		
		for (Integer mouseID : mpGroup.getMouseRowIDs()) {
			String genotypeDisplay = allelePairMap.get(mouseID);
			genotypeDisplay = this.formatGenotypeDisplay(genotypeDisplay);
			genotypeDisplays.add(genotypeDisplay);
		}
		
		return genotypeDisplays;
	}
	
	public List<String> getPhenoTerms() {
		
		HmdcAnnotationGroup mpGroup = (HmdcAnnotationGroup) mav.getModel().get("mpGroup");

		return 	mpGroup.getColumns();
	}
	
	public String getPhenoGridCheck(String genoRow, String phenoCol) {
		String check = "";
		
		HmdcAnnotationGroup mpGroup = (HmdcAnnotationGroup) mav.getModel().get("mpGroup");
		Map<Integer, String> allelePairMap = mpGroup.getAllelePairMap();
		Map<String, Integer> columnIDMap = mpGroup.getColumnIDMap();
		Map<Integer, Map<Integer, Integer>> countMap = mpGroup.getCountMap();
		
		// find row
		Integer foundRowID = null;
		for (Integer rowID : mpGroup.getMouseRowIDs()) {
			String genotypeDisplay = allelePairMap.get(rowID);
			genotypeDisplay = this.formatGenotypeDisplay(genotypeDisplay);
			
			if (genotypeDisplay.equals(genoRow)) {
				foundRowID = rowID;
			}
		}
		
		Integer foundColumnID = columnIDMap.get(phenoCol);
		
		Integer count = countMap.get(foundRowID).get(foundColumnID);
		if (count > 0) {
			check = "check";
		}
		
		return check;
	}
	
	public List<String> getDiseaseGenotypeClusterStrings() {
		List<String> genotypeDisplays = new ArrayList<String>();
		
		HmdcAnnotationGroup omimGroup = (HmdcAnnotationGroup) mav.getModel().get("omimGroup");
		Map<Integer, String> allelePairMap = omimGroup.getAllelePairMap();
		
		
		for (Integer mouseID : omimGroup.getMouseRowIDs()) {
			String genotypeDisplay = allelePairMap.get(mouseID);
			genotypeDisplay = this.formatGenotypeDisplay(genotypeDisplay);
			genotypeDisplays.add(genotypeDisplay);
		}
		
		return genotypeDisplays;
	}
	
	public List<String> getDiseaseHumanMarkers() {
		List<String> humanMarkers = new ArrayList<String>();
		HmdcAnnotationGroup omimGroup = (HmdcAnnotationGroup) mav.getModel().get("omimGroup");
		Map<Integer, String> humanSymbolMap = omimGroup.getHumanSymbolMap();
		
		for (Integer rowID : omimGroup.getHumanRowIDs()) {
			String humanSymbol = humanSymbolMap.get(rowID);
			humanMarkers.add(humanSymbol);
		}
		
		
		return humanMarkers;
	}
	
	
	public String getDiseaseGridCheck(String genoRow, String diseaseCol) {
		String check = "";
		
		HmdcAnnotationGroup omimGroup = (HmdcAnnotationGroup) mav.getModel().get("omimGroup");
		Map<Integer, String> allelePairMap = omimGroup.getAllelePairMap();
		Map<String, Integer> columnIDMap = omimGroup.getColumnIDMap();
		Map<Integer, Map<Integer, Integer>> countMap = omimGroup.getCountMap();
		
		// find row
		Integer foundRowID = null;
		for (Integer rowID : omimGroup.getMouseRowIDs()) {
			String genotypeDisplay = allelePairMap.get(rowID);
			genotypeDisplay = this.formatGenotypeDisplay(genotypeDisplay);
			
			if (genotypeDisplay.equals(genoRow)) {
				foundRowID = rowID;
			}
		}
		
		Integer foundColumnID = columnIDMap.get(diseaseCol);
		
		Integer count = countMap.get(foundRowID).get(foundColumnID);
		if (count > 0) {
			check = "check";
		}
		
		return check;
	}
	
	private String formatGenotypeDisplay(String genotypeDisplay) {
		return genotypeDisplay.replace("<sup>","<").replace("</sup>", ">");
	}
	
}
