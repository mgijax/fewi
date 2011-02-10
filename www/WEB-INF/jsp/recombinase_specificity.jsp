<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

<title>${allele.symbol} - ${systemDisplayStr} Recombinase Specificity Detail MGI Mouse</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope

    Allele allele = (Allele)request.getAttribute("allele");
    AlleleSystem alleleSystem 
      = (AlleleSystem)request.getAttribute("alleleSystem");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");
%>

<style type="text/css">

a {
  text-decoration: none;
}
.creDataCat {
  font-size:12px;
  font-weight:bold;
}
.creResultButton {
  font-family:Verdana, Arial,Helvetica;
  color:#002255;
  font-weight:bolder;
  background-color:#eeeeee;
  border: 1px #7D95B9 solid;
  padding-top:5px;
  padding-bottom:5px;
  padding-left:8px;
  padding-right:8px;
  cursor: pointer;
}
.creActiveButton {
  background-color:#aaaaaa;
}
.summaryHeaderCell {
  position:relative;
  font-size:11px;
}
.summaryDataCell {
  position:relative;
  left:-5px;
  top:-5px;
  font-size:10px;
  white-space: wrap;
  padding-right:1px;
}
.galImageInfoBox {
  font-size:9px;
  color:#002255;
  background-color:#eeeeee;
  border: 1px #7D95B9 solid;
  cursor: pointer;
  width:86px;
  height:25px;
}
.galImageInfoBoxSelected {
  font-size:9px;
  color:#002255;
  background-color:#cccccc;
  border: 2px #7D95B9 solid;
  cursor: pointer;
  width:86px;
  height:25px;
}
.selectedImage {
  border: 2px dashed #F00;
}
.sectionIntro {
  font-size:10px;
  font-style:italic;
  padding-top:2px;
  padding-bottom:4px;
  padding-left:4px;
  color:#222222;
}
.resetButton {
  font-size: 10px;
  font-family: Verdana,Arial,Helvetica;
  color: #002255;
  font-weight: bold;
  background-color: #eeeeee;
  border: 1px #7D95B9 solid;
  padding: 1px;
  cursor: pointer;
}


</style>

<script>
</script>


${templateBean.templateBodyStartHtml}


<!-- iframe for history manager's use -->
<iframe id="yui-history-iframe" src="/fewi/js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="TODO.shtml">	
  <span class="titleBarMainTitle">
    <%=FormatHelper.superscript(allele.getSymbol())%> - ${systemDisplayStr}
  </span>
  <br/>
  <span class="titleBarSubTitle">
    Recombinase Specificity Detail
  </span>
</div>


