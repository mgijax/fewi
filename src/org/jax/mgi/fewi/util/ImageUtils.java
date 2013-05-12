package org.jax.mgi.fewi.util;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mgi.frontend.datamodel.ImagePane;
import mgi.frontend.datamodel.Image;

/**
 * 
 * Provides static utility functions related to images.
 * Some generate HTML snippets for different displays.
 * 
 * @author kstone
 */
public class ImageUtils 
{
	 private static Logger logger = LoggerFactory.getLogger(ImageUtils.class);
	/*
	 * create the image pane HTML snippet without any scaling.
	 */
	public static String createImagePaneHTML(ImagePane pane)
	{
		return createImagePaneHTML(pane,null);
	}
	public static String createImagePaneHTML(ImagePane pane, Integer maxWidth)
	{
		return createImagePaneHTML(pane,null,null);
	}
	
	
	/*
	 * Create an image pane HTML snippet that can resie an image pane
	 * (either scaled up or down) to match the desired width
	 * if desiredWidth is null or non-positive, then scale=1 is used
	 */
	public static String createImagePaneHTML(ImagePane pane, Integer maxWidth, Integer maxHeight)
	{
		return createImagePaneHTML(pane.getWidth(),pane.getHeight(),
				pane.getX(),pane.getY(),
				pane.getImage().getWidth(),
				pane.getImage().getHeight(),
				pane.getImage().getPixeldbNumericID(),
				maxWidth,maxHeight);
	}
	public static String createImagePaneHTML(SolrGxdImage image, Integer maxWidth, Integer maxHeight)
	{
		return createImagePaneHTML(image.getPaneWidth(),image.getPaneHeight(),
				image.getPaneX(),image.getPaneY(),
				image.getImageWidth(),
				image.getImageHeight(),
				image.getPixeldbID(),
				maxWidth,maxHeight);
	}
	// a super specific method signature to allow bypassing hibernate objects
	public static String createImagePaneHTML(Integer paneWidth,Integer paneHeight, 
			Integer paneX, Integer paneY,
			Integer imageWidth, Integer imageHeight,
			String pixeldbID,
			Integer maxWidth,Integer maxHeight)
	{
		// need to account for null pane values
		// if height or width is null(or less than 1), we need to set them to the height/width of the image object as a failsafe
		if(paneHeight<=1 || paneHeight <=1)
		{
			paneWidth=imageWidth;
			paneHeight=imageHeight;
		}
		
		// default to a scale/ratio of 1
		double widthScaleFactor=1,heightScaleFactor=1;
		if(maxWidth!=null && maxWidth>0 && paneWidth > maxWidth)
		{
			// calculate the scale (or ratio) that we need to adjust the pane width to match the desired width
			widthScaleFactor = (double) maxWidth / (double) paneWidth;
		}
		if(maxHeight!=null && maxHeight>0 && paneHeight > maxHeight)
		{
			// calculate the scale (or ratio) that we need to adjust the pane Height to match the desired Height
			heightScaleFactor = (double) maxHeight / (double) paneHeight;
		}
		double scaleFactor = Math.min(widthScaleFactor, heightScaleFactor);
		
		// calculate all the six relative values for displaying an image pane.
		// NOTE: if scale is 1 (the default) then the values are equal to their database coordinates
		paneX =scale(paneX,scaleFactor);
		paneY = scale(paneY,scaleFactor);
		paneWidth = scale(paneWidth, scaleFactor);
		paneHeight = scale(paneHeight, scaleFactor);
		imageWidth = scale(imageWidth, scaleFactor);
		imageHeight = scale(imageHeight, scaleFactor);

		
		// set up any urls
		String pixelDBURL = ContextLoader.getConfigBean().getProperty("PIXELDB_URL");
		//pixelDBURL = "http://www.informatics.jax.org/webshare/fetch_pixels.cgi?id=";
		//String fewiURL    = ContextLoader.getConfigBean().getProperty("FEWI_URL");
		
		// calculate the styles for the outer div and the image tags
		// the div just needs to be sized to the window of the pane we are viewing
		String divStyle = "position:relative; "+
				"width:"+paneWidth+"px; "+
				"height:"+paneHeight+"px; ";
		// the img tag needs to be positioned to the offset where the pane is
		// also the clip must be defined as rect(top,right,bottom,left);
		String imgStyle = "position:absolute; "+
				"left:-"+paneX+"px; "+
				"top:-"+paneY+"px; "+
				"clip: rect("+paneY+"px "+(paneWidth+paneX)+"px "+(paneHeight+paneY)+"px "+paneX+"px); ";

		// build the image tag
		String imgSrc = pixelDBURL+pixeldbID;
		// we set the explicit width and height of the image to deal with scaling
		String imgTag = "<img width=\""+imageWidth+"\" height=\""+imageHeight+"\" "+
				"style=\""+imgStyle+"\" src=\""+imgSrc+"\"/>";
		
		// build the wrapper div tag
		String htmlOutput = "<div style=\""+divStyle+"\">"+imgTag+"</div>";
		return htmlOutput;
	}
	
	/* 
	 * returns the scaled integer
	*/
	private static int scale(Integer value,double scaleFactor)
	{
		int dim = value==null ? 0 : (int) value;
		return (int) Math.floor(dim*scaleFactor);
	}
}
