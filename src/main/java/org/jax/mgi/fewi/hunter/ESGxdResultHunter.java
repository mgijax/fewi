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
	public ESGxdProfileMarkerHunter esGxdProfileMarkerHunter;	
	
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

	@Value("${es.gxdresult.index}")
	public void setESIndex(String esIndex) {
		super.esIndex = esIndex;
	}
}