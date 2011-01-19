<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Reference reference = (Reference)request.getAttribute("reference"); %>
    
${templateBean.templateHeadHtml}

<title>Associated Human Diseases</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
</style>

<script>
</script>

${templateBean.templateBodyStartHtml}


<!-- iframe for history manager's use -->
<iframe id="yui-history-iframe" src="/fewi/js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="foo_help.shtml">	
	<span class="titleBarMainTitle">Associated Human Diseases</span>
</div>


<jsp:include page="marker_header.jsp"></jsp:include><br>

<br>
Note: Diseases listed here are those where a mutant allele of this gene is 
involved in a mouse genotype used as a model. This does not mean that 
mutations in this gene contribute to or are causative of the disease.
<br><br>
<em>To see all mouse models associated with a human disease phenotype, click on the disease term below. </em> 


<!-- paginator -->
<table style="width:100%;">
  <tr>
    <td class="paginator">
      <div id="paginationTop">&nbsp;</div>
    </td>
  </tr>
</table>

<!-- data table div: filled by YUI, called via js below -->
<div id="dynamicdata"></div>

<!-- including this file will start the data injection -->
<script type="text/javascript">
  <%@ include file="/js/omim_summary.js" %>
</script>
<br>
<a href="${configBean.FEWI_URL}marker/${marker.primaryID}/vocab/omim">All ${marker.symbol} mutant alleles</a> associated with human diseases.

${templateBean.templateBodyStopHtml}

