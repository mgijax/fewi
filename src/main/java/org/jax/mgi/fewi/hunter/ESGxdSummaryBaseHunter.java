package org.jax.mgi.fewi.hunter;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fewi.propertyMapper.ESPropertyMapper;
import org.jax.mgi.fewi.searchUtil.ESSearchOption;
import org.jax.mgi.fewi.searchUtil.FacetConstants;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.ESAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdGeneMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdStageMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
import org.jax.mgi.fewi.sortMapper.ESSortMapper;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.shr.fe.indexconstants.ImagePaneFields;
import org.jax.mgi.snpdatamodel.document.ESEntity;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.json.JsonData;

public class ESGxdSummaryBaseHunter<T extends ESEntity> extends ESHunter<T> {	
	/***
	 * The constructor sets up this hunter so that it is specific to gxd summary
	 * pages. Each item in the constructor sets a value that it has inherited from
	 * its superclass, and then relies on the superclass to perform all of the
	 * needed work via the hunt() method.
	 */
	public ESGxdSummaryBaseHunter(Class<T> clazz) {
		super(clazz);
		/*
		 * Setup the property map. This maps from the properties of the incoming filter
		 * list to the corresponding field names in the Solr implementation.
		 */

		/*
		 * marker nomenclature
		 */
		propertyMap.put(SearchConstants.MRK_NOMENCLATURE, new ESPropertyMapper(GxdResultFields.NOMENCLATURE));

		// jnum
		propertyMap.put(SearchConstants.REF_ID, new ESPropertyMapper(GxdResultFields.JNUM));

		// marker ID
		propertyMap.put(SearchConstants.MRK_ID, new ESPropertyMapper(GxdResultFields.MARKER_MGIID));

		// marker Symbol
		propertyMap.put(SearchConstants.MRK_SYMBOL, new ESPropertyMapper(GxdResultFields.MARKER_SYMBOL));

		// vocab annotation ids
		propertyMap.put(GxdResultFields.ANNOTATION, new ESPropertyMapper(GxdResultFields.ANNOTATION));

		// Assay type
		propertyMap.put(SearchConstants.GXD_ASSAY_TYPE, new ESPropertyMapper(GxdResultFields.ASSAY_TYPE));

		// Theiler Stage
		propertyMap.put(SearchConstants.GXD_THEILER_STAGE, new ESPropertyMapper(GxdResultFields.THEILER_STAGE));

		// Age
		propertyMap.put(SearchConstants.GXD_AGE_MIN, new ESPropertyMapper(GxdResultFields.AGE_MIN));
		propertyMap.put(SearchConstants.GXD_AGE_MAX, new ESPropertyMapper(GxdResultFields.AGE_MAX));

		// is Expressed (from QF)
		propertyMap.put(SearchConstants.GXD_DETECTED, new ESPropertyMapper(GxdResultFields.DETECTION_LEVEL));

		// detection level (from facet)
		propertyMap.put(FacetConstants.GXD_DETECTED, new ESPropertyMapper(GxdResultFields.DETECTION_LEVEL));

		// structure
		propertyMap.put(SearchConstants.STRUCTURE, new ESPropertyMapper(GxdResultFields.STRUCTURE_ANCESTORS));
		propertyMap.put(SearchConstants.STRUCTURE_EXACT, new ESPropertyMapper(GxdResultFields.STRUCTURE_EXACT));

		// structure key
		propertyMap.put(SearchConstants.STRUCTURE_KEY, new ESPropertyMapper(GxdResultFields.STRUCTURE_KEY));

		// annotated structure key (does not include children)
		propertyMap.put(GxdResultFields.ANNOTATED_STRUCTURE_KEY,
				new ESPropertyMapper(GxdResultFields.ANNOTATED_STRUCTURE_KEY));

		// structure ID
		propertyMap.put(SearchConstants.STRUCTURE_ID, new ESPropertyMapper(GxdResultFields.STRUCTURE_ID));

		// allele ID
		propertyMap.put(SearchConstants.ALL_ID, new ESPropertyMapper(GxdResultFields.ALLELE_ID));

		propertyMap.put(SearchConstants.PRIMARY_KEY, new ESPropertyMapper(GxdResultFields.KEY));

		propertyMap.put(SearchConstants.GXD_IS_WILD_TYPE, new ESPropertyMapper(GxdResultFields.IS_WILD_TYPE));

		propertyMap.put(SearchConstants.GXD_MUTATED_IN, new ESPropertyMapper(GxdResultFields.MUTATED_IN));

		// probe key
		propertyMap.put(SearchConstants.PROBE_KEY, new ESPropertyMapper(GxdResultFields.PROBE_KEY));

		// antibody key
		propertyMap.put(SearchConstants.ANTIBODY_KEY, new ESPropertyMapper(GxdResultFields.ANTIBODY_KEY));

		// anatomical system
		propertyMap.put(SearchConstants.ANATOMICAL_SYSTEM, new ESPropertyMapper(GxdResultFields.ANATOMICAL_SYSTEM));

		// marker key
		propertyMap.put(SearchConstants.MRK_KEY, new ESPropertyMapper(GxdResultFields.MARKER_KEY));

		// mouse coordinates
		propertyMap.put(SearchConstants.MOUSE_COORDINATE, new ESPropertyMapper(GxdResultFields.MOUSE_COORDINATE));

		/*
		 * Setup the sort mapping.
		 */
		sortMap.put(SortConstants.GXD_GENE, new ESSortMapper(GxdResultFields.R_BY_MRK_SYMBOL));
		sortMap.put(GxdResultFields.M_BY_MRK_SYMBOL, new ESSortMapper(GxdResultFields.M_BY_MRK_SYMBOL));
		sortMap.put(SortConstants.GXD_ASSAY_TYPE, new ESSortMapper(GxdResultFields.R_BY_ASSAY_TYPE));
		sortMap.put(GxdResultFields.A_BY_ASSAY_TYPE, new ESSortMapper(GxdResultFields.A_BY_ASSAY_TYPE));
		sortMap.put(SortConstants.GXD_AGE, new ESSortMapper(GxdResultFields.R_BY_AGE));
		sortMap.put(SortConstants.GXD_STRUCTURE, new ESSortMapper(GxdResultFields.R_BY_STRUCTURE));
		sortMap.put(SortConstants.GXD_DETECTION, new ESSortMapper(GxdResultFields.R_BY_EXPRESSED));
		sortMap.put(SortConstants.GXD_GENOTYPE, new ESSortMapper(GxdResultFields.R_BY_MUTANT_ALLELES));
		sortMap.put(SortConstants.GXD_REFERENCE, new ESSortMapper(GxdResultFields.R_BY_REFERENCE));
		sortMap.put(SortConstants.GXD_LOCATION, new ESSortMapper(GxdResultFields.M_BY_LOCATION));

		// these are only available on the join index gxdImagePane (use wisely my
		// friend)
		sortMap.put(SortConstants.BY_IMAGE_ASSAY_TYPE, new ESSortMapper(ImagePaneFields.BY_ASSAY_TYPE));
		sortMap.put(SortConstants.BY_IMAGE_MARKER, new ESSortMapper(ImagePaneFields.BY_MARKER));
		sortMap.put(SortConstants.BY_IMAGE_HYBRIDIZATION_ASC, new ESSortMapper(ImagePaneFields.BY_HYBRIDIZATION_ASC));
		sortMap.put(SortConstants.BY_IMAGE_HYBRIDIZATION_DESC, new ESSortMapper(ImagePaneFields.BY_HYBRIDIZATION_DESC));

		/*
		 * Groupings list of fields that can be uniquely grouped on
		 */
		this.groupFields.put(SearchConstants.MRK_KEY, GxdResultFields.MARKER_KEY);
		this.groupFields.put(SearchConstants.GXD_ASSAY_KEY, GxdResultFields.ASSAY_KEY);
		this.groupFields.put(SearchConstants.STRUCTURE_EXACT, GxdResultFields.STRUCTURE_EXACT);
		this.groupFields.put(SearchConstants.GXD_THEILER_STAGE, GxdResultFields.THEILER_STAGE);

		// group fields to return (if defined)
		this.groupReturnedFields.put(SearchConstants.STRUCTURE_EXACT, Arrays.asList(GxdResultFields.STRUCTURE_EXACT));
		this.groupReturnedFields.put(SearchConstants.GXD_THEILER_STAGE, Arrays.asList(GxdResultFields.THEILER_STAGE));

		/*
		 * subclasses define what they need to return; that is not dealt with here
		 */
	}	
	
