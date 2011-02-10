<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

<title>Batch Summary</title>

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
	<span class="titleBarMainTitle">Batch Summary</span>
</div>


<div id="summDiv">
	<div id="querySummary">
		<span class="title">You searched for:</span><br/>
		<span class="label">Number of IDs/symbols entered:</span> 
			${inputIdCount}<br/>
		<span class="label">Input Type:</span>
			${batchQueryForm.idType}<br/>
		<c:if test="${batchQueryForm.hasFile}">
			<span class="label">Input File Name: </span> 
				${batchQueryForm.fileName}<br/>
			<span class="label">Input File Type:</span> 
				${batchQueryForm.fileType}<br/>
			<span class="label">ID/Symbol Column:</span> 
				${batchQueryForm.idColumn}<br/>				
		</c:if>
		<span class="label">Output options:</span> 
			${batchQueryForm.outputOptions}<br/>
		<span class="title">Your results:</span> <span id="totalCount"></span> matching rows, 
		<span id="markerCount"></span> matching genes/markers found.<br/>

	</div>
</div>	


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

<script type="text/javascript">
	// attribute columns
	var nomenclature = ${batchQueryForm.nomenclature};
	var loco = ${batchQueryForm.location};
	var ensembl = ${batchQueryForm.ensembl};
	var entrez = ${batchQueryForm.entrez};
	var vega = ${batchQueryForm.vega};
	
	// additional columns
	var go = ${batchQueryForm.go};
	var mp = ${batchQueryForm.mp};
	var omim = ${batchQueryForm.omim};
	var allele = ${batchQueryForm.allele};
	var exp = ${batchQueryForm.exp};
	var refsnp = ${batchQueryForm.refsnp};	
	var refseq = ${batchQueryForm.refseq};
	var uniprot = ${batchQueryForm.uniprot};
</script>

<!-- including this file will start the data injection -->
<script type="text/javascript">
  <%@ include file="/js/batch_summary.js" %>
</script>

${templateBean.templateBodyStopHtml}

