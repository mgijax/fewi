<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
    
${templateBean.templateHeadHtml}

<title>Gene Expression Literature Summary</title>

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
<div id="titleBarWrapper" userdoc="gxdindex_help.shtml">	
	<span class="titleBarMainTitle">Gene Expression Literature Summary</span>
</div>

<h2><b>You searched for:</b></h2>
<c:if test="${not empty queryForm.nomen}">
	<span class="label">Marker Symbol/Name:</span> 
	${queryForm.nomen}<br/></c:if>
<c:if test="${not empty queryForm.assayTypesSelected}">
	<span class="label">Assay Type(s):</span> 
	${queryForm.assayTypesSelected}<br/></c:if>
<c:if test="${not empty queryForm.agesSelected}">
	<span class="label">Ages:</span> 
	${queryForm.agesSelected}<br/></c:if>	
<c:if test="${not empty queryForm.author}">
	<span class="label">Author:</span> 
	${queryForm.author}<br/></c:if>
<c:if test="${not empty queryForm.journal}">
	<span class="label">Journal:</span>
	${queryForm.journal}<br/></c:if>
<c:if test="${not empty queryForm.year}">
	<span class="label">Year:</span> 
	${queryForm.year}<br/></c:if>
<c:if test="${not empty queryForm.text}">
	<span class="label">Text:</span> 
	${queryForm.text}<br/></c:if>


<div>
<hr>
${totalCount} matching records from ${refCount} references
<hr><br>

<h1><b>Summary by Age and Assay:</b></h1> 
<i>Numbers in the table indicate the number of 
results matching the search criteria.</i>
<br><br>

<h1><b>Summary by Gene and Reference:</b></h1> Number indicates the number of results matching the search criteria recorded for each reference.<br>
<b>* Indicates detailed expression data entries available</b><br><br>

       <c:if test="${not empty summaryRows}">
         <table class="outline" width="100%">
         <c:forEach var="row" items="${summaryRows}" >
           <% stripe.reset(); %>
           <table class="outline" width="100%">
           <tr class="${geneResult.next}"><td colspan="2"><b>${row.symbol}&nbsp;&nbsp;${row.name}</b></td></tr>
           <tr class="${geneResult.next}"><td><b>Results&nbsp;&nbsp;</b></td><td><b>Reference</b></td></tr>
           <c:if test="${not empty row.referenceRecords}">
         	<c:forEach var="innerrow" items="${row.referenceRecords}" >
         		<tr class="${stripe.next}"><td width="20px">${innerrow.count}<c:if test="${innerrow.isFullyCoded}">*</c:if></td><td><b>${innerrow.jnum}</b>  ${innerrow.longCitation} </td></tr>
         	</c:forEach>  
           </c:if>
           </table>
         </c:forEach>
         </table>
       </c:if>

</div>


${templateBean.templateBodyStopHtml}

