<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
${templateBean.templateHeadHtml}

<title>Human Disease Portal</title>
<meta http-equiv="X-UA-Compatible" content="chrome=1">


<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!-- import jquery UI specifically for this page -->
<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />
<script src="${configBean.WEBSHARE_URL}js/jquery-ui-1.10.2.custom.min.js"></script>

<style>
.ui-autocomplete {
  max-height: 300px;
  overflow-y: auto;
  /* prevent horizontal scrollbar */
  overflow-x: hidden;
	font-size:90%;
}
/* IE 6 doesn't support max-height
 * we use height instead, but this forces the menu to always be this tall
 */
* html .ui-autocomplete {
  height: 300px;
}
.ui-menu .ui-menu-item {
	padding-left:0.4em;
}
.ui-menu .ui-menu-item a {
	padding:0px;
}

.left { float: left; }
.right { float: right; }

#resultSummary {border:0;padding-left:5px; text-align: left; font-size: 12px;}
#resultSummary .selected a,
#resultSummary .selected a:focus,
#resultSummary .selected a:hover{ margin-left: 0px;border:1px solid #808080;border-bottom:solid 1px #eee; color:black; background:none; background-color:#eee;}
#resultSummary .yui-content{background-color:#eee;border:1px solid #808080; border-top: none;}
#resultSummary .yui-nav {border-bottom:solid 1px black;}

#diseasePortalSearch {border:0;padding-left:5px; text-align: left; font-size: 12px;}
#diseasePortalSearch .selected a,
#diseasePortalSearch .selected a:focus,
#diseasePortalSearch .selected a:hover{ margin-left: 0px;border:1px solid #808080;border-bottom:solid 1px #eee; color:black; background:none; background-color:#eee;}
#diseasePortalSearch .yui-content{background-color:#eee;border:1px solid #808080; border-top: none;}
#diseasePortalSearch .yui-nav {border-bottom:solid 1px black;}

table.noborder, table.noborder td , table.noborder th { border: none; }


body.yui-skin-sam .yui-panel .hd,
body.yui-skin-sam .yui-ac-hd { background:none; background-color:#025; color:#fff; font-weight: bold;}
body.yui-skin-sam .yui-ac-hd {padding: 5px;}
body.yui-skin-sam div#outer {overflow:visible;}

.yui-dt table {width: 100%;}

td.yui-dt-col-assayID div.yui-dt-liner span {font-size: 75%;}

.yui-skin-sam .yui-tt .bd
{
	background-color:#ddf; 
	color:#005;
	border:2px solid #005;
}

#diseasePortalQueryForm .queryCat1 { background-color: #ffeac3; }
#diseasePortalQueryForm .queryCat2 { background-color: #dfba93; }

</style>
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
<div id="titleBarWrapper" userdoc="DISEASEPORTAL_help.shtml">	
	<span class="titleBarMainTitle">Human Disease Portal</span>
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


<div id="outer" style="background-color: #ffdab3;">    
    <div id="toggleQF" class="summaryControl" style="display:none"><span id="toggleImg" class="qfExpand" style="margin-right:15px;"></span><span id="toggleLink" class="filterButton">Click to modify search</span></div>
    <div id="qwrap">
    	<%@ include file="/WEB-INF/jsp/disease_portal_form.jsp" %>
    </div>
</div>
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

<script type="text/javascript">
	var querystring = "${querystring}";
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/disease_portal_summary.js"></script>


${templateBean.templateBodyStopHtml}