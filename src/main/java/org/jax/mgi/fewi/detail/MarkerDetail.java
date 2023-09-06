package org.jax.mgi.fewi.detail;

import java.text.DecimalFormat;

import org.jax.mgi.fe.datamodel.Marker;

/*
 * Logic for renderering the marker detail page
 */
public class MarkerDetail {

	private final Marker marker;
	
	public MarkerDetail(Marker marker) {
		this.marker = marker;
	}
	
	/*
	 * Display string of marker location in Genetic Map section
	 *   
	 *   Conditionally displays based on chromosome, centimorgan, cytboand, and QTL type
	 * 
	 * See MarkerDetailTest for examples of output values
	 */
	public String getGeneticMapLocation() {
		StringBuilder sb = new StringBuilder();
		
		boolean hasCentimorgans = marker.getPreferredCentimorgans() != null;
		boolean hasCytoband = marker.getPreferredCytoband() != null;
		
		// Change UN to Unknown
		String chromosome = marker.getGeneticChromosome();
		if (chromosome.equals("UN")) {
			chromosome = "Unknown";
		}
		
		// add Chromosome
		if (hasCentimorgans || hasCytoband) {
			sb.append("Chromosome ");
			sb.append(chromosome);
		}
		
		// add centimorgan and units
		if (hasCentimorgans && !chromosome.equals("Unknown")) {
			sb.append(", ");
			if (marker.getPreferredCentimorgans().getCmOffset() > 0.0) {
				DecimalFormat df = new DecimalFormat("0.00");
				sb.append(df.format(marker.getPreferredCentimorgans().getCmOffset()));
				sb.append(" ");
				sb.append(marker.getPreferredCentimorgans().getMapUnits());
			}
		}
		
		// add cytoband
		if (hasCytoband) {
			sb.append(", cytoband ");
			sb.append(marker.getPreferredCytoband().getCytogeneticOffset());
		}
		
		// add QTL text
		if (hasCentimorgans) {
			if (marker.getPreferredCentimorgans().getCmOffset() > 0.0) {
				if (marker.getMarkerType().equals("QTL")) {
					sb.append(" <span style=\"font-style: italic;font-size: smaller;\">(cM position of peak correlated region/marker)</span>");
				}
			}
			// special format for Syntenic QTL
			else if (marker.getPreferredCentimorgans().getCmOffset() == -1.0) {
				if (marker.getMarkerType().equals("QTL")) {
					sb.append("cM position of peak correlated region/marker: ");
				}
				sb.append("Syntenic");
			}
		}
		
		return sb.toString();
	}
}
