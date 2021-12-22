package org.jax.mgi.fewi.hunter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jackson.map.ObjectMapper;
import org.jax.mgi.fewi.propertyMapper.SolrJoinMapper;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.FacetConstants;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.shr.fe.indexconstants.ImagePaneFields;
import org.jax.mgi.shr.jsonmodel.GxdImageMeta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Base Hunter class for the GXD Result solr index (needed so we can have
 * multiple subclasses for facets)
 */

@Repository
public class SolrGxdSummaryBaseHunter extends SolrHunter<SolrGxdEntity> {
	
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	/***
	 * The constructor sets up this hunter so that it is specific to gxd
	 * summary pages. Each item in the constructor sets a value that it has
	 * inherited from its superclass, and then relies on the superclass to
	 * perform all of the needed work via the hunt() method.
	 */
	public SolrGxdSummaryBaseHunter() {
		/*
		 * Setup the property map. This maps from the properties of the incoming
		 * filter list to the corresponding field names in the Solr
		 * implementation.
		 */

		/*
		 * marker nomenclature
		 */
		propertyMap.put(SearchConstants.MRK_NOMENCLATURE,
				new SolrPropertyMapper(GxdResultFields.NOMENCLATURE));

		// jnum
		propertyMap.put(SearchConstants.REF_ID, new SolrPropertyMapper(
				GxdResultFields.JNUM));

		// marker ID
		propertyMap.put(SearchConstants.MRK_ID, new SolrPropertyMapper(
				GxdResultFields.MARKER_MGIID));

		// marker Symbol
		propertyMap.put(SearchConstants.MRK_SYMBOL, new SolrPropertyMapper(
				GxdResultFields.MARKER_SYMBOL));

		// vocab annotation ids
		propertyMap.put(GxdResultFields.ANNOTATION, new SolrPropertyMapper(
				GxdResultFields.ANNOTATION));

		// Assay type
		propertyMap.put(SearchConstants.GXD_ASSAY_TYPE, new SolrPropertyMapper(
				GxdResultFields.ASSAY_TYPE));

		// Theiler Stage
		propertyMap.put(SearchConstants.GXD_THEILER_STAGE,
				new SolrPropertyMapper(GxdResultFields.THEILER_STAGE));

		// Age
		propertyMap.put(SearchConstants.GXD_AGE_MIN, new SolrPropertyMapper(
				GxdResultFields.AGE_MIN));
		propertyMap.put(SearchConstants.GXD_AGE_MAX, new SolrPropertyMapper(
				GxdResultFields.AGE_MAX));

		// is Expressed (from QF)
		propertyMap.put(SearchConstants.GXD_DETECTED, new SolrPropertyMapper(
				GxdResultFields.DETECTION_LEVEL));

		// detection level (from facet)
		propertyMap.put(FacetConstants.GXD_DETECTED, new SolrPropertyMapper(
				GxdResultFields.DETECTION_LEVEL));

		// structure
		propertyMap.put(SearchConstants.STRUCTURE, new SolrPropertyMapper(
				GxdResultFields.STRUCTURE_ANCESTORS));
		propertyMap.put(SearchConstants.STRUCTURE_EXACT,
				new SolrPropertyMapper(GxdResultFields.STRUCTURE_EXACT));

		// structure key
		propertyMap.put(SearchConstants.STRUCTURE_KEY, new SolrPropertyMapper(
				GxdResultFields.STRUCTURE_KEY));

		// annotated structure key (does not include children)
		propertyMap
				.put(GxdResultFields.ANNOTATED_STRUCTURE_KEY,
						new SolrPropertyMapper(
								GxdResultFields.ANNOTATED_STRUCTURE_KEY));

		// structure ID
		propertyMap.put(SearchConstants.STRUCTURE_ID, new SolrPropertyMapper(
				GxdResultFields.STRUCTURE_ID));

		// allele ID
		propertyMap.put(SearchConstants.ALL_ID, new SolrPropertyMapper(
				GxdResultFields.ALLELE_ID));

		propertyMap.put(SearchConstants.PRIMARY_KEY, new SolrPropertyMapper(
				GxdResultFields.KEY));

		propertyMap.put(SearchConstants.GXD_IS_WILD_TYPE,
				new SolrPropertyMapper(GxdResultFields.IS_WILD_TYPE));

		propertyMap.put(SearchConstants.GXD_MUTATED_IN, new SolrPropertyMapper(
				GxdResultFields.MUTATED_IN));

		// probe key
		propertyMap.put(SearchConstants.PROBE_KEY, new SolrPropertyMapper(
				GxdResultFields.PROBE_KEY));

		// antibody key
		propertyMap.put(SearchConstants.ANTIBODY_KEY, new SolrPropertyMapper(
				GxdResultFields.ANTIBODY_KEY));

		// anatomical system
		propertyMap.put(SearchConstants.ANATOMICAL_SYSTEM,
				new SolrPropertyMapper(GxdResultFields.ANATOMICAL_SYSTEM));

		// marker key
		propertyMap.put(SearchConstants.MRK_KEY, new SolrPropertyMapper(
				GxdResultFields.MARKER_KEY));

		// mouse coordinates
		propertyMap.put(SearchConstants.MOUSE_COORDINATE,
			new SolrPropertyMapper(GxdResultFields.MOUSE_COORDINATE));

		/*
		 * Setup the sort mapping.
		 */
		sortMap.put(SortConstants.GXD_GENE, new SolrSortMapper(
				GxdResultFields.R_BY_MRK_SYMBOL));
		sortMap.put(GxdResultFields.M_BY_MRK_SYMBOL, new SolrSortMapper(
				GxdResultFields.M_BY_MRK_SYMBOL));
		sortMap.put(SortConstants.GXD_ASSAY_TYPE, new SolrSortMapper(
				GxdResultFields.R_BY_ASSAY_TYPE));
		sortMap.put(GxdResultFields.A_BY_ASSAY_TYPE, new SolrSortMapper(
				GxdResultFields.A_BY_ASSAY_TYPE));
		sortMap.put(SortConstants.GXD_AGE, new SolrSortMapper(
				GxdResultFields.R_BY_AGE));
		sortMap.put(SortConstants.GXD_STRUCTURE, new SolrSortMapper(
				GxdResultFields.R_BY_STRUCTURE));
		sortMap.put(SortConstants.GXD_DETECTION, new SolrSortMapper(
				GxdResultFields.R_BY_EXPRESSED));
		sortMap.put(SortConstants.GXD_GENOTYPE, new SolrSortMapper(
				GxdResultFields.R_BY_MUTANT_ALLELES));
		sortMap.put(SortConstants.GXD_REFERENCE, new SolrSortMapper(
				GxdResultFields.R_BY_REFERENCE));
		sortMap.put(SortConstants.GXD_LOCATION, new SolrSortMapper(
				GxdResultFields.M_BY_LOCATION));

		// these are only available on the join index gxdImagePane (use wisely my
		// friend)
		sortMap.put(SortConstants.BY_IMAGE_ASSAY_TYPE, new SolrSortMapper(ImagePaneFields.BY_ASSAY_TYPE));
		sortMap.put(SortConstants.BY_IMAGE_MARKER, new SolrSortMapper(ImagePaneFields.BY_MARKER));
		sortMap.put(SortConstants.BY_IMAGE_HYBRIDIZATION_ASC, new SolrSortMapper(ImagePaneFields.BY_HYBRIDIZATION_ASC));
		sortMap.put(SortConstants.BY_IMAGE_HYBRIDIZATION_DESC, new SolrSortMapper(ImagePaneFields.BY_HYBRIDIZATION_DESC));
		
		/*
		 * Groupings list of fields that can be uniquely grouped on
		 */
		this.groupFields.put(SearchConstants.MRK_KEY,
				GxdResultFields.MARKER_KEY);
		this.groupFields.put(SearchConstants.GXD_ASSAY_KEY,
				GxdResultFields.ASSAY_KEY);
		this.groupFields.put(SearchConstants.STRUCTURE_EXACT,
				GxdResultFields.STRUCTURE_EXACT);
		this.groupFields.put(SearchConstants.GXD_THEILER_STAGE,
				GxdResultFields.THEILER_STAGE);

		// group fields to return (if defined)
		this.groupReturnedFields.put(SearchConstants.STRUCTURE_EXACT,
				Arrays.asList(GxdResultFields.STRUCTURE_EXACT));
		this.groupReturnedFields.put(SearchConstants.GXD_THEILER_STAGE,
				Arrays.asList(GxdResultFields.THEILER_STAGE));

		/*
		 * subclasses define what they need to return; that is not dealt with
		 * here
		 */
	}

