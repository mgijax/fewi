package org.jax.mgi.fewi.summary;

/**
 * Is: contains cache-able data for a single marker/allele that we don't want to retrieve redundantly
 * from Solr for every single matching document.
 */
public class QSFeaturePart {
	private String primaryID;
	private String symbol;
	private String name;
	private String chromosome;
	private String location;
	private String strand;

	public String getPrimaryID() {
		return primaryID;
	}
	public void setPrimaryID(String primaryID) {
		this.primaryID = primaryID;
	}
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
	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	@Override
	public String toString() {
		return symbol;
	}
}
