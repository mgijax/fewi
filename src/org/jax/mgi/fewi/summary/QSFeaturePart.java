package org.jax.mgi.fewi.summary;

import java.util.List;

/**
 * Is: contains cache-able data for a single marker/allele that we don't want to retrieve redundantly
 * from Solr for every single matching document.
 */
public class QSFeaturePart {
	private String symbol;
	private String name;
	private String chromosome;
	private String startCoord;
	private String endCoord;
	private String strand;

	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getChromosome() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	public String getStartCoord() {
		return startCoord;
	}
	public void setStartCoord(String startCoord) {
		this.startCoord = startCoord;
	}
	public String getEndCoord() {
		return endCoord;
	}
	public void setEndCoord(String endCoord) {
		this.endCoord = endCoord;
	}
	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}
	@Override
	public String toString() {
		return symbol;
	}
}
