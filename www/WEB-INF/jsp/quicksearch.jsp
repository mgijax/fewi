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

<style type="text/css">
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="QUICK_SEARCH_help.shtml">	
	<span class="titleBarMainTitle">Quick Search Results for ${query}</span>
</div>

Bucket 1 : Markers and Alleles<p/>
<div id="b1Results"></div>

Bucket 2 : Vocabulary Terms<p/>
<div id="b2Results"></div>

Bucket 3 : Matches by ID<p/>
<div id="b3Results"></div>

<p/>
Search MGI with Google<p/>

<style>
#b3Table { border-collapse: collapse }
#b3Table th { font-weight: bold; padding: 3px; border: 1px solid black; }
#b3Table td { padding: 3px; border: 1px solid black; }
</style>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_main.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_bucket3.js"></script>
<script>
var queryString="${e:forJavaScript(queryString)}";
var query = "${query}";
var fewiurl = "${configBean.FEWI_URL}";

qsMain();
</script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

