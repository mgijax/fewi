<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>MGI Quick Search Results</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
    
%>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<form action="${configBean.FEWI_URL}quicksearch/summary" METHOD="get">
<div id="titleBarWrapper" userdoc="QUICK_SEARCH_help.shtml">	
	<span class="titleBarMainTitle">Quick Search Results</span> for: <input id='queryField' name='query' size='30' type='text' value='${query}'>
	&nbsp; <input class="qsButton" type="submit" name="submit" value="Search Again">
	&nbsp; <input class="qsButton" type="submit" name="reset" value="Reset" onClick="clearField(); return false;">
	&nbsp; <span class="qsButton" style="margin-left:35px;" onclick="window.open('${configBean.MGIHOME_URL}feedback/feedback_form.cgi?subject=Quick Search')">
    Your Input Welcome
  </span>
</div>
</form>

<div id="filterButtons">
   <b>Filter results by: </b>
   <a id="functionFilter" class="filterButton">Molecular Function <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="processFilter" class="filterButton">Biological Process <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="componentFilter" class="filterButton">Cellular Component <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
</div>
<div id="breadbox" class="hidden">
  <div id="filterSummary">
  	<b>Filtered by: </b>
    <span id="filterList"></span>
  </div>
</div>

<!-- for filter popup (re-used by all filter buttons) -->
<div class="facetFilter">
	<div id="facetDialog">
		<div class="hd">Filter</div>
		<div class="bd">
			<form:form method="GET"
				action="${configBean.FEWI_URL}quicksearch/summary">
				<img src="/fewi/mgi/assets/images/loading.gif">
			</form:form>
		</div>
	</div>
</div>
	
<div id="errorDiv" class="hidden">
</div>

<div id="b1Header" class="qsHeader">Genome Features
  <span id="b1Counts" class="resultCount"></span>
  <span class="helpCursor" onmouseover="return overlib('<div class=detailRowType>This list includes genes, QTL, cytogenetic markers, and other genome features whose name, symbol, synonym, or accession ID matched some or all of your search text.<br/><br/>This list also includes genome features associated with vocabulary terms matching your search text. <br/><br/></div><div class=\'detailRowType\'>See <a href=\'${configBean.USERHELP_URL}QUICK_SEARCH_help.shtml\'>Using the Quick Search Tool</a> for more information and examples.</div>', STICKY, CAPTION, 'Genome Features', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')" onmouseout="nd();">
       <img src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif" border="0">
  </span>
  <div id="featurePaginator" class="featurePaginator"></div>
</div>
<div id="b1Results"></div>

<!-- Note: b5 appears out of order (before b2) for historical reasons. -->
<div id="b5Header" class="qsHeader">Alleles
  <span id="b5Counts" class="resultCount"></span>
  <span class="helpCursor" onmouseover="return overlib('<div class=detailRowType>This list includes alleles whose name, symbol, synonym, or accession ID matched some or all of your search text.<br/><br/>This list also includes alleles associated with vocabulary terms matching your search text. <br/><br/></div><div class=\'detailRowType\'>See <a href=\'${configBean.USERHELP_URL}QUICK_SEARCH_help.shtml\'>Using the Quick Search Tool</a> for more information and examples.</div>', STICKY, CAPTION, 'Alleles', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')" onmouseout="nd();">
       <img src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif" border="0">
  </span>
  <div id="allelePaginator" class="allelePaginator"></div>
</div>
<div id="b5Results"></div>

<div id="b2Header" class="qsHeader">Vocabulary Terms
  <span id="b2Counts" class="resultCount"></span>
  <span class="helpCursor" onmouseover="return overlib('<div class=detailRowType>Use the vocabulary terms listed here <ul><li>to learn MGI\'s official terms</li><li>to focus on detailed research topics</li><li>to explore related research areas</li><li>to investigate alternative areas</li></ul></div><div class=\'detailRowType\'>See <a href=\'${configBean.USERHELP_URL}QUICK_SEARCH_help.shtml\'>Using the Quick Search Tool</a> for more information and examples.</div>', STICKY, CAPTION, 'Vocabulary Terms', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')" onmouseout="nd();">
    <img src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif" border="0">
  </span>
  <div id="vocabPaginator" class="vocabPaginator"></div>
