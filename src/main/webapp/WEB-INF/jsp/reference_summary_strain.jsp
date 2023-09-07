<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.Sequence" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!--begin custom header content for this example-->
<script type="text/javascript">
	document.documentElement.className = "yui-pe";
</script>

<script src="${configBean.FEWI_URL}assets/js/rowexpansion.js"></script>
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/reference_summary.css" />

<title>References</title>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">

<!-- begin header bar -->
<div id="titleBarWrapper" userdoc="reference_help.shtml#results_refqf ">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References associated with this Strain</span>
</div>
<!-- end header bar -->

<%@ include file="/WEB-INF/jsp/strain/strain_header.jsp" %>
<style>
div.message { font-weight: bold; padding-top: 10px; padding-bottom: 10px }
</style>
<div id="summary">

	<%@ include file="/WEB-INF/jsp/reference_summary_filters.jsp" %>
	<%@ include file="/WEB-INF/jsp/reference_summary_count.jsp" %>

	<div id="rightcolumn">
		<div class="innertube">
			<div id="paginationTop">&nbsp;</div>
		</div>
	</div>
</div>
	
<%@ include file="/WEB-INF/jsp/reference_summary_toolbar.jsp" %>

<div id="dynamicdata"></div>
<div id="paginationWrap">
	<div id="paginationBottom">&nbsp;</div>
</div>

<div class="facetFilter">
	<div id="facetDialog">
		<div class="hd">Filter</div>	
		<div class="bd">
			<form:form method="GET" action="${configBean.FEWI_URL}reference/summary">		
			</form:form>
		</div>
	</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${e:forJavaScript(queryString)}";
	var defaultSort = "${defaultSort}";
</script>


<script type="text/javascript">
	<%@ include file="/js/reference_summary.js" %>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

<%@ include file="/WEB-INF/jsp/reference_summary_filter_setup.jsp" %>
