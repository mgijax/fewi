package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.util.FormatHelper;

public class SnpQueryForm implements Cloneable {

	private String nomen;
	private String withinRange = "2000";
	private String markerID;
	private String coordinateUnit;
	private String coordinate;
	private String locationSearch;
	private String geneSearch;
	private String selectedTab;
	private List<String> referenceStrains;
	private String selectedChromosome;
	private String searchGeneBy;
	private boolean hideStrains = true;
	private List<String> selectedStrains;
	private List<String> functionClassFilter;
	private String displayStrains;			// for text file output
	private String startMarker;
	private String endMarker;
	private String allowNullsForReferenceStrains;
	private String allowNullsForComparisonStrains;
	private List<String> alleleAgreementFilter;
	
	/* These fields are set by zooming in using the heatmap on the summary display.  They should not be
	 * submitted as part of an actual query form submission.
	 */
	private Integer sliceMaxCount;
	private Long sliceStartCoord;
	private Long sliceEndCoord;

	private static int allStrainsCount = 101;

	public SnpQueryForm clone() {
		// implementing this custom to ensure deep copy of Lists

		SnpQueryForm newQF = new SnpQueryForm();
		newQF.nomen = this.nomen;
		newQF.withinRange = this.withinRange;
		newQF.markerID = this.markerID;
		newQF.coordinateUnit = this.coordinateUnit;
		newQF.coordinate = this.coordinate;
		newQF.locationSearch = this.locationSearch;
		newQF.geneSearch = this.geneSearch;
		newQF.selectedTab = this.selectedTab;
		newQF.selectedChromosome = this.selectedChromosome;
		newQF.searchGeneBy = this.searchGeneBy;
		newQF.hideStrains = this.hideStrains;
		newQF.displayStrains = this.displayStrains;
		newQF.startMarker = this.startMarker;
		newQF.endMarker = this.endMarker;
		newQF.allowNullsForReferenceStrains = this.allowNullsForReferenceStrains;
		newQF.allowNullsForComparisonStrains = this.allowNullsForComparisonStrains;
		newQF.sliceEndCoord = this.sliceEndCoord;
		newQF.sliceStartCoord = this.sliceStartCoord;
		newQF.sliceMaxCount = this.sliceMaxCount;

		if (this.referenceStrains != null) {
			newQF.referenceStrains = new ArrayList<String>(this.referenceStrains);
		}
		if (this.selectedStrains != null) {
			newQF.selectedStrains = new ArrayList<String>(this.selectedStrains);
		}
		if (this.functionClassFilter != null) {
			newQF.functionClassFilter = new ArrayList<String>(this.functionClassFilter);
		}
		if (this.alleleAgreementFilter != null) {
			newQF.alleleAgreementFilter = new ArrayList<String>(this.alleleAgreementFilter);
		}
		return newQF;
	}
	
	public void setDefaults() {
		if (this.allowNullsForReferenceStrains == null) {
			this.setAllowNullsForReferenceStrains("no");
		}
		if (this.allowNullsForComparisonStrains == null) {
			this.setAllowNullsForComparisonStrains("yes");
		}
	}
	public List<String> getAlleleAgreementFilter() {
		return alleleAgreementFilter;
	}
	public String getAllowNullsForReferenceStrains() {
		return FormatHelper.noScript(FormatHelper.noAlert(allowNullsForReferenceStrains));
	}

	public Integer getSliceMaxCount() {
		return sliceMaxCount;
	}

	public void setSliceMaxCount(Integer sliceMaxCount) {
		this.sliceMaxCount = sliceMaxCount;
	}

	public Long getSliceStartCoord() {
		return sliceStartCoord;
	}

	public void setSliceStartCoord(Long sliceStartCoord) {
		this.sliceStartCoord = sliceStartCoord;
	}

	public Long getSliceEndCoord() {
		return sliceEndCoord;
	}

	public void setSliceEndCoord(Long sliceEndCoord) {
		this.sliceEndCoord = sliceEndCoord;
	}

	public void setAllowNullsForReferenceStrains(String allowNullsForReferenceStrains) {
		this.allowNullsForReferenceStrains = allowNullsForReferenceStrains;
	}

	public String getAllowNullsForComparisonStrains() {
		return FormatHelper.noScript(FormatHelper.noAlert(allowNullsForComparisonStrains));
	}

	public void setAllowNullsForComparisonStrains(String allowNullsForComparisonStrains) {
		this.allowNullsForComparisonStrains = allowNullsForComparisonStrains;
	}

