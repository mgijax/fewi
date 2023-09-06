package org.mgi.fewi.util.link;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.MarkerID;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.jax.mgi.fewi.util.link.IDLinker.ActualDB;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IDLinkerTest {

	private IDLinker linker;
	
	/*
	 * Urls for testing
	 */
	private final String FEWI_URL = "www.informatics.jax.org/";
	private final String ENTREZ_GENE_URL = "entrezgene.com/@@@@";
	
	@Before
	public void setUp() {
		this.linker = new IDLinker();
		this.linker.setGlobalProperties(makeTestProperties());
		this.linker.setlogicalDbMap(makeTestActualDbMap());
	}
	
	private Properties makeTestProperties() {
		Properties props = new Properties();
		
		props.setProperty("FEWI_URL", FEWI_URL);
		return props;
	}
	
	private HashMap<String,List<ActualDB>> makeTestActualDbMap() {
		HashMap<String,List<ActualDB>> ldbToAdb = new HashMap<String,List<ActualDB>>();
		
		// entrez gene
		ActualDB entrezGene = this.linker.new ActualDB("Entrez Gene");
		entrezGene.setUrl(ENTREZ_GENE_URL);
		ldbToAdb.put("Entrez Gene", Arrays.asList(entrezGene));
		
		return ldbToAdb;
	}
	
	/*
	 * Tests for getDefaultMarkerLink
	 */
	@Test
	public void testDefaultMarkerLinkMouse() {
		Marker marker = new Marker();
		marker.setSymbol("Test");
		marker.setOrganism("mouse");
		marker.setPrimaryID("MGI:TEST");
		
		String expected = "<a href='" + FEWI_URL + "marker/MGI:TEST'>Test</a>";
		Assert.assertEquals(expected, this.linker.getDefaultMarkerLink(marker));
	}
	
	@Test
	public void testDefaultMarkerLinkNonMouse() {
		Marker marker = new Marker();
		marker.setSymbol("Test");
		marker.setOrganism("human");

		MarkerID entrezGeneID = new MarkerID();
		entrezGeneID.setAccID("TEST_EG");
		entrezGeneID.setLogicalDB("Entrez Gene");
		
		marker.setIds(Arrays.asList(entrezGeneID));
		
		String expected = "<a href='" + ENTREZ_GENE_URL.replace("@@@@", "TEST_EG") + "' target='_blank'>Test</a>";
		Assert.assertEquals(expected, this.linker.getDefaultMarkerLink(marker));
	}
	
	@Test
	public void testDefaultMarkerLinkNonMouseNoEntrezGene() {
		Marker marker = new Marker();
		marker.setSymbol("Test");
		marker.setOrganism("human");
		
		marker.setIds(new ArrayList<MarkerID>());
		
		Assert.assertEquals("Test", this.linker.getDefaultMarkerLink(marker));
	}

}