	protected boolean doJoinProfileMarker(SearchParams searchParams, ESSearchOption searchOption, 
			ESGxdProfileMarkerHunter esGxdProfileMarkerHunter, List<Query> extraQueries) {
		if ( searchParams.getFilter() == null ) {
			return false;
		}
		List<Filter> joinQueryFilters = searchParams.getFilter().collectJoinQueryFilters();
		if ( joinQueryFilters == null || joinQueryFilters.isEmpty() ) {
			return false;
		}
		
		Filter joinFilter = joinQueryFilters.get(0);
		if (!"gxdProfileMarker".equals(joinFilter.getFromIndex())) {
			return false;
		}
		
		// check if ui pass in markerMgiid
		List<Filter> inFilters = searchParams.getFilter().collectFilters(Filter.Operator.OP_IN);
		for (Filter filter : inFilters) {
			if (filter.getValues() != null && !filter.getValues().isEmpty()) {
				String field = getMappedField(filter.getProperty());
				if ( GxdResultFields.MARKER_MGIID.equals(field) ) {
					return false;
				}
			}
		}
		
		SearchParams p = new SearchParams();
		p.setPageSize(SearchConstants.SEARCH_MAX_PAGE_SIZE_FOR_MARKER_PROFILE);   // max out total of gxd_profile_marker index
		SearchResults<ESAssayResult> s = new SearchResults<ESAssayResult>();
		ESSearchOption o = new ESSearchOption();
		o.setExtraQueries(extraQueries);
		o.setClazz(ESAssayResult.class);
		o.setReturnFields(List.of(GxdResultFields.MARKER_MGIID));
		esGxdProfileMarkerHunter.hunt(p, s, o);
		if ( s.getResultObjects() == null || s.getResultObjects().size() < 1) {
			return true;
		}
		
		List<String> mgiIds = new ArrayList<String>();
		for (ESAssayResult r: s.getResultObjects()) {
			if ( r.getMarkerMgiid() == null || r.getMarkerMgiid().isBlank() ) {
				continue;
			}
			mgiIds.add(r.getMarkerMgiid());
		}
		List<Filter> allfilters = new ArrayList<Filter>();
		allfilters.add(searchParams.getFilter());
		allfilters.add(Filter.term_in(GxdResultFields.MARKER_MGIID, mgiIds));
		searchParams.setFilter(Filter.and(allfilters));
		return false;
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T extends ESEntity> void parseFirstDoc(SearchResponse resp, SearchResults<T> searchResults,
			SearchParams searchParams) {

		Map<String, Aggregate> aggs = resp.aggregations();

		for (Map.Entry<String, Aggregate> entry : aggs.entrySet()) {
			String aggName = entry.getKey();
			Aggregate groupAgg = entry.getValue();

			if (groupAgg == null || !groupAgg.isSterms()) {
				continue;
			}

			List<StringTermsBucket> buckets = groupAgg.sterms().buckets().array();

			for (StringTermsBucket bucket : buckets) {
				Aggregate topAgg = bucket.aggregations().get("first_doc");
				if (topAgg == null || !topAgg.isTopHits())
					continue;

				HitsMetadata<JsonData> hitsMeta = topAgg.topHits().hits();
				if (hitsMeta == null || hitsMeta.hits().isEmpty())
					continue;

				Map<String, Object> src = hitsMeta.hits().get(0).source().to(Map.class);

				switch (aggName) {
				case GxdResultFields.MARKER_KEY -> {
					searchResults.addResultObjects((T) mapToMarker(src));
				}
				case GxdResultFields.ASSAY_KEY -> {
					searchResults.addResultObjects((T) mapToAssay(src));
				}
				case GxdResultFields.STRUCTURE_EXACT -> {
					String structureId = (String) src.get(GxdResultFields.STRUCTURE_EXACT);
					searchResults.addResultObjects((T) new SolrString(structureId));
				}
				case GxdResultFields.THEILER_STAGE -> {
					Object stageObj = src.get(GxdResultFields.THEILER_STAGE);
					if (stageObj != null) {
						searchResults.addResultObjects((T) new SolrString(stageObj.toString()));
					}
				}
				case GxdResultFields.STAGE_MATRIX_GROUP -> {
					searchResults.addResultObjects((T) mapToStageMatrix(src, bucket.docCount()));
				}				
				case GxdResultFields.GENE_MATRIX_GROUP -> {
					searchResults.addResultObjects((T) mapToGeneMatrix(src, bucket.docCount()));
				}				
				}
			}
		}
	}
	
	private ESGxdStageMatrixResult mapToStageMatrix(Map<String, Object> src, Long docCount) {
		ESGxdStageMatrixResult mResult = new ESGxdStageMatrixResult();
		mResult.setDetectionLevel((String) src.get(GxdResultFields.DETECTION_LEVEL));
		mResult.setStructureId((String) src.get(GxdResultFields.STRUCTURE_EXACT));
		mResult.setTheilerStage(toInt(src.get(GxdResultFields.THEILER_STAGE)));		
		mResult.setGeneSymbol((String) src.get(GxdResultFields.MARKER_SYMBOL));
		mResult.setPrintname((String) src.get(GxdResultFields.STRUCTURE_PRINTNAME));
		if ( docCount != null ) {
			mResult.setCount((int) docCount.longValue());
		}
		return mResult;
	}		
	
	private ESGxdGeneMatrixResult mapToGeneMatrix(Map<String, Object> src, Long docCount) {
		ESGxdGeneMatrixResult mResult = new ESGxdGeneMatrixResult();
		mResult.setDetectionLevel((String) src.get(GxdResultFields.DETECTION_LEVEL));
		mResult.setStructureId((String) src.get(GxdResultFields.STRUCTURE_EXACT));
		mResult.setTheilerStage(toInt(src.get(GxdResultFields.THEILER_STAGE)));		
		mResult.setGeneSymbol((String) src.get(GxdResultFields.MARKER_SYMBOL));
		mResult.setPrintname((String) src.get(GxdResultFields.STRUCTURE_PRINTNAME));
		if ( docCount != null ) {
			mResult.setCount((int) docCount.longValue());
		}
		return mResult;
	}	
	
	// Helper: map source to marker
	private ESGxdMarker mapToMarker(Map<String, Object> src) {
		ESGxdMarker marker = new ESGxdMarker();
		marker.setMgiid((String) src.get(GxdResultFields.MARKER_MGIID));
		marker.setSymbol((String) src.get(GxdResultFields.MARKER_SYMBOL));
		marker.setName((String) src.get(GxdResultFields.MARKER_NAME));
		marker.setType((String) src.get(GxdResultFields.MARKER_TYPE));
		marker.setChr((String) src.get(GxdResultFields.CHROMOSOME));
		marker.setCm(getString(src.get(GxdResultFields.CENTIMORGAN)));
		marker.setCytoband((String) src.get(GxdResultFields.CYTOBAND));
		marker.setStartCoord(getString(src.get(GxdResultFields.START_COORD)));
		marker.setEndCoord(getString(src.get(GxdResultFields.END_COORD)));
		marker.setStrand((String) src.get(GxdResultFields.STRAND));
		marker.setMarkerKey(toInt(src.get(GxdResultFields.MARKER_KEY)));
		marker.setByMrkSymbol(toInt(src.get(GxdResultFields.M_BY_MRK_SYMBOL)));
		return marker;
	}

	// Helper: map source to assay
	private ESGxdAssay mapToAssay(Map<String, Object> src) {
		ESGxdAssay assay = new ESGxdAssay();
		assay.setMarkerSymbol((String) src.get(GxdResultFields.MARKER_SYMBOL));
		assay.setAssayKey((String) src.get(GxdResultFields.ASSAY_KEY));
		assay.setAssayMgiid((String) src.get(GxdResultFields.ASSAY_MGIID));
		assay.setAssayType((String) src.get(GxdResultFields.ASSAY_TYPE));
		assay.setJNum((String) src.get(GxdResultFields.JNUM));
		assay.setMiniCitation((String) src.get(GxdResultFields.SHORT_CITATION));
		assay.setHasImage(Boolean.TRUE.equals(src.get(GxdResultFields.ASSAY_HAS_IMAGE)));
		return assay;
	}	
	
	private void checkFilter(Filter filter) {
		if (filter.isBasicFilter()) {
			return;
		} else {
			List<Filter> flist = filter.getNestedFilters();
			Boolean foundTitle = Boolean.FALSE;
			Boolean foundAbstract = Boolean.FALSE;
			String textToSearch = "";

			for (Filter f : flist) {
				if (f.isBasicFilter()) {
					if (f.getProperty().equals(
							SearchConstants.REF_TEXT_ABSTRACT)) {
						textToSearch = f.getValue();
						foundAbstract = Boolean.TRUE;
					}
					if (f.getProperty().equals(SearchConstants.REF_TEXT_TITLE)) {
						textToSearch = f.getValue();
						foundTitle = Boolean.TRUE;
					}

				} else {
					checkFilter(f);
				}
			}

			if (foundTitle && foundAbstract) {
				filter.setProperty(SearchConstants.REF_TEXT_TITLE_ABSTRACT);
				filter.setValue(textToSearch);
				filter.setOperator(Filter.Operator.OP_CONTAINS);
				filter.setNestedFilters(new ArrayList<Filter>());
			}
		}
	}
}
