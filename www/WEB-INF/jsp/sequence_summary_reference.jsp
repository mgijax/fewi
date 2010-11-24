<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Reference reference = (Reference)request.getAttribute("reference"); %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

${templateBean.templateHeadHtml}

  <title>Mouse Sequence Summary Report</title>
  <%@ include file="/WEB-INF/jsp/includes.jsp" %>

${templateBean.templateBodyStartHtml}

<iframe id="yui-history-iframe" src="/fewi/js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="sequence_detail.shtml">    
    <!--myTitle -->
    <span class="titleBarMainTitle">Mouse Sequences Summary Report</span>
</div>


<!-- structural table -->
<table class="detailStructureTable">

<!-- ID/Version -->
<tr >
  <td class="detailCat1">
       <b>Reference</b>
  </td>
  <td class="detailData1">
    <a style="font-size:x-large;  font-weight: bold;" 
      href="${configBean.FEWI_URL}reference/${reference.jnumID}">${reference.jnumID}</a>
    <br/>
    ${reference.shortCitation}
  </td>
</tr>
</table>



<hr>

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