</div>
<div id="b2Results"></div>

<!-- Note: b4 appears out of order (before b3) for historical reasons. -->
<div id="b4Header" class="qsHeader">Strains and Stocks
  <span id="b4Counts" class="resultCount"></span>
  <span class="helpCursor" onmouseover="return overlib('<div class=detailRowType>This list includes mouse strains or stocks that matched the name, synonym, or accession ID matched some or all of your search text.</div><div class=\'detailRowType\'>See <a href=\'${configBean.USERHELP_URL}QUICK_SEARCH_help.shtml\'>Using the Quick Search Tool</a> for more information and examples.</div>', STICKY, CAPTION, 'Strains', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')" onmouseout="nd();">
    <img src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif" border="0">
  </span>
  <div id="strainPaginator" class="strainPaginator"></div>
</div>
<div id="b4Results"></div>

<div id="b3Header" class="qsHeader">Other Results by ID
  <span id="b3Counts" class="resultCount"></span>
  <span class="helpCursor" onmouseover="return overlib('<div class=detailRowType>This section includes links to sequences, orthology relationships, SNPs and other results whose accession ID matched an item in your search text.</div><div class=\'detailRowType\'>See <a href=\'${configBean.USERHELP_URL}QUICK_SEARCH_help.shtml\'>Using the Quick Search Tool</a> for more information and examples.</div>', STICKY, CAPTION, 'Other Results By ID', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')" onmouseout="nd();">
    <img src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif" border="0">
  </span>
</div>
<div id="b3Results"></div>

<div id="b5Header" class="qsHeader">Search MGI with Google
  <span id="b5Counts" class="resultCount"></span>
  <span class="helpCursor" onmouseover="return overlib('<div class=detailRowType>Use Google to search for your text on MGI\'s web pages including:<ul><li>FAQs</li><li>Help pages</li><li>Reference abstracts</li><li>Phenotypic details for alleles</li><li>Image captions</li><li>...and other pages</li></ul></div><div class=\'detailRowType\'>See <a href=\'${configBean.USERHELP_URL}QUICK_SEARCH_help.shtml\'>Using the Quick Search Tool</a> for more information and examples.</div>', STICKY, CAPTION, 'Search MGI with Google', HAUTO, BELOW, WIDTH, 375, DELAY, 600, CLOSECLICK, CLOSETEXT, 'Close X')" onmouseout="nd();">
    <img src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif" border="0">
  </span>
</div>
<form method="get" action="http://www.google.com/search">
  <input type="text" name="q" size="25" maxlength="255" value="${query}">
  <input type="submit" value="Search" class="buttonLabelKLF">
  <input type="hidden" name="sitesearch" value="informatics.jax.org">
</form>

<style>
.helpCursor { cursor: help; }
.qsHeader { width: 100%; background-color: #F0F8FF; color: #002255; margin-top: 0.75em; 
	font-size: 18px; font-weight: bold; line-height: 1.25; vertical-align: top;
	padding-left: 5px; padding-right: 5px; padding-top: 2px; padding-bottom: 2px; 
	clear: both;
	}
