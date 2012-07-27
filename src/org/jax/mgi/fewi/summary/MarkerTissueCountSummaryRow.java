package org.jax.mgi.fewi.summary;

import mgi.frontend.datamodel.MarkerTissueCount;

import org.jax.mgi.fewi.config.ContextLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a marker tissue count;  represents on row in summary
 */
public class MarkerTissueCountSummaryRow {

  //-------------------
  // instance variables
  //-------------------

  private Logger logger = LoggerFactory.getLogger(MarkerTissueCountSummaryRow.class);

  // encapsulated row object
  private MarkerTissueCount mtc;

  // config values
  String fewiUrl   = ContextLoader.getConfigBean().getProperty("FEWI_URL");
  String pywiUrl   = ContextLoader.getConfigBean().getProperty("WI_URL");
  String javawiUrl = ContextLoader.getConfigBean().getProperty("JAVAWI_URL");


  //-------------
  // constructors
  //-------------

  // hide the default constructor - we NEED a marker tissue count to wrap
  private MarkerTissueCountSummaryRow () {}

  public MarkerTissueCountSummaryRow (MarkerTissueCount mtc) {
    this.mtc = mtc;
    return;
  }


  //------------------------------------------------------------------------
  // public instance methods;  JSON serializer will call all public methods
  //------------------------------------------------------------------------


  // structure field
  public String getStructure() {
    return "<a href=\"" + pywiUrl + "searches/anatdict.cgi?id=" +mtc.getStructureKey() + "\">" + "TS" + mtc.getStructure() + "</a>";
  }


  // count of ALL tissues
  public String getAll() {
    if (mtc.getAllResultCount() > 0) {
      return "<a href=\"" + pywiUrl + "searches/expression_report.cgi?sort=Anatomical structure&returnType=assay results&_Marker_key=" + mtc.getMarkerKey()+ "&_Structure_key=" + mtc.getStructureKey()+ "\">" + mtc.getAllResultCount() + "</a>";
    }
    else {
      return "0";
    }
  }


  // count of tissues which this gene were detected
  public String getDetected() {
    if (mtc.getDetectedResultCount() > 0) {
      return "<a href=\"" + pywiUrl + "searches/expression_report.cgi?sort=Anatomical%20structure&expressed=1&returnType=assay%20results&_Marker_key=" + mtc.getMarkerKey()+ "&_Structure_key=" + mtc.getStructureKey() + "\">" + mtc.getDetectedResultCount() + "</a>";
    }
    else {
      return "0";
    }
  }


  // count of tissues which this gene were NOT detected
  public String getNotDetected() {
    if (mtc.getNotDetectedResultCount() > 0) {
      return "<a href=\"" + pywiUrl + "searches/expression_report.cgi?sort=Anatomical%20structure&expressed=0&returnType=assay%20results&_Marker_key=" + mtc.getMarkerKey()+ "&_Structure_key=" + mtc.getStructureKey() + "\">" + mtc.getNotDetectedResultCount() + "</a>";
    }
    else {
      return "0";
    }
  }

}
