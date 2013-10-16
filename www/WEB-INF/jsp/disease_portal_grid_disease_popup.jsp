<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/jsp/google_analytics_pageview.jsp" %>

<% 
  // Genotype genotype = (Genotype)request.getAttribute("genotype"); 
  NotesTagConverter ntc = new NotesTagConverter();
%>

<script>
	// change window title on page load
	// 00D7 is unicode for &times;
    document.title = '${gridClusterString} \u00D7 ${diseaseName} Grid Drill Down';
</script>
<style>
#hdpDiseasePopupTable
{
	border: solid 1px #ccc;
}
tr.genoRow:hover
{
  background-color: #cde;
  cursor: pointer;
}
</style>

<!-- Table and Wrapping div -->
<div id="hdpDiseasePopupWrap">
<h2>Data for ${gridClusterString} &times; <a href="${configBean.FEWI_URL}disease/${diseaseId}">${diseaseName}</a></h2>

<table id="hdpDiseasePopupTable">

<!-- Column Headers -->
<c:if test="${not empty genoClusters}">
<tr><th>Genotype</th></tr>
<c:forEach var="genoCluster" items="${genoClusters}" varStatus="status">
        <c:set var="scriptGeno" value="${genoCluster.genotype}" scope="request"/>
        <%  // unhappily resorting to scriptlet
        Genotype genotype = (Genotype)request.getAttribute("scriptGeno");
	String allComp = new String();
	if ( (genotype != null) && (genotype.getCombination1() != null) ) {
          allComp = genotype.getCombination1().trim();
          allComp = ntc.convertNotes(allComp, '|',true);
        }
        %>
        <tr class="genoRow" onClick="window.open('${configBean.FEWI_URL}diseasePortal/genoCluster/view/${genoCluster.genoClusterKey}'); return true;">
        <td style="color:blue;">
        <%=allComp%>
        </td></tr>
</c:forEach>
</c:if>

<c:if test="${not empty humanMarkers}">
<tr><th>Human Marker</th></tr>
<c:forEach var="humanMarker" items="${humanMarkers}" varStatus="status">
        <tr><td>
        ${humanMarker.symbol}
        </td></tr>
</c:forEach>
</c:if>
</table>
</div>
