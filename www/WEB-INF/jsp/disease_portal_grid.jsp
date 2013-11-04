<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.springframework.web.util.UriUtils" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page import = "org.jax.mgi.fewi.controller.DiseasePortalController" %>
<%@ page import = "org.jax.mgi.fewi.searchUtil.entities.SolrDpGridCluster.SolrDpGridClusterMarker" %>
<%@ page trimDirectiveWhitespaces="true" %>

<%
	String queryString = (String) request.getAttribute("queryString");
	// need to url encode the querystring
	request.setAttribute("encodedQueryString", UriUtils.encodeQuery(queryString,"UTF-8"));
%>

<style type="text/css">
  #hdpGridTableWrap
  {
  	display: table;
  	border: 2px solid #AAA; 
  	background-color:#FFF; 
  }

  #hdpGridTable 
  { 
  	display: table-cell;
  	border-spacing:0px; 
  	border-collapse:collapse; 
  }
  #hdpGridTable a { text-decoration: none; }
  
  #hdpGridTablePadder{ display: table-cell; }
  #hdpGridTablePadder img { visibility: hidden; }
   
  #hdpGridTable th {border: 1px solid #AAA; border-top: none; padding: 4px 3px; vertical-align:bottom;}
  #hdpGridTable th > div { width:20px; overflow: visible; }
  #hdpGridTable td {border: 1px solid #DDD; padding: 2px 1px 0px 3px; height: 20px; }
  #hdpGridTable tr .dc {border-left-color: #BFE4FF; border-right-color: #BFE4FF; }
  #hdpGridTable tr .vb .dc { border-right-color: #BFE4FF; }
  #hdpGridTable .vb { vertical-align: bottom; border-left: none; border-right: none;}
  #hdpGridTable .cc 
  {
  	color: #000;
	margin-right: 8px;
	margin-top: 6px;
	font-size: 10px;
	font-weight: bold;
	padding: 0px;
  }
  #hdpGridTable .gridCellLink
  {
  	cursor: pointer;
  	width: 100%;
  	height: 100%;
  }
  #hdpGridTable .mk
  {
  	padding-top: 6px;
  	padding-left: 6px;
  }
  
  #hdpGridTable .vb .partialRight
  { 
  	position: absolute;
	bottom: -4px;
	width: 23px;
	height: 14px;
	border-right: 1px solid #AAA;
  }
  #hdpGridTable .vb .partialDoubleRight
  { 
  	position: absolute;
	bottom: -4px;
	width: 19px;
	height: 14px;
	border-right: 8px solid #DDD;
  }
  #hdpGridTable .rightBorder { border-right: 1px solid #DDD;}
  #hdpGridTable .rightDoubleBorder { border-right: 8px solid #DDD;}
  .diseaseButton
  {
  	width:10px;
  	height:10px;
  	background-color: blue;
  	cursor:pointer;
  	border: solid 1px black;
  }
  .row1 
  { 
  	border-top: 2px solid #DDD;
  }
  .gridCheck
  {
  	cursor: pointer;
  }
  .gridCheck:hover { background-color: #FFFFCC;}
  #hdpGridTable tr .rcb { width: 18px; }
  
  .gridHl
  {
  	background-color: #FFFFCC;
  }
  
  /* #filterButtons
  {
  	position: absolute;
	top: -60px;
	right: -150px;
  } */
  button { cursor: pointer; }
  
  #hdpGridTable .tooltip 
  {
	border: 1px solid #808080;
	background-color: #FFFFCA;
	font-size: 90%;
	position: absolute;
	top: 8px;
	left: -50px;
	white-space: nowrap;
	padding: 2px;
  }
  
  #hdpGridTable .mpBin_1 { background-color: #C6D6E8; }
  #hdpGridTable .mpBin_2 { background-color: #879EBA; }
  #hdpGridTable .mpBin_3 { background-color: #49648B; }
  #hdpGridTable .mpBin_4 { background-color: #002255; }
  
  #hdpGridTable .dMBin_0 {}
  #hdpGridTable .dMBin_1 { background-color: #C6D6E8; }
  #hdpGridTable .dMBin_2 { background-color: #879EBA; }
  #hdpGridTable .dMBin_3 { background-color: #49648B; }
  #hdpGridTable .dMBin_4 { background-color: #002255; }
  
  
  #hdpGridTable .dHBin_0 {}
  #hdpGridTable .dHBin_1 { background: url('${configBean.FEWI_URL}assets/images/hdp/human_cell_sprite.gif') 0px 0px; }
  #hdpGridTable .dHBin_2 { background: url('${configBean.FEWI_URL}assets/images/hdp/human_cell_sprite.gif') 0px -44px; }
  #hdpGridTable .dHBin_3 { background: url('${configBean.FEWI_URL}assets/images/hdp/human_cell_sprite.gif') 0px -90px; }
  #hdpGridTable .dHBin_4 { background: url('${configBean.FEWI_URL}assets/images/hdp/human_cell_sprite.gif') 0px -134px; }
  #hdpGridTable .dHBin_5 { background-color: #ffdab3; }
  #hdpGridTable .dHBin_6 { background-color: #f7be76; }
  #hdpGridTable .dHBin_7 { background-color: #fba039; }
  #hdpGridTable .dHBin_8 { background-color: #ff8700; }
</style>

<div id="hdpGridTableWrap">
	<c:if test="${empty gridClusters}">No genes found that match your search criteria.
		<br/>
		<button class="hide" id="filterReset">Remove Filters</button>
	</c:if>
	<c:if test="${not empty gridClusters}">
	<!-- Grid Table -->
	<table id="hdpGridTable">
	
	<!-- Row of Column Headers -->
	<tr>
	  <th rowspan="2" style="white-space: nowrap;">Human Marker</th>
	  <th rowspan="2">
	  		<div style="position:relative; padding-top: 20px; width:100%; text-align: right;">
		  		<div id="filterButtons"><button id="filterSubmit">Submit Filters</button>
		  	</div></div>
		  	<br/>Mouse Marker
	  	</th>
	  <th style="border-right: none;"></th>
		<!--  mp header columns -->
		<c:set var="lastColImage" />
		<c:forEach var="mpHeaderCol" items="${mpHeaderColumns}" varStatus="status">
			<th class="vb"><div><c:out value="${mpHeaderCol}" escapeXml="false" /></div>
			<div style="position:relative;">
			<div class="<c:out value="${status.last && not empty diseaseColumns ? 'partialDoubleRight': 'partialRight'}"/>"></div>
			</div></th>
			<c:if test="${status.last}"><c:set var="lastColImage" value="${mpHeaderCol}"/></c:if>
	    </c:forEach>
		<!-- disease columns -->
		<c:forEach var="diseaseCol" items="${diseaseColumns}" varStatus="status">
			<th class="vb"><div><c:out value="${diseaseCol}" escapeXml="false" /></div>
			<div style="position:relative;"><div class="dc partialRight"></div></div></th>
			<c:if test="${status.last}"><c:set var="lastColImage" value="${diseaseCol}"/></c:if>
	    </c:forEach>
	</tr>
	<tr>
	<th style="background-color:#DDE;">F</th>
	<c:forEach var="mpHeader" items="${mpHeaders}" varStatus="status">
		<th <c:if test="${status.last && not empty diseaseColumns}">class="rightDoubleBorder"</c:if>>
			<input class="gridCheck" type="checkbox" filter="fHeader" value="${mpHeader}" colid="mp_${status.count}" />
		</th>		
	</c:forEach>
	<c:forEach var="diseaseName" items="${diseaseNames}" varStatus="status">
		<th class="dc"><input class="gridCheck" type="checkbox" filter="fHeader" value="${diseaseName}" colid="d_${status.count}"/></th>
	</c:forEach>
	</tr>
	
	<c:forEach var="gridCluster" items="${gridClusters}" varStatus="gcStatus">
	  <tr class="${gcStatus.index % 2 == 0 ? 'row1' : 'row2'}">
	    <td>
	      <c:forEach var="humanSymbol" items="${gridCluster.humanSymbols}" varStatus="status">
		      <% String humanSymbol = (String) pageContext.getAttribute("humanSymbol"); %>
		      <c:if test="${ not empty gridCluster.homologeneId}"><a href="${configBean.FEWI_URL}homology/${gridCluster.homologeneId}"><%= FormatHelper.superscript(humanSymbol) %></a></c:if>
		      <c:if test="${ empty gridCluster.homologeneId}"><%= FormatHelper.superscript(humanSymbol) %></c:if>
	        <c:if test="${!status.last}">, </c:if>
	      </c:forEach>
	    </td>
	    <td>
	      <c:forEach var="mouseMarker" items="${gridCluster.mouseMarkers}" varStatus="status">
	      	  <% SolrDpGridClusterMarker mouseMarker = (SolrDpGridClusterMarker) pageContext.getAttribute("mouseMarker"); %>
		      <a href="${configBean.FEWI_URL}marker/key/${mouseMarker.markerKey}"><%= FormatHelper.superscript(mouseMarker.getSymbol()) %></a>
	        <c:if test="${!status.last}">, </c:if>
	      </c:forEach>
	    </td>
		<td class="rcb"><input class="gridCheck" type="checkbox" filter="fGene" value="${gridCluster.gridClusterKey}"/></td>   
        <c:forEach var="mpHeader" items="${gridCluster.mpHeaderCells}" varStatus="status"><c:if test="${not mpHeader.hasPopup}"><td class="mp_${status.count} <c:if test="${status.last && not empty gridCluster.diseaseCells}"> rightDoubleBorder </c:if>"></td></c:if>
        	<c:if test="${mpHeader.hasPopup}"><td class="mp_${status.count} <c:if test="${status.last && not empty gridCluster.diseaseCells}"> rightDoubleBorder </c:if> cc mpBin_${mpHeader.mpBin}" onClick="javascript:popupGenotypeSystem ('${configBean.FEWI_URL}diseasePortal/gridSystemCell?${encodedQueryString}&gridClusterKey=${gridCluster.gridClusterKey}&termHeader=${mpHeader.term}', '${gridCluster.gridClusterKey}', '${mpHeader.term}'); return false;">
        		<div class="gridCellLink"><div class="mk">${mpHeader.mpMark}</div></div><div style="position: relative;"><div class="hide tooltip">Gene(s): <b>${gridCluster.title}</b><br/>Phenotype: <b>${mpHeader.term}</b></div></div> </td></c:if></c:forEach>
        <c:forEach var="disease" items="${gridCluster.diseaseCells}" varStatus="status"><c:if test="${not disease.hasPopup}"><td class="dc d_${status.count} <c:if test="${status.last}"> rightBorder </c:if>"></td></c:if>
	        <c:if test="${disease.hasPopup}"><td class="dc d_${status.count} cc <c:if test="${status.last}"> rightBorder </c:if> dMBin_${disease.diMouseBin}" onClick="javascript:popupGenotypeSystem ('${configBean.FEWI_URL}diseasePortal/gridDiseaseCell?${encodedQueryString}&gridClusterKey=${gridCluster.gridClusterKey}&termHeader=${disease.term}', '${gridCluster.gridClusterKey}', '${disease.term}'); return false;">
	            <div class="dHBin_${disease.diHumanBin} gridCellLink"><div class="mk">${disease.diseaseMark}</div></div><div style="position: relative;"><div class="hide tooltip">Gene(s): <b>${gridCluster.title}</b><br/>Disease: <b>${disease.term}</b></div></div> </td></c:if></c:forEach>
	  </tr>
	</c:forEach>
	
	</table>
	<div id="hdpGridTablePadder"><c:out value="${lastColImage}" escapeXml="false" /></div>
	</c:if>
</div>



<!-- JavaScript -->
<script type="text/javascript">

// specific to genotype/system popups 
var popupNextX = 0;   // x position of top-left corner of next popup
var popupNextY = 0;   // y position of top-left corner of next popup

// pop up a new window for the cluster/system
function popupGenotypeSystem (url, gridClusterKey, termId)
{
  // unique window name
  var windowName;
  windowName = "genoSystemPopup_" + gridClusterKey + "_" + termId;

  // open the window small but scrollable and resizable
  var child = window.open (url, windowName,
      'width=800,height=600,resizable=yes,scrollbars=yes,alwaysRaised=yes');

  // move the new window and bring it to the front
  child.moveTo (popupNextX, popupNextY);
  child.focus();

  // set the position for the next new window (at position 400,400 we will
  // start over at 0,0)
  if (popupNextX >= 400) {
      popupNextX = 0;
      popupNextY = 0;
  }
  else {
      popupNextX = popupNextX + 20;
      popupNextY = popupNextY + 20;
  }
  return;
}

// pop up a new window for the cluster/system
function popupGenotypeDisease (url, gridClusterKey, termId)
{
  // unique window name
  var windowName;
  windowName = "genoDiseasePopup_" + gridClusterKey + "_" + termId;

  // open the window small but scrollable and resizable
  var child = window.open (url, windowName,
      'width=800,height=600,resizable=yes,scrollbars=yes,alwaysRaised=yes');

  // move the new window and bring it to the front
  child.moveTo (popupNextX, popupNextY);
  child.focus();

  // set the position for the next new window (at position 400,400 we will
  // start over at 0,0)
  if (popupNextX >= 400) {
      popupNextX = 0;
      popupNextY = 0;
  }
  else {
      popupNextX = popupNextX + 20;
      popupNextY = popupNextY + 20;
  }
  return;
}

// add the click handler (from disease_portal_summary.js) to the submit button
$("#filterSubmit").click(gridFilterSubmitClick);
/* if(isGFiltersSet())
{
	//$("#filterSubmit").hide();
	$("#filterReset").show();
} */
// add the click handler (from disease_portal_summary.js) to the gridCheck filters
$(".gridCheck").click(gridCheckClick);

// reselect any filters that may be active
if(_GF)
{
	_GF.reselectActiveBoxes();
}

// add the tooltip on hover (in,out) event handlers
$(".gridCellLink").hover(function(e){
	var tdDiv = $(this);
	tdDiv.siblings().find(".tooltip").show();
},function(e){
	var tdDiv = $(this);
	tdDiv.siblings().find(".tooltip").hide();
});

</script>
