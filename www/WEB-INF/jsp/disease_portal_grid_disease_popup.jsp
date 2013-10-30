<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/jsp/google_analytics_pageview.jsp" %>


<% 
  // Genotype genotype = (Genotype)request.getAttribute("genotype"); 
  NotesTagConverter ntc = new NotesTagConverter();
%>

<style type="text/css">
  #hdpSystemPopupWrap
  {
  	display: table;
  	border: 2px solid #AAA; 
  	background-color:#FFF; 
  	font-family: Verdana,Arial,Helvetica;
  }
  #hdpSystemPopupTable 
  { 
  	display: table-cell;
  	border-spacing:0px; 
  	border-collapse:collapse; 
  	font-family: Verdana,Arial,Helvetica;
  	font-size: 12px;
  }
  #hdpSystemPopupHeader{ 
    min-height:32px; 
    min-width:500px;
    background-color:#DFEFFF; 
    margin-bottom:12px;
    padding:3px;
    border:thin solid #002255;
    font-family: Verdana,Arial,Helvetica; 
    font-size: 20px;
  }
  #hdpSystemPopupTablePadder{ display: table-cell; }
  #hdpSystemPopupTablePadder img { visibility: hidden; }
  #hdpSystemPopupTable th {border: 1px solid #AAA; border-top: none; padding: 4px 4px; }
  #hdpSystemPopupTable th > div { width:20px; overflow: visible; }
  #hdpSystemPopupTable td {border: 1px solid #AAA; padding: 4px 4px;}
  #hdpSystemPopupTable .vb { vertical-align: bottom; border-left: none; border-right: none;}
  #hdpSystemPopupTable .vb .partialRight
  { 
  	position: absolute;
	bottom: -4px;
	width: 24px;
	height: 14px;
	border-right: 1px solid #AAA;
  }
  .cell_outer { position: relative; }
  
  tr.genoRow:hover
  {
  	background-color: #cde;
  	cursor: pointer;
  }
  .row1 { background-color: #F1F1F1; }

</style>

<script>
	// change window title on page load
	// 00D7 is unicode for &times;
    document.title = '${gridClusterString} \u00D7 ${diseaseHeader} Grid Drill Down';
</script>

<!-- Table and Wrapping div -->

<div id="hdpSystemPopupHeader">Data for ${gridClusterString} and ${diseaseHeader}</div>


<div id="hdpSystemPopupWrap">
<table id="hdpSystemPopupTable">

<!-- Column Headers -->
<tr>

  <th>
    Genotype
  </th>
	
  <!--  mp header columns -->
  <c:set var="lastColImage" />
  <c:forEach var="diseaseHeaderCol" items="${diseaseTermColumns}" varStatus="status">
    <th class="vb">
      <div><c:out value="${diseaseHeaderCol}" escapeXml="false" /></div>
      <div style="position:relative;"><div class="partialRight"></div>
    </th>
    <c:if test="${status.last}"><c:set var="lastColImage" value="${diseaseHeaderCol}"/></c:if>
  </c:forEach>

</tr>



<c:forEach var="popupRow" items="${popupRows}" varStatus="status">

    <c:set var="scriptGeno" value="${popupRow.genotype}" scope="request"/>
    <%  // unhappily resorting to scriptlet
    Genotype genotype = (Genotype)request.getAttribute("scriptGeno");
	String allComp = new String();
	if ( (genotype != null) && (genotype.getCombination1() != null) ) {
      allComp = genotype.getCombination1().trim();
      allComp = ntc.convertNotes(allComp, '|',true);
    }
    %>
	<tr class="genoRow ${status.index % 2 == 0 ? 'row1' : 'row2'}" onClick="window.open('${configBean.FEWI_URL}diseasePortal/genoCluster/view/${popupRow.genoClusterKey}'); return true;">
    <td style="color:blue;"> 
    <%=allComp%>
    </td>

    <c:forEach var="diseaseTerm" items="${popupRow.mpCells}" varStatus="status">
      <td><div class="cell_outer">
                <c:if test="${diseaseTerm.hasPopup}">
                    <div>${diseaseTerm.displayMark}</div>
                </c:if>
      </div>
      </td>
    </c:forEach>
    </tr>

</c:forEach>

<c:forEach var="popupRow" items="${humanPopupRows}" varStatus="status">
	<tr class="genoRow ${status.index % 2 == 0 ? 'row1' : 'row2'}">
    <td style="color:blue;"> 
    ${popupRow.marker.symbol}
    </td>

    <c:forEach var="diseaseTerm" items="${popupRow.gridCells}" varStatus="status">
      <td><div class="cell_outer">
                <c:if test="${diseaseTerm.hasPopup}">
                    <div>${diseaseTerm.displayMark}</div>
                </c:if>
      </div>
      </td>
    </c:forEach>
    </tr>

</c:forEach>

</table>
<div id="hdpSystemPopupTablePadder"><c:out value="${lastColImage}" escapeXml="false" /></div>
</div>

<br/><br/>
<c:if test="${not empty humanMarkers}">
<tr><th>Human Marker</th></tr>
<c:forEach var="humanMarker" items="${humanMarkers}" varStatus="status">
        <tr><td>
        ${humanMarker.symbol}
        </td></tr>
</c:forEach>
</c:if>
