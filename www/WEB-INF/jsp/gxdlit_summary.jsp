<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
    
${templateBean.templateHeadHtml}

<title>Gene Expression Literature Results</title>

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
	<span class="titleBarMainTitle">Gene Expression Literature Results</span>
</div>



<div id="summary">

	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
				<div id="filterSummary" style="display:none;" class="filters">
					<span class="label">Filters:</span>
					<span id="filterList"></span><br/>
					<span id="fCount"><span id="filterCount">0</span> item(s) match after applying filter(s).</span>
				</div>
			</div>
		</div>
	</div>

	<div id="querySummary">
		<div class="innertube">
			<span class="title">You searched for:</span><br>
			<span class="count">${totalCount} 
		    <c:if test="${totalCount == limit}"> of ${limit}+ </c:if> 
			matching record<c:if test="${totalCount != 1}">s</c:if> from ${refCount} reference<c:if test="${totalCount != 1}">s</c:if>.</span><br>
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
				<c:if test="${queryForm.authorScope eq 'any'}">
					<span class="label">Any Author:</span></c:if>
				<c:if test="${queryForm.authorScope eq 'first'}">
					<span class="label">First Author:</span></c:if>
				<c:if test="${queryForm.authorScope eq 'last'}">
					<span class="label">Last Author:</span></c:if>					
				${queryForm.author}<br/></c:if>
			<c:if test="${not empty queryForm.journal}">
				<span class="label">Journal:</span>
				${queryForm.journal}<br/></c:if>
			<c:if test="${not empty queryForm.year}">
				<span class="label">Year:</span> 
				${queryForm.year}<br/></c:if>
			<c:if test="${not empty queryForm.text}">
				<span class="label">Text 
				<c:choose>
					<c:when test="${queryForm.inTitle}">
						 in Title 				
						<c:if test="${queryForm.inAbstract}">
							 or Abstract </c:if>
					</c:when>
					<c:when test="${queryForm.inAbstract}">
						 in Abstract
					</c:when>				
				</c:choose>
				:</span>
				${queryForm.text}<br/>
			</c:if>		</div>
	</div>
	
	<div id="rightcolumn">
		<div class="innertube">
			<div id="paginationTop">&nbsp;</div>
		</div>
	</div>
	
</div>

<div style="clear:left">
<c:if test="${totalCount != 0}">
<span class="extraLarge">Summary by Age and Assay:</span><i> Numbers in the table indicate the number of results matching the search criteria.</i><br>
<c:if test="${not empty pairTable}">
	<table class="outline">
	<tr class="outline stripe3">
	<!-- Setup the age header -->
	<td class="outline"><a href="${configBean.USERHELP_URL}gxdindex_help.shtml#irbaa" onClick='openUserhelpWindow("gxdindex_help.shtml#irbaa"); return false;'">Age</a></td>
	<c:forEach var="age" items="${pairTable.ages}">
		<td class="outline"><c:if test="${age != 'E' && age != 'A'}">E</c:if>${age}</td>
	</c:forEach>
	</tr>
	<!-- Setup the x access header -->
	<c:forEach var="type" items="${pairTable.assayTypes}">
		<tr class="outline">
		<td class="outline">${type.assayType}</td>
			<c:forEach var="count" items="${type.counts}">
			<td class="outline">
				<c:if test="${not empty count}">${count.countUrl}</c:if>
			</td>
			</c:forEach>
	    </tr>
	</c:forEach>
	</table>
</c:if>
<br>
<span class="extraLarge">Summary by Gene and Reference:</span><i> Number indicates the number of results matching the search criteria recorded for each reference.</i><br>
<b>* Indicates detailed expression data entries available</b><br>

       <c:if test="${not empty summaryRows}">
         <table class="outline" width="100%">
         <c:forEach var="row" items="${summaryRows}" >
           <% stripe.reset(); %>
           <table class="outline" width="100%">
           <tr class="${geneResult.next}"><td colspan="2"><b>${row.symbol}&nbsp;&nbsp;${row.name}</b></td></tr>
           <tr class="${geneResult.next}"><td><b>Results&nbsp;&nbsp;</b></td><td><b>Reference</b></td></tr>
           <c:if test="${not empty row.referenceRecords}">
         	<c:forEach var="innerrow" items="${row.referenceRecords}" >
         		<tr class="${stripe.next}">
         		<td width="20px" style="vertical-align:text-top;"><a href="${configBean.FEWI_URL}gxdlit/key/${innerrow.indexKey}">${innerrow.count}</a><c:if test="${innerrow.isFullyCoded}">*</c:if></td><td><b>${innerrow.jnum}</b>  ${innerrow.longCitation} </td></tr>
         	</c:forEach>  
           </c:if>
           </table>
         </c:forEach>
         </table>
       </c:if>
</c:if>
</div>


${templateBean.templateBodyStopHtml}

