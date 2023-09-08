package unittest.fewi.detail;

import java.util.ArrayList;
import java.util.Arrays;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleCellLine;
import mgi.frontend.datamodel.AlleleNote;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerQtlExperiment;
import mgi.frontend.datamodel.phenotype.PhenoTableSystem;

import org.jax.mgi.fewi.detail.AlleleDetail;
import org.junit.Assert;
import org.junit.Test;

/*
 * Unit tests for the various ribbons and logic for rendering allele detail page
 */
public class AlleleDetailTest {

	/*
	 * Mutation Origin section
	 */
	@Test
	public void testMutationOriginNotExists() {
		Allele allele = mockEmptyAllele();
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertFalse(detail.getHasMutationOrigin());
	}

	@Test
	public void testMutationOriginHasStrain() {
		Allele allele = mockEmptyAllele();
		allele.setStrain("test");
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasMutationOrigin());
	}
	
	@Test
	public void testMutationOriginHasTransmission() {
		Allele allele = mockEmptyAllele();
		allele.setTransmissionType("test");
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasMutationOrigin());
	}
	
	@Test
	public void testMutationOriginHasMutantCellLines() {
		Allele allele = mockEmptyAllele();
		AlleleCellLine cellLine = new AlleleCellLine();
		cellLine.setMutant(true);
		allele.setAlleleCellLines(Arrays.asList(cellLine));
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasMutationOrigin());
	}
	
	@Test
	public void testMutationOriginHasParentCellLine() {
		Allele allele = mockEmptyAllele();
		AlleleCellLine cellLine = new AlleleCellLine();
		cellLine.setParent(true);
		allele.setAlleleCellLines(Arrays.asList(cellLine));
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasMutationOrigin());
	}
	
	/*
	 * IMSR ribbon section
	 */
	@Test
	public void testIMSRRibbonQtl() {
		Allele allele = mockEmptyAllele();
		allele.setAlleleType("QTL");
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertFalse(detail.getHasIMSR());
	}
	@Test
	public void testIMSRRibbonAppears() {
		Allele allele = mockEmptyAllele();
		allele.setAlleleType("test");
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasIMSR());
	}
	
	/*
	 * Expression ribbon
	 */
	@Test
	public void testExpressionRibbonHasAssayResultCount() {
		Allele allele = mockEmptyAllele();
		allele.setCountOfExpressionAssayResults(1);
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasExpression());
	}
	
	@Test
	public void testExpressionRibbonNoAssayResultCount() {
		Allele allele = mockEmptyAllele();
		allele.setCountOfExpressionAssayResults(0);
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertFalse(detail.getHasExpression());
	}
	
	/*
	 * References Ribbon
	 */
	@Test
	public void testReferencesRibbonHasReferenceCount() {
		Allele allele = mockEmptyAllele();
		allele.setCountOfReferences(1);
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasReferences());
	}
	
	@Test
	public void testReferencesRibbonNoReferenceCount() {
		Allele allele = mockEmptyAllele();
		allele.setCountOfReferences(0);
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertFalse(detail.getHasReferences());
	}
	
	/*
	 * Disease Models Ribbon
	 */
	@Test
	public void testDiseaseModelRibbonHasModels() {
		Allele allele = mockEmptyAllele();
		allele.setHasDiseaseModel(true);
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasDiseaseModel());
	}
	
	@Test
	public void testDiseaseModelRibbonNoModels() {
		Allele allele = mockEmptyAllele();
		allele.setHasDiseaseModel(false);
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertFalse(detail.getHasDiseaseModel());
	}

	/*
	 * Phenotypes Ribbon
	 */
	@Test
	public void testPhenotypesRibbonHasPhenotypes() {
		Allele allele = mockEmptyAllele();
		PhenoTableSystem phenoSystem = new PhenoTableSystem();
		allele.setPhenoTableSystems(Arrays.asList(phenoSystem));
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasPhenotypes());
	}
	
	@Test
	public void testPhenotypesRibbonNoPhenotypes() {
		Allele allele = mockEmptyAllele();
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertFalse(detail.getHasPhenotypes());
	}
	
	/*
	 * Recombinase Ribbon
	 */
	@Test
	public void testRecombinaseRibbonHasDriver() {
		Allele allele = mockEmptyAllele();
		allele.setDriver("Mario Andretti");
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasRecombinaseData());
	}
	
	@Test
	public void testRecombinaseRibbonNoDriver() {
		Allele allele = mockEmptyAllele();
		allele.setNotes(new ArrayList<AlleleNote>());
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertFalse(detail.getHasRecombinaseData());
	}
	
	/*
	 * Notes Ribbon
	 */
	@Test
	public void testNotesRibbonNoNotes() {
		Allele allele = mockEmptyAllele();
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertFalse(detail.getHasNotes());
	}
	@Test
	public void testNotesRibbonHasQtlMappingNote() {
		Allele allele = mockEmptyAllele();
		allele.setAlleleType("QTL");
		MarkerQtlExperiment qtlMapping = new MarkerQtlExperiment();
		qtlMapping.setNoteType("TEXT-QTL");
		qtlMapping.setNote("test");
		allele.getMarker().setQtlExperiments(Arrays.asList(qtlMapping));
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasNotes());
	}
	@Test
	public void testNotesRibbonHasQtlCandidateGeneNote() {
		Allele allele = mockEmptyAllele();
		allele.setAlleleType("QTL");
		MarkerQtlExperiment qtlCandidates = new MarkerQtlExperiment();
		qtlCandidates.setNoteType("TEXT-QTL-Candidate Genes");
		qtlCandidates.setNote("test");
		allele.getMarker().setQtlExperiments(Arrays.asList(qtlCandidates));
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasNotes());
	}
	@Test
	public void testNotesRibbonHasQtlNotesButAlleleNotQTL() {
		Allele allele = mockEmptyAllele();
		allele.setAlleleType("test");
		MarkerQtlExperiment qtlMapping = new MarkerQtlExperiment();
		qtlMapping.setNoteType("TEXT-QTL");
		qtlMapping.setNote("test");
		MarkerQtlExperiment qtlCandidates = new MarkerQtlExperiment();
		qtlCandidates.setNoteType("TEXT-QTL-Candidate Genes");
		qtlCandidates.setNote("test");
		allele.getMarker().setQtlExperiments(Arrays.asList(qtlMapping,qtlCandidates));
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertFalse(detail.getHasNotes());
	}
	@Test
	public void testNotesRibbonHasDerivationNote() {
		Allele allele = mockEmptyAllele();
		AlleleNote derivationNote = new AlleleNote();
		derivationNote.setNoteType("Derivation");
		derivationNote.setNote("test");
		allele.setNotes(Arrays.asList(derivationNote));
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasNotes());
	}
	@Test
	public void testNotesRibbonHasGeneralNote() {
		Allele allele = mockEmptyAllele();
		AlleleNote generalNote = new AlleleNote();
		generalNote.setNoteType("General");
		generalNote.setNote("test");
		allele.setNotes(Arrays.asList(generalNote));
		AlleleDetail detail = new AlleleDetail(allele);
		Assert.assertTrue(detail.getHasNotes());
	}
	
	/*
	 * helper functions
	 */
	private Allele mockEmptyAllele()
	{
		Allele allele = new Allele();
		allele.setAlleleCellLines(new ArrayList<AlleleCellLine>());
		allele.setNotes(new ArrayList<AlleleNote>());
                allele.setCountOfHtExperiments(0);
		
		Marker marker = new Marker();
		marker.setQtlExperiments(new ArrayList<MarkerQtlExperiment>());
		allele.setMarkers(Arrays.asList(marker));
		return allele;
	}
}
