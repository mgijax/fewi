<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/google_analytics_pageview.jsp" %>
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/disease_portal.css" />

<title>${marker.symbol} phenotype data</title>

<style>
#markerList {
  font-family: Verdana,Arial,Helvetica;
  font-size: 12px;
  padding-bottom:4px;
}
</style>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- Table and Wrapping div -->

<%@ include file="/WEB-INF/jsp/marker_header.jsp" %>

<div id="hdpSystemPopupHeader">
  Mammalian Phenotype annotations related to ${termHeader}
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
<tr><td>*</td><td>Aspects of the system are reported to show a normal phenotype.</td></tr>
<tr><td class="bsn_legend">!</td><td>Indicates phenotype varies with strain background.</td></tr>
<tr><td></td><td>Darker colors indicate <span title="The blue squares indicate mouse data and get progressively darker with more supporting annotations. The lightest color represents one annotation. 2-5 annotations is represented by a darker shade, 6-99 annotations darker still and more than 100 annotations by the darkest color." style="color: blue; text-decoration: underline">more annotations</span></td></tr>
</table><p>

<%@ include file="/WEB-INF/jsp/disease_portal_grid_popup_grid.jsp" %>

<script>
document.title = '${marker.symbol} ';
document.title = document.title + ' ${termHeader} phenotype data';
</script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
