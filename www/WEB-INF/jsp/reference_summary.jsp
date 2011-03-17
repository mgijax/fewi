<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!--begin custom header content for this example-->
<script type="text/javascript">
	document.documentElement.className = "yui-pe";
</script>

<script src="${configBean.FEWI_URL}assets/js/rowexpansion.js"></script>

<title>References</title>

${templateBean.templateBodyStartHtml}

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
	<div id="toggleQF">Click to modify search</div>
	<div id="qwrap" style="display:none;">
		<%@ include file="/WEB-INF/jsp/reference_form.jsp" %>
	</div>
</div>

<div id="resultbar" class="bluebar">Results</div>

<div id="summary">

	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
				<div id="filterSummary" style="display:none;" class="filters">
					<span class="label">Filters:</span>
					<span id="filterList"></span><br/>
					<span id="fCount"><span id="filterCount">0</span> item(s) match after applying filter(s).</span>
				</div>
			</div>
		</div>
	</div>

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
				${referenceQueryForm.author}<br/></c:if>
			<c:if test="${not empty referenceQueryForm.journal}">
				<span class="label">Journal:</span>
				${referenceQueryForm.journal}<br/></c:if>
			<c:if test="${not empty referenceQueryForm.year}">
				<span class="label">Year:</span> 
				${referenceQueryForm.year}<br/></c:if>
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
				${referenceQueryForm.text}<br/>
			</c:if>	
			<c:if test="${not empty referenceQueryForm.id}">
				<span class="label">ID:</span> 
				${referenceQueryForm.id}<br/></c:if>
			<span id="totalCount" class="count">0</span> item(s) match your unfiltered query.<br/>
		</div>
	</div>

	<div id="rightcolumn">
		<div class="innertube">
			<div id="paginationTop">&nbsp;</div>
		</div>
	</div>
</div>
	
<div id="toolbar" class="bluebar">
	<div id="filterDiv">
		Filter by: 
		<a id="authorFilter" class="filterButton">Author <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
		<a id="journalFilter" class="filterButton">Journal <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
		<a id="yearFilter" class="filterButton">Year <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
		<a id="curatedDataFilter" class="filterButton">Data <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
	</div>
	<div id="otherDiv">
		<a id="toggleAbstract" class="filterButton">Show All Abstracts</a> 
	</div>
	<div id="downloadDiv">
		<a id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a> 
	</div>
</div>

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
	var querystring = "${queryString}";
	var defaultSort = "${defaultSort}";
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/reference_summary.js"></script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/reference_query.js"></script>

<script type="text/javascript">
	var qDisplay = true;
	var toggleQF = function() {
        var qf = YAHOO.util.Dom.get('qwrap');
        var toggleLink = YAHOO.util.Dom.get('toggleQF');
        var toggleImg = YAHOO.util.Dom.get('toggleImg');
        
        var attributes = { height: { to: 375 }};
        var height = '';

        if (!qDisplay){
        	attributes = { height: { to: 0  }};
        	setText(toggleLink, "Click to modify search");
        	YAHOO.util.Dom.removeClass(toggleImg, 'qfCollapse');
        	YAHOO.util.Dom.addClass(toggleImg, 'qfExpand');
        	qDisplay = true;
        } else {            
        	YAHOO.util.Dom.setStyle(qf, 'height', '0px');
        	YAHOO.util.Dom.setStyle(qf, 'display', 'none');
        	YAHOO.util.Dom.removeClass(toggleImg, 'qfExpand');
        	YAHOO.util.Dom.addClass(toggleImg, 'qfCollapse');
        	setText(toggleLink, "Click to hide search");
        	qDisplay = false;
        	changeVisibility('qwrap');
        }
		var myAnim = new YAHOO.util.Anim('qwrap', attributes);
		myAnim.duration = 0.5;
		myAnim.animate();

	};

	YAHOO.util.Event.addListener("toggleQF", "click", toggleQF);
</script>

${templateBean.templateBodyStopHtml}
