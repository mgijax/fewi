<!-- all imports are declared in the wrapping jsp either assay_gel_detail or assay_insitu_detail -->
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${assay.markerSymbol} ${assay.assayType} Gene Expression Assay - GXD</title>

<meta name="description" content="View ${assay.assayType} gene expression results for ${assay.markerSymbol} with structure, expression level, image, reference."/>
<meta name="keywords" content="MGI, GXD, mouse, mice, murine, Mus musculus, ${assay.markerSymbol}, gene expression, ${assay.assayType}"/> 
<meta name="robots" content="NOODP" />
<meta name="robots" content="NOYDIR" />

<!-- <script type="text/javascript" src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.8.0.min.js"></script> -->

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%
    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1Gxd","detailCat2Gxd");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1Gxd","detailData2Gxd");
%>
<% ExpressionAssay assay = (ExpressionAssay) request.getAttribute("assay"); %>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_query_results_help.shtml#detail">	
	<div id="gxdLogoDiv">
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
	</div>
	<div id="gxdCenteredTitle">
	  <span class="titleBarMainTitleGxd">Gene Expression Data</span><br/>
	  Assay Details
	</div>
    <div id="headerRightGxd">
	<div class="yourInputButton">
		<form name="YourInputForm">
			<input class="searchToolButton" value="Your Input Welcome" name="yourInputButton" onclick='window.open("${configBean.MGIHOME_URL}feedback/feedback_form.cgi?accID=${assay.primaryID}")' onmouseover="return overlib('We welcome your corrections and new data. Click here to contact us.', LEFT, WIDTH, 200, TIMEOUT, 2000);" onmouseout="nd();" type="button">
		</form>
	</div>
    </div>
</div>



<!-- structural table -->
<style type="text/css">
.assayDetailsRow {
	padding-left: 8px;
	padding-top: 2px;
	padding-bottom: 2px;
	width: 100%;
}
.assayDetailsInfoType {
  font-weight:bolder;
}
}
.assayData {
	width: 100%; 
}
.assayData td{
	padding: 4px;
}
#assayResultTable {
  padding:4px; 
  border: 2px solid black;
}
#assayResultTable th{
  padding:5px;
  padding-left:8px;
  border: 1px solid gray;
}
#assayResultTable td{
  padding-left:6px;
  padding:4px; 
  border: 1px solid gray;
}
 .copySymbol
 {
 	font-size:110%;
 	font-weight:bold;
 	text-decoration:none !important;
 	padding-left:8px;
 }
  .imagePaneWrapper
 {
 	display:inline-block;
 	padding:4px;
 }
 .specDivider {
  width: 98%;
  color: #3399CC;}
 .specimenOpen,.specimenClose {cursor:pointer;}
 .header {background-color:#E0E0E0;font-weight:bold;}
</style>

<table class="detailStructureTable">

<!-- --------------------- Assay Details --------------------- -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Assay 
    </td>
    <td class="<%=rightTdStyles.getNext() %>">

    <table class='assayData'>
      <tr>
       <td class='rightBorderThinGray' align='right' width='1%' nowrap='nowrap'>
         <span class='label'>
           Reference: 
         </span>
       </td>
       <td>
         <span class='' id='assayReference'>
           <a href='${configBean.FEWI_URL}reference/${reference.jnumID}'>
           ${reference.jnumID}</a> 
           ${reference.shortCitation}
         </span>
       </td>
      </tr>
      <tr>
       <td class='rightBorderThinGray' align='right' nowrap='nowrap'>
         <span class='label'>
           Assay type: 
         </span>
       </td>
       <td>
         <span class='' id='assayType'>
           ${assay.assayType}
         </span>
       </td>
      </tr>
      <tr>
       <td class='rightBorderThinGray' align='right' nowrap='nowrap'>
         <span class='label'>
           MGI Accession ID: 
         </span>
       </td>
       <td>
         <span class='' id='assayID'>
           ${assay.primaryID}
         </span>
       </td>
      </tr>
      <tr>
       <td class='rightBorderThinGray' align='right' nowrap='nowrap'>
         <span class='label'>
           Gene symbol: 
         </span>
       </td>
       <td>
         <span class='' id='assayMarkerSymbol'>
           <a href="${configBean.FEWI_URL}marker/${assay.markerID}">${assay.markerSymbol}</a>
         </span>
       </td>
      </tr>
      <tr>
       <td class='rightBorderThinGray' align='right' nowrap='nowrap'>
         <span class='label'>
           Gene name: 
         </span>
       </td>
       <td>
         <span class='' id='assayMarkerName'>
           ${assay.markerName} </span>
         </span>
       </td>
      </tr>
    </table>
     

    <!-- --------------------- Probe Details --------------------- -->

    <div style='height:1px; margin-top:4px; margin-bottom:2px; background-color:#000000;'></div>

    <div style='background-color:#F0F0F0;'>
    <table class='assayData'>

      <c:if test="${not empty assay.reporterGene}">
      <tr>
       <td class='rightBorderThinGray' align='right' width='1%' nowrap='nowrap'>
         <span class='label'>
           Reporter: 
         </span>
       </td>
       <td>
         <span class='' id='assayReporterGene'>
           ${assay.reporterGene}
         </span>
       </td>
      </tr>
      </c:if>

      <c:if test="${assay.isDirectDetection == '1' }">
      <tr>
       <td class='rightBorderThinGray' align='right' nowrap='nowrap'>
       </td>
       <td>
         <span class=''>Direct Detection </span>
       </td>
      </tr>
      </c:if>

      <c:if test="${not empty assay.probeName}">
      <tr>
       <td class='rightBorderThinGray' align='right' nowrap='nowrap'>
         <span class='label'>
           Probe: 
         </span>
       </td>
       <td>
         <span class='' id='assayProbeName'>
           <a href="${configBean.FEWI_URL}probe/${assay.probeID}">
            <%= FormatHelper.superscript(assay.getProbeName()) %> 
           </a>
         </span>
       </td>
      </tr>
      </c:if>

      <c:if test="${not empty assay.probePreparation}">
      <tr>
       <td class='rightBorderThinGray' align='right' nowrap='nowrap'>
         <span class='label'>
           Probe preparation: 
         </span>
       </td>
       <td>
         <span class='' id='assayProbePreparation'>
          ${assay.probePreparation}
         </span>
       </td>
      </tr>
      </c:if>

      <c:if test="${not empty assay.visualizedWith}">
      <tr>
       <td class='rightBorderThinGray' align='right' nowrap='nowrap'>
         <span class='label'>
           Visualized with: 
         </span>
       </td>
       <td>
         <span class='' id='assayVisualize'>
          ${assay.visualizedWith}
         </span>
       </td>
      </tr>
      </c:if>

      <c:if test="${not empty assay.antibody}">
      <tr>
       <td class='rightBorderThinGray' align='right' nowrap='nowrap'>
         <span class='label'>
           Antibody: 
         </span>
       </td>
       <td>
         <span class='' id='assayAntibody'>
          <a href="${configBean.FEWI_URL}antibody/key/${assay.antibodyKey}">
            <%= FormatHelper.superscript(assay.getAntibody()) %> 
          </a>
         </span>
       </td>
      </tr>
      </c:if>

      <c:if test="${not empty assay.detectionSystem}">
      <tr>
       <td class='rightBorderThinGray' align='right' nowrap='nowrap'>
         <span class='label'>
           Detection system: 
         </span>
       </td>
       <td>
         <span class='' id='assayDetectionSystem'>
           ${assay.detectionSystem} 
         </span>
       </td>
      </tr>
      </c:if>

      <c:if test="${not empty assay.note}">
      <tr>
       <td class='rightBorderThinGray' align='right' nowrap='nowrap'>
         <span class='label'>
           Assay notes: 
         </span>
       </td>
       <td>
         <span class='' id='assayNote'>
           <%= ntc.convertNotes(FormatHelper.formatVerbatim(assay.getNote()), '|') %>
         </span>
       </td>
      </tr>
      </c:if>

    </table>
    </div>

    </td>
  </tr>
