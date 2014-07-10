<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
${templateBean.templateHeadHtml}

<title>MGI Allele Summary</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<meta name="description" content="Alleles matching your query">
<meta name="keywords" content="MGI, mouse, allele, disease models">
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/>

<style>
</style>

<!--begin custom header content for this example-->
<script type="text/javascript">
</script>
<script type="text/javascript" src="${configBean.WEBSHARE_URL}js/hideshow.js"></script>


${templateBean.templateBodyStartHtml}


<!-- iframe for history manager's use -->
<iframe id="yui-history-iframe" src="/fewi/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<!-- header bar -->
<div id="titleBarWrapper" userdoc="ALLELE_summary_help.shtml">
  <span class="titleBarMainTitle">Phenotypes, Alleles & Disease Models Search</span>
</div>



<!-- header table -->
<%@ include file="/WEB-INF/jsp/marker_header.jsp" %>
<br/>

<div id="summary">
	<c:if test="${not empty mutationInvolves}">
	<div id="leftcolumn" style="float: left; vertical-align: middle; font-size: 125%; font-weight: bold; padding-top: 30px">
		Genomic mutations involving ${marker.symbol}
	</div>
	</c:if>
	<div id="rightcolumn" style="float: right;">
			<div class="innertube">
				<div id="paginationTop">&nbsp;</div>
			</div>
	</div>
</div>

<div id="toolbar" class="bluebar" style="">
	<div id="downloadDiv">
		<span class="label">Export:</span>
		<a id="textDownload"  class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
        <a id="excelDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a>
	</div>
</div>

<!-- data table div: filled by YUI, called via js below -->
<div id="dynamicdata"></div>

<div id="paginationWrap" style="width: 468px; float:right;">
	<div id="paginationBottom">&nbsp;</div>
</div>

<script type="text/javascript">
window.querystring="${queryString}";
window.fewiurl="${configBean.FEWI_URL}";
</script>

<!-- including this file will start the data injection -->
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/allele_summary.js"></script>




${templateBean.templateBodyStopHtml}
