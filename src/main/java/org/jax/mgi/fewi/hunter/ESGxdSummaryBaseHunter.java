package org.jax.mgi.fewi.hunter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.T;
import org.jax.mgi.fewi.propertyMapper.ESPropertyMapper;
import org.jax.mgi.fewi.searchUtil.FacetConstants;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
import org.jax.mgi.fewi.sortMapper.ESSortMapper;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.shr.fe.indexconstants.ImagePaneFields;
import org.jax.mgi.snpdatamodel.document.ESEntity;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.TopHitsAggregate;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
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
		sortMap.put(SortConstants.BY_IMAGE_ASSAY_TYPE, new ESSortMapper(GxdResultFields.A_BY_ASSAY_TYPE));
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

	@Override
	protected int getPageSize(SearchParams searchParams) {
		// temp fix, got ES server exception when more than 10000. might be configurable on
		// server, need check.
		int size = searchParams.getPageSize();
		if (size > 10000) {
			size = 10000;
		}
		return size;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends ESEntity> boolean processESSearchAggregation(SearchResponse resp, SearchResults<T> searchResults,
			SearchParams searchParams) {
		// loop over your group aggregation
		Map<String, Aggregate> aggs = resp.aggregations();

		for (Map.Entry<String, Aggregate> entry : aggs.entrySet()) {
			String aggName = entry.getKey(); // aggregation name, e.g. "markerSymbol"
			Aggregate groupAgg = entry.getValue(); // the aggregation object

			if (groupAgg == null || !groupAgg.isSterms()) {
				return true; // nothing to do
			}

			StringTermsAggregate ta = groupAgg.sterms();
			List<StringTermsBucket> buckets = ta.buckets().array();

//			searchResults.setTotalCount(buckets.size());
			for (StringTermsBucket bucket : buckets) {
				Aggregate topAgg = bucket.aggregations().get("first_doc");
				if (topAgg == null || !topAgg.isTopHits()) {
					return true;
				}

				TopHitsAggregate tha = topAgg.topHits();
				HitsMetadata<JsonData> hitsMeta = tha.hits();
				if (hitsMeta != null && !hitsMeta.hits().isEmpty()) {
					Hit<JsonData> hit = hitsMeta.hits().get(0);
					Map<String, Object> src = hit.source().to(Map.class);

					if (GxdResultFields.MARKER_KEY.equals(aggName)) {
						SolrGxdMarker marker = new SolrGxdMarker();
						marker.setMgiid((String) src.get(GxdResultFields.MARKER_MGIID));
						marker.setSymbol((String) src.get(GxdResultFields.MARKER_SYMBOL));
						marker.setName((String) src.get(GxdResultFields.MARKER_NAME));
						marker.setType((String) src.get(GxdResultFields.MARKER_TYPE));
						marker.setChr((String) src.get(GxdResultFields.CHROMOSOME));
						marker.setCm(src.get(GxdResultFields.CENTIMORGAN) != null
								? src.get(GxdResultFields.CENTIMORGAN).toString()
								: null);
						marker.setCytoband((String) src.get(GxdResultFields.CYTOBAND));
						marker.setStartCoord(src.get(GxdResultFields.START_COORD) != null
								? src.get(GxdResultFields.START_COORD).toString()
								: null);
						marker.setEndCoord(src.get(GxdResultFields.END_COORD) != null
								? src.get(GxdResultFields.END_COORD).toString()
								: null);
						marker.setStrand((String) src.get(GxdResultFields.STRAND));

						searchResults.addResultObjects((T) marker);
					} else if (GxdResultFields.ASSAY_KEY.equals(aggName)) {
						// we got ourselves a good old fashioned assay object
						SolrGxdAssay a = new SolrGxdAssay();
						a.setMarkerSymbol((String) src.get(GxdResultFields.MARKER_SYMBOL));
						a.setAssayKey((String) src.get(GxdResultFields.ASSAY_KEY));
						a.setAssayMgiid((String) src.get(GxdResultFields.ASSAY_MGIID));
						a.setAssayType((String) src.get(GxdResultFields.ASSAY_TYPE));
						a.setJNum((String) src.get(GxdResultFields.JNUM));
						a.setMiniCitation((String) src.get(GxdResultFields.SHORT_CITATION));

						boolean hasImage = false;
						Object hasImageO = src.get(GxdResultFields.ASSAY_HAS_IMAGE);
						if (hasImageO != null) {
							hasImage = (Boolean) hasImageO;
						}
						a.setHasImage(hasImage);

						searchResults.addResultObjects((T) a);
					} else if (GxdResultFields.STRUCTURE_EXACT.equals(aggName)) {
						// we got ourselves a structure (EMAPA) ID
						String structureExactId = (String) src.get(GxdResultFields.STRUCTURE_EXACT);
						searchResults.addResultObjects((T) new SolrString(structureExactId));
					} else if (GxdResultFields.THEILER_STAGE.equals(aggName)) {
						// searching for stages
						Integer theilerStage = (Integer) src.get(GxdResultFields.THEILER_STAGE);
						searchResults.addResultObjects((T) new SolrString(theilerStage.toString()));
					}
				}
			}
		}
		return true;
	}

}
