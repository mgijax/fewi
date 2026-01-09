package org.jax.mgi.fewi.hunter;

import java.util.List;

import org.jax.mgi.fewi.searchUtil.ESSearchOption;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.snpdatamodel.document.ESEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;

/**
 * Generic hunter for the gxdResultHasImage index that can operate on any subclass of ESEntity.
 */
@Repository
public class ESGxdResultHasImageHunter<T extends ESEntity> extends ESGxdSummaryBaseHunter<T> {

	@Autowired
	public ESGxdProfileMarkerHunter esGxdProfileMarkerHunter;
	
    /***
     * Default constructor uses ESEntity as the type.
     */
    public ESGxdResultHasImageHunter() {
        super((Class<T>) ESEntity.class); // unchecked cast
        keyString = GxdResultFields.RESULT_KEY;
    }

    /**
     * Constructor that accepts a specific subclass of ESEntity.
     * @param clazz the class of the entity subclass
     */
    public ESGxdResultHasImageHunter(Class<T> clazz) {
        super(clazz);
        keyString = GxdResultFields.RESULT_KEY;
    }

    /**
     * Constructor with ES host/port/index parameters.
     */
    public ESGxdResultHasImageHunter(Class<T> clazz, String host, String port, String index) {
        super(clazz);
        keyString = GxdResultFields.RESULT_KEY;
        this.esHost = host;
        this.esPort = port;
        this.esIndex = index;
    }  
    
	@Override
	protected boolean preProcessSearchParams(SearchParams searchParams, ESSearchOption searchOption) {
		List<Query> extraQueries = getQueryFromJoinFilter(searchParams);
		searchOption.setExtraQueries(extraQueries);
		return false;
	}      

    @Value("${es.gxdresulthasimage.index}")
    public void setESIndex(String esIndex) {
        super.esIndex = esIndex;
    }
}