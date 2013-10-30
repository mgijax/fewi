<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/jsp/google_analytics_pageview.jsp" %>

<!-- Assumes title and header are set -->

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
  .bsn
  {
  	color: #FF8300;
  	font-weight: bold;
  	font-size: 120%;
  	font-family: serif;
  	top: -2px;
  	right: -1px;
  	position: absolute;
  }
  .cell_outer { position: relative; }
  
  tr.genoRow:hover
  {
  	background-color: #cde;
  	cursor: pointer;
  }
  .row1 { background-color: #F1F1F1; }

</style>


<!-- Table and Wrapping div -->
<div id="hdpSystemPopupWrap">
<table id="hdpSystemPopupTable">

<!-- Column Headers -->
<tr>
  <th>Genotype</th>
	
  <!--  header columns -->
  <c:set var="lastColImage" />
  <c:forEach var="headerCol" items="${termColumns}" varStatus="status">
    <th class="vb">
      <div><c:out value="${headerCol}" escapeXml="false" /></div>
      <div style="position:relative;"><div class="partialRight"></div>
    </th>
    <c:if test="${status.last}"><c:set var="lastColImage" value="${headerCol}"/></c:if>
  </c:forEach>
</tr>

<c:forEach var="popupRow" items="${popupRows}" varStatus="status">
    <c:set var="scriptGeno" value="${popupRow.genotype}" scope="request"/>
    <%  // unhappily resorting to scriptlet
    Genotype genotype = (Genotype)request.getAttribute("scriptGeno");
	String allComp = new String();
	String ifChimeric = new String();
	String ifConditional = new String();
	if ( (genotype != null) && (genotype.getCombination1() != null) ) {
      allComp = genotype.getCombination1().trim();
      allComp = ntc.convertNotes(allComp, '|',true);
    }
	if ( genotype.getExistsAs().equals("Chimeric") ) {
		ifChimeric = " (chimeric)";
	}
	if ( genotype.getIsConditional() == 1) {
		ifConditional = " (conditional)";
	}
	%>
	<tr class="genoRow ${status.index % 2 == 0 ? 'row1' : 'row2'}" onClick="window.open('${configBean.FEWI_URL}diseasePortal/genoCluster/view/${popupRow.genoClusterKey}'); return true;">
    <td style="color:blue;"> 
      <%=allComp%>
      <span style="color:black;"> 
        <%=ifChimeric%><%=ifConditional%> 
      </span>
    </td>

    <c:forEach var="cell" items="${popupRow.gridCells}" varStatus="status">
      <td><div class="cell_outer">
                <c:if test="${cell.hasPopup}">
                    <div>${cell.displayMark}</div>
                    <c:if test="${cell.hasBackgroundNote}"><div class="bsn">!</div></c:if>
                </c:if>
      </div>
      </td>
    </c:forEach>
    </tr>
</c:forEach>

<c:if test="${not empty humanPopupRows}">
	<!-- Add human data if it has been set -->
	<c:forEach var="popupRow" items="${humanPopupRows}" varStatus="status">
	
		<c:set var="hasHomologeneId" value="${not empty popupRow.marker.homologeneId}"/>
		
		<tr class="genoRow ${status.index % 2 == 0 ? 'row1' : 'row2'}" 
			<c:if test="${hasHomologeneId}">onClick="window.open('${configBean.FEWI_URL}homology/${popupRow.marker.homologeneId}'); return true;"</c:if> >
	    
	    <td <c:if test="${hasHomologeneId}">style="color:blue;"</c:if> > 
	    ${popupRow.marker.symbol}
	    </td>
	
	    <c:forEach var="cell" items="${popupRow.gridCells}" varStatus="status">
	      <td><div class="cell_outer">
	                <c:if test="${cell.hasPopup}">
	                    <div>${cell.displayMark}</div>
	                </c:if>
	      </div>
	      </td>
	    </c:forEach>
	    </tr>
	
	</c:forEach>
</c:if>

</table>
<div id="hdpSystemPopupTablePadder"><c:out value="${lastColImage}" escapeXml="false" /></div>
</div>
