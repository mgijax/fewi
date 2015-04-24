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
  <c:if test='${source == "HomoloGene"}'>
    <span>${homology.source} (${homology.version}, <fmt:formatDate type="date" value="${homology.date}" />)</span><br/>
    <span><a href="${fn:replace(externalUrls.HomoloGene, "@@@@", homology.primaryID)}" target="_blank" class="extUrl">${homology.primaryID}</a></span>
  </c:if>
  <c:if test='${source == "HGNC"}'>
    <span>HUGO Gene Nomenclature Committee (HGNC)</span><br/>
  </c:if>
  <c:if test='${source == "hybrid"}'>
  <span>${homology.secondarySource}</span><br/>
  </c:if>
  </td>
</tr>
</table>
