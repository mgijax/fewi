<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/google_analytics_pageview.jsp" %>
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/disease_portal.css" />

<script>
	// change window title on page load
	// 00D7 is unicode for &times;
    document.title = '${gridClusterString} \u00D7 ${termHeader} Grid Drill Down';
</script>

<style>
#markerList {
  font-family: Verdana,Arial,Helvetica;
  font-size: 12px;
  padding-bottom:4px;
}
</style>

</head>

<body style="margin: 8px; min-width: 1px;">

<!-- Table and Wrapping div -->

<div id="hdpSystemPopupHeader">
  Data for ${gridClusterString} and ${termHeader} <c:if test="${ termHeader!='normal phenotype'}"> abnormalities </c:if>
</div>

<div id="markerList">
<c:forEach var="marker" items="${markers}" varStatus="status">
  Find Mice: IMSR strains or lines carrying any ${marker.symbol} Mutation
  <a href='${configBean.IMSRURL}summary?gaccid=${marker.primaryID}&states=ES+Cell&states=embryo&states=live&states=ovaries&states=sperm' target='_blank'>
    (${marker.countForImsr} available)
  </a><br/>
</c:forEach>
</div>

<table id="hdpSystemPopupLegend">
<tr>
	<td>*</td><td>Aspects of the system are reported to show a normal phenotype</td></tr><tr>
	<td class="bsn_legend">!</td><td>Indicates phenotype varies with strain background</td></tr><tr>
	<td></td><td><span class="highlight">Highlighted Column</span> contain at least one phenotype or disease result matching your search term(s)</td>
</tr>
</table>

<%@ include file="/WEB-INF/jsp/disease_portal_grid_popup_grid.jsp" %>

</body>
</html>
