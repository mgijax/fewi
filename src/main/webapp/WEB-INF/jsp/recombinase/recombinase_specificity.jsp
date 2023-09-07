<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page import = "org.jax.mgi.fewi.summary.RecomImage" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${allele.symbol} - ${systemDisplayStr} Recombinase Activity Detail MGI Mouse</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope

  Allele allele = (Allele)request.getAttribute("allele");
  AlleleSystem alleleSystem 
    = (AlleleSystem)request.getAttribute("alleleSystem");

  StyleAlternator leftTdStyles 
    = new StyleAlternator("detailCat1","detailCat2");
  StyleAlternator rightTdStyles 
    = new StyleAlternator("detailData1","detailData2");

  NotesTagConverter ntc = new NotesTagConverter(); 

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
  width:116px;
  height:40px;
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

#alleleInfo td {
  padding-top:2px;
  padding-bottom:2px;
  padding-left:4px;
  padding-right:4px;
}


</style>

<script>

function hide (i)
{
  var elem = document.getElementById(i);
  if (elem == null) { return false; }
  elem.style.display = 'none';
  return true;
}

function show (i)
{
  var elem = document.getElementById(i);
  if (elem == null) { return false; }
  elem.style.display = '';
  return true;
}

</script>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="RECOMBINASE_detail_help.shtml">	
  <span class="titleBarMainTitle">
    <%=FormatHelper.superscript(allele.getSymbol())%> - ${systemDisplayStr}
  </span>
  <br/>
  <span class="titleBarSubTitle">
    Recombinase Activity Detail
  </span>
</div>


