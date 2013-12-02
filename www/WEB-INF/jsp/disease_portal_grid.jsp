<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page import = "org.jax.mgi.fewi.controller.DiseasePortalController" %>
<%@ page trimDirectiveWhitespaces="true" %>

<%
	String queryString = (String) request.getAttribute("queryString");
	// need to url encode the querystring
	request.setAttribute("encodedQueryString", FormatHelper.encodeQueryString(queryString));
%>

<style type="text/css">
  
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
	  <th rowspan="2" style="white-space: nowrap;">Human Gene</th>
	  <th rowspan="2">
	  		<div style="position:relative; padding-top: 20px; width:100%; text-align: right;">
		  		<div id="filterButtons">
		  		<button style="width:160px;" class="filterSubmit" title="Resubmit the query keeping only results that have data in the selected rows and columns">Apply Filters:&#x00A;Retain selected col/rows</button>
		  	</div></div>
		  	<br/>Mouse Gene
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
	<th><img id="filterIcon" class="filterSubmit" title="filter: select rows or columns to keep" src="${configBean.FEWI_URL}assets/images/hdp/filterIcon.png"></th>
	<c:forEach var="mpHeader" items="${mpHeaders}" varStatus="status">
		<th class="mp_${status.count} <c:if test="${status.last && not empty diseaseColumns}"> rightDoubleBorder </c:if>">
			<input class="gridCheck" type="checkbox" filter="fHeader" value="${mpHeader}" colid="mp_${status.count}" />
		</th>		
	</c:forEach>
	<c:forEach var="diseaseName" items="${diseaseNames}" varStatus="status">
		<th class="d_${status.count} dc"><input class="gridCheck" type="checkbox" filter="fHeader" value="${diseaseName}" colid="d_${status.count}"/></th>
	</c:forEach>
	</tr>
	
	<c:forEach var="gridCluster" items="${gridClusters}" varStatus="gcStatus">
	  <tr class="${gcStatus.index % 2 == 0 ? 'row1' : 'row2'}">
	    <td><div class="mc">
	      <c:forEach var="humanSymbol" items="${gridCluster.humanSymbols}" varStatus="status">
		      <c:if test="${ not empty gridCluster.homologeneId}"><a href="${configBean.FEWI_URL}homology/${gridCluster.homologeneId}"><fewi:super value="${humanSymbol}"/></a></c:if>
		      <c:if test="${ empty gridCluster.homologeneId}"><fewi:super value="${humanSymbol}"/></c:if>
	        <c:if test="${!status.last}">, </c:if>
	      </c:forEach>
	    </div></td>
	    <td><div class="mc">
	      <c:forEach var="mouseMarker" items="${gridCluster.mouseMarkers}" varStatus="status">
		      <a href="${configBean.FEWI_URL}marker/key/${mouseMarker.markerKey}"><fewi:super value="${mouseMarker.symbol}"/></a>
	        <c:if test="${!status.last}">, </c:if>
	      </c:forEach>
	    </div></td>
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
	<c:if test="${moreDiseases}"><div id="moreDiseases" title="Click to rebuild grid with no disease column limit"><a>See more diseases not shown</a></div></c:if>
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
$(".filterSubmit").click(gridFilterSubmitClick);
$("#moreDiseases").click(gridMoreDiseasesClick);
/* if(isGFiltersSet())
{
	//$("#filterSubmit").hide();
	$("#filterReset").show();
} */
// add the click handler (from disease_portal_summary.js) to the gridCheck filters
$(".gridCheck").click(gridCheckClick).attr("title","check to keep after filtering");

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
