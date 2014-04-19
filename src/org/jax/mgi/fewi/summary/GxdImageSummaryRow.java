package org.jax.mgi.fewi.summary;

//public class GxdMarkerSummary {
//
//}

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around an image (from solr);  represents on row in summary
 */
public class GxdImageSummaryRow {
	//-------------------
		// instance variables
		//-------------------

	  private Logger logger = LoggerFactory.getLogger(GxdImageSummaryRow.class);

		// encapsulated row object
		private SolrGxdImage image;

		// config values
	  String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

		//-------------
		// constructors
		//-------------

	  public GxdImageSummaryRow (SolrGxdImage image) {
	  	this.image=image;
	  }

	  public String getImage() {
		  return "<a style=\"display:inline-block;\" href=\""+fewiUrl+"image/"+image.getImageID()+
				  "\">"+ImageUtils.createImagePaneHTML(image,200,200)+"</a>" +
				"<span class=\"copySymbol\">&copy;</span>";
	  }
	  public String getImageLabel(){
		  return image.getImageLabel();
	  }
	  public String getMetaData()
	  {
		  String html = "<table id=\"gxd_image_meta\">" +
		  		"<tr><th>Gene</th><th>Assay Type</th><th>Result Details</th></tr>";
		  for(String metaData : image.getMetaData())
		  {
			  // each meta data row needs to be unpacked. It has a format like such "gene"||"assayType"||"specLabel1"|"assayID1","specLabel2"|"assayID2" etc
			  // as of 2013-04-18 there are no pipe or comma characters in any specimen label.
			  // if this changes, we can either pick different delimiters, or use a better format, like JSON, for encoding this data
			  
			  // parse the meta data into columns
			  String[] cols = metaData.split("\\|\\|"); // split on the double pipes ||
			  if(cols.length>1)
			  {
				  String assayType = cols[0];
				  String markerSymbol = cols[1];
				  
				  String specLabelsHtml="";
				  boolean firstSpecimen=true;
				  if(cols.length>2 && cols[2] != null)
				  {
					  String[] specLabels = cols[2].split("\t\t"); // split by double tab ,
					  for(String specLabel : specLabels)
					  {
						  if(specLabel != null)
						  {
							  String[] specItems = specLabel.split("\\|"); // split by single pipe |
							  if(specItems.length > 1)
							  {
								  if(!firstSpecimen) specLabelsHtml += ", ";
								  String label = specItems[0];
								  String assayId = specItems[1];
								  if(label == null || label.equals("null"))
								  {
									  label = image.getImageLabel();
									  specLabelsHtml += "<a class=\"nowrap\" href=\""+fewiUrl+"assay/"+assayId+"\">"+image.getImageLabel()+"</a>";
								  }
								  else
								  {
									  String anchorLink = FormatHelper.makeCssSafe(label)+"_id"; // needs to match css id for specimens on assay detail
									  specLabelsHtml += "<a class=\"nowrap\" href=\""+fewiUrl+"assay/"+assayId+"#"+anchorLink+"\">"+FormatHelper.superscript(label)+"</a>";
								  }
								  
								  firstSpecimen=false;
							  }
						  }
					  }
					  
				  }
				  if (specLabelsHtml == "")
				  {
					  // link by figure label + pane label if this is blot (or if something is wrong with specimen label that caused it to be blank)
					  specLabelsHtml = "<a class=\"nowrap\" href=\""+fewiUrl+"assay/"+image.getAssayID()+"\">"+image.getImageLabel()+"</a>";
				  }
				  html += "<tr><td class=\"nowrap\">"+markerSymbol+"</td>"+
				  		"<td class=\"nowrap\">"+assayType+"</td>" +
				  		"<td>"+specLabelsHtml+"</td></tr>";
			  }
		  }
		  html += "</table>";
		  return html;
	  }
}
