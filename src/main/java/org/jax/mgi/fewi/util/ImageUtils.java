package org.jax.mgi.fewi.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.jax.mgi.fe.datamodel.ImagePane;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;

import com.objectplanet.image.PngEncoder;

/**
 * 
 * Provides static utility functions related to images.
 * Some generate HTML snippets for different displays.
 * 
 * @author kstone
 */
public class ImageUtils 
{
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
				maxWidth,maxHeight, null);
	}

	public static String createImagePaneHTML(SolrGxdImage image, Integer maxWidth, Integer maxHeight)
	{
		return createImagePaneHTML(image.getPaneWidth(),image.getPaneHeight(),
				image.getPaneX(),image.getPaneY(),
				image.getImageWidth(),
				image.getImageHeight(),
				image.getPixeldbID(),
				maxWidth,maxHeight, null);
	}

	public static String createImagePaneHTML(SolrGxdImage image, Integer maxWidth, Integer maxHeight, String extraDivStyle)
	{
		return createImagePaneHTML(image.getPaneWidth(),image.getPaneHeight(),
				image.getPaneX(),image.getPaneY(),
				image.getImageWidth(),
				image.getImageHeight(),
				image.getPixeldbID(),
				maxWidth,maxHeight, extraDivStyle);
	}
	
	// a super specific method signature to allow bypassing hibernate objects
	public static String createImagePaneHTML(Integer paneWidth,Integer paneHeight, 
			Integer paneX, Integer paneY,
			Integer imageWidth, Integer imageHeight,
			String pixeldbID,
			Integer maxWidth,Integer maxHeight, String extraDivStyle)
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
		
		if (extraDivStyle != null) {
			divStyle = divStyle + extraDivStyle;
		}

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
	
	
	
	//========== The following code is for generating text images =====================
    
    private static String imagesFilePath = "/../images/hdp/"; // relative to web-inf
	private static String rotatedImagePath = "assets/images/hdp/";
	private static String rotatedImageUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL") + rotatedImagePath;
	
	/*
	 * Takes in a text string and rotation angle (vertical is 270.0)
	 * 	Then generates an image, encodes in in base 64, and returns an img tag 
	 * 	that will display the encoded image. No need to generate files.
	 */
	public static String getRotatedTextImageTag(String text,double rotationAngle) throws Exception
    {
		return getRotatedTextImageTag(text,rotationAngle,"");
    }
	/*
	 * This version allows you to add extra tag attributes to the img tag
	 */
    public static String getRotatedTextImageTag(String text,double rotationAngle,String tagAttributes) throws Exception
    {
    	String filename = genImageFileName(text);
    	createRotatedTextImageFile(text,rotationAngle,filename);
    	String imgTag = "<img src=\""+rotatedImageUrl+filename+"\" />";
    	
//    	byte[] imageBytes = rotatedTextToImageBytes(text,rotationAngle);
//    	byte[] b64ImageBytes = new org.apache.commons.codec.binary.Base64().encode(imageBytes);
//    	
//    	String imgTag = "<img src=\"data:image/png;base64,"+new String(b64ImageBytes)+"\" />";
    	return imgTag;
    	
    }

    
    /*
     * This version will abbreviate the text (with an ellipsis) if it exceeds maxCharacters
     * 	The original text will be inserted on img tag as a title
     */
    public static String getRotatedTextImageTagAbbreviated(String text,double rotationAngle,int maxCharacters) throws Exception
    {
    	String imgText = text;
    	if(text.length() > maxCharacters) imgText = text.substring(0,maxCharacters-1)+"...";
 
    	String filename = genImageFileName(text,maxCharacters);
    	
    	createRotatedTextImageFile(imgText,rotationAngle,filename);
		
    	String imgTag = "<img title=\""+text+"\" src=\""+rotatedImageUrl+filename+"\" />";
    	return imgTag;
    }
    
    /*
     * generates a rotated text image file if the filename does not currently exist
     */
    private static void createRotatedTextImageFile(String imgText,double rotationAngle,String filename) throws Exception
    {
    	// do nothing if we can't find web-inf path
    	if(ContextLoader.getWebInfPath()==null || ContextLoader.getWebInfPath().equals("")) return;
    	
    	String fullImageFilePath = ContextLoader.getWebInfPath()+imagesFilePath;
    	File f = new File(fullImageFilePath+filename);

        if (!f.exists()) {
			f.createNewFile();
        }
        if(f.length()==0)
        {
	    	byte[] imageBytes = rotatedTextToImageBytes(imgText,rotationAngle);
	    	FileOutputStream fop = new FileOutputStream(f);
	        fop.write(imageBytes);
			fop.flush();
			fop.close();
        }
    }
    
    private static String genImageFileName(String text,int extra)
    {
    	return genImageFileName(text+"_"+extra);
    }
    private static String genImageFileName(String text)
    {
    	return text.hashCode()+".png";
    }
    
    /*
     * These static variables help speed up processing of multiple images
     */
    private static Font imageFont = new Font("MyriadPro", Font.BOLD, 12);
    private static Color transparentColor = new Color(0,0,0,0);
    private static FontRenderContext fontContext = null;
    static
    {
    	BufferedImage buffer = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buffer.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        fontContext = g2.getFontRenderContext();
    }
    
    private static Rectangle2D getTextBounds(String text)
    {
        Rectangle2D bounds = imageFont.getStringBounds(text,fontContext);
        return bounds;
    }
    
    /*
	 * Takes in a text string and rotation angle (vertical is 270.0)
	 * 	Then generates an image of the text rotated at the desired angle.
	 * 	returns the image as byte array.
	 */
    public static byte[] rotatedTextToImageBytes(String text,double rotationAngle) throws Exception
    {
        BufferedImage rotatedImage = rotatedTextToImage(text,rotationAngle);

        byte[] imageInByte;
        imageInByte = convertImageToBytes(rotatedImage);
        return imageInByte;
    }
    
    private static byte[] convertImageToBytes(BufferedImage image) throws Exception
    {
    	// convert to indexed format for performance
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
        //ImageIO.write( image, "png", baos );
        PngEncoder pe = new PngEncoder(PngEncoder.COLOR_TRUECOLOR_ALPHA);
        pe.encode(image, baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        return imageInByte;
    }
    
    public static BufferedImage rotatedTextToImage(String text,double rotationAngle) throws Exception
    {
    	Rectangle2D bounds = getTextBounds(text);
        
        // calculate the size of the text
        int height = (int) bounds.getHeight();
        int width = (int) bounds.getWidth();
        
        // prepare some output
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buffer.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setFont(imageFont); 
        
        g2.setBackground(transparentColor);
        g2.setColor(Color.BLACK);
        int y = (int)-bounds.getY();
        g2.drawString(text,0,y);

        BufferedImage rotatedImage = rotate(buffer, rotationAngle, transparentColor);
        return rotatedImage;
    }

     /**
     * Rotates the current JpgImage object a specified number of degrees.
     * <p>
     * You should be aware of 2 things with regard to image rotation.
     * First, the more times you rotate an image, the more the image
     * degrades. So instead of rotating an image 90 degrees and then
     * rotating it again 45 degrees, you should rotate it once at a
     * 135 degree angle.
     * <p>
     * Second, a rotated image will always have a rectangular border
     * with sides that are vertical and horizontal, and all of the area
     * within this border will become part of the resulting image.
     * Therefore, if you rotate an image at an angle that's not a
     * multiple of 90 degrees, your image will appear to be placed
     * at an angle against a rectangular background of the specified Color.
     * For this reason, an image rotated 45 degrees and then another 45 degrees
     * will not be the same as an image rotated 90 degrees.
     *
     * @param  degrees    the number of degrees to rotate the image
     * @param  backgroundColor    the background color used for areas
     *                            in the resulting image that are not
     *                            covered by the image itself
     */
    private static BufferedImage rotate(BufferedImage bi, double degrees, Color backgroundColor) {
        /*
         * Okay, this required some strange geometry. Before an image
         * is rotated, the origin is at the top left corner of the
         * rectangle that contains the image. After an image is rotated,
         * you want the origin to get moved to a spot that will allow
         * the entire rotated image to be framed within a rectangle.
         * Unfortunately, this does not happen automatically.
         *
         * That's where the strange geometry comes in. We essentially
         * need to rotate the image, and then determine what the width
         * and height of the new image is, and then determine where the
         * new origin should be. The width and height is easy (you can
         * also use the AffineTransform getWidth and getHeight methods),
         * but the new origin...well...not so easy. Unfortunately, my
         * trigonometry skills aren't sharp enough to be able to give you
         * a good explanation of what's going on with this method without
         * drawing everything out for you. If you want to figure it out
         * for yourself, just draw an axis on a sheet of paper, place a
         * smaller rectangular piece of paper on the axis, and start
         * rotating it along the axis to see what's going on. Then pull
         * out your old trig books and start calculating.
         *
         * BTW, if there's an easier way to do this, I'd love to know about it.
         */
        
        // adjust the angle that was passed so it's between 0 and 360 degrees
        double positiveDegrees = (degrees % 360) + ((degrees < 0) ? 360 : 0);
        double degreesMod90 = positiveDegrees % 90;
        double radians = Math.toRadians(positiveDegrees);
        double radiansMod90 = Math.toRadians(degreesMod90);
        
        // don't bother with any of the rest of this if we're not really rotating
        if (positiveDegrees == 0) {
            return bi;
        }
        
        // figure out which quadrant we're in (we'll want to know this later)
        int quadrant = 0;
        if (positiveDegrees < 90) {
            quadrant = 1;
        } else if ((positiveDegrees >= 90) && (positiveDegrees < 180)) {
            quadrant = 2;
        } else if ((positiveDegrees >= 180) && (positiveDegrees < 270)) {
            quadrant = 3;
        } else if (positiveDegrees >= 270) {
            quadrant = 4;
        }
        
        // get the height and width of the rotated image (you can also do this
        // by applying a rotational AffineTransform to the image and calling
        // getWidth and getHeight against the transform, but this should be a
        // faster calculation)
        int height = bi.getHeight();
        int width = bi.getWidth();
        double side1 = (Math.sin(radiansMod90) * height) + (Math.cos(radiansMod90) * width);
        double side2 = (Math.cos(radiansMod90) * height) + (Math.sin(radiansMod90) * width);
        
        double h = 0;
        int newWidth = 0, newHeight = 0;
        if ((quadrant == 1) || (quadrant == 3)) {
            h = (Math.sin(radiansMod90) * height);
            newWidth = (int)side1;
            newHeight = (int)side2;
        } else {
            h = (Math.sin(radiansMod90) * width);
            newWidth = (int)side2;
            newHeight = (int)side1;
        }

        // figure out how much we need to shift the image around in order to
        // get the origin where we want it
        int shiftX = (int)(Math.cos(radians) * h) - ((quadrant == 3) || (quadrant == 4) ? width : 0);
        int shiftY = (int)(Math.sin(radians) * h) + ((quadrant == 2) || (quadrant == 3) ? height : 0);

        // create a new BufferedImage of the appropriate height and width and
        // rotate the old image into it, using the shift values that we calculated
        // earlier in order to make sure the new origin is correct
        BufferedImage newbi = new BufferedImage(newWidth, newHeight, bi.getType());
        Graphics2D g2d = newbi.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setBackground(backgroundColor);
        g2d.clearRect(0, 0, newWidth, newHeight);
        g2d.rotate(radians);
        g2d.drawImage(bi, shiftX, -shiftY, null);
        return newbi;

    }
}
