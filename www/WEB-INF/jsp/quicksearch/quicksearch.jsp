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
<div id="titleBarWrapper" userdoc="QUICK_SEARCH_help.shtml" style="height: 54px">	
	<span class="titleBarMainTitle">Quick Search Results</span> for:
	<fewi:select id="queryType" name="queryType" items="${queryTypes}" value="${queryType}" />
	<input id='queryField' name='query' size='30' type='text' value='${query}'>
	&nbsp; <input class="qsButton" type="submit" name="submit" value="Search Again">
	&nbsp; <input class="qsButton" type="submit" name="reset" value="Reset" onClick="clearField(); return false;">
	&nbsp; <span class="qsButton" style="margin-left:35px;" onclick="window.open('${configBean.MGIHOME_URL}feedback/feedback_form.cgi?subject=Quick Search')">
    Your Input Welcome
  </span>
  <div id="examples" class="example" style="text-align: center; padding-top: 4px;">
  Examples:&nbsp;&nbsp;embry*&nbsp;develop*&nbsp;&nbsp;&nbsp;&nbsp;NM_013627&nbsp;&nbsp;&nbsp;&nbsp;MGI:97490&nbsp;&nbsp;&nbsp;&nbsp;Pax*&nbsp;&nbsp;&nbsp;&nbsp;axial&nbsp;"skeletal&nbsp;dysplasia"&nbsp;&nbsp;&nbsp;&nbsp;Tg(ACTB-cre)2Mrt&nbsp;&nbsp;&nbsp;&nbsp;
  Chr1&nbsp;&nbsp;&nbsp;&nbsp;Chr1:194732198&nbsp;&nbsp;&nbsp;&nbsp;Chr1:194732198-294732198
  </div>
</div>
</form>

<%@ include file="/WEB-INF/jsp/quicksearch/qs_filters.jsp" %>

<div id="errorDiv" class="hidden">
</div>

<script>
	// turn on tabbed display
	$(function() {
		$( "#querytabs" ).tabs(<c:if test="${not empty snpQueryForm.selectedTab}">{active: ${e:forJavaScript(snpQueryForm.selectedTab)}}</c:if>);
	});
</script>

<div id="querytabs" style="margin-top: 15px;">
  <ul style="background-color: #F0F8FF;">
		<li><span class="label" id="featureTab"><a href="#tabs-gf">Genome Features <span id="gfCount"></span></a></span></li>
		<li><span class="label" id="alleleTab"><a href="#tabs-a">Alleles <span id="aCount"></span></a></span></li>
		<li><span class="label" id="vocabTermTab"><a href="#tabs-vt">Vocabulary Terms <span id="vtCount"></span></a></span></li>
		<li><span class="label" id="strainTab"><a href="#tabs-ss">Strains and Stocks <span id="ssCount"></span></a></span></li>
		<li><span class="label" id="otherIdTab"><a href="#tabs-id">Other Results by ID <span id="idCount"></span></a></span></li>
  </ul>
	<div>
		<div id="tabs-gf">
			<%@ include file="/WEB-INF/jsp/quicksearch/qs_feature_bucket.jsp" %>
		</div> <!-- tabs-gf -->
		<div id="tabs-a">
			<%@ include file="/WEB-INF/jsp/quicksearch/qs_allele_bucket.jsp" %>
		</div> <!-- tabs-a -->
		<div id="tabs-vt">
			<%@ include file="/WEB-INF/jsp/quicksearch/qs_vocab_term_bucket.jsp" %>
		</div> <!-- tabs-vt -->
		<div id="tabs-ss">
			<%@ include file="/WEB-INF/jsp/quicksearch/qs_strain_bucket.jsp" %>
		</div> <!-- tabs-ss -->
		<div id="tabs-id">
			<%@ include file="/WEB-INF/jsp/quicksearch/qs_other_ids_bucket.jsp" %>
		</div> <!-- tabs-id -->
	</div> <!-- unnamed -->
</div> <!-- querytabs -->

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

<%@ include file="/WEB-INF/jsp/quicksearch/qs_styles.jsp" %>

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
		filters.addFilter('phenotypeFilter', 'Phenotype', 'phenotypeFilter', 'phenotypeFilter', fewiurl + 'quicksearch/featureBucket/phenotype');
		filters.addFilter('diseaseFilter', 'Disease', 'diseaseFilter', 'diseaseFilter', fewiurl + 'quicksearch/featureBucket/disease');
		filters.addFilter('featureTypeFilter', 'Feature Type', 'featureTypeFilter', 'featureTypeFilter', fewiurl + 'quicksearch/featureBucket/featureType');
		filters.addFilter('expressionFilter', 'Expression', 'expressionFilter', 'expressionFilter', fewiurl + 'quicksearch/featureBucket/expression');
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

