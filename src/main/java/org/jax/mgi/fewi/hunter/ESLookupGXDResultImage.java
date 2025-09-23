package org.jax.mgi.fewi.hunter;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.shr.fe.indexconstants.ImagePaneFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Repository class for performing a LOOKUP JOIN between the GXD Image Pane index
 * and the GXD Result Has Image index in Elasticsearch. 
 * <p>
 * This class extends {@link ESLookup} and is responsible for initializing
 * the lookup indexes, specifying which fields to keep from the join, 
 * and ensuring only valid image records are included in queries.
 * </p>
 * <p>
 * The indices are injected via Spring {@link Value} annotations:
 * <ul>
 *     <li>{@code es.gxdimagepane.index} – the Elasticsearch index for image panes</li>
 *     <li>{@code es.gxdresulthasimage.index} – the Elasticsearch index for result-to-image relationships</li>
 * </ul>
 * </p>
 */

@Repository
public class ESLookupGXDResultImage extends ESLookup {

    /**
     * Elasticsearch index name for GXD Image Pane documents.
     * Injected from the application properties.
     */	
	@Value("${es.gxdimagepane.index}")
	private String gxdImagePaneIndexName;

	public ESLookupGXDResultImage() {
	}

    /**
     * Initializes the lookup configuration after construction.
     * <p>
     * This method sets up:
     * <ul>
     *     <li>The lookup indexes used for joining data.</li>
     *     <li>The list of fields to keep in the joined results.</li>
     *     <li>A filter to exclude any records where IMAGE_ID is null.</li>
     * </ul>
     * </p>
     */	
	@PostConstruct
	public void init() {
		this.lookupIndexes = List.of(new ESLookupIndex(this.gxdImagePaneIndexName, GxdResultFields.RESULT_KEY));

		String keep = "KEEP ";
		keep += GxdResultFields.MARKER_KEY + "," + GxdResultFields.MARKER_SYMBOL + ",";
		keep += ImagePaneFields.IMAGE_PANE_KEY + "," + IndexConstants.IMAGE_ID + ",";
		keep += ImagePaneFields.IMAGE_PIXELDBID + "," + ImagePaneFields.IMAGE_LABEL + ",";
		keep += ImagePaneFields.PANE_X + "," + ImagePaneFields.PANE_Y + ",";
		keep += ImagePaneFields.PANE_WIDTH + "," + ImagePaneFields.PANE_HEIGHT + ",";
		keep += ImagePaneFields.IMAGE_WIDTH + "," + ImagePaneFields.IMAGE_HEIGHT + ",";
		keep += GxdResultFields.ASSAY_MGIID + "," + ImagePaneFields.IMAGE_META;
		this.extraStatements.add(keep);
		this.extraStatements.add("WHERE " + IndexConstants.IMAGE_ID + " is not NULL");
	}

    /**
     * Sets the Elasticsearch index name for GXD Result Has Image documents.
     * <p>
     * This value is injected from the application properties.
     * </p>
     *
     * @param indexName the Elasticsearch index name
     */	
	@Override
	@Value("${es.gxdresulthasimage.index}")
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
}
