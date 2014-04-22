package unittest.fewi.searchUtil.entities;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridCluster;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridCluster.SolrDpGridClusterMarker;
import org.junit.Test;

public class SolrHdpGridClusterTest {

	/*
	 * Test the parsing of mouse and human markers from Solr (which are in a conflated field).
	 */
	@Test
	public void testParseMouseMarkersEmpty() {
		SolrHdpGridCluster cluster = new SolrHdpGridCluster();
		cluster.setMouseSymbols(Arrays.asList(""));
		List<SolrDpGridClusterMarker> markers = cluster.getMouseMarkers();
		Assert.assertEquals(0,markers.size());
	}
	@Test
	public void testParseMouseMarkers() {
		SolrHdpGridCluster cluster = new SolrHdpGridCluster();
		cluster.setMouseSymbols(Arrays.asList("Pax6||1234||Paired box 6||protein coding gene",
				"Pax5||2345||Paired box 5||QTL"));
		List<SolrDpGridClusterMarker> markers = cluster.getMouseMarkers();
		Assert.assertEquals(2,markers.size());
		Assert.assertEquals("Pax6",markers.get(0).getSymbol());
		Assert.assertEquals("1234",markers.get(0).getMarkerKey());
		Assert.assertEquals("Paired box 6",markers.get(0).getName());
		Assert.assertEquals("protein coding gene",markers.get(0).getFeatureType());
		Assert.assertEquals("Pax5",markers.get(1).getSymbol());
		Assert.assertEquals("2345",markers.get(1).getMarkerKey());
		Assert.assertEquals("Paired box 5",markers.get(1).getName());
		Assert.assertEquals("QTL",markers.get(1).getFeatureType());
	}
	
	@Test
	public void testParseHumanMarkersEmpty() {
		SolrHdpGridCluster cluster = new SolrHdpGridCluster();
		cluster.setHumanSymbols(Arrays.asList(""));
		List<SolrDpGridClusterMarker> markers = cluster.getHumanMarkers();
		Assert.assertEquals(0,markers.size());
	}
	@Test
	public void testParseHumanMarkers() {
		SolrHdpGridCluster cluster = new SolrHdpGridCluster();
		cluster.setHumanSymbols(Arrays.asList("Pax6||1234||Paired box 6||gene",
				"Pax5||2345||Paired box 5"));
		List<SolrDpGridClusterMarker> markers = cluster.getHumanMarkers();
		Assert.assertEquals(2,markers.size());
		Assert.assertEquals("Pax6",markers.get(0).getSymbol());
		Assert.assertEquals("1234",markers.get(0).getMarkerKey());
		Assert.assertEquals("Paired box 6",markers.get(0).getName());
		Assert.assertEquals("gene",markers.get(0).getFeatureType());
		Assert.assertEquals("Pax5",markers.get(1).getSymbol());
		Assert.assertEquals("2345",markers.get(1).getMarkerKey());
		Assert.assertEquals("Paired box 5",markers.get(1).getName());
		Assert.assertEquals("",markers.get(1).getFeatureType());
	}

}
