package org.jax.mgi.fewi.hunter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.shr.fe.indexconstants.ImagePaneFields;
import org.jax.mgi.shr.jsonmodel.GxdImageMeta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Hunter for the gxdResultHasImage solr index (like gxdResult, but only with
 * classical data that has associated images)
 * 
 */

@Repository
public class ESGxdResultHasImageHunter extends ESGxdSummaryBaseHunter<SolrGxdImage> {
	/***
		 * The constructor sets up this hunter so that it is specific to GXD
		 * summary pages. Each item in the constructor sets a value that it has
		 * inherited from its superclass, and then relies on the superclass to
		 * perform all of the needed work via the hunt() method.
		 */
	public ESGxdResultHasImageHunter() {
		super(SolrGxdImage.class);
		/*
		 * The name of the field we want to iterate through the documents for and place
		 * into the output. In this case we want to actually get a specific field, and
		 * return it rather than a list of keys.
		 */
		keyString = GxdResultFields.RESULT_KEY;
	}

	@Value("${es.gxdresulthasimage.index}")
	public void setESIndex(String esIndex) {
		super.esIndex = esIndex;
	}
	
	@Override
	public List<SolrGxdImage> processLookupResponse(JsonNode root) throws Exception {
	    List<String> columns = new ArrayList<>();
	    root.get("columns").forEach(col -> columns.add(col.get("name").asText()));

	 // Precompute column positions in one pass
	    int idxImagePaneKey = -1;
	    int idxImageId = -1;
	    int idxPixelDbId = -1;
	    int idxImageLabel = -1;
	    int idxPaneWidth = -1;
	    int idxPaneHeight = -1;
	    int idxPaneX = -1;
	    int idxPaneY = -1;
	    int idxImageWidth = -1;
	    int idxImageHeight = -1;
	    int idxAssayId = -1;
	    int idxImageMeta = -1;

	    for (int i = 0; i < columns.size(); i++) {
	        switch (columns.get(i)) {
	            case ImagePaneFields.IMAGE_PANE_KEY:
	                idxImagePaneKey = i;
	                break;
	            case ImagePaneFields.IMAGE_ID:
	                idxImageId = i;
	                break;
	            case ImagePaneFields.IMAGE_PIXELDBID:
	                idxPixelDbId = i;
	                break;
	            case ImagePaneFields.IMAGE_LABEL:
	                idxImageLabel = i;
	                break;
	            case ImagePaneFields.PANE_WIDTH:
	                idxPaneWidth = i;
	                break;
	            case ImagePaneFields.PANE_HEIGHT:
	                idxPaneHeight = i;
	                break;
	            case ImagePaneFields.PANE_X:
	                idxPaneX = i;
	                break;
	            case ImagePaneFields.PANE_Y:
	                idxPaneY = i;
	                break;
	            case ImagePaneFields.IMAGE_WIDTH:
	                idxImageWidth = i;
	                break;
	            case ImagePaneFields.IMAGE_HEIGHT:
	                idxImageHeight = i;
	                break;
	            case GxdResultFields.ASSAY_MGIID:
	                idxAssayId = i;
	                break;
	            case ImagePaneFields.IMAGE_META:
	                idxImageMeta = i;
	                break;
	        }
	    }

	    List<SolrGxdImage> results = new ArrayList<>();

	    for (JsonNode row : root.get("values")) {
	        SolrGxdImage image = new SolrGxdImage();
	        if (idxImagePaneKey >= 0) image.setImagePaneKey(row.get(idxImagePaneKey).asInt());
	        if (idxImageId >= 0) image.setImageID(row.get(idxImageId).asText());
	        if (idxPixelDbId >= 0) image.setPixeldbID(row.get(idxPixelDbId).asText());
	        if (idxImageLabel >= 0) image.setImageLabel(row.get(idxImageLabel).asText());
	        if (idxPaneWidth >= 0) image.setPaneWidth(row.get(idxPaneWidth).asInt());
	        if (idxPaneHeight >= 0) image.setPaneHeight(row.get(idxPaneHeight).asInt());
	        if (idxPaneX >= 0) image.setPaneX(row.get(idxPaneX).asInt());
	        if (idxPaneY >= 0) image.setPaneY(row.get(idxPaneY).asInt());
	        if (idxImageWidth >= 0) image.setImageWidth(row.get(idxImageWidth).asInt());
	        if (idxImageHeight >= 0) image.setImageHeight(row.get(idxImageHeight).asInt());
	        if (idxAssayId >= 0) image.setAssayID(row.get(idxAssayId).asText());
	        if (idxImageMeta >= 0) {
	        	image.setMetaData(extractImageMeta(row.get(idxImageMeta)));
	        }

	        results.add(image);
	    }

	    return results;
	}
	
	/**
	 * Extracts image metadata JSON from a JsonNode and adds it to the provided list.
	 */
	private List<GxdImageMeta> extractImageMeta(JsonNode val) {
		List<GxdImageMeta> metaData = new ArrayList<>();
	    List<String> metaJson = new ArrayList<>();

	    if (val.isArray()) {
	        for (JsonNode n : val) {
	            metaJson.add(n.asText());
	        }
	    } else {
	        metaJson.add(val.asText());
	    }

	    for (String metaDataJson : metaJson) {
	        try {
	            metaData.add(mapper.readValue(metaDataJson, GxdImageMeta.class));
	        } catch (IOException e) {
	            log.error("Error parsing GxdImageMeta JSON from GXD Image Summary Solr Index", e);
	        }
	    }
	    return metaData;
	}
}