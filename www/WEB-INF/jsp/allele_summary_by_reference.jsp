<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
${templateBean.templateHeadHtml}

<title>MGI Allele Summary</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<meta name="description" content="Alleles matching your query">
<meta name="keywords" content="MGI, mouse, allele, disease models">
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/>

<style>
</style>

<!--begin custom header content for this example-->
<script type="text/javascript">
</script>

${templateBean.templateBodyStartHtml}


<!-- iframe for history manager's use -->
<iframe id="yui-history-iframe" src="/fewi/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<!-- header bar -->
<div id="titleBarWrapper" userdoc="ALLELE_summary_help.shtml">
  <span class="titleBarMainTitle">Phenotypic Alleles</span>
  <br>
  <span class="titleBarSubTitle"> Query Results -- Summary</span>
</div>



<!-- header table -->
<table class="summaryHeader">
<tr >
  <td class="summaryHeaderCat1">
       <b>Reference</b>
  </td>
  <td class="summaryHeaderData1">

    <a style="font-size:x-large;  font-weight: bold; padding-bottom:10px;"
      href="${configBean.FEWI_URL}reference/${reference.jnumID}">
      ${reference.jnumID}
    </a>

    <div style="padding:4px;"> </div>

    ${reference.shortCitation}
  </td>
</tr>
</table>

<br/>

<!-- data table div: filled by YUI, called via js below -->
<div id="dynamicdata"></div>

<div id="paginationWrap" style="width: 468px">
	<div id="paginationBottom">&nbsp;</div>
</div>

<script type="text/javascript">
window.querystring="${queryString}";
window.fewiurl="${configBean.FEWI_URL}";
</script>
<!-- including this file will start the data injection -->
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/allele_summary.js"></script>




${templateBean.templateBodyStopHtml}