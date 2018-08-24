package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.List;
import org.jax.mgi.fewi.util.FormatHelper;

public class SnpQueryForm {

	private String nomen;
	private String withinRange = "2000";
	private String markerID;
	private String coordinateUnit;
	private String coordinate;
	private String locationSearch;
	private String geneSearch;
	private String selectedTab;
	private String referenceStrain;
	private String selectedChromosome;
	private String searchGeneBy;
	private String searchBySameDiff = "";
	private boolean hideStrains = true;
	private List<String> selectedStrains;
	private List<String> functionClassFilter;
	private String displayStrains;			// for text file output

	private static int allStrainsCount = 88;

	public List<String> getFunctionClassFilter() {
		return functionClassFilter;
	}
	public void setFunctionClassFilter(List<String> functionClassFilter) {
		this.functionClassFilter = functionClassFilter;
	} 
	public String getNomen() {
		return nomen;
	}
	public void setNomen(String nomen) {
		this.nomen = nomen;
	}
	public String getWithinRange() {
		return withinRange;
	}
	public void setWithinRange(String withinRange) {
		this.withinRange = withinRange;
	}
	public String getMarkerID() {
		return markerID;
	}
	public void setMarkerID(String markerID) {
		this.markerID = markerID;
	}
	public String getCoordinateUnit() {
		return coordinateUnit;
	}
	public void setCoordinateUnit(String coordinateUnit) {
		this.coordinateUnit = coordinateUnit;
	}
	public String getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
	public String getLocationSearch() {
		return locationSearch;
	}
	public void setLocationSearch(String locationSearch) {
		this.locationSearch = locationSearch;
	}
	public String getGeneSearch() {
		return geneSearch;
	}
	public void setGeneSearch(String geneSearch) {
		this.geneSearch = geneSearch;
	}
	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	public String getReferenceStrain() {
		return referenceStrain;
	}
	public void setReferenceStrain(String referenceStrain) {
		this.referenceStrain = referenceStrain;
	}
	public String getSelectedChromosome() {
		return selectedChromosome;
	}
	public void setSelectedChromosome(String selectedChromosome) {
		this.selectedChromosome = selectedChromosome;
	}
	public String getSearchGeneBy() {
		return searchGeneBy;
	}
	public void setSearchGeneBy(String searchGeneBy) {
		this.searchGeneBy = searchGeneBy;
	}
	public String getDisplayStrains() {
		return displayStrains;
	}
	public void setDisplayStrains(String displayStrains) {
		this.displayStrains = displayStrains;
	}
	public String getSearchBySameDiff() {
		return searchBySameDiff;
	}
	public void setSearchBySameDiff(String searchBySameDiff) {
		this.searchBySameDiff = searchBySameDiff;
	}
	public boolean isHideStrains() {
		return hideStrains;
	}
	public void setHideStrains(boolean hideStrains) {
		this.hideStrains = hideStrains;
	}
	public List<String> getSelectedStrains() {
		return selectedStrains;
	}
	public void setSelectedStrains(List<String> selectedStrains) {
		this.selectedStrains = selectedStrains;
	}

	/* a couple of supporting methods for the getParameterDescriptions()
	 * method; could be spun out into a superclass which provides them for
	 * all query forms
	 */
	protected String bold(String s) {
		if ((s == null) || (s.length() == 0)) { return ""; }
		return "<span class='bold'>" + s + "</span>";
	}

	protected String italic(String s) {
		if ((s == null) || (s.length() == 0)) { return ""; }
		return "<span class='italic'>" + s + "</span>";
	}

	protected String smallGray(String s) {
		if ((s == null) || (s.length() == 0)) { return ""; }
		return "<span class='smallGray'>" + s + "</span>";
	}

	/* if the coordinate range in s has the greater before the lesser,
	 * swap them
	 */
	protected String orientCoordinates(String s) {
		if ((s == null) || (s.length() == 0)) { return ""; }

		int pos = s.indexOf("-");
		if (pos > 0) {
			try {
				Double d1 = Double.parseDouble(s.substring(0, pos));
				Double d2 = Double.parseDouble(s.substring(pos + 1));
				if (d1 > d2) {
					return s.substring(pos + 1)
						+ "-" + s.substring(0, pos);
				}
			} catch (Exception e) {}
		}

	       	return s;	// fallback: display original string
	}

	public List<String> getReferenceStrainList() {
		List<String> strains = new ArrayList<String>();
		if (this.referenceStrain != null) {
			strains.add(this.referenceStrain);
		}
		return strains;
	}
	
