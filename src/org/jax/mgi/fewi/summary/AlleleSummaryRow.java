package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleSynonym;
import mgi.frontend.datamodel.phenotype.AlleleSummaryDisease;
import mgi.frontend.datamodel.phenotype.AlleleSummarySystem;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.FormatHelper;

/**
 * wrapper around a marker tissue count;  represents on row in summary
 */
public class AlleleSummaryRow 
{
  //-------------------
  // instance variables
  //-------------------
  private static String NORMAL_PHENOTYPE="normal phenotype";
  private static String PHENOTYPE_NOT_ANALYZED="phenotype not analyzed";
  private static String CELL_LINE="Cell Line";
  private static String CHIMERIC="Chimeric";
  
  // encapsulated row object
  private final Allele allele;

  // config values
  String fewiUrl   = ContextLoader.getConfigBean().getProperty("FEWI_URL");
  String webshareUrl = ContextLoader.getConfigBean().getProperty("WEBSHARE_URL");
  String pywiUrl   = ContextLoader.getConfigBean().getProperty("WI_URL");
  String javawiUrl = ContextLoader.getConfigBean().getProperty("JAVAWI_URL");

  public AlleleSummaryRow (Allele allele) {
    this.allele = allele;
  }

  //------------------------------------------------------------------------
  // public instance methods;  JSON serializer will call all public methods
  //------------------------------------------------------------------------
  // structure field
  public String getNomen() {
	StringBuilder sb = new StringBuilder("");
	// allele symbol link
	sb.append("<a style=\"line-height: 1.8; \" href=\"").append(fewiUrl).append("allele/").append(allele.getPrimaryID()).append("\">");
	sb.append(FormatHelper.superscript(allele.getSymbol())).append("</a>");
	// gene + allele name
	sb.append("<br>").append(FormatHelper.superscript(allele.getGeneName())).append("; ").append(FormatHelper.superscript(allele.getName()));
	if(allele.hasPrimaryImage())
	{
		sb.append("<br><a href=\"").append(fewiUrl).append("image/phenoSummary/allele/")
			.append(allele.getPrimaryID()).append("\">");
			sb.append("<img height=\"11\" width=\"15\" src=\"").append(webshareUrl).append("images/mgi_camera.gif\" />");
		sb.append("</a>");
	}
    return sb.toString();
  }
  public String getChr(){
	  return allele.getChromosome();
  }
  public String getSynonyms(){
	  List<String> synonyms = new ArrayList<String>();
	  for(AlleleSynonym synonym : allele.getSynonyms())
	  {
		  synonyms.add(FormatHelper.superscript(synonym.getSynonym()));
	  }
	  return "<span style=\"line-height: 1.4; \">"+StringUtils.join(synonyms,", ")+"</span>";
  }
  public String getCategory(){
	  String category = "<span style=\"white-space: nowrap;\">"+allele.getAlleleType()+"</span>";
	  if(allele.getAlleleSubType()!=null && !allele.getAlleleSubType().equals(""))
	  {
		  category += "<br>("+allele.getAlleleSubType()+")";
	  }
	  if(CELL_LINE.equalsIgnoreCase(allele.getTransmissionType()))
	  {
		  category += "<br><b>(Cell Line)</b>";
	  }
	  else if(CHIMERIC.equalsIgnoreCase(allele.getTransmissionType()))
	  {
		  category += "<br><b>(Chimeric)</b>";
	  }
	  return category;
  }
  public String getSystems(){
	  List<String> systems = new ArrayList<String>();
	  
	  for(AlleleSummarySystem system : allele.getSummarySystems())
	  {
		  if(!NORMAL_PHENOTYPE.equals(system.getSystem())
				  && !PHENOTYPE_NOT_ANALYZED.equals(system.getSystem())) 
		  {
			  systems.add(system.getSystem());
		  }
	  }
	  return StringUtils.join(systems,", ");
  }
  public String getDiseases(){
	  List<String> diseases = new ArrayList<String>();
	  for(AlleleSummaryDisease disease : allele.getSummaryDiseases())
	  {
		  String diseaseDisplay = String.format("<a href=\"%sdisease/%s\">%s</a> %s",fewiUrl,disease.getOmimID(),disease.getDisease(),disease.getOmimID());
		  diseases.add(diseaseDisplay);
	  }
	  return StringUtils.join(diseases,"; ");
  }
}