	public void setAlleleAgreementFilter(List<String> alleleAgreementFilter) {
		this.alleleAgreementFilter = alleleAgreementFilter;
	}
	public List<String> getFunctionClassFilter() {
		return functionClassFilter;
	}
	public void setFunctionClassFilter(List<String> functionClassFilter) {
		this.functionClassFilter = functionClassFilter;
	} 
	public String getNomen() {
		return FormatHelper.noScript(FormatHelper.noAlert(nomen));
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
		return FormatHelper.noAlert(FormatHelper.noScript(coordinate));
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
		return FormatHelper.noScript(FormatHelper.noAlert(selectedTab));
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	public List<String> getReferenceStrains() {
		return referenceStrains;
	}
	public void setReferenceStrains(List<String> referenceStrains) {
		this.referenceStrains = referenceStrains;
	}
	public String getSelectedChromosome() {
		return FormatHelper.noScript(FormatHelper.noAlert(selectedChromosome));
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
	public String orientCoordinates(String s) {
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

	public String getStartMarker() {
		return FormatHelper.noScript(FormatHelper.noAlert(startMarker));
	}

	public void setStartMarker(String startMarker) {
		this.startMarker = startMarker;
	}

	public String getEndMarker() {
		return FormatHelper.noScript(FormatHelper.noAlert(endMarker));
	}

	public void setEndMarker(String endMarker) {
		this.endMarker = endMarker;
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
			out.add("Gene Symbol/Name: " + bold(FormatHelper.superscript(FormatHelper.cleanHtml(nomen))) + smallGray(breadth));

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
		if ((coordinate != null) && (coordinateUnit != null) && (selectedChromosome != null)
				&& (!coordinate.trim().equals("")) && (!selectedChromosome.trim().equals("")) ) {
			String cu = "bp";
			if ("mbp".equalsIgnoreCase(coordinateUnit)) {
				cu = "Mbp";
			}
			out.add("Genome Region: " + bold("Chr" + FormatHelper.cleanHtml(selectedChromosome) + ":") + bold(FormatHelper.cleanHtml(orientCoordinates(coordinate))) + " " + cu);

		} else if ( (selectedChromosome != null) && (!selectedChromosome.trim().equals("")) ) {
			out.add("Genome Region: " + bold("Chr" + FormatHelper.cleanHtml(selectedChromosome)));
		}

		// marker range
		if ( (startMarker != null) && (endMarker != null) && (!startMarker.trim().equals("")) && (!endMarker.trim().equals("")) ) {
			out.add("Marker Range: between " + bold(FormatHelper.cleanHtml(startMarker)) + " and " + bold(FormatHelper.cleanHtml(endMarker)));
		}
		
		// Reference Strain
		boolean showNullMessages = false;
		if ((referenceStrains != null) && (referenceStrains.size() > 0)) {
			String label = "Reference Strain";
			if (referenceStrains.size() == 1) {
				out.add(label + ": " + bold(FormatHelper.superscript(FormatHelper.cleanHtml(referenceStrains.get(0)))));
			} else {
				label = label + "s";
				out.add(label + ": " + bold("" + referenceStrains.size()));
			}
			showNullMessages = true;
			out.add("Include SNPs With No Allele Call in Some Reference Strains: "
				+ bold(FormatHelper.cleanHtml(allowNullsForReferenceStrains)));
		}

		// Selected Strains
		if (selectedStrains != null) {
			int strainCount = selectedStrains.size();
			if (strainCount == allStrainsCount) {
				out.add("Selected Strains: " + bold("ALL"));
			} else if (showNullMessages){
				out.add("Comparison Strains: " + bold("" + strainCount));
			} else {
				out.add("Selected Strains: " + bold("" + strainCount));
			}
			if (showNullMessages) {
				out.add("Include SNPs With No Allele Call in Some Comparison Strains: "
					+ bold(FormatHelper.cleanHtml(allowNullsForComparisonStrains)));
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
		if(referenceStrains != null) ret += "referenceStrains=" + referenceStrains + "\n";
		
		if(searchGeneBy != null) ret += "searchGeneBy=" + searchGeneBy + "\n";
		if(functionClassFilter != null) ret += "functionClassFilter=" + functionClassFilter + "\n";
		
		ret += "hideStrains=" + hideStrains + "\n";

		ret += "]";
				
		return ret;
	}
}
