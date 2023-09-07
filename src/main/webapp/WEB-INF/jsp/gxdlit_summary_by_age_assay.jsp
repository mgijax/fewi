<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Gene Expression Literature Summary - MGI</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_literature_help.shtml">	
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
	<span class="titleBarMainTitleGxd" style='display:inline-block; margin-top: 20px;'>Gene Expression Literature Summary</span>
</div>

<!-- header table -->
<table class="summaryHeader">
<tbody><tr>
  <td class="summaryHeaderCat1Gxd">
       <div style="padding-top: 5px;">Assay</div>
       <div style="padding-top: 7px;">Age</div>
  </div></td>
  <td class="summaryHeaderData1">
	<div class="extraLarge">${e:forHtml(assayType)}</div>
	<div class="extraLarge">
	<c:if test="${age == 'E'}">Embryonic, DPC unknown</c:if>
	<c:if test="${age == 'P'}">Postnatal</c:if>
	<c:if test="${age != 'E' && age !='P'}">${e:forHtml(age)} DPC</c:if>
	</div>
  </td>
</tr>
</tbody></table><br>

<span class="count">${totalCount} 
<c:if test="${totalCount == limit}"> of ${limit}+ </c:if> 
matching records from ${refCount} references.</span><br><br>


<div style="clear:left">
<span class="extraLarge">Summary by Gene and Reference:</span><i> Number indicates the number of results matching the search criteria recorded for each reference.</i><br>
<c:if test="${hasFullyCoded}"><b>* Indicates detailed expression data entries available</b><br></c:if>

       <c:if test="${not empty summaryRows}">
         <table class="outline" width="100%">
         <c:forEach var="row" items="${summaryRows}" >
           <% stripe.reset(); %>
           <table class="outline" width="100%">
           <tr class="${geneResult.next}"><td colspan="2"><b>${row.symbol}&nbsp;&nbsp;${row.name}</b>&nbsp;&nbsp;
	<c:if test="${not empty row.synonyms}">
             (Synonyms:
             <c:forEach var="synonym" items="${row.synonyms}" varStatus="status"
>${synonym}<c:if test="${!status.last}">, </c:if></c:forEach>)
           </c:if>
</td></tr>
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

</div>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

