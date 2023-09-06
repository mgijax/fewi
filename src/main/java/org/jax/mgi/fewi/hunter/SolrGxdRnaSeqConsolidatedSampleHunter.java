package org.jax.mgi.fewi.hunter;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRnaSeqConsolidatedSample;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/* Is: a hunter specifically for RNA-Seq consolidated samples.
 */
@Repository
public class SolrGxdRnaSeqConsolidatedSampleHunter extends SolrGxdSummaryBaseHunter {
	public SolrGxdRnaSeqConsolidatedSampleHunter() {
		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output. In this case we want to actually get a
		 * specific field, and return it rather than a list of keys.
		 */
		keyString = GxdResultFields.CONSOLIDATED_SAMPLE_KEY;

		// column fields (by consolidated sample, aka: bioreplicate set)
		this.returnedFields.add(GxdResultFields.STRUCTURE_EXACT);
		this.returnedFields.add(GxdResultFields.THEILER_STAGE);
		this.returnedFields.add(GxdResultFields.STRAIN);
		this.returnedFields.add(GxdResultFields.GENOTYPE);
		this.returnedFields.add(GxdResultFields.ASSAY_MGIID);
		this.returnedFields.add(GxdResultFields.AGE);
		this.returnedFields.add(GxdResultFields.STRUCTURE_PRINTNAME);
		this.returnedFields.add(GxdResultFields.CONSOLIDATED_SAMPLE_KEY);
		this.returnedFields.add(GxdResultFields.SEX);
	}

	 @Override
	 protected void packInformation(QueryResponse rsp, SearchResults<SolrGxdEntity> sr, SearchParams sp) {
	
		 SolrDocumentList sdl = rsp.getResults();
		 logger.debug("packing GXD RNA-Seq consolidated sample data");
		
		 // Iterate through the response documents, extracting the information that was configured above.
		 String wildType = "wild-type";
		
		 for (SolrDocument doc : sdl)
		 {
			 SolrGxdRnaSeqConsolidatedSample resultObject = new SolrGxdRnaSeqConsolidatedSample();
			
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

			 // Add result to SearchResults
			 sr.addResultObjects(resultObject);
		 }
		 this.packFacetData(rsp, sr);
	 }

	@Value("${solr.gxdConsolidatedSample.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
