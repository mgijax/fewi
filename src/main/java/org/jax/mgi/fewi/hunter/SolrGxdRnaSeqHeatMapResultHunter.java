package org.jax.mgi.fewi.hunter;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRnaSeqHeatMapResult;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.springframework.stereotype.Repository;

/* Is: a hunter specifically for RNA-Seq expression results.  It only returns the pieces of
 * the results that 
 */
@Repository
public class SolrGxdRnaSeqHeatMapResultHunter extends SolrGxdSummaryBaseHunter {
	public SolrGxdRnaSeqHeatMapResultHunter() {
		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output. In this case we want to actually get a
		 * specific field, and return it rather than a list of keys.
		 */
		keyString = GxdResultFields.RESULT_KEY;

		// column fields (by consolidated sample, aka: bioreplicate set)
		this.returnedFields.add(GxdResultFields.CONSOLIDATED_SAMPLE_KEY);
		
		// row fields (by marker)
		this.returnedFields.add(GxdResultFields.ENSEMBL_GMID);
		this.returnedFields.add(GxdResultFields.MARKER_SYMBOL);
		this.returnedFields.add(GxdResultFields.MARKER_MGIID);
		
		// data cells (average quantile normalized TPM)
		this.returnedFields.add(GxdResultFields.AVG_QN_TPM_LEVEL);
	}

	 @Override
	 protected void packInformation(QueryResponse rsp, SearchResults<SolrGxdEntity> sr, SearchParams sp) {
	
		 SolrDocumentList sdl = rsp.getResults();
		 logger.debug("packing GXD RNA-Seq Heat Map data");
		
		 // Iterate through the response documents, extracting the information that was configured above.
		 String wildType = "wild-type";
		
		 for (SolrDocument doc : sdl)
		 {
			 SolrGxdRnaSeqHeatMapResult resultObject = new SolrGxdRnaSeqHeatMapResult();
			
			 // consolidated sample (bioreplicate set) fields
			 
			 String structureID = (String) doc.getFieldValue(GxdResultFields.STRUCTURE_EXACT);
			 Integer theilerStage = (Integer) doc.getFieldValue(GxdResultFields.THEILER_STAGE);
			 String strain = (String) doc.getFieldValue(GxdResultFields.STRAIN);
			 String genotype = (String) doc.getFieldValue(GxdResultFields.GENOTYPE);
			 String assayMgiID = (String) doc.getFieldValue(GxdResultFields.ASSAY_MGIID);
			 String age = (String) doc.getFieldValue(GxdResultFields.AGE);
			 String structure = (String) doc.getFieldValue(GxdResultFields.STRUCTURE_PRINTNAME);
			 String consolidatedSampleKey = (String) doc.getFieldValue(GxdResultFields.CONSOLIDATED_SAMPLE_KEY);
			 String sex = (String) doc.getFieldValue(GxdResultFields.SEX);

			 if (genotype == null) {
				 genotype = wildType;
			 } else {
				 genotype = FormatHelper.stripAlleleTags(genotype).replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			 }

			 resultObject.setStructureID(structureID);
			 resultObject.setTheilerStage(theilerStage);
			 resultObject.setStrain(strain);
			 resultObject.setAlleles(genotype);
			 resultObject.setAssayMgiID(assayMgiID);
			 resultObject.setAge(age);
			 resultObject.setStructure(structure);
			 resultObject.setConsolidatedSampleKey(consolidatedSampleKey);
			 resultObject.setSex(sex);

			 // marker-related fields
			 
			 String markerSymbol = (String) doc.getFieldValue(GxdResultFields.MARKER_SYMBOL);
			 String markerMgiID = (String) doc.getFieldValue(GxdResultFields.MARKER_MGIID);
			 String markerEnsemblGMID = (String) doc.getFieldValue(GxdResultFields.ENSEMBL_GMID);

			 resultObject.setMarkerSymbol(markerSymbol);
			 resultObject.setMarkerMgiID(markerMgiID);
			 resultObject.setMarkerEnsemblGeneModelID(markerEnsemblGMID);
			 
			 // data cells (average quantile normalized TPM)
			 String tpm = (String) doc.getFieldValue(GxdResultFields.AVG_QN_TPM_LEVEL);
			 resultObject.setAvergageQNTPM(tpm);

			 // Add result to SearchResults
			 sr.addResultObjects(resultObject);
		 }
		
		 this.packFacetData(rsp, sr);
	 }

}
