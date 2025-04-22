<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "java.util.List" %>
<%@ page import = "org.jax.mgi.shr.jsonmodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="${title}"
	description="${seoDescription}"
	keywords="${seoKeywords}"
/>


<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailListCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailListBg1","detailListBg2");
    BrowserTerm term = (BrowserTerm) request.getAttribute("term");
%>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var browserUrl = "${browserUrl}";
	var termPaneUrl = "${termPaneUrl}";
	var searchPaneUrl = "${searchPaneUrl}";
	var treeInitialUrl = "${treeInitialUrl}";
	var treeChildrenUrl = "${treeChildrenUrl}";
</script>
<SCRIPT TYPE="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></SCRIPT>
<script type="text/javascript" src='${configBean.FEWI_URL}assets/js/vocabbrowser_detail.js'></script>
<script type="text/javascript" src='${configBean.FEWI_URL}assets/js/external/jquery.easy-autocomplete.min.js'></script>
<script type="text/javascript" src='${configBean.FEWI_URL}assets/js/external/jstree/jstree.min.js'></script>
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/easy-autocomplete.min.css">
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/reference_summary.css" />
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/js/external/jstree/themes/default/style.min.css" />
    
<style>
td.bordered { border: 1px solid black }
#termPaneDetails th {
  padding: 4px;
  width: 1px;
  white-space: nowrap;
}
td.top { vertical-align: top }
td.padded { padding: 4px }
td.padTop { padding-top: 2px }
.bold { font-weight: bold }
.ygtvlabel { background-color: white; color: black; }
.ygtvlabel:link { background-color: white; color: black; }
.ygtvlabel:visited { background-color: white; color: black; }
.ygtvlabel:hover { background-color: white; color: blue; text-decoration: underline; }
.ygtvfocus { background-color: white }
.ygtv-highlight { background-color: white }
.ygtv-highlight0 { background-color: white }
.ygtv-highlight1 { background-color: white }
.ygtv-highlight2 { background-color: white }
.synonymTag { font-size: 0.8em; color: #222; font-style: normal; }
.jstree-default .jstree-clicked {
	font-weight: bold;
}
.goHeaderLink { font-weight: bold; text-decoration: none; }
</style>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<c:set var="outerDiv" value="outer"/>
<c:choose>
  <c:when test="${branding == 'GXD'}">
	<style>
		.highlight { background-color: #EBCA6D }
		.jstree-clicked {background: #EBCA6D !important}
		.jstree-themeicon, .jstree-themeicon-custom {display: none !important}

	</style>
    <div id="titleBarWrapperGxd" userdoc="${helpDoc}">
      <a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
      <span class="titleBarMainTitleGxd" style="display:inline-block; margin-top: 20px">${pageTitle}</span>
    </div>
	<c:set var="outerDiv" value="outerGxd"/>
  </c:when>
  <c:otherwise>
	<style>
		.highlight { background-color: #BEEBFF; }
	</style>
	<div id="titleBarWrapper" userdoc="${helpDoc}" style="max-width: none;">
		<div name="centeredTitle">
			<span class="titleBarMainTitle">${pageTitle}</span>
		</div>
		<c:if test="${branding == 'GO'}">
			<div id="goCategories">
				<a href="${browserUrl}GO:0003674" class="goHeaderLink">Molecular Function</a> |
				<a href="${browserUrl}GO:0008150" class="goHeaderLink">Biological Process</a> |
				<a href="${browserUrl}GO:0005575" class="goHeaderLink">Cellular Component</a>
			</div>
			<script>
				$('#titleBarWrapper').css({'padding-bottom':'4px'});
			</script>
		</c:if>
	</div>
  </c:otherwise>
</c:choose>

<!-- 3-section table: search, term detail, tree view -->

<div id="${outerDiv}" style="padding: 2px">
  <table style="width: 100%; background-color: white; border:0">
  <tr><td id='searchTD' rowspan="2" class="bordered top padTop" style="width: 30%">
      <div style='width: 100%;' id='searchContainer'>
      <div style='width: 100%; text-align: center; font-size:125%; font-weight: bold; clear:both; padding-bottom: 8px;' id='searchTitle'>${searchPaneTitle}</div>
      <div id="searchPane" style="width:100%; overflow:auto; text-align:center;">
      </div>
      </div>
    </td>
    <td id='detailTD' class="bordered top padTop">
      <div style='width: 100%' id='detailContainer'>
      <div style='width: 100%; text-align: center; font-size:125%; font-weight: bold' id='detailTitle'>${termPaneTitle}<br></div>
      <div id="detail" style="width:100%; overflow: auto">
      </div>
      </div>
    </td>
  </tr>
  <tr><td id='treeTD' class="bordered top padTop">
	<div style='width:100%' id='treeViewContainer'>
        <div style='width: 100%; text-align: center; font-size:125%; font-weight: bold' id="treeMainTitle">${treePaneTitle}</div>
	<div style="width: 100%; text-align: center" id='treeTitle'>
	</div>
	<div id="treeViewDiv" style="overflow:auto"></div>
	</div>
  </td></tr>
</table>
</div>

<c:if test="${not empty message}">
<div id="message">${message}</div>
</c:if>

<script type="text/javascript">
	fetchTermPane("${termID}");
	setInitialTermID("${termID}");
	fetchSearchPane("${searchTerm}");
	initializeTreeView("${termID}");
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
