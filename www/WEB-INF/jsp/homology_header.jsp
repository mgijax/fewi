<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!-- header table -->
<table class="summaryHeader">
<tr>
  <td class="summaryHeaderCat1">
       <span class="label">Source</span><br/>
       <span class="label">Class ID</span>
  </td>
  <td class="summaryHeaderData1">
    <span>${homology.source} (${homology.version}, <fmt:formatDate type="date" value="${homology.date}" />)</span><br/>
    <span><a href="${fn:replace(externalUrls.HomoloGene, "@@@@", homology.primaryID)}" target="_blank" class="extUrl">${homology.primaryID}</a></span>
  </td>
</tr>
</table>