	/* returns a List of Strings which describe in plain Engligh the
	 * parameters encoded in this query form.  (These Strings can be used
	 * as the "You Searched For" text on the SNP summary page.)
	 */
	public List<String> getYouSearchedFor() {
		List<String> out = new ArrayList<String>();

		// ordering of these 'if' statements is important, to ensure
		// that the output order is correct for both QF flavors

		// Gene Nomenclature
		if (nomen != null) {
			String breadth = "";
			if ("marker_symbol".equals(searchGeneBy)) {
				breadth = " searching current mouse symbols";
			} else if ("homologSymbols".equals(searchGeneBy)) {
				breadth = " searching current mouse and homolog symbols";
			} else if ("nomenclature".equals(searchGeneBy)) {
				breadth = " searching current symbols/names, synonyms &amp; homologs";
			}
			out.add("Gene Symbol/Name: " + bold(FormatHelper.superscript(nomen)) + smallGray(breadth));

			// Include SNPs located...
			if (withinRange != null) {
				String wr = null;
				if ("0".equals(withinRange)) {
					wr = "within";
				} else if ("2000".equals(withinRange)) {
					wr ="2 kb upstream/downstream of";
				} else if ("10000".equals(withinRange)) {
					wr ="10 kb upstream/downstream of";
				}
				if (wr != null) {
					out.add("Include SNPs located: " + bold(wr) + " specified genes");
				}
			}
		}

		// Genome Region (chromosome + coordinates)
		if ((coordinate != null) && (coordinateUnit != null) && (selectedChromosome != null)) {
			String cu = "bp";
			if ("mbp".equalsIgnoreCase(coordinateUnit)) {
				cu = "Mbp";
			}
			out.add("Genome Region: " + bold("Chr" + selectedChromosome + ":") + bold(orientCoordinates(coordinate)) + " " + cu);

		} else if (selectedChromosome != null) {
			out.add("Genome Region: " + bold("Chr" + selectedChromosome));
		}

		// Reference Strain
		if ((referenceStrain != null) && (referenceStrain.length() > 0)) {
			out.add("Reference Strain: " + bold(FormatHelper.superscript(referenceStrain)));

			if(searchBySameDiff != null && searchBySameDiff.length() > 0) {
				if(searchBySameDiff.equals("diff_reference")) {
					out.add("SNPs with alleles " + bold("different from") + " the Reference Strain");
				}
				if(searchBySameDiff.equals("same_reference")) {
					out.add("SNPs with alleles " + bold("same as") + " the Reference Strain");
				}
			}
		}

		// Selected Strains
		if (selectedStrains != null) {
			int strainCount = selectedStrains.size();
			if (strainCount == allStrainsCount) {
				out.add("Selected Strains: " + bold("ALL"));
			} else {
				out.add("Selected Strains: " + bold("" + strainCount));
			}
		}

		// sorting (always show the default - and only - sort method)
		out.add("Sorted by genome location");
		return out;
	}
	
	@Override
	public String toString() {

		String ret = "SnpQueryForm [\n";
		
		if(nomen != null) ret += "nomen=" + nomen + "\n";
		if(withinRange != null) ret += "withinRange=" + withinRange + "\n";
		if(markerID != null) ret += "markerID=" + markerID + "\n";
		if(coordinateUnit != null) ret += "coordinateUnit=" + coordinateUnit + "\n";
		if(coordinate != null) ret += "coordinate=" + coordinate + "\n";
		if(locationSearch != null) ret += "locationSearch=" + locationSearch + "\n";
		if(geneSearch != null) ret += "geneSearch=" + geneSearch + "\n";
		if(selectedTab != null) ret += "selectedTab=" + selectedTab + "\n";
		if(selectedChromosome != null) ret += "selectedChromosome=" + selectedChromosome + "\n";
		
		if(selectedStrains != null) ret += "selectedStrains=" + selectedStrains + "\n";
		if(referenceStrain != null) ret += "referenceStrain=" + referenceStrain + "\n";
		
		if(searchGeneBy != null) ret += "searchGeneBy=" + searchGeneBy + "\n";
		if(searchBySameDiff != null) ret += "searchBySameDiff=" + searchBySameDiff + "\n";
		
		if(functionClassFilter != null) ret += "functionClassFilter=" + functionClassFilter + "\n";
		
		ret += "hideStrains=" + hideStrains + "\n";

		ret += "]";
				
		return ret;
	}
}
