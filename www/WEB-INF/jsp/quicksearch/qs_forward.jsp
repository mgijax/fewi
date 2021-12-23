<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Forwarding to ${forwardTo}</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
    
%>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<form id="forwardForm" action="${forwardToUrl}" METHOD="post">
<div id="titleBarWrapper" userdoc="QUICK_SEARCH_help.shtml" style="height: 54px">	
  <span class="titleBarMainTitle">Forwarding Quick Search Results to ${forwardToText}</span>
  <input type="hidden" id="ids" name="ids" value="" />
</div>
</form>

<div id="statusDiv" style="margin-left:5px;">
  <div id="spinnerDiv"><img src='/fewi/mgi/assets/images/loading.gif' height='21' width='21'></div>
  <span id="statusText" style="display:inline"> Retrieving data...  Please be patient...</span>
</div>
<div id="errorDiv" class="hidden">
</div>

<%@ include file="/WEB-INF/jsp/quicksearch/qs_styles.jsp" %>

<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/fewi_utils.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/widgets/DataCache.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/widgets/Paginator.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_main.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/quicksearch/qs_forward.js"></script>
<script>
  var queryString="${e:forJavaScript(queryString)}";
  var fewiurl = "${configBean.FEWI_URL}";
  var dataEndpoint = "${dataEndpoint}" + "?" + queryString;
  qsfGetData(dataEndpoint);
</script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

