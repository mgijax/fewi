package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.ESSearchOption;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.ESAssayResult;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.snpdatamodel.document.ESEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class ESGxdResultHunter<T extends ESEntity> extends ESGxdSummaryBaseHunter<T> {

	@Autowired
	private ESGxdProfileMarkerHunter esGxdProfileMarkerHunter;

	/***
	 * Default constructor uses ESEntity as the type.
	 */
	public ESGxdResultHunter() {
		super((Class<T>) ESAssayResult.class); // unchecked cast
		keyString = GxdResultFields.RESULT_KEY;
	}

	/**
	 * Constructor that accepts a specific subclass of ESEntity.
	 * 
	 * @param clazz the class of the entity subclass
	 */
	public ESGxdResultHunter(Class<T> clazz) {
		super(clazz);
		keyString = GxdResultFields.RESULT_KEY;
	}

	/**
	 * Constructor that also accepts ES host/port/index parameters.
	 */
	public ESGxdResultHunter(Class<T> clazz, String host, String port, String index) {
		super(clazz);
		keyString = GxdResultFields.RESULT_KEY;
		this.esHost = host;
		this.esPort = port;
		this.esIndex = index;
	}

	@Override
	protected SearchParams preProcessSearchParams(SearchParams searchParams, ESSearchOption searchOption) {
		if ( searchParams.getFilter() == null ) {
			return searchParams;
		}
		List<Filter> joinQueryFilters = searchParams.getFilter().collectJoinQueryFilters();
		if ( joinQueryFilters == null || joinQueryFilters.isEmpty() ) {
			return searchParams;
		}
		
		Filter joinFilter = joinQueryFilters.get(0);
		if (!"gxdProfileMarker".equals(joinFilter.getFromIndex())) {
			return searchParams;
		}
		
		SearchParams p = new SearchParams();
		p.setPageSize(60_000);
		p.setFilter(joinFilter.getJoinQuery());
		SearchResults<ESAssayResult> s = new SearchResults<ESAssayResult>();
		ESSearchOption option = new ESSearchOption();
		option.setClazz(ESAssayResult.class);
		option.setReturnFields(List.of(GxdResultFields.MARKER_MGIID));
		this.esGxdProfileMarkerHunter.hunt(p, s, option);
		
		List<String> mgiIds = new ArrayList<String>();
		for (ESAssayResult r: s.getResultObjects()) {
			mgiIds.add(r.getMarkerMgiid());
		}
		List<Filter> allfilters = new ArrayList<Filter>();
		allfilters.add(searchParams.getFilter());
		allfilters.add(Filter.in(GxdResultFields.MARKER_MGIID, mgiIds));
		searchParams.setFilter(Filter.and(allfilters));
		return searchParams;
	}

	@Value("${es.gxdresult.index}")
	public void setESIndex(String esIndex) {
		super.esIndex = esIndex;
	}
}