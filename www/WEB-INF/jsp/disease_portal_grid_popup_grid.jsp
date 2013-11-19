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
  #hdpSystemPopupTable td {border: 1px solid #DDD; padding: 4px 4px;}
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
  	top: -4px;
  	right: 4px;
  	position: absolute;
  }
  .cell_outer { position: relative; width:100%;height:18px;}
  
  tr.genoRow:hover
  {
  	background-color: #FFFFCC;
  	cursor: pointer;
  }
  .row1 { border-top: 2px solid #DDD; }

  #hdpSystemPopupTable .humanHeaderRow { background-color: #EFEFEF; }
  #hdpSystemPopupTable .cc
  {
        color: #000;
        margin-right: 8px;
        margin-top: 6px;
        font-size: 10px;
        font-weight: bold;
        padding: 0px;
  }
  #hdpSystemPopupTable .gridCellLink
  {
        cursor: pointer;
        width: 100%;
        height: 100%;
  }
  #hdpSystemPopupTable .mk
  {
        padding-top: 6px;
        padding-left: 6px;
  }
  #hdpSystemPopupTable .mpBin_0 { }
  #hdpSystemPopupTable .mpBin_1 { background-color: #C6D6E8; }
  #hdpSystemPopupTable .mpBin_2 { background-color: #879EBA; }
  #hdpSystemPopupTable .mpBin_3 { background-color: #49648B; }
  #hdpSystemPopupTable .mpBin_4 { background-color: #002255; }
  
  
  #hdpSystemPopupTable .dHBin_0 {}
  #hdpSystemPopupTable .dHBin_1 { background: url('${configBean.FEWI_URL}assets/images/hdp/human_cell_sprite.gif') 0px 0px; } 
  #hdpSystemPopupTable .dHBin_2 { background: url('${configBean.FEWI_URL}assets/images/hdp/human_cell_sprite.gif') 0px -44px; }
  #hdpSystemPopupTable .dHBin_3 { background: url('${configBean.FEWI_URL}assets/images/hdp/human_cell_sprite.gif') 0px -90px; }
  #hdpSystemPopupTable .dHBin_4 { background: url('${configBean.FEWI_URL}assets/images/hdp/human_cell_sprite.gif') 0px -134px; }
  #hdpSystemPopupTable .dHBin_5 { background-color: #ffdab3; }
  #hdpSystemPopupTable .dHBin_6 { background-color: #f7be76; }
  #hdpSystemPopupTable .dHBin_7 { background-color: #fba039; }
  #hdpSystemPopupTable .dHBin_8 { background-color: #ff8700; }

</style>


<!-- Table and Wrapping div -->
<div id="hdpSystemPopupWrap">
<table id="hdpSystemPopupTable">

<!-- Column Headers -->
<tr>
	<c:choose>
		<c:when  test="${empty popupRows and not empty humanPopupRows}">
			<!-- if we have genes, but no genotypes, display "Gene" header -->
			<th>Human Gene</th>
		</c:when>
		<c:otherwise>
	  		<th>Genotype</th>
	  	</c:otherwise>
	</c:choose>
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
	<tr title="click row to see phenotype details" class="genoRow ${status.index % 2 == 0 ? 'row1' : 'row2'}" onClick="window.open('${configBean.FEWI_URL}diseasePortal/genoCluster/view/${popupRow.genoClusterKey}'); return true;">
    <td style="color:blue;"> 
      <%=allComp%>
      <span style="color:black;"> 
        <%=ifChimeric%><%=ifConditional%> 
      </span>
    </td>

    <c:forEach var="cell" items="${popupRow.gridCells}" varStatus="status">
      <td class="cc mpBin_${cell.mpBin}"><div class="cell_outer">
	    <c:if test="${cell.hasPopup}">
		    <div class="gridCellLink">
			    <div class="mk">${cell.mpPopupMark}</div>
		    </div>
		<c:if test="${cell.hasBackgroundNote}"><div class="bsn">!</div></c:if>
	    </c:if>
      </div></td>
    </c:forEach>
    </tr>
</c:forEach>

<c:if test="${not empty humanPopupRows}">
	<!-- Add human data if it has been set -->
	<c:if test="${not empty popupRows}">
		<!-- Add "Gene" header if we have Genotypes above -->
		<tr class="humanHeaderRow"><th>Human Gene</th>
			<c:forEach var="termColumn" items="${termColumns}"><td></td></c:forEach>
		</tr>
	</c:if>
	<c:forEach var="popupRow" items="${humanPopupRows}" varStatus="status">
	
		<c:set var="hasHomologeneId" value="${not empty popupRow.marker.homologeneId}"/>
		
		<tr <c:if test="${hasHomologeneId}">title="click gene for homology details"</c:if> class="genoRow ${status.index % 2 == 0 ? 'row1' : 'row2'}" 
			<c:if test="${hasHomologeneId}">onClick="window.open('${configBean.FEWI_URL}homology/${popupRow.marker.homologeneId}'); return true;"</c:if> >
	    
	    <td <c:if test="${hasHomologeneId}">style="color:blue;"</c:if> > 
	    ${popupRow.marker.symbol}
	    </td>
	
	    <c:forEach var="cell" items="${popupRow.gridCells}" varStatus="status">
	      <td class="cc dHBin_${cell.diHumanBin}"><div class="cell_outer">
                <c:if test="${cell.hasPopup}">
                    <div class="gridCellLink">
						<div class="mk">${cell.diseaseMark}</div>
					</div>
                </c:if>
          </div></td>
	    </c:forEach>
	    </tr>
	
	</c:forEach>
</c:if>

</table>
<div id="hdpSystemPopupTablePadder"><c:out value="${lastColImage}" escapeXml="false" /></div>
</div>
