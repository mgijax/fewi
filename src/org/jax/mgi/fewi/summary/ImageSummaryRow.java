package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.Image;
import mgi.frontend.datamodel.ImageAllele;
import mgi.frontend.datamodel.Genotype;
import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.util.FormatHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An ImageSummaryRow represents a row in the image summary.  This page
 * has a rather unusual tabular layout, and this object will be responsible
 * for outputting an entire 'row', including row/column spans  and sub-rows
 * when needed.  The is closely coupled with the table definition found
 * within the jsp
 */
public class ImageSummaryRow {

  //--------------------
  // instance variables
  //--------------------

  // logger & config values
  private Logger logger = LoggerFactory.getLogger(ImageSummaryRow.class);
  private String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
  private String pixeldbUrl = ContextLoader.getConfigBean().getProperty("PIXELDB_URL");

  // encapsulated row object and object data
  private Image image;
  private Image thumbImage;
  private List<Genotype> genotypes;

  // converter for curator 'tags' in the data
  private NotesTagConverter ntc;

  //--------------
  // constructors
  //--------------

  // hide the default constructor - we NEED an image to wrap
  private ImageSummaryRow () {}
  public  ImageSummaryRow (Image image, Image thumbImage) {

    //set instance variables
    this.image = image;
    this.thumbImage = thumbImage;
    this.genotypes = image.getGenotypes();
    try {
      ntc = new NotesTagConverter();
    }catch (Exception e) {}

    return;
  }


  //------------------------------------------------------------------------
  // public methods
  //------------------------------------------------------------------------

  // image height
  public String getImageHeight() {
    return this.thumbImage.getHeight().toString();
  }

  // image width
  public String getImageWidth() {
    return this.thumbImage.getWidth().toString();
  }

  // image ID
  public String getImageId() {
    return this.image.getMgiID();
  }

  // alleles for this image
  public List<ImageAllele> getAlleles() {
    return this.image.getImageAlleles();
  }

  // allele's reference
  public Reference getReference() {
    return this.image.getReference();
  }




  // img tag
  public String getImgTag() {

    StringBuffer imgTag = new StringBuffer();
    imgTag.append("<a href='" + fewiUrl);
    imgTag.append("image/" + this.image.getMgiID() + "'>");
    imgTag.append("<img width='" + this.getImageWidth());
    imgTag.append("' height='" + this.getImageHeight() + "'");
    imgTag.append("src='" + pixeldbUrl);
    imgTag.append(thumbImage.getPixeldbNumericID() + "'>");
    return imgTag.toString();
  }

  // caption
  public String getCaption() {
    String cleanCaption = thumbImage.getCaption();
    if (cleanCaption==null) {return "";}
    cleanCaption = cleanCaption.trim().replaceAll("[\\r\\n]", "").replaceAll("[']", "\'");
    return cleanCaption;
  }

  // copyright
  public String getCopyright() {
    return image.getCopyright();
  }

  // alleleic composition
  public String getAllelicComp(Genotype thisGenotype) {
    String cleanAllelicComp = thisGenotype.getCombination1();
    if (cleanAllelicComp==null) {return "";}
    return FormatHelper.newline2HTMLBR(ntc.convertNotes(cleanAllelicComp, '|'));
  }

  // genetic background
  public String getGenBackground(Genotype thisGenotype) {
    String cleanGenBackground = thisGenotype.getBackgroundStrain();
    if (cleanGenBackground==null) {return "";}
    return FormatHelper.superscript(cleanGenBackground);
  }


  //------------------------------------------------------------------------
  // pheno summary by allele
  //------------------------------------------------------------------------
  public String getPhenoByAlleleRow() {

    // collection of formatted display strings to be returned
    StringBuffer summaryRow = new StringBuffer();

    // only the first row needs the first two table cells (with ROWSPAN)
    boolean firstRowInColumn = true;


    // if we have more than one genotype, we need output multiple rows,
    // and ROWSPAN the first two columns together into a single cell
    if (genotypes.size() > 1) {

        for (Genotype genotype: genotypes) {

          summaryRow.append("<tr>");

          // only output the image & caption columns on first pass, and
          // merge the cells via rowspan
          if (firstRowInColumn) {

            // image
            summaryRow.append("<td rowspan=" + genotypes.size() + ">");
            summaryRow.append(this.getImgTag());
            summaryRow.append("</td>");

            // caption
            summaryRow.append("<td rowspan=" + genotypes.size() + ">");
            summaryRow.append(this.getCaption());
            summaryRow.append("</td>");

            // toggle boolean;  bypass this section after first pass
            firstRowInColumn = false;
          }

          // allelic composition
          summaryRow.append("<td>");
          summaryRow.append(this.getAllelicComp(genotype));
          summaryRow.append("</td>");

          // genetic background
          summaryRow.append("<td>");
          summaryRow.append(this.getGenBackground(genotype));
          summaryRow.append("</td>");

          summaryRow.append("</tr>");

          summaryRow.append("");
        }
    }

    // if there are only 1 (or 0) genotypes, everything fits into a single
    // row, and therefore no ROWSPAN needed
    if (genotypes.size() < 2) {

        summaryRow.append("<tr>");

        // image
        summaryRow.append("<td>");
        summaryRow.append(this.getImgTag());
        summaryRow.append("</td>");

        // caption
        summaryRow.append("<td>");
        summaryRow.append(this.getCaption());
        summaryRow.append("</td>");

        // allelic composition
        summaryRow.append("<td>");
        if (genotypes.size() != 0){
          summaryRow.append(this.getAllelicComp(genotypes.get(0)));
        }
        summaryRow.append("</td>");

        // genetic background
        summaryRow.append("<td>");
        if (genotypes.size() != 0){
          summaryRow.append(this.getGenBackground(genotypes.get(0)));
        }
        summaryRow.append("</td>");

        summaryRow.append("</tr>");

        summaryRow.append("");
    }

    return summaryRow.toString();
  }




  //------------------------------------------------------------------------
  // private methods
  //------------------------------------------------------------------------


  // scaled height, keeping aspect ratio for image with imageDisplayWidth
//  private int getModifiedHeight() {
//      double width = image.getWidth().doubleValue();
//      double height = image.getHeight().doubleValue();
//      int modifiedHeight = (int)((height * imageDisplayWidth) / width);
//      return modifiedHeight;
//  }

}
