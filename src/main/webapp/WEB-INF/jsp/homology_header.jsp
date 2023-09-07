<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!-- header table -->
<table class="summaryHeader">
<tr>
  <td class="summaryHeaderCat1">
       <span class="label">Source</span><br/>
       <c:if test="${not empty homology.primaryID}">
       <span class="label">Class ID</span>
       </c:if>
  </td>
  <td class="summaryHeaderData1">
  <c:if test='${source == "Alliance Clustered"}'>
    <span>Alliance of Genome Resources</span><br/>
  </c:if>
  <c:if test='${source == "Alliance Direct"}'>
    <span>Alliance of Genome Resources</span><br/>
  </c:if>
  </td>
</tr>
</table>
