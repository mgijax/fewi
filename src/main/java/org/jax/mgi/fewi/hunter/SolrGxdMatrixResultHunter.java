package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrJoinMapper;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.ESDagEdge;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdGeneMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdStageMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.shr.fe.indexconstants.DagEdgeFields;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Hunter for the GXD Result tissue matrices
 * 
 * It loads a slimmed version of the SolrAssayResult objects
 * 
 */

@Repository
public class SolrGxdMatrixResultHunter extends SolrGxdSummaryBaseHunter {
	public SolrGxdMatrixResultHunter() {
		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output. In this case we want to actually get a
		 * specific field, and return it rather than a list of keys.
		 */
		keyString = GxdResultFields.RESULT_KEY;

		this.returnedFields.add(GxdResultFields.DETECTION_LEVEL);
		// this.returnedFields.add(GxdResultFields.STRUCTURE_PRINTNAME);
		this.returnedFields.add(GxdResultFields.STRUCTURE_EXACT);
		this.returnedFields.add(GxdResultFields.THEILER_STAGE);
		this.returnedFields.add(GxdResultFields.MARKER_SYMBOL);
		// this.returnedFields.add(GxdResultFields.RESULT_KEY);

		this.groupFields.put(SearchConstants.STAGE_MATRIX_GROUP,
				GxdResultFields.STAGE_MATRIX_GROUP);
		this.groupFields.put(SearchConstants.GENE_MATRIX_GROUP,
				GxdResultFields.GENE_MATRIX_GROUP);
	}

	 @Override
	 protected void packInformation(QueryResponse rsp,
	 SearchResults<SolrGxdEntity> sr,
	 SearchParams sp) {
	
		 // A list of all the primary keys in the document
		
		 SolrDocumentList sdl = rsp.getResults();
		 logger.debug("packing gxd test hunt data");
		
		 /**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */
		
		 for (SolrDocument doc : sdl)
		 {
			 //logger.debug(doc.toString());
			 // Set the result object
			 String detectionLevel = (String)
					 doc.getFieldValue(GxdResultFields.DETECTION_LEVEL);
			 //String printname = (String)
			 //	doc.getFieldValue(GxdResultFields.STRUCTURE_PRINTNAME);
			 String structureId = (String)
					 doc.getFieldValue(GxdResultFields.STRUCTURE_EXACT);
			 Integer theilerStage = (Integer)
					 doc.getFieldValue(GxdResultFields.THEILER_STAGE);
			 String geneSymbol = (String)
					 doc.getFieldValue(GxdResultFields.MARKER_SYMBOL);
			
			 ESGxdMatrixResult resultObject = new ESGxdMatrixResult();
			
			 resultObject.setDetectionLevel(detectionLevel);
			 //resultObject.setPrintname(printname);
			 resultObject.setStructureId(structureId);
			 resultObject.setTheilerStage(theilerStage);
			 resultObject.setGeneSymbol(geneSymbol);
			
			 // Add result to SearchResults
			 sr.addResultObjects(resultObject);
		 }
		
		 this.packFacetData(rsp, sr);
	 }

