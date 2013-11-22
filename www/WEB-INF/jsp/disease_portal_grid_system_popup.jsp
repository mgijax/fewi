<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/jsp/google_analytics_pageview.jsp" %>
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/disease_portal.css" />

<script>
	// change window title on page load
	// 00D7 is unicode for &times;
    document.title = '${gridClusterString} \u00D7 ${termHeader} Grid Drill Down';
</script>

<!-- Table and Wrapping div -->

<div id="hdpSystemPopupHeader">Data for ${gridClusterString} and ${termHeader} <c:if test="${ termHeader!='normal phenotype'}"> abnormalities </c:if>   </div>

<table id="hdpSystemPopupLegend">
<tr>
	<td>*</td><td>Aspects of the system are reported to show a normal phenotype</td></tr><tr>
	<td class="bsn_legend">!</td><td>Indicates phenotype varies with strain background</td>
</tr>
</table>

<%@ include file="/WEB-INF/jsp/disease_portal_grid_popup_grid.jsp" %>
