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
<div id="titleBarWrapper" userdoc="QUICK_SEARCH_help.shtml">	
	<span class="titleBarMainTitle">Quick Search Results for ${query}</span>
</div>

<div id="filterButtons">
   <a id="functionFilter" class="filterButton">Molecular Function <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="processFilter" class="filterButton">Biological Process <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
   <a id="componentFilter" class="filterButton">Cellular Component <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
</div>
<div id="breadbox">
  <div id="filterSummary">
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
	
Bucket 1 : Markers and Alleles<p/>
<div id="b1Results"></div>

<p/>
Bucket 2 : Vocabulary Terms<p/>
<div id="b2Results"></div>

<p/>
Bucket 3 : Matches by ID<p/>
<div id="b3Results"></div>

<p/>
Search MGI with Google<p/>

<style>
#b1Table { border-collapse: collapse }
#b1Table th { font-weight: bold; padding: 3px; border: 1px solid black; }
#b1Table td { padding: 3px; border: 1px solid black; }

#b2Table { border-collapse: collapse }
#b2Table th { font-weight: bold; padding: 3px; border: 1px solid black; }
#b2Table td { padding: 3px; border: 1px solid black; }

#b3Table { border-collapse: collapse }
#b3Table th { font-weight: bold; padding: 3px; border: 1px solid black; }
#b3Table td { padding: 3px; border: 1px solid black; }

.facetFilter .yui-panel .bd { width: 285px; }
</style>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/filters.js"></script>
<script>
		function getQuerystring() {
	  		return queryString + filters.getUrlFragment();
		}
</script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/fewi_utils.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_main.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_bucket1.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_bucket2.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_bucket3.js"></script>
<script>
var queryString="${e:forJavaScript(queryString)}";
var query = "${query}";
var fewiurl = "${configBean.FEWI_URL}";

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