<!-- structural table -->
<table class="detailStructureTable">


  <!-- Allele Information -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Tissue Information
    </td>
    <td class="<%=rightTdStyles.getNext() %>">


    <table border="0" cellpadding="4" cellspacing="0" width="100%">
    <tr>

      <td class="rightBorderThinGray" align="right" valign="middle" width="1%" nowrap="nowrap">
          <span class='creDataCat'>Allele: </span>
      </td>

      <td>
        <table>
        <tr>

          <td>
            <a href="${configBean.FEWI_URL}allele/${allele.primaryID}">
              <b><%=FormatHelper.superscript(allele.getSymbol())%></b>
            </a>
            <br>
            <span class="small">

              <!-- TODO Marker Name - display if it's not the same as allele name -->
              ${allele.name}
            </span>
          </td>

          <td style='padding-left:20px;'>
            <span class="small" style="font-weight:bold">Driver:</span> ${allele.driver}
            <br>
            <span class="small" style="font-weight:bold">Type:</span> ${allele.alleleType}
          </td>

        </tr>
        </table>
      </td>
    </tr>

    <c:if test="${not empty allele.inducibleNote}">
    <tr>
      <td class="rightBorderThinGray" align="right" valign="middle" width="1%" nowrap="nowrap">
        <span class='creDataCat'>Inducer: </span>
      </td>
      <td>
        ${allele.inducibleNote}
      </td>

    </tr>
    </c:if>

    <tr>
      <td class="rightBorderThinGray" align="right" valign="middle" nowrap="nowrap">
        <span class='creDataCat'>Synonym: </span>
      </td>
      <td>
        <span class='small'>
          ${synonymsString}
        </span>
      </td>
    </tr>

    <tr>
      <td class="rightBorderThinGray" align="right" valign="middle" nowrap="nowrap">
        <span class='creDataCat'>Molecular description: </span>
      </td>
      <td>
        <span class='small'>
          ${allele.molecularDescription}
        </span>
      </td>
    </tr>

    <tr>
      <td class="rightBorderThinGray" align="right" valign="middle" nowrap="nowrap">
        <span class='creDataCat'>Find mice (IMSR): </span>
      </td>
      <td>

        Mouse Strains:
       <c:if test="${empty allele.imsrStrainCount}">
          0 lines available
       </c:if>
       <c:if test="${not empty allele.imsrStrainCount}">
          ${allele.imsrStrainCount} lines available
       </c:if>

        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

        Cell Lines:
       <c:if test="${empty allele.imsrCellLineCount}">
          0 lines available
       </c:if>
       <c:if test="${not empty allele.imsrCellLineCount}">
          ${allele.imsrCellLineCount} lines available
       </c:if>

      </td>
    </tr>


    <c:if test="${not empty otherSystems}">
    <tr>
      <td class="rightBorderThinGray" align="right" valign="middle" width="1%" nowrap="nowrap">
        <span class='creDataCat'>Additional Tissues:</span>
      </td>
      <td>
        <c:forEach var="oSystem" items="${otherSystems}" >

          <a href="${configBean.FEWI_URL}recombinase/specificity?id=${oSystem.alleleID}&systemKey=${oSystem.otherSystemKey}">
            ${oSystem.otherSystem}
          </a>
          
        </c:forEach>
      </td>

    </tr>
    </c:if>







    </table>

      
    </td>
  </tr>


  <!-- Tissue Information -->
  <c:if test="${not empty otherAlleles}">
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Tissue Information
    </td>
    <td class="<%=rightTdStyles.getNext() %>">



        <c:forEach var="oAllele" items="${otherAlleles}" >

          <a href="${configBean.FEWI_URL}recombinase/specificity?id=${oAllele.otherAlleleID}&systemKey=${oAllele.systemKey}">
            ${oAllele.otherAlleleID}
          </a>
          
        </c:forEach>



      
    </td>
  </tr>
  </c:if>


  <!-- Image Gallery -->
  <c:if test="${not empty galleryImages}">

    <tr  valign=top ALIGN=left>
      <td class="<%=leftTdStyles.getNext() %>" >
        Images
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >

        <c:forEach var="galleryImage" items="${galleryImages}" >
          <br/>
        </c:forEach>
    
      </td>
    </tr>
 
  </c:if>





  <!-- Recombinase Specificity  -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Recombinase Specificity 
    </td>
    <td class="<%=rightTdStyles.getNext() %>">

      <div style="position: relative; height:40px;">

        <div class="sectionIntro" style="position: absolute; top:3px; left:2px; width:200px;">
          Click heading to resort table.
          <img src="" id="InfoIcon"
             onMouseOver="return overlib('<em>MGI\'s annotations reflect statements made by the authors or data providers.   Notes contain additional information pertaining to the assay result.</em><br /><br /><strong>Structures:</strong><br />Recombinase specificity results are associated with structures described in the Mouse Anatomical Dictionary (AD).<br /><br /><strong>Assayed Age:</strong><br />Age of the specimen when assayed.  Because recombinase activity is maintained throughout the cell lineage, expression may have occurred at an earlier age.<br /><br /><strong>Levels:</strong><br />Absent, Present*, Ambiguous, Trace, Weak, Moderate, Strong, Very Strong<br /><em>*\'Present\' is used when the author does not describe expression level explicitly.</em><br /><br /><strong>Patterns:</strong><br />Homogeneous, Non-Homogeneous, Diffuse, Graded, Patchy, Regionally Restricted, Scattered, Single cells, Spotted, Ubiquitous, Widespread, Not Specified, Not Applicable', WIDTH, 350, DELAY, 500, CAPTION, 'Structures, Ages, Levels, and Patterns', ANCHOR, 'InfoIcon', ANCHORALIGN, 'UR', 'UL', STICKY, CLOSECLICK, CLOSETEXT, 'close X');" onMouseOut="nd();">
        </div>

        <span id="showAssayInfoButton"
          class="creResultButton creActiveButton"
          onMouseOver="return overlib('Show columns containing the assay type, detection method, reporter gene, probe or antibody, and notes about the assay.', WIDTH, 225, DELAY, 750, ANCHOR, 'showAssayInfoButton', ANCHORALIGN, 0,0,0,1.05, STICKY, TIMEOUT, 3000, FGCOLOR, '#FFFFFF');"
          onMouseOut="nd();"
          style="position: absolute; top:20px; left:534px">
          Assays
        </span>

        <span id="showGenoTypeButton"
          class="creResultButton"
          onMouseOver="return overlib('Show columns containing the allelic composition, genetic strain background, sex, and notes about the specimen', WIDTH, 225, DELAY, 750, ANCHOR, 'showGenoTypeButton', ANCHORALIGN, 0,0,0,1.05, STICKY, TIMEOUT, 3000, FGCOLOR, '#FFFFFF');"
          onMouseOut="nd();"
          style="position: absolute; top:20px; left:608px">
          Genotypic Background
        </span>

        <span id="showResultNotesButton"
          class="creResultButton"
          onMouseOver="return overlib('Show a column containing additional information recorded by MGI curators about the assay result.', WIDTH, 225, DELAY, 750, ANCHOR, 'showResultNotesButton', ANCHORALIGN, 0,0,0,1.05, STICKY, TIMEOUT, 3000, FGCOLOR, '#FFFFFF');"
          onMouseOut="nd();"
          style="position: absolute; top:20px; left:784px">
          Result Notes
        </span>

      </div>


      <!-- data table div: filled by YUI, called via js below -->
      <div id="dynamicdata"></div>

      <!-- including this file will start the data injection -->
      <script type="text/javascript">
        <%@ include file="/js/recombinase_specificity_summary.js" %>
      </script>
      
    </td>
  </tr>


  <!-- Referencecs  -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
       References
    </td>
    <td class="<%=rightTdStyles.getNext() %>">

      <table border="0" cellpadding="4" cellspacing="0">
      <tbody>

      <tr>
        <td class="rightBorderThinGray" align="right" valign="middle" nowrap="nowrap">
          <span class='creDataCat'>All for this allele: </span>
        </td>
        <td>
          <a href="${configBean.FEWI_URL}reference/${allele.primaryID}">
            ${allele.countOfReferences}
          </a>
        </td>
      </tr>

      </tbody>
      </table>

    </td>
  </tr>





<!-- close structural table and page template-->
</table>
${templateBean.templateBodyStopHtml}