.resultCount { font-size: 10px; font-weight: normal; color: #676767; }
.hidden { display: none; }
.shown { display: inline; }

.qsButton {
    font-size: 12px;
    font-family: Verdana,Arial,Helvetica;
    color: #002255;
    font-weight: bolder;
    background-color: #eeeeee;
    border-width: 1px;
    border-style: solid;
    border-color: #7d95b9;
    padding: 2px;
    display: inline;
    text-decoration: none;
    cursor: hand;
}

#filterSummary { margin-top: 5px; margin-bottom: 5px; }

#b1Results { max-height: 300px; overflow-y: auto; width: 100%; }
#b2Results { max-height: 300px; overflow-y: auto; width: 100%; }
#b4Results { max-height: 300px; overflow-y: auto; width: 100%; }
#b5Results { max-height: 300px; overflow-y: auto; width: 100%; }

#b1Table { border-collapse: collapse; width: 100% }
#b1Table th { font-weight: bold; padding: 3px; }
#b1Table td { padding: 3px; }
#b1Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b1Table tr:first-child { background-color: #dfefff; }

#b2Table { border-collapse: collapse; width: 100% }
#b2Table th { font-weight: bold; padding: 3px; }
#b2Table td { padding: 3px; }
#b2Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b2Table tr:first-child { background-color: #dfefff; }

#b3Table { border-collapse: collapse; width: 100% }
#b3Table th { font-weight: bold; padding: 3px; }
#b3Table td { padding: 3px; }
#b3Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b3Table tr:first-child { background-color: #dfefff; }

#b4Table { border-collapse: collapse; width: 100% }
#b4Table th { font-weight: bold; padding: 3px; }
#b4Table td { padding: 3px; }
#b4Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b4Table tr:first-child { background-color: #dfefff; }

#b5Table { border-collapse: collapse; width: 100% }
#b5Table th { font-weight: bold; padding: 3px; }
#b5Table td { padding: 3px; }
#b5Table td sup { padding: 3px; line-height: 1.9em; }
#b5Table tr:nth-child(odd) { background-color: #f2f2f2; }
#b5Table tr:first-child { background-color: #dfefff; }

.noWrap { white-space: nowrap; }
.facetFilter .yui-panel .bd { width: 285px; }

.termType { font-variant: small-caps; font-size: 10px; font-family: Verdana, Arial, Helvetica; }
.nameCol { width: 25%; }
.termCol { width: 40%; }
.dataCol { width: 10%; }
.bestMatchCol { } 
.small { font-size: 10px; }

#featurePaginator {
	float: right;
    font-size: 12px;
    margin-top: 10px;
    font-weight: normal;
    margin-right: 10px;
    text-decoration: none;
}
#allelePaginator {
	float: right;
    font-size: 12px;
    margin-top: 10px;
    font-weight: normal;
    margin-right: 10px;
    text-decoration: none;
}
#vocabPaginator {
	float: right;
    font-size: 12px;
    margin-top: 10px;
    font-weight: normal;
    margin-right: 10px;
    text-decoration: none;
}
#strainPaginator {
	float: right;
    font-size: 12px;
    margin-top: 10px;
    font-weight: normal;
    margin-right: 10px;
    text-decoration: none;
}
#errorDiv {
	color: red;
	padding-top: 10px;
	font-weight: bold;
}
</style>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/filters.js"></script>
<script>
		function getQuerystring() {
	  		return queryString + filters.getUrlFragment();
		}
</script>
<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/fewi_utils.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/widgets/DataCache.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/widgets/Paginator.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/jquery.paging.min.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_main.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_bucket1.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_bucket2.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_bucket3.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_bucket4.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_bucket5.js"></script>
<script>
function clearField() {
	$('#queryField')[0].value = '';
}

var queryString="${e:forJavaScript(queryString)}";
var query = "${e:forJavaScript(query)}";
var fewiurl = "${configBean.FEWI_URL}";
var genomeBuild = "${configBean.ASSEMBLY_VERSION}";

qsMain();

function initializeFilterLibrary(delay) {
	if (window.filtersLoaded) {
		console.log('initializing filters');
		filters.setFewiUrl(fewiurl);
		filters.setQueryStringFunction(getQuerystring);
		filters.setSummaryNames('filterSummary', 'filterList');
		filters.addFilter('goProcessFilter', 'Process', 'processFilter', 'processFilter', fewiurl + 'quicksearch/featureBucket/process');
		filters.addFilter('goFunctionFilter', 'Function', 'functionFilter', 'functionFilter', fewiurl + 'quicksearch/featureBucket/function');
		filters.addFilter('goComponentFilter', 'Component', 'componentFilter', 'componentFilter', fewiurl + 'quicksearch/featureBucket/component');
		filters.registerCallback("filterCallback", qsProcessFilters);
		filters.registerCallback("gaLogCallback", qsLogFilters);
		filters.setRemovalDivStyle('block');
	} else {
		setTimeout(function() { initializeFilterLibrary(delay) }, delay);
	}
}
initializeFilterLibrary(250);	// check for filters.js library being loaded every 250ms
</script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

