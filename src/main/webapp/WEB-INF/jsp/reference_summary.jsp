<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

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

<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">

<!-- begin header bar -->
<div id="titleBarWrapper" userdoc="reference_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References</span>
</div>
<!-- end header bar -->

<div id="outer"  class="bluebar">
	<span id="toggleImg" class="qfExpand"></span>
	<div id="toggleQF"><span id="toggleLink" class="filterButton">Click to modify search</span></div>
	<div id="qwrap" style="display:none;">
		<%@ include file="/WEB-INF/jsp/reference_form.jsp" %>
	</div>
</div>

<div id="resultbar" class="bluebar">Results</div>

<div id="summary">

	<%@ include file="/WEB-INF/jsp/reference_summary_filters.jsp" %>

	<div id="querySummary">
		<div class="innertube">
			<span class="title">You searched for:</span><br/>
			<c:if test="${not empty referenceQueryForm.author}">
				<c:if test="${referenceQueryForm.authorScope eq 'any'}">
					<span class="label">Any Author:</span></c:if>
				<c:if test="${referenceQueryForm.authorScope eq 'first'}">
					<span class="label">First Author:</span></c:if>
				<c:if test="${referenceQueryForm.authorScope eq 'last'}">
					<span class="label">Last Author:</span></c:if>					
				${e:forHtml(referenceQueryForm.author)}<br/></c:if>
			<c:if test="${not empty referenceQueryForm.journal}">
				<span class="label">Journal:</span>
				${e:forHtml(referenceQueryForm.journal)}<br/></c:if>
			<c:if test="${not empty referenceQueryForm.year}">
				<span class="label">Year:</span> 
				${e:forHtml(referenceQueryForm.year)}<br/></c:if>
			<c:if test="${not empty referenceQueryForm.text}">
				<span class="label">Text 
				<c:choose>
					<c:when test="${referenceQueryForm.inTitle}">
						 in Title 				
						<c:if test="${referenceQueryForm.inAbstract}">
							 or Abstract </c:if>
					</c:when>
					<c:when test="${referenceQueryForm.inAbstract}">
						 in Abstract
					</c:when>				
				</c:choose>
				:</span>
				${e:forHtml(referenceQueryForm.text)}<br/>
			</c:if>	
			<c:if test="${not empty referenceQueryForm.id}">
				<span class="label">ID:</span> 
				${e:forHtml(referenceQueryForm.id)}<br/></c:if>
				<span class="bold"><span id="totalCount" class="count">0</span> reference(s)</span> match your unfiltered search.<br/>
		</div>
	</div>

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
			<img src="/fewi/mgi/assets/images/loading.gif">	
			</form:form>
		</div>
	</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${e:forJavaScript(queryString)}";
	var defaultSort = "${defaultSort}";
	var qDisplay = true;
	parseParameters(querystring);
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/reference_query.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/reference_summary.js"></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

<%@ include file="/WEB-INF/jsp/reference_summary_filter_setup.jsp" %>