<!-- structural table -->
<table class="detailStructureTable">



  <!-- Table of Contents -->
  <tr>
    <td CLASS="data2" COLSPAN="2" ALIGN="center">
      <div style="font-size: 0.9em;height:2em;" id="toc">

        <a href="#alleleDetails" class="MP">Allele Information</a>&nbsp;
        <c:if test="${not empty otherAlleles}">
          | <a href="#tissueInfo" class="MP">Tissue Information</a>&nbsp;
        </c:if>
        <c:if test="${not empty galleryImagesRows}">
          | <a href="#imageGallery" class="MP">Images</a>&nbsp;
        </c:if>
        | <a href="#recombinaseActivity" class="MP">Recombinase Activity</a>&nbsp;
        | <a href="#refSection" class="MP">References</a>
      </div>
    </td>
  </tr>



  <!-- Allele Information -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      <a name="alleleDetails"></A>
      Allele Information
    </td>
    <td class="<%=rightTdStyles.getNext() %>">


    <table id="alleleInfo" border="0" cellpadding="4" cellspacing="0" width="100%" >
    <tr>

      <td class="rightBorderThinGray" align="right" valign="middle" width="1%" nowrap="nowrap">
          <span class='creDataCat'>Allele: </span>
      </td>

      <td>
        <table>
        <tr>

          <td> 
            <a href="${configBean.FEWI_URL}allele/${allele.primaryID}?recomRibbon=open">
              <b><%=FormatHelper.superscript(allele.getSymbol())%></b>
            </a>
            <br>
            <span class="small">
              <c:if test="${allele.geneName!=allele.name}">
                ${allele.geneName};
              </c:if>
              ${allele.name}
            </span>
          </td>

          <td style='padding-left:20px;'>
            <span class="small" style="font-weight:bold">Driver:</span> ${allele.driver}
            <br>
            <span class="small" style="font-weight:bold">Type:</span> ${allele.alleleType}
            <c:if test="${not empty allele.alleleSubType}">(${allele.alleleSubType})</c:if>
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
    
    <c:if test="${not empty allele.collection}">
	  <tr>
	    <td class="rightBorderThinGray" align="right" valign="middle" nowrap="nowrap">
	    	<span class="creDataCat">Collection:</span>
	    </td>
	    <td class="">${allele.collection}</td>
	  </tr>
	</c:if>

    <tr>
      <td class="rightBorderThinGray" align="right" valign="middle" nowrap="nowrap">
        <span class='creDataCat'>Find mice (IMSR): </span>
      </td>
      <td>

        Mouse Strains:
       <c:if test="${empty allele.imsrStrainCount or allele.imsrStrainCount=='0'}">
          0 strains available
       </c:if>
       <c:if test="${not empty allele.imsrStrainCount and allele.imsrStrainCount!='0'}">
          <a href='${configBean.IMSRURL}summary?gaccid=${allele.primaryID}&states=archived&states=embryo&states=live&states=ovaries&states=sperm' 
            target="_blank">
          ${allele.imsrStrainCount} <fewi:plural value="strain" size="${allele.imsrStrainCount}"/> available
          </a>
       </c:if>

        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

        Cell Lines:
       <c:if test="${empty allele.imsrCellLineCount or allele.imsrCellLineCount=='0'}">
          0 lines available
       </c:if>
       <c:if test="${not empty allele.imsrCellLineCount and allele.imsrCellLineCount!='0'}">
          <a href='${configBean.IMSRURL}summary?gaccid=${allele.primaryID}&states=ES+Cell&states=archived' target="_blank">
          ${allele.imsrCellLineCount} <fewi:plural value="line" size="${allele.imsrCellLineCount}"/> available
          </a>
       </c:if>

      </td>
    </tr>

    <c:if test="${not empty otherSystems}">
    <tr>
      <td class="rightBorderThinGray" align="right" valign="middle" width="1%" nowrap="nowrap">
        <span class='creDataCat'>Additional Tissues:</span>
      </td>
      <td>

      <%=FormatHelper.superscript(allele.getSymbol())%> activity also observed in: <br>

      <c:choose>
    
      <c:when test="${otherSystemsSize < '7'}">
        <c:forEach var="oSystem" items="${otherSystems}" varStatus="status">
          <a href="${configBean.FEWI_URL}recombinase/specificity?id=${oSystem.alleleID}&system=<fewi:encode value="${oSystem.otherSystem}"/>">
            ${oSystem.otherSystem}</a><c:if test="${not status.last}">,</c:if>
        </c:forEach>

      </c:when>
      
      <c:when test="${otherSystemsSize > '6'}">
        <div id='systemListDefault'>
          <img src='${configBean.WEBSHARE_URL}images/rightArrow.gif'
            onClick='show("systemListHidden"); hide("systemListDefault");'
            style='cursor: pointer;'>
          <c:forEach var="oSystem" items="${otherSystems}" varStatus="status">
            <c:if test="${status.count < '5' }">
              <a href="${configBean.FEWI_URL}recombinase/specificity?id=${oSystem.alleleID}&system=<fewi:encode value="${oSystem.otherSystem}"/>">
                ${oSystem.otherSystem}</a><c:if test="${status.count < '4'}">,</c:if>
              <c:if test="${status.count == '4' }">
                <span onClick='show("systemListHidden"); hide("systemListDefault");' 
                style='cursor: pointer; color:blue;' class="small">...(more)
                </span>
              </c:if>
            </c:if>
          </c:forEach>
        </div>

        <div id='systemListHidden' style="display:none;">
          <img src='${configBean.WEBSHARE_URL}images/downArrow.gif'
            onClick='show("systemListDefault"); hide("systemListHidden");'
            style='cursor: pointer;'>
          <c:forEach var="oSystem" items="${otherSystems}" varStatus="status">
          <a href="${configBean.FEWI_URL}recombinase/specificity?id=${oSystem.alleleID}&system=<fewi:encode value="${oSystem.otherSystem}"/>">
            ${oSystem.otherSystem}</a><c:if test="${not status.last}">,</c:if>
          </c:forEach>
          <span onClick='show("systemListDefault"); hide("systemListHidden");' 
            style='cursor: pointer; color:blue;' class="small"> (less)
          </span>
        </div>

      </c:when>
    
      <c:otherwise>
        error
      </c:otherwise>
      </c:choose>

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
      <a name="tissueInfo"></A>
      Tissue Information
    </td>
    <td class="<%=rightTdStyles.getNext() %>">

      <table border="0" cellpadding="4" cellspacing="0" width="100%">
      <tbody>
      <tr>
      <td class="rightBorderThinGray" align="right" valign="middle" width="1%" nowrap="nowrap">
        <b>${systemDisplayStr}</b>
      </td>

      <td>
       Other recombinase alleles with activity in ${systemDisplayStr} tissues: <br>

      <c:choose>
    
      <c:when test="${otherAllelesSize < '7'}">
        <c:forEach var="oAllele" items="${otherAlleles}" varStatus="status">
          <% AlleleSystemOtherAllele asoa = (AlleleSystemOtherAllele)pageContext.getAttribute("oAllele"); %>
          <a href="${configBean.FEWI_URL}recombinase/specificity?systemKey=<fewi:encode value="${alleleSystem.system}"/>">
            <%=FormatHelper.superscript(asoa.getOtherAlleleSymbol())%></a><c:if test="${not status.last}">,</c:if>
        </c:forEach>
      </c:when>

      <c:when test="${otherAllelesSize > '6'}">

        <div id='alleleListDefault'>
          <img src='${configBean.WEBSHARE_URL}images/rightArrow.gif'
            onClick='show("alleleListHidden"); hide("alleleListDefault");'
            style='cursor: pointer;'>
          <c:forEach var="oAllele" items="${otherAlleles}" varStatus="status">
            <% AlleleSystemOtherAllele asoa = (AlleleSystemOtherAllele)pageContext.getAttribute("oAllele"); %>
            <c:if test="${status.count < '5' }">
              <a href="${configBean.FEWI_URL}recombinase/specificity?id=${oAllele.otherAlleleID}&system=<fewi:encode value="${alleleSystem.system}"/>">
                <%=FormatHelper.superscript(asoa.getOtherAlleleSymbol())%></a><c:if test="${status.count < '4' }">,</c:if>
                <c:if test="${status.count == '4' }">
                  <span onClick='show("alleleListHidden"); hide("alleleListDefault");' 
                  style='cursor: pointer; color:blue;' class="small">...(more)
                  </span>
                </c:if>
            </c:if>  
          </c:forEach>
        </div>

        <div id='alleleListHidden' style="display:none;">
          <img src='${configBean.WEBSHARE_URL}images/downArrow.gif'
            onClick='show("alleleListDefault"); hide("alleleListHidden");'
            style='cursor: pointer;'>
          <c:forEach var="oAllele" items="${otherAlleles}" varStatus="status">
            <% AlleleSystemOtherAllele asoa = (AlleleSystemOtherAllele)pageContext.getAttribute("oAllele"); %>
            <a href="${configBean.FEWI_URL}recombinase/specificity?id=${oAllele.otherAlleleID}&system=<fewi:encode value="${alleleSystem.system}"/>">
              <%=FormatHelper.superscript(asoa.getOtherAlleleSymbol())%></a><c:if test="${not status.last}">,</c:if>
          </c:forEach>
          <span onClick='show("alleleListDefault"); hide("alleleListHidden");' 
            style='cursor: pointer; color:blue;' class="small"> (less)
          </span>
        </div>

      </c:when>
    
      <c:otherwise>
        error
      </c:otherwise>
      </c:choose>
      </td>

      </tr>
      </tbody>
      </table>

    </td>
  </tr>
  </c:if>



  <!-- Image Gallery -->
  <c:if test="${not empty galleryImagesRows}">

  <tr  valign=top ALIGN=left>
    <td class="<%=leftTdStyles.getNext() %>" >
      <a name="imageGallery"></A>
      Images
    </td>

    <td class="<%=rightTdStyles.getNext() %>" >

      <div class="sectionIntro">
        Drag images to compare to others or to data in the table below. Drag corners to resize images for more detail.
        <span style='' class="resetButton"
          onClick="window.location.reload();">
          Reset Images
        </span>
      </div>


      <% int leftDist = 10; %>

      <c:forEach var="galleryImageRow" items="${galleryImagesRows}" >
      <div style="position:relative; height:${galleryImageRow.rowHeight}px; padding-top:4px; padding-bottom:4px;">

        <c:forEach var="galleryImage" items="${galleryImageRow.recomImages}" >
          <% RecomImage galleryImage = (RecomImage)pageContext.getAttribute("galleryImage"); %>

          <span class='galImageInfoBox' style='position:absolute;left:<%=(leftDist-3)%>px;' >
            ${galleryImage.jnumID}
            <c:if test="${not empty galleryImage.externalLink}">
              <%=ntc.convertNotes(galleryImage.getExternalLink(), '|')%>
              <br/>
            </c:if>
            Fig.&nbsp;${galleryImage.figureLabel}
          </span> 

          <img src='${configBean.PIXELDB_URL}${galleryImage.pixeldbNumericID}' 
            id='creImg${galleryImage.pixeldbNumericID}' 
            ${galleryImage.mouseUp}
            style='position:absolute; top:50px; left:<%=leftDist%>px; z-index:${galleryImage.indexZ};'> 

          <script>
            (function() { 
              var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;  
              var resize = new YAHOO.util.Resize(
                  'creImg${galleryImage.pixeldbNumericID}', 
                  { handles: 'bl,br,tl,tr',  
                    knobHandles: true,  
                    width: '80px',  
                    height: '${galleryImage.modifiedHeight}px',  
                    proxy: true,   
                    ratio: true,   
                    draggable: true,   
                    animate: true,   
                    animateDuration: .75,   
                    animateEasing: YAHOO.util.Easing.backBoth   
                  }
              ); 
            })(); 
          </script> 
          
          <% leftDist = leftDist + 130; %>
        </c:forEach>

        <% leftDist = 10; %>

      </div>
      </c:forEach>

    
      </td>
    </tr>
 
  </c:if>



  <!-- Recombinase activity  -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      <a name="recombinaseActivity"></A>
      Recombinase Activity 
    </td>
    <td class="<%=rightTdStyles.getNext() %>">

      <div style="position: relative; height:52px;">

        <div class="sectionIntro" style="position: absolute; top:3px; left:2px; width:200px;">
          Click heading to re-sort table.
          <img src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif" id="InfoIcon"
             onMouseOver="return overlib('<em>MGI\'s annotations reflect statements made by the authors or data providers.   Notes contain additional information pertaining to the assay result.</em><br /><br /><strong>Structures:</strong><br />Recombinase activity results are associated with structures described in the Mouse Anatomical Dictionary (AD).<br /><br /><strong>Assayed Age:</strong><br />Age of the specimen when assayed.  Because recombinase activity is maintained throughout the cell lineage, expression may have occurred at an earlier age.<br /><br /><strong>Levels:</strong><br />Absent, Present*, Ambiguous, Trace, Weak, Moderate, Strong, Very Strong<br /><em>*\'Present\' is used when the author does not describe expression level explicitly.</em><br /><br /><strong>Patterns:</strong><br />Homogeneous, Non-Homogeneous, Diffuse, Graded, Patchy, Regionally Restricted, Scattered, Single cells, Spotted, Ubiquitous, Widespread, Not Specified, Not Applicable', WIDTH, 350, DELAY, 500, CAPTION, 'Structures, Ages, Levels, and Patterns', ANCHOR, 'InfoIcon', ANCHORALIGN, 'UR', 'UL', STICKY, CLOSECLICK, CLOSETEXT, 'close X');" onMouseOut="nd();">
        </div>

        <span id="showAssayInfoButton"
          class="creResultButton creActiveButton"
          onMouseOver="return overlib('Show columns containing the assay type, detection method, reporter gene, probe or antibody, and notes about the assay.', WIDTH, 225, DELAY, 750, ANCHOR, 'showAssayInfoButton', ANCHORALIGN, 0,0,0,1.05, STICKY, TIMEOUT, 3000, FGCOLOR, '#FFFFFF');"
          onMouseOut="nd();"
          style="position: absolute; top:24px; left:574px">
          Assays
        </span>

        <span id="showGenoTypeButton"
          class="creResultButton"
          onMouseOver="return overlib('Show columns containing the allelic composition, genetic strain background, sex, and notes about the specimen', WIDTH, 225, DELAY, 750, ANCHOR, 'showGenoTypeButton', ANCHORALIGN, 0,0,0,1.05, STICKY, TIMEOUT, 3000, FGCOLOR, '#FFFFFF');"
          onMouseOut="nd();"
          style="position: absolute; top:24px; left:648px">
          Genotypic Background
        </span>

        <span id="showResultNotesButton"
          class="creResultButton"
          onMouseOver="return overlib('Show a column containing additional information recorded by MGI curators about the assay result.', WIDTH, 225, DELAY, 750, ANCHOR, 'showResultNotesButton', ANCHORALIGN, 0,0,0,1.05, STICKY, TIMEOUT, 3000, FGCOLOR, '#FFFFFF');"
          onMouseOut="nd();"
          style="position: absolute; top:24px; left:824px">
          Result Notes
        </span>

        <span id="paginationTop" style="position: absolute; top:-2px; left:260px">&nbsp;</span>

      </div>

      <!-- data table div: filled by YUI, called via js below -->
      <div id="dynamicdata"></div>

      <!-- including this file will start the data injection -->
      <script type="text/javascript">
        <%@ include file="/js/recombinase/recombinase_specificity_summary.js" %>
      </script>
      
    </td>
  </tr>


  <!-- Referencecs  -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      <a name="refSection"></A>
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
          <a href="${configBean.FEWI_URL}reference/allele/${allele.primaryID}?typeFilter=Literature">
            ${allele.countOfReferences} reference(s)
          </a>
        </td>
      </tr>

      </tbody>
      </table>

    </td>
  </tr>





<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
