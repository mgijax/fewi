package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.ImageUtils;
import org.jax.mgi.shr.jsonmodel.GxdImageMeta;
import org.jax.mgi.shr.jsonmodel.GxdSpecimenLabel;

/**
 * wrapper around an image (from solr); represents on row in summary
 */
public class GxdImageSummaryRow {
	// -------------------
	// instance variables
	// -------------------

	// encapsulated row object
	private final SolrGxdImage image;

	// config values
	String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

	private int maxWidth = 200;
	private int maxHeight = 200;
	private boolean showCopyright = true;
	private boolean linkToDetail = true;

	// -------------
	// constructors
	// -------------

	public GxdImageSummaryRow(SolrGxdImage image) {
		this.image = image;
	}

	public void setMaxWidth(int width) { this.maxWidth = width; }
	public void setMaxHeight(int height) { this.maxHeight = height; }
	public void hideCopyright() { this.showCopyright = false; }
	public void skipDetailLink() { this.linkToDetail = false; }

	public String getImage() {
		String copyright = "";
		if (showCopyright) {
			copyright = "<span class=\"copySymbol\">&copy;</span>";
		}

		if (!linkToDetail) {
			return "<span style=\"display:inline-block; padding-top: 3px;\">"
				+ ImageUtils.createImagePaneHTML(image, maxWidth, maxHeight, "border: 1px solid black;") + "</span>" + copyright;
		}

		return "<a style=\"display:inline-block;\" href=\"" + fewiUrl
				+ "image/" + image.getImageID() + "\">"
				+ ImageUtils.createImagePaneHTML(image, maxWidth, maxHeight) + "</a>"
				+ copyright;
	}

	public String getImageLabel() {
		return image.getImageLabel();
	}

	public String getMetaData() {
		String html = "<table id=\"gxd_image_meta\">"
				+ "<tr><th>Gene</th><th>Assay Type</th><th>Result Details</th></tr>";
		
		for (GxdImageMeta metaData : image.getMetaData()) {

			// generate the correct specimen label links
			List<String> specLabelLinks = new ArrayList<String>();
			for(GxdSpecimenLabel labelObject : metaData.getSpecimenLabels()) {
				
				String label = labelObject.getLabel();
				String assayId = labelObject.getAssayId();
				
				if (label != null && !label.equals("")) {
					// needs to match css id for specimens on assay detail
					String anchorLink = FormatHelper.makeCssSafe(label) + "_id"; 
					
					String specLabelLink = "<a class=\"nowrap\" href=\""
							+ fewiUrl
							+ "assay/"
							+ assayId
							+ "#"
							+ anchorLink
							+ "\">"
							+ FormatHelper.superscript(label)
							+ "</a>";
					
					specLabelLinks.add(specLabelLink);
				} 

			}
			
			// join all links for insitu assays with specimens 
			String specLabelsHtml = StringUtils.join(specLabelLinks, ", ");
			
			/*
			 *  link by figure label + pane label if this is blot (or if
			 *  something is wrong with specimen label that caused it to
			 *  be blank)
			 */
			if (specLabelsHtml == "") {
				specLabelsHtml = "<a class=\"nowrap\" href=\"" + fewiUrl
						+ "assay/" + image.getAssayID() + "\">"
						+ image.getImageLabel() + "</a>";
			}
			html += "<tr><td class=\"nowrap\">" + metaData.getMarkerSymbol() + "</td>"
					+ "<td class=\"nowrap\">" + metaData.getAssayType() + "</td>"
					+ "<td>" + specLabelsHtml + "</td></tr>";
		}
		html += "</table>";
		return html;
	}
}
