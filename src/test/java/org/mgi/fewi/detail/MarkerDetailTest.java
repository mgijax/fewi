package org.mgi.fewi.detail;

import java.util.ArrayList;

import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.MarkerLocation;
import org.jax.mgi.fewi.detail.MarkerDetail;
import org.junit.Assert;
import org.junit.Test;

public class MarkerDetailTest {

	/*
	 * Test genetic map string generation
	 */
	@Test
	public void testGeneticMapLocationNoCoords() {
		Marker marker = mockMarker();
		String locationString = (new MarkerDetail(marker)).getGeneticMapLocation();
		Assert.assertEquals("", locationString);
	}

	@Test
	public void testGeneticMapLocationCytoband() {
		Marker marker = mockMarker();
		marker.getLocations().add(mockCytoband("Test CHR", "test band"));
		String locationString = (new MarkerDetail(marker)).getGeneticMapLocation();
		Assert.assertEquals("Chromosome Test CHR, cytoband test band", locationString);
	}

	@Test
	public void testGeneticMapLocationCytobandUnknown() {
		Marker marker = mockMarker();
		marker.getLocations().add(mockCytoband("UN", "test band"));
		String locationString = (new MarkerDetail(marker)).getGeneticMapLocation();
		Assert.assertEquals("Chromosome Unknown, cytoband test band", locationString);
	}

	@Test
	public void testGeneticMapLocationCentimorgan() {
		Marker marker = mockMarker();
		marker.getLocations().add(mockCentimorgan("Test CHR", 35.4905f));
		String locationString = (new MarkerDetail(marker)).getGeneticMapLocation();
		Assert.assertEquals("Chromosome Test CHR, 35.49 cM", locationString);
	}

	@Test
	public void testGeneticMapLocationCentimorganUnknown() {
		Marker marker = mockMarker();
		marker.getLocations().add(mockCentimorgan("UN", 35.4905f));
		String locationString = (new MarkerDetail(marker)).getGeneticMapLocation();
		Assert.assertEquals("Chromosome Unknown", locationString);
	}

	@Test
	public void testGeneticMapLocationCentimorganSyntenic() {
		Marker marker = mockMarker();
		marker.getLocations().add(mockCentimorgan("Test CHR", -1.0f));
		String locationString = (new MarkerDetail(marker)).getGeneticMapLocation();
		Assert.assertEquals("Chromosome Test CHR, Syntenic", locationString);
	}

	@Test
	public void testGeneticMapLocationCentimorganAndCytoband() {
		Marker marker = mockMarker();
		marker.getLocations().add(mockCentimorgan("Test CHR", 35.4905f));
		marker.getLocations().add(mockCytoband("Test CHR", "test band"));
		String locationString = (new MarkerDetail(marker)).getGeneticMapLocation();
		Assert.assertEquals("Chromosome Test CHR, 35.49 cM, cytoband test band", locationString);
	}

	@Test
	public void testGeneticMapLocationSyntenicQTL() {
		Marker marker = mockMarker();
		marker.getLocations().add(mockCentimorgan("Test CHR", -1.0f));
		marker.setMarkerType("QTL");
		String locationString = (new MarkerDetail(marker)).getGeneticMapLocation();
		Assert.assertEquals("Chromosome Test CHR, cM position of peak correlated region/marker: Syntenic", locationString);
	}

	@Test
	public void testGeneticMapLocationCentimorganQTL() {
		Marker marker = mockMarker();
		marker.getLocations().add(mockCentimorgan("Test CHR", 35.4905f));
		marker.setMarkerType("QTL");
		String locationString = (new MarkerDetail(marker)).getGeneticMapLocation();
		Assert.assertEquals("Chromosome Test CHR, 35.49 cM <span style=\"font-style: italic;font-size: smaller;\">(cM position of peak correlated region/marker)</span>", locationString);
	}

	// Helpers

	/*
	 * return a mock marker for testing
	 */
	private Marker mockMarker() {
		Marker marker = new Marker();
		marker.setLocations(new ArrayList<MarkerLocation>());
		marker.setMarkerType("test");

		return marker;
	}

	private MarkerLocation mockCytoband(String chromosome, String cytoOffset) {
		MarkerLocation location = new MarkerLocation();
		location.setLocationType("cytogenetic");
		location.setChromosome(chromosome);
		location.setCytogeneticOffset(cytoOffset);
		return location;
	}

	private MarkerLocation mockCentimorgan(String chromosome, Float cmOffset) {
		MarkerLocation location = new MarkerLocation();
		location.setLocationType("centimorgans");
		location.setChromosome(chromosome);
		location.setCmOffset(cmOffset);
		location.setMapUnits("cM");
		return location;
	}
}
