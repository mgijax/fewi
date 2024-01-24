<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>

<HTML><HEAD>

<script type='text/javascript' src='${configBean.FEWI_URL}assets/js/yui-2.8.custom.min.js'></script>
<script type='text/javascript' src='${configBean.FEWI_URL}assets/js/hideshow.js'></script>
<!-- CSS -->
<!-- YUI Reset -->
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/yui-reset.css">

<!-- Combo-handled YUI CSS files: -->
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/yui-2.8-combo.css">

<!-- MGI CSS files: -->
<link href="${configBean.WEBSHARE_URL}css/mgi_template01.css" rel="stylesheet" type="text/css"/>
<link href="${configBean.WEBSHARE_URL}css/mgi.css" rel="stylesheet" type="text/css"/>


<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<title>${pageTitle}</title>

<meta name="description" content="${seoDescription}" />
<meta name="keywords" content="${seoKeywords}" />
<meta name="robots" content="NOODP" />
<meta name="robots" content="NOYDIR" />


<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker marker = (Marker)request.getAttribute("marker");

    StyleAlternator leftTdStyles
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles
      = new StyleAlternator("detailData1","detailData2");

%>

<style type="text/css">
td.padLR { padding-left:4px; padding-right:4px }
td.padTop { padding-top:4px }
td.padded { padding:4px; }
td.label { text-align: left; vertical-align: top; width: 1%; white-space: nowrap}
td.right { text-align: right }
.popupHeader {
    background-color: #d0e0f0;
    border: thin solid #002255;
    margin-bottom: 5px;
    min-height: 82px;
    min-width: 500px;
    padding: 4px;
    width: 1187px;
    margin-left: 5px;
}
a.noUnderline { text-decoration: none }
</style>

</HEAD><BODY CLASS="yui-skin-sam">

<div class='popupHeader'>
    <table style='' cellpadding='0' cellspacing='0'>
    <TR STYLE="">
      <TD>
      <a href="javascript:newWindow('${configBean.HOMEPAGES_URL}')" style='margin-left:2px; margin-right:10px;'><img src="${configBean.FEWI_URL}assets/images/mgi_logo.gif" 
          alt="Mouse Genome Informatics" border="0" width="100"></a>  
      </TD>

      <TD>
        &nbsp;&nbsp;&nbsp;
      </TD>

      <TD>
	<div name="centeredTitle">
	    <span class="titleBarMainTitle" style="font-size:18px">${pageTitleSuperscript}</span><br/>
	</div>
      </TD>
    </TR>
    </table>
</div>

<!-- paginator -->
<div id="summary" style="width:1200px;">
	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
				<div id="filterSummary" class="filters">
				</div>
			</div>
		</div>
	</div>
	<div id="querySummary">
		<div class="innertube">
		</div>
	</div>
	<div id="rightcolumn">
		<div class="innertube">
			<div id="paginationTop">&nbsp;</div>
		</div>
	</div>
</div>

<!-- data table div: filled by YUI, called via js below -->
<div id="dynamicdata" style="margin-left:5px"></div>
<div id="paginationWrap" style="width:1200px;">
	<div id="paginationBottom">&nbsp;</div>
</div>

<script type="text/javascript">
    var fewiurl = "${configBean.FEWI_URL}";
    var alleleID = "${alleleID}";
    var querystring = "alleleID=" + alleleID + "&";
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/allele_mutation_involves.js"></script>

</BODY></HTML>
