package org.jax.mgi.fewi.searchUtil.entities;

import java.util.List;

import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.shr.jsonmodel.GxdImageMeta;
import org.jax.mgi.snpdatamodel.document.BaseESDocument;

public class SolrGxdImage extends BaseESDocument implements SolrGxdEntity {

	Integer imagePaneKey;
	String imageID;
	String pixeldbID;
	String imageLabel;
	Integer paneX;
	Integer paneY;
	Integer paneWidth;
	Integer paneHeight;
	Integer imageWidth;
	Integer imageHeight;
	String assayID;
	List<GxdImageMeta> metaData;

	public String getAssayID() {
		return assayID;
	}

	public void setAssayID(String assayID) {
		this.assayID = assayID;
	}

	public List<GxdImageMeta> getMetaData() {
		return metaData;
	}

	public void setMetaData(List<GxdImageMeta> metaData) {
		this.metaData = metaData;
	}

	public Integer getImagePaneKey() {
		return imagePaneKey;
	}

	public void setImagePaneKey(Integer imagePaneKey) {
		this.imagePaneKey = imagePaneKey;
	}

	public String getImageID() {
		return imageID;
	}

	public void setImageID(String imageID) {
		this.imageID = imageID;
	}

	public String getPixeldbID() {
		return pixeldbID;
	}

	public void setPixeldbID(String pixeldbID) {
		this.pixeldbID = pixeldbID;
	}

	public String getImageLabel() {
		return imageLabel;
	}

	public void setImageLabel(String imageLabel) {
		this.imageLabel = imageLabel;
	}

	public Integer getPaneX() {
		return paneX;
	}

	public void setPaneX(Integer paneX) {
		this.paneX = paneX;
	}

	public Integer getPaneY() {
		return paneY;
	}

	public void setPaneY(Integer paneY) {
		this.paneY = paneY;
	}

	public Integer getPaneWidth() {
		return paneWidth;
	}

	public void setPaneWidth(Integer paneWidth) {
		this.paneWidth = paneWidth;
	}

	public Integer getPaneHeight() {
		return paneHeight;
	}

	public void setPaneHeight(Integer paneHeight) {
		this.paneHeight = paneHeight;
	}

	public Integer getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(Integer imageWidth) {
		this.imageWidth = imageWidth;
	}

	public Integer getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(Integer imageHeight) {
		this.imageHeight = imageHeight;
	}
}
