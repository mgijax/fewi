<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
${templateBean.templateHeadHtml}

<title>Human – Mouse Disease Connection</title>

<meta http-equiv="X-UA-Compatible" content="chrome=1">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/disease_portal.css" />

<!-- import jquery UI specifically for this page -->
<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />
<script src="${configBean.WEBSHARE_URL}js/jquery-ui-1.10.2.custom.min.js"></script>

<%-- Please add styles to disease_portal.css
<style>
</style>
--%>
<!--[if IE]>
<style>

#toggleImg{height:1px;}

/*
body.yui-skin-sam div#outer {position:relative;}
#qwrap, #expressionSearch .yui-navset {position:absolute;}
#toggleQF { overflow:auto; }
*/
body.yui-skin-sam div#outer {position:relative;}
#expressionSearch .yui-navset {position:absolute;}
</style>
<![endif]-->

<!--begin custom header content for this example-->
<script type="text/javascript">
    document.documentElement.className = "yui-pe";
</script>

${templateBean.templateBodyStartHdpHtml}


<!-- iframe for history manager's use -->
<iframe id="yui-history-iframe" name="yui-history-iframe" src="/fewi/blank.html"></iframe>
<input id="yui-history-field" name="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="disease_connection_help.shtml">	
	<span class="titleBarMainTitle"></span>
	<div class="yourInputButton">
		<form name="YourInputForm">
			<input class="searchToolButton" 
			value="Provide Feedback"  
			name="yourInputButton" 
			onclick='window.open("${configBean.MGIHOME_URL}feedback/feedback_form.cgi?")' 
			onmouseover="return overlib('We welcome your feedback and suggestions. Click here to contact us.', LEFT, WIDTH, 200, TIMEOUT, 2000);" 
			onmouseout="nd();" 
			type="button">
		</form>
	</div>
</div>


<div id="toggleControl" style="background-color: #ffdab3; border: solid 1px #7F7F7F;">    
    <div id="toggleQF" class="summaryControl" style="display:none; padding: 8px 10px;"><span id="toggleImg" class="qfExpand" style="margin:0px 15px 0px 0px;"></span><span id="toggleLink" class="filterButton">Click to modify search</span></div>
</div>
<div id="outer" style="border:none; background-color: transparent; width:100%;"><div id="qwrap" style="margin-top: 10px;"><%@ include file="/WEB-INF/jsp/disease_portal_form.jsp" %></div></div>
<br clear="all" />
<div class="summaryControl" style="display:none;">
	<div id="resultbar" class="bluebar" style="background-color: #ffdab3;">Results</div>
		<%@ include file="/WEB-INF/jsp/disease_portal_summary.jsp" %>
	
	</div>
</div>


<script type="text/javascript">
    var fewiurl = "${configBean.FEWI_URL}";
    var assemblyBuild = "${configBean.ASSEMBLY_VERSION}";
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/fewi_utils.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/disease_portal_query.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/disease_portal_autocomplete.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/disease_portal_upload.js"></script>
<% 	
 	String queryString = (String) request.getAttribute("querystring");
	// need to url encode the querystring
	request.setAttribute("encodedQueryString", FormatHelper.encodeQueryString(queryString));
%>
<script type="text/javascript">
	var querystring = "${encodedQueryString}";
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/disease_portal_summary.js"></script>


${templateBean.templateBodyStopHtml}