<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="Search Mouse SNPs from dbSNP"
	description="${seoDescription}"
	keywords="${seoKeywords}"
/>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- begin header bar -->
<div id="titleBarWrapper" userdoc="SNP_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">Search Mouse SNPs from dbSNP</span>
</div>
<!-- end header bar -->

<c:if test="${configBean.snpsOutOfSync == 'true'}">
<style>
#outOfSync { background-color:#FFFFCC; border: 1px solid black; font-size: 0.9em; padding: 5px; }
#outOfSyncLabel { font-size: 1em; font-weight: bold; }
</style>
<div id="outOfSync">
  <span id="outOfSyncLabel">Genome Coordinate Discrepancy</span><BR/>
  The genome coordinates for mouse RefSNPs are derived from a different NCBI build of the mouse genome
  than are the genome coordinates for MGI genes (see <a href="${configBean.USERHELP_URL}SNP_discrepancy_help.shtml" target="_blank">details</a>).
</div>
</c:if>


<div id="outer">
	<span id="toggleImg" class="qfCollapse"></span>
	<c:if test="${not empty querystring}">
	<div id="toggleQF"><span id="toggleLink" class="filterButton">Click to hide search</span></div>
	<br>
	</c:if>
	<div id="qwrap">
		<%@ include file="/WEB-INF/jsp/snp/form.jsp" %>
	</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/snp_query.js"></script>

<script type="text/javascript">
	snpqry.setQueryFormDisplay(true);
	snpqry.setQueryFormHeight();

	// need to wait a half-second before wiring up the checkbox updating functions, making sure to
	// allow time for the tab buttons to be created
	window.setTimeout(function() {
		$("#ui-id-1").click(snpqry.updateQF1);
		$("#ui-id-2").click(snpqry.updateQF2); 

		<c:if test="${not empty snpQueryForm.referenceStrains}">
		// show Reference strain controls
		$('input[name=referenceMode]')[1].click();
		</c:if>
		<c:if test="${empty snpQueryForm.referenceStrains}">
		// show only Comparison strain controls
		$('input[name=referenceMode]')[0].click();
		</c:if>
		}, 500);
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
