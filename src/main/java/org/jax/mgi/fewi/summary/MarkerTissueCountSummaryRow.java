package org.jax.mgi.fewi.summary;

import org.jax.mgi.fe.datamodel.MarkerTissueCount;
import org.jax.mgi.fewi.config.ContextLoader;


/**
 * wrapper around a marker tissue count;  represents on row in summary
 */
public class MarkerTissueCountSummaryRow {

  //-------------------
  // instance variables
  //-------------------

  // encapsulated row object
  private MarkerTissueCount mtc;

  // config values
  String fewiUrl   = ContextLoader.getConfigBean().getProperty("FEWI_URL");
  String pywiUrl   = ContextLoader.getConfigBean().getProperty("WI_URL");


  //-------------
  // constructors
  //-------------

  public MarkerTissueCountSummaryRow (MarkerTissueCount mtc) {
    this.mtc = mtc;
    return;
  }


  //------------------------------------------------------------------------
  // public instance methods;  JSON serializer will call all public methods
  //------------------------------------------------------------------------


  // structure field
  public String getStructure() {
    return "<a href=\"" + fewiUrl + "vocab/gxd/anatomy/" +mtc.getStructureTerm().getPrimaryId() + "\">" + mtc.getStructure() + "</a>";
  }


  // count of ALL tissues
  public String getAll() {
    if (mtc.getAllResultCount() > 0) {
      //return "<a href=\"" + pywiUrl + "searches/expression_report.cgi?sort=Anatomical structure&returnType=assay results&_Marker_key=" + mtc.getMarkerKey()+ "&_Structure_key=" + mtc.getStructureKey()+ "\">" + mtc.getAllResultCount() + "</a>";
      return "<a href=\""+fewiUrl+"gxd/summary?markerMgiId="+mtc.getMarkerId()+"&annotatedStructureKey="+mtc.getStructureKey()+"\">"+mtc.getAllResultCount() +"</a>";
    }
    else {
      return "0";
    }
  }


  // count of tissues which this gene were detected
  public String getDetected() {
    if (mtc.getDetectedResultCount() > 0) {
      //return "<a href=\"" + pywiUrl + "searches/expression_report.cgi?sort=Anatomical%20structure&expressed=1&returnType=assay%20results&_Marker_key=" + mtc.getMarkerKey()+ "&_Structure_key=" + mtc.getStructureKey() + "\">" + mtc.getDetectedResultCount() + "</a>";
      return "<a href=\""+fewiUrl+"gxd/summary?markerMgiId="+mtc.getMarkerId()+"&annotatedStructureKey="+mtc.getStructureKey()+"&detected=Yes\">"+mtc.getDetectedResultCount() +"</a>";
    }
    else {
      return "0";
    }
  }


  // count of tissues which this gene were NOT detected
  public String getNotDetected() {
    if (mtc.getNotDetectedResultCount() > 0) {
      //return "<a href=\"" + pywiUrl + "searches/expression_report.cgi?sort=Anatomical%20structure&expressed=0&returnType=assay%20results&_Marker_key=" + mtc.getMarkerKey()+ "&_Structure_key=" + mtc.getStructureKey() + "\">" + mtc.getNotDetectedResultCount() + "</a>";
      return "<a href=\""+fewiUrl+"gxd/summary?markerMgiId="+mtc.getMarkerId()+"&annotatedStructureKey="+mtc.getStructureKey()+"&detected=Explicit-No\">"+mtc.getNotDetectedResultCount() +"</a>";
    }
    else {
      return "0";
    }
  }

}
