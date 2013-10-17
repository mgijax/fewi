<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "java.net.URLEncoder" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page import = "org.jax.mgi.fewi.controller.DiseasePortalController" %>
<%@ page import = "org.jax.mgi.fewi.searchUtil.entities.SolrDpGridCluster.SolrDpGridClusterMarker" %>
<%@ page trimDirectiveWhitespaces="true" %>

<%
	String queryString = (String) request.getAttribute("queryString");
	// need to url encode the querystring
	request.setAttribute("encodedQueryString", URLEncoder.encode(queryString, "UTF-8"));
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
  #hdpGridTablePadder{ display: table-cell; }
  #hdpGridTablePadder img { visibility: hidden; }
   
  #hdpGridTable th {border: 1px solid #AAA; border-top: none; padding: 4px 4px; vertical-align:bottom;}
  #hdpGridTable th > div { width:20px; overflow: visible; }
  #hdpGridTable td {border: 1px solid #AAA; padding: 4px 4px; }
  #hdpGridTable .vb { vertical-align: bottom; border-left: none; border-right: none;}
  #hdpGridTable .gridCellLink {cursor: pointer;}
  #hdpGridTable .vb .partialRight
  { 
  	position: absolute;
	bottom: -4px;
	width: 24px;
	height: 14px;
	border-right: 1px solid #AAA;
  }
  #hdpGridTable .vb .partialDoubleRight
  { 
  	position: absolute;
	bottom: -4px;
	width: 23px;
	height: 14px;
	border-right: 2px solid #AAA;
  }
  #hdpGridTable .rightBorder { border-right: 1px solid #AAA;}
  #hdpGridTable .rightDoubleBorder { border-right: 2px solid #AAA;}
  .diseaseButton
  {
  	width:10px;
  	height:10px;
  	background-color: blue;
  	cursor:pointer;
  	border: solid 1px black;
  }
  .row1 { background-color: #F1F1F1; }
  .gridCheck
  {
  	cursor: pointer;
  }
  .gridCheck:hover { background-color: #BCD;}
  
  /* #filterButtons
  {
  	position: absolute;
	top: -60px;
	right: -150px;
  } */
  button { cursor: pointer; }
</style>


<div id="hdpGridTableWrap">
	<c:if test="${empty gridClusters}">No genes found that match your search criteria.
		<br/>
		<button class="hide" id="filterReset">Reset Active Filters</button>
	</c:if>
	<c:if test="${not empty gridClusters}">
	<!-- Grid Table -->
	<table id="hdpGridTable">
	
	<!-- Row of Column Headers -->
	<tr>
	  <th rowspan="2">Human Marker</th>
	  <th rowspan="2">
	  	<div style="position:relative; padding-top: 20px;">
		  	<div id="filterButtons"><button id="filterSubmit">Submit Filters</button>
		  			
		  			<!-- <button class="hide" id="filterReset">Reset Active Filters</button>--></div></div>
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
			<div style="position:relative;"><div class="partialRight"></div></div></th>
			<c:if test="${status.last}"><c:set var="lastColImage" value="${diseaseCol}"/></c:if>
	    </c:forEach>
	</tr>
	<tr>
	<th style="background-color:#DDE;">F</th>
	<c:forEach var="mpHeader" items="${mpHeaders}" varStatus="status">
		<th><input class="gridCheck" type="checkbox" filter="fHeader" value="${mpHeader}"/></th>		
	</c:forEach>
	<c:forEach var="diseaseName" items="${diseaseNames}" varStatus="status">
		<th><input class="gridCheck" type="checkbox" filter="fHeader" value="${diseaseName}"/></th>
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
		<td><input class="gridCheck" type="checkbox" filter="fGene" value="${gridCluster.gridClusterKey}"/></td>
            
        <c:forEach var="mpHeader" items="${gridCluster.mpHeaderCells}" varStatus="status">
              <td class="<c:if test="${status.last && not empty gridCluster.diseaseCells}">rightDoubleBorder</c:if>" >
                <c:if test="${mpHeader.hasPopup}">
                    <div title="click to see data represented in this cell" class="gridCellLink"
                         onClick="javascript:popupGenotypeSystem ('${configBean.FEWI_URL}diseasePortal/gridSystemCell?${encodedQueryString}&gridClusterKey=${gridCluster.gridClusterKey}&mpHeader=${mpHeader.term}', '${gridCluster.gridClusterKey}', '${mpHeader.term}')
                         ; return false;">${mpHeader.displayMark}</div>
                </c:if>
              </td>
            </c:forEach>

            <c:forEach var="disease" items="${gridCluster.diseaseCells}" varStatus="status">
            <td>
	          <c:if test="${disease.hasPopup}">
                    <div title="click to see data represented in this cell" class="gridCellLink"
                         onClick="javascript:popupGenotypeSystem ('${configBean.FEWI_URL}diseasePortal/gridDiseaseCell?${encodedQueryString}&gridClusterKey=${gridCluster.gridClusterKey}&termId=${disease.termId}&term=${disease.term}', '${gridCluster.gridClusterKey}', '${disease.termId}')
                         ; return false;">${disease.displayMark}</div>
	          </c:if>
              </td>
            </c:forEach>

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

</script>
