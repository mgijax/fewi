package org.jax.mgi.fewi.hunter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.entities.ESGxdImage;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.shr.fe.indexconstants.ImagePaneFields;
import org.jax.mgi.shr.jsonmodel.GxdImageMeta;
import org.jax.mgi.snpdatamodel.document.BaseESDocument;
import org.jax.mgi.snpdatamodel.document.ESEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Hunter for the gxdResultHasImage solr index (like gxdResult, but only with
 * classical data that has associated images)
 * 
 */

@Repository
public class ESGxdImagePaneHunter<T extends ESEntity> extends ESGxdSummaryBaseHunter<T> {

    public ESGxdImagePaneHunter() {
    	super((Class<T>)ESGxdImage.class);
        keyString = ImagePaneFields.IMAGE_PANE_KEY;
    }

    public ESGxdImagePaneHunter(Class<T> clazz, String host, String port, String index) {
        super(clazz);
        keyString = ImagePaneFields.IMAGE_PANE_KEY;
        this.esHost = host;
        this.esPort = port;
        this.esIndex = index;
    }

	@Value("${es.gxdimagepane.index}")
	public void setESIndex(String esIndex) {
		super.esIndex = esIndex;
	}

	@Override
	public <T extends ESEntity> List<T> processLookupResponse(JsonNode root, Class<T> clazz) throws Exception {
//	public List<SolrGxdImage> processLookupResponse(JsonNode root) throws Exception {
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

//		List<SolrGxdImage> results = new ArrayList<>();
		List<T> results = new ArrayList<>();
		for (JsonNode row : root.get("values")) {
			T tObj = clazz.getDeclaredConstructor().newInstance();
			if (tObj instanceof ESGxdImage) {
				ESGxdImage image = (ESGxdImage)tObj;
				if (idxImagePaneKey >= 0)
					image.setImagePaneKey(row.get(idxImagePaneKey).asInt());
				if (idxImageId >= 0)
					image.setImageID(row.get(idxImageId).asText());
				if (idxPixelDbId >= 0)
					image.setPixeldbID(row.get(idxPixelDbId).asText());
				if (idxImageLabel >= 0)
					image.setImageLabel(row.get(idxImageLabel).asText());
				if (idxPaneWidth >= 0)
					image.setPaneWidth(row.get(idxPaneWidth).asInt());
				if (idxPaneHeight >= 0)
					image.setPaneHeight(row.get(idxPaneHeight).asInt());
				if (idxPaneX >= 0)
					image.setPaneX(row.get(idxPaneX).asInt());
				if (idxPaneY >= 0)
					image.setPaneY(row.get(idxPaneY).asInt());
				if (idxImageWidth >= 0)
					image.setImageWidth(row.get(idxImageWidth).asInt());
				if (idxImageHeight >= 0)
					image.setImageHeight(row.get(idxImageHeight).asInt());
				if (idxAssayId >= 0)
					image.setAssayID(row.get(idxAssayId).asText());
				if (idxImageMeta >= 0) {
//	        	image.setMetaData(extractImageMeta(row.get(idxImageMeta)));
				}
			}
			results.add(tObj);
		}

		return results;
	}

	/**
	 * Extracts image metadata JSON from a JsonNode and adds it to the provided
	 * list.
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