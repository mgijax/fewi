package org.jax.mgi.fewi.util;

import java.util.Comparator;

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.RelatedMarker;

/* Is: a wrapper over a TSS (transcription start site) marker.
 * Does: encapsulates the marker data and exposes it for the TSS table on the marker detail page,
 * 		including providing a few convenience methods.
 */
public class TssMarkerWrapper {
	private Marker sourceMarker;
	private RelatedMarker tssMarker;
	private int sortableChromosome;
	private Double startCoordinate;
	
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
		
		// start coordinate of this TSS
		MarkerLocation coords = tssMarker.getRelatedMarker().getPreferredCoordinates();
		if (coords == null) {
			this.startCoordinate = null;
		}
		this.startCoordinate = coords.getStartCoordinate();
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
				if (o1.startCoordinate != null) {
					if (o2.startCoordinate != null) {
						c = o1.startCoordinate.compareTo(o2.startCoordinate);
					} else {
						c = -1;		// o1 first
					}
				} else if (o2.startCoordinate != null) {
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
