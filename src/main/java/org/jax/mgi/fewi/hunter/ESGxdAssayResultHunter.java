package org.jax.mgi.fewi.hunter;

import org.apache.poi.ss.formula.functions.T;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.snpdatamodel.document.ESEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Generic hunter for GXD assay results that can operate on any subclass of ESEntity.
 */
@Repository
public class ESGxdAssayResultHunter<T extends ESEntity> extends ESGxdSummaryBaseHunter<T> {

    /***
     * Default constructor uses SolrAssayResult as the type.
     */
    public ESGxdAssayResultHunter() {
        super((Class<T>) SolrAssayResult.class); // unchecked cast
    }

    /**
     * Constructor that accepts a specific subclass of ESEntity.
     * @param clazz the class of the entity subclass
     */
    public ESGxdAssayResultHunter(Class<T> clazz) {
        super(clazz);
    }

    @Value("${es.gxdresult.index}")
    public void setESIndex(String esIndex) {
        super.esIndex = esIndex;
    }
}
