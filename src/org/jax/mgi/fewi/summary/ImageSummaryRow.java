package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.Image;
import mgi.frontend.datamodel.Genotype;

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

  //-------------------
  // instance variables
  //-------------------

  // logger & config values
  private Logger logger = LoggerFactory.getLogger(ImageSummaryRow.class);
  private String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

  // encapsulated row object and object data
  private Image image;
  private List<Genotype> genotypes;

  // converter for curator 'tags' in the data
  private NotesTagConverter ntc;


  //-------------
  // constructors
  //-------------

  // hide the default constructor - we NEED an image to wrap
  private ImageSummaryRow () {}
  public  ImageSummaryRow (Image image) {
    this.image = image;
    this.genotypes = image.getGenotypes();
    try {
      ntc = new NotesTagConverter();
    }catch (Exception e) {}

    return;
  }


  //------------------------------------------------------------------------
  // public methods
  //------------------------------------------------------------------------

  // functionality to generate a single summary row
  public String getRow() {

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
            summaryRow.append("<a href='" + fewiUrl);
            summaryRow.append("image/pheno/" + image.getMgiID() + "'>");
            summaryRow.append("<img width='150' height='" + this.getModifiedHeight() + "'");
            summaryRow.append("src='http://www.informatics.jax.org/pixeldb/fetch_pixels.cgi?id=");
            summaryRow.append(image.getPixeldbNumericID() + "'>");
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
        summaryRow.append("<a href='" + fewiUrl + "image/pheno/" + image.getMgiID() + "'>");
        summaryRow.append("<img width='150' height='" + this.getModifiedHeight() + "'");
        summaryRow.append("src='http://www.informatics.jax.org/pixeldb/fetch_pixels.cgi?id=");
        summaryRow.append(image.getPixeldbNumericID() + "'>");
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

  private String getCaption() {
      String cleanCaption = image.getCaption();
      if (cleanCaption==null) {return "";}
      cleanCaption = cleanCaption.trim().replaceAll("[\\r\\n]", "").replaceAll("[']", "\'");
      cleanCaption = FormatHelper.superscript(cleanCaption);
      return cleanCaption;
  }

  private String getAllelicComp(Genotype thisGenotype) {
      String cleanAllelicComp = thisGenotype.getCombination1();
      if (cleanAllelicComp==null) {return "";}
      return FormatHelper.newline2HTMLBR(ntc.convertNotes(cleanAllelicComp, '|'));
  }

  private String getGenBackground(Genotype thisGenotype) {
      String cleanGenBackground = thisGenotype.getBackgroundStrain();
      if (cleanGenBackground==null) {return "";}
      return FormatHelper.superscript(cleanGenBackground);
  }


  // scaled height, keeping aspect ratio for image with 150px width
  private int getModifiedHeight() {
      double width = image.getWidth().doubleValue();
      double height = image.getHeight().doubleValue();
      int modifiedHeight = (int)((height * 150) / width);
      return modifiedHeight;
  }

}