	@Override
	protected void packInformationByGroup(QueryResponse rsp,
			SearchResults<SolrGxdEntity> sr, SearchParams sp) {

		GroupResponse gr = rsp.getGroupResponse();
		// get the group command. In our case, there should only be one.
		GroupCommand gc = gr.getValues().get(0);

		// total count of groups
		int groupCount = gc.getNGroups();
		sr.setTotalCount(groupCount);

		// A list of all the primary keys in the document
		List<String> keys = new ArrayList<String>();

		// A mapping of documentKey -> Row level Metadata objects.
		Map<String, MetaData> metaList = new HashMap<String, MetaData>();

		List<Group> groups = gc.getValues();
		logger.debug("packing information for group: " + gc.getName());

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */

		for (Group g : groups) {
			String key = g.getGroupValue();
			// int numFound = (int) g.getResult().getNumFound();
			// get the top document of the group
			SolrDocument sd = g.getResult().get(0);
			if (gc.getName().equals(GxdResultFields.STAGE_MATRIX_GROUP)) {
				// group on fields relevant to tissue by stage matrix
				String detectionLevel = (String) sd
						.getFieldValue(GxdResultFields.DETECTION_LEVEL);
				String structureId = (String) sd
						.getFieldValue(GxdResultFields.STRUCTURE_EXACT);
				Integer theilerStage = (Integer) sd
						.getFieldValue(GxdResultFields.THEILER_STAGE);

				ESGxdStageMatrixResult resultObject = new ESGxdStageMatrixResult();

				resultObject.setDetectionLevel(detectionLevel);
				resultObject.setStructureId(structureId);
				resultObject.setTheilerStage(theilerStage);
				resultObject.setCount(((Long) g.getResult().getNumFound())
						.intValue());

				// Add result to SearchResults
				sr.addResultObjects(resultObject);
			} else if (gc.getName().equals(GxdResultFields.GENE_MATRIX_GROUP)) {
				// group on fields relevant to tissue by gene matrix
				// GxdGeneMatrixResult
				String detectionLevel = (String) sd
						.getFieldValue(GxdResultFields.DETECTION_LEVEL);
				String structureId = (String) sd
						.getFieldValue(GxdResultFields.STRUCTURE_EXACT);
				Integer theilerStage = (Integer) sd
						.getFieldValue(GxdResultFields.THEILER_STAGE);
				String geneSymbol = (String) sd
						.getFieldValue(GxdResultFields.MARKER_SYMBOL);

				ESGxdGeneMatrixResult resultObject = new ESGxdGeneMatrixResult();

				resultObject.setDetectionLevel(detectionLevel);
				resultObject.setStructureId(structureId);
				resultObject.setTheilerStage(theilerStage);
				resultObject.setGeneSymbol(geneSymbol);
				resultObject.setCount(((Long) g.getResult().getNumFound())
						.intValue());

				// Add result to SearchResults
				sr.addResultObjects(resultObject);
			}

			/**
			 * In order to support older pages we pack the score directly as
			 * well as in the metadata.
			 */

			if (this.keyString != null) {
				keys.add(key);
				// scoreKeys.add("" + doc.getFieldValue("score"));
			}
		}

		// Include the information that was asked for.

		if (keys != null) {
			sr.setResultKeys(keys);
		}

		if (sp.includeRowMeta()) {
			sr.setMetaMapping(metaList);
		}

		this.packFacetData(rsp, sr);
	}

	@Override
	protected void packInformationForJoin(QueryResponse rsp,
			SearchResults<SolrGxdEntity> sr, SearchParams sp) {

		// A list of all the primary keys in the document

		SolrDocumentList sdl = rsp.getResults();
		logger.debug("packing gxd matrix join hunt data");

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */

		for (SolrDocument doc : sdl) {

			ESDagEdge edge = new ESDagEdge();
			edge.setChildId((String) doc.getFieldValue(DagEdgeFields.CHILD_ID));
			edge.setChildTerm((String) doc
					.getFieldValue(DagEdgeFields.CHILD_TERM));
			edge.setChildStartStage((Integer) doc
					.getFieldValue(DagEdgeFields.CHILD_START_STAGE));
			edge.setChildEndStage((Integer) doc
					.getFieldValue(DagEdgeFields.CHILD_END_STAGE));
			edge.setParentId((String) doc
					.getFieldValue(DagEdgeFields.PARENT_ID));
			edge.setParentTerm((String) doc
					.getFieldValue(DagEdgeFields.PARENT_TERM));
			edge.setParentStartStage((Integer) doc
					.getFieldValue(DagEdgeFields.PARENT_START_STAGE));
			edge.setParentEndStage((Integer) doc
					.getFieldValue(DagEdgeFields.PARENT_END_STAGE));

			sr.addResultObjects(edge);
		}

		this.packFacetData(rsp, sr);
	}

	@Value("${solr.gxdResult.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@Value("${solr.gxdDagEdge.url}")
	public void setVocabTermChildUrl(String vocabTermChildUrl) {
		/*
		 * Joined indices List of indices that can be joined to this index. This
		 * means you query *this* index (the "from"), but return docs from the
		 * joined index (the "to").
		 */
		this.joinIndices.put("dagDirectEdge",
				new SolrJoinMapper(vocabTermChildUrl,
						DagEdgeFields.RELATED_DESCENDENT, "gxdResult",
						GxdResultFields.STRUCTURE_EXACT, "edgeType:direct"));

		this.joinIndices.put("dagDescendentEdge", new SolrJoinMapper(
				vocabTermChildUrl, DagEdgeFields.EMAPS_ID, "gxdResult",
				GxdResultFields.EMAPS_ID, "edgeType:descendent"));
	}
}
