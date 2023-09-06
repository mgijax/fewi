package org.jax.mgi.fewi.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;

import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.MarkerLocation;
import org.jax.mgi.fe.datamodel.RelatedMarker;

/* Is: a wrapper over a TSS (transcription start site) marker.
 * Does: encapsulates the marker data and exposes it for the TSS table on the marker detail page,
 * 		including providing a few convenience methods.
 */
public class TssMarkerWrapper {
	private Marker sourceMarker;
	private RelatedMarker tssMarker;
	private int sortableChromosome;
	private Double startCoordinate;
	private Double distanceFromStart;
	
	public TssMarkerWrapper(Marker src, RelatedMarker tss) {
		this.sourceMarker = src;
		this.tssMarker = tss;
		this.cacheValues();
	}
	
	// set up any internal caches of data (to avoid re-retrieval for repeated calls when sorting, etc.)
	private void cacheValues() {
		// sortable chromosome value (1-19, then X, Y, and anything else)
		String chrom = tssMarker.getRelatedMarker().getChromosome();
		try {
			this.sortableChromosome = Integer.parseInt(chrom);
		} catch (NumberFormatException e) {
			if ("X".equals(chrom)) {
				this.sortableChromosome = 20;
			} else if ("Y".equals(chrom)) {
				this.sortableChromosome = 21;
			}
			this.sortableChromosome = 22;		// catch-all
		}
		
		// Find the start coordinate of this TSS and its distance from the start of the source gene.
		
		MarkerLocation tssCoords = tssMarker.getRelatedMarker().getPreferredCoordinates();
		MarkerLocation mrkCoords = sourceMarker.getPreferredCoordinates();

		if (tssCoords == null) {
			this.startCoordinate = null;
			this.distanceFromStart = 0.0;
		} else {
			this.startCoordinate = tssCoords.getStartCoordinate();
			this.distanceFromStart = computeDistance(mrkCoords, tssCoords);
		}
	}
	
	public static Double computeDistance(MarkerLocation mrkCoords, MarkerLocation tssCoords) {
		// To find the distance, we figure using the midpoint of the TSS coordinates.  For + strand
		// markers, we figure from the marker's start.  For - strand markers, use the marker's end.
			
		Double midpoint = (double) Math.round((tssCoords.getStartCoordinate() + tssCoords.getEndCoordinate()) / 2.0);
		if (mrkCoords != null) {
			if ("+".equals(mrkCoords.getStrand())) {
				return midpoint - mrkCoords.getStartCoordinate();
			} else {
				return mrkCoords.getEndCoordinate() - midpoint;
			}
		}
		return 0.0;
	}
	
	public String getDistanceFromStart() {
		if (this.distanceFromStart == 0.0) { return "0"; }
		NumberFormat formatter = new DecimalFormat("#,###");
		return formatter.format(this.distanceFromStart);
	}
	
	public String getPrimaryID() {
		return tssMarker.getRelatedMarkerID();
	}
	
	public String getSymbol() {
		return tssMarker.getRelatedMarkerSymbol();
	}
	
	public String getLocation() {
		return tssMarker.getRelatedMarkerLocation();
	}
	
	public TssMarkerWrapperComparator getComparator() {
		return new TssMarkerWrapperComparator();
	}
	
	private class TssMarkerWrapperComparator implements Comparator<TssMarkerWrapper> {
		@Override
		public int compare(TssMarkerWrapper o1, TssMarkerWrapper o2) {
			// by chromosome first, then by start coordinate
			int c = Integer.compare(o1.sortableChromosome, o2.sortableChromosome);
			if (c == 0) {
				if (o1.distanceFromStart != null) {
					if (o2.distanceFromStart!= null) {
						c = o1.distanceFromStart.compareTo(o2.distanceFromStart);
					} else {
						c = -1;		// o1 first
					}
				} else if (o2.distanceFromStart != null) {
					c = 1;			// o2 first
				} else {
					// fall back on symbol if chromosomes match and neither has coordinates (shouldn't happen)
					c = o1.getSymbol().compareTo(o2.getSymbol());
				}
			}
			return c;
		}
	}
}
