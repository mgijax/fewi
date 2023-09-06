package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jax.mgi.fe.datamodel.Genotype;
import org.jax.mgi.fe.datamodel.Image;
import org.jax.mgi.fe.datamodel.ImageAllele;
import org.jax.mgi.fe.datamodel.ImagePaneSet;
import org.jax.mgi.fe.datamodel.Reference;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;


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
  private String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
  private String pixeldbUrl = ContextLoader.getConfigBean().getProperty("PIXELDB_URL");

  // encapsulated row object and object data
  private Image image;
  private Image thumbImage;
  private List<Genotype> genotypes;
  private Reference reference;

  // optional - key to object (marker/allele etc) of this summary
  private int summaryObjectKey;

  // converter for curator 'tags' in the data
  private NotesTagConverter ntc;

  //--------------
  // constructors
  //--------------
  public  ImageSummaryRow (Image image, Image thumbImage) {

    //set instance variables
    this.image = image;
    this.thumbImage = thumbImage;
    this.genotypes = image.getGenotypes();
    this.reference = image.getReference();
    try {
      ntc = new NotesTagConverter();
    }catch (Exception e) {}

    return;
  }


  //------------------------------------------------------------------------
  // public methods
  //------------------------------------------------------------------------

  // image
  public Image getImage() {
    return this.image;
  }

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
    return this.reference;
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
    return ntc.convertNotes(image.getCopyright(), '|');
  }

  // alleleic composition
  public String getAllelicComp(Genotype thisGenotype) {
    String cleanAllelicComp = thisGenotype.getCombination3();
    if (cleanAllelicComp==null) {return "";}
    return FormatHelper.newline2HTMLBR(ntc.convertNotes(cleanAllelicComp, '|'));
  }

  // genetic background
  public String getGenBackground(Genotype thisGenotype) {
    String cleanGenBackground = thisGenotype.getBackgroundStrain();
    if (cleanGenBackground==null) {return "";}
    return FormatHelper.superscript(cleanGenBackground);
  }


  // abbreviated list of authors
  public String getShortAuthor(){
    String authors = this.reference.getAuthors();
    // one author
    if (authors.indexOf(';') == -1){return authors;}
    // more than 1 author
    return authors.substring(0, authors.indexOf(';')) + ", et al.";
  }

  // assay types in image panes
  public String getAssayTypesInPage(){
    ImagePaneSet currentSet;
    StringBuffer assayTypes = new StringBuffer();
    List<ImagePaneSet> filteredSetList= new ArrayList<ImagePaneSet>();
    Iterator<ImagePaneSet> setIter;

    // first, remove pane sets NOT for given marker
    setIter = this.image.getImagePaneSets().iterator();
    while (setIter.hasNext()) {
      currentSet = setIter.next();
      if (currentSet.getMarkerKey() == summaryObjectKey) {
          filteredSetList.add(currentSet);
      }
    }

    // display is determined by uniqueness of assay types in remaining sets
    if (this.multiAssayTypesInImage(filteredSetList)) { //multiple types of assays
      setIter = filteredSetList.iterator();
      while (setIter.hasNext()) {
          currentSet = setIter.next();
          assayTypes.append(currentSet.getPaneLabels());
          assayTypes.append(" : ");
          assayTypes.append(currentSet.getAssayType());
          assayTypes.append("<br/>");
      }
    }
    else {
      setIter = filteredSetList.iterator();
      while (setIter.hasNext()) {
          currentSet = setIter.next();
          if (currentSet.getPaneLabels() != null && !currentSet.getPaneLabels().equals(" ")) {
            assayTypes.append(currentSet.getPaneLabels());
            assayTypes.append(" : ");
          }
          assayTypes.append(currentSet.getAssayType());
          assayTypes.append("<br/>");
      }
    }




//    if (uniqueAssayTypes.size() == 1) {
//        String uniqueType = uniqueAssayTypes.iterator().next();
//        assayTypes.append(uniqueType);
//    }
//    else {
//        setIter = this.image.getImagePaneSets().iterator();
//        while (setIter.hasNext()) {
//            currentSet = setIter.next();
//            assayTypes.append(currentSet.getPaneLabels());
//            assayTypes.append(" ");
//            assayTypes.append(currentSet.getAssayType());
//            assayTypes.append(" ");
//            assayTypes.append(currentSet.getSequenceNum());
//            assayTypes.append("<br/>");
//        }
//    }



    return assayTypes.toString();
  }

  // some summarys will set this value
  public void setSummaryObjectKey(int keyValue) {
    this.summaryObjectKey = keyValue;
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
            summaryRow.append("<td align='center' rowspan=" + genotypes.size() + ">");
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
        summaryRow.append("<td align='center'>");
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
  private boolean multiAssayTypesInImage(List<ImagePaneSet> inputSetList) {

    ImagePaneSet currentSet;
    List<String> uniqueAssayTypes = new ArrayList<String>();
    Iterator<ImagePaneSet> setIter = inputSetList.iterator();

    // get a unique list of assay types
    while (setIter.hasNext()) {
        currentSet = setIter.next();
        if(!uniqueAssayTypes.contains(currentSet.getAssayType())) {
          uniqueAssayTypes.add(currentSet.getAssayType());
        }
    }

    // act accordingly
    if (uniqueAssayTypes.size() > 1) {
      return true;
    }
    return false;
  }

}
