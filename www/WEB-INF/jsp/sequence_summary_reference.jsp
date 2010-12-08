<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Reference reference = (Reference)request.getAttribute("reference"); %>

${templateBean.templateHeadHtml}

  <title>Mouse Sequences Summary Report</title>
  <%@ include file="/WEB-INF/jsp/includes.jsp" %>

${templateBean.templateBodyStartHtml}

<!-- iframe for history manager's use -->
<iframe id="yui-history-iframe" src="/fewi/js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="sequence_summary_report.shtml">    
  <span class="titleBarMainTitle">Mouse Sequences Summary Report</span>
</div>


<!-- header table -->
<table class="summaryHeader">
<tr >
  <td class="summaryHeaderCat1">
       <b>Reference</b>
  </td>
  <td class="summaryHeaderData1">
    <a style="font-size:x-large;  font-weight: bold;" 
      href="${configBean.FEWI_URL}reference/${reference.jnumID}">${reference.jnumID}</a>
    <br/>
    ${reference.shortCitation}
  </td>
</tr>
</table>


<table style="width:100%;">
  <tr>
    <td class="paginator">
      <div id="paginationTop">&nbsp;</div>
    </td>
  </tr>
</table>

<div id="dynamicdata"></div>




<script type="text/javascript">
  <%@ include file="/js/sequence_summary.js" %>
</script>

${templateBean.templateBodyStopHtml}