	@Override
	protected void packInformation(QueryResponse rsp,
			SearchResults<SolrGxdEntity> sr, SearchParams sp) {

		// A list of all the primary keys in the document

		SolrDocumentList sdl = rsp.getResults();
		logger.debug("packing gxd test hunt data");

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */

		for (SolrDocument doc : sdl) {

			// logger.debug(doc.toString());
			// Set the result object
			String assayKey = (String) doc
					.getFieldValue(GxdResultFields.ASSAY_KEY);
			String age = (String) doc.getFieldValue(GxdResultFields.AGE);
			String assayMgiid = (String) doc
					.getFieldValue(GxdResultFields.ASSAY_MGIID);
			String detectionLevel = (String) doc
					.getFieldValue(GxdResultFields.DETECTION_LEVEL);
			String jNum = (String) doc.getFieldValue(GxdResultFields.JNUM);
			String markerSymbol = (String) doc
					.getFieldValue(GxdResultFields.MARKER_SYMBOL);
			String markerMgiid = (String) doc
					.getFieldValue(GxdResultFields.MARKER_MGIID);
			String markerName = (String) doc
					.getFieldValue(GxdResultFields.MARKER_NAME);
			String printname = (String) doc
					.getFieldValue(GxdResultFields.STRUCTURE_PRINTNAME);
			Integer theilerStage = (Integer) doc
					.getFieldValue(GxdResultFields.THEILER_STAGE);
			String assayType = (String) doc
					.getFieldValue(GxdResultFields.ASSAY_TYPE);
			@SuppressWarnings("unchecked")
			List<String> figures = (List<String>) doc
					.getFieldValue(GxdResultFields.FIGURE);
			String miniCitation = (String) doc
					.getFieldValue(GxdResultFields.SHORT_CITATION);
			String genotype = (String) doc
					.getFieldValue(GxdResultFields.GENOTYPE);
			@SuppressWarnings("unchecked")
			List<String> figuresPlain = (List<String>) doc
					.getFieldValue(GxdResultFields.FIGURE_PLAIN);
			String pubmedId = (String) doc
					.getFieldValue(GxdResultFields.PUBMED_ID);

			SolrAssayResult resultObject = new SolrAssayResult();
			resultObject.setAssayKey(assayKey);
			resultObject.setAssayType(assayType);
			resultObject.setAge(age);
			resultObject.setAssayMgiid(assayMgiid);
			resultObject.setDetectionLevel(detectionLevel);
			resultObject.setFigures(figures);
			resultObject.setGenotype(genotype);
			resultObject.setJNum(jNum);
			resultObject.setMarkerSymbol(markerSymbol);
			resultObject.setPrintname(printname);
			resultObject.setShortCitation(miniCitation);
			resultObject.setTheilerStage(theilerStage);
			resultObject.setMarkerMgiid(markerMgiid);
			resultObject.setMarkerName(markerName);
			resultObject.setFiguresPlain(figuresPlain);
			resultObject.setPubmedId(pubmedId);
			resultObject.setCellType((String) doc.getFieldValue(GxdResultFields.CELL_TYPE));

			// fields specific to RNA-Seq data
			resultObject.setTpmLevel((String) doc.getFieldValue(GxdResultFields.TPM_LEVEL));
			resultObject.setAvgQnTpmLevel((String) doc.getFieldValue(GxdResultFields.AVG_QN_TPM_LEVEL));
			resultObject.setBiologicalReplicates((String) doc.getFieldValue(GxdResultFields.BIOLOGICAL_REPLICATES));
			resultObject.setStrain((String) doc.getFieldValue(GxdResultFields.STRAIN));
			resultObject.setSex((String) doc.getFieldValue(GxdResultFields.SEX));
			resultObject.setNotes((String) doc.getFieldValue(GxdResultFields.NOTES));

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
			if (gc.getName().equals(GxdResultFields.MARKER_KEY)) {
				// we got ourselves a good old fashioned marker object
				SolrGxdMarker m = new SolrGxdMarker();
				m.setMgiid((String) sd
						.getFieldValue(GxdResultFields.MARKER_MGIID));
				m.setSymbol((String) sd
						.getFieldValue(GxdResultFields.MARKER_SYMBOL));
				m.setName((String) sd
						.getFieldValue(GxdResultFields.MARKER_NAME));
				m.setType((String) sd
						.getFieldValue(GxdResultFields.MARKER_TYPE));
				m.setChr((String) sd.getFieldValue(GxdResultFields.CHROMOSOME));
				m.setStrand((String) sd.getFieldValue(GxdResultFields.STRAND));
				m.setStartCoord((String) sd
						.getFieldValue(GxdResultFields.START_COORD));
				m.setEndCoord((String) sd
						.getFieldValue(GxdResultFields.END_COORD));
				m.setCytoband((String) sd
						.getFieldValue(GxdResultFields.CYTOBAND));
				m.setCm((String) sd.getFieldValue(GxdResultFields.CENTIMORGAN));
				sr.addResultObjects(m);
			} else if (gc.getName().equals(GxdResultFields.ASSAY_KEY)) {
				// we got ourselves a good old fashioned assay object
				SolrGxdAssay a = new SolrGxdAssay();
				a.setMarkerSymbol((String) sd
						.getFieldValue(GxdResultFields.MARKER_SYMBOL));
				a.setAssayKey((String) sd
						.getFieldValue(GxdResultFields.ASSAY_KEY));
				a.setAssayMgiid((String) sd
						.getFieldValue(GxdResultFields.ASSAY_MGIID));
				a.setAssayType((String) sd
						.getFieldValue(GxdResultFields.ASSAY_TYPE));
				a.setJNum((String) sd.getFieldValue(GxdResultFields.JNUM));
				a.setMiniCitation((String) sd
						.getFieldValue(GxdResultFields.SHORT_CITATION));
				a.setHasImage((Boolean) sd
						.getFieldValue(GxdResultFields.ASSAY_HAS_IMAGE));

				sr.addResultObjects(a);
			} else if (gc.getName().equals(GxdResultFields.STRUCTURE_EXACT)) {
				// we got ourselves a structure (EMAPA) ID
				String structureExactId = (String) sd
						.getFieldValue(GxdResultFields.STRUCTURE_EXACT);
				sr.addResultObjects(new SolrString(structureExactId));
			} else if (gc.getName().equals(GxdResultFields.THEILER_STAGE)) {
				// searching for stages
				Integer theilerStage = (Integer) sd
						.getFieldValue(GxdResultFields.THEILER_STAGE);
				sr.addResultObjects(new SolrString(theilerStage.toString()));
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

	/*
	 * Custom implementation for the join response
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void packInformationForJoin(QueryResponse rsp,
			SearchResults<SolrGxdEntity> sr, SearchParams sp) {

		// A list of all the primary keys in the document

		SolrDocumentList sdl = rsp.getResults();
		logger.debug("packing gxd join hunt data");

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */

		for (SolrDocument doc : sdl) {

			SolrGxdImage image = new SolrGxdImage();
			image.setImagePaneKey((Integer) doc.getFieldValue(ImagePaneFields.IMAGE_PANE_KEY));
			image.setImageID((String) doc.getFieldValue(IndexConstants.IMAGE_ID));
			String pixeldbID = (String) doc.getFieldValue(ImagePaneFields.IMAGE_PIXELDBID);
			image.setPixeldbID(pixeldbID);
			image.setImageLabel((String) doc.getFieldValue(ImagePaneFields.IMAGE_LABEL));
			image.setPaneWidth((Integer) doc.getFieldValue(ImagePaneFields.PANE_WIDTH));
			image.setPaneHeight((Integer) doc.getFieldValue(ImagePaneFields.PANE_HEIGHT));
			image.setPaneX((Integer) doc.getFieldValue(ImagePaneFields.PANE_X));
			image.setPaneY((Integer) doc.getFieldValue(ImagePaneFields.PANE_Y));
			image.setImageWidth((Integer) doc.getFieldValue(ImagePaneFields.IMAGE_WIDTH));
			image.setImageHeight((Integer) doc.getFieldValue(ImagePaneFields.IMAGE_HEIGHT));
			image.setAssayID((String) doc.getFieldValue(GxdResultFields.ASSAY_MGIID));
			
			List<String> metaDataJsons = (List<String>) doc.getFieldValue(ImagePaneFields.IMAGE_META);
			List<GxdImageMeta> metaData = new ArrayList<GxdImageMeta>();
			for(String metaDataJson : metaDataJsons) {
				try {
					metaData.add(objectMapper.readValue(metaDataJson, GxdImageMeta.class));
				} catch (IOException e) {
					logger.error("Error parsing GxdImageMeta JSON from GXD Image Summary Solr Index", e);
				} 
			}
			image.setMetaData(metaData);

			sr.addResultObjects(image);
		}

		this.packFacetData(rsp, sr);
	}

	/*
	 * gather and facet-related data from 'rsp' and package it into 'sr'
	 */
	protected void packFacetData(QueryResponse rsp,
			SearchResults<SolrGxdEntity> sr) {
		if (this.facetString == null) {
			return;
		}

		logger.debug("this.facetString = " + this.facetString);
		List<String> facet = new ArrayList<String>();

		for (Count c : rsp.getFacetField(this.facetString).getValues()) {
			facet.add(c.getName());
		}

		logger.debug("facets -> "+StringUtils.join(facet,", "));
		if (facet.size() > 0) {
			sr.setResultFacets(facet);
		}
	}

	@Value("${solr.gxdResult.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@Value("${solr.gxdImagePane.url}")
	public void setImagePaneJoinUrl(String imagePaneUrl) {
		/*
		 * Joined indices List of indices that can be joined to this index. This
		 * means you query *this* index (the "from"), but return docs from the
		 * joined index (the "to").
		 */
		this.joinIndices.put("gxdImagePane", new SolrJoinMapper(imagePaneUrl,
				GxdResultFields.RESULT_KEY, "gxdResultHasImage",
				GxdResultFields.RESULT_KEY));
	}
}
