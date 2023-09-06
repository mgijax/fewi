<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!-- header table -->
<table class="summaryHeader">
<tr>
  <td class="summaryHeaderCat1">
       <span class="label">Name</span><br/>
       <span class="label">ID</span>
  </td>
  <td class="summaryHeaderData1">
    <a href="${configBean.FEWI_URL}strain/${strain.primaryID}" class="symbolLink"><fewi:super value="${strain.name}"/></a><br/>
    <span>${strain.primaryID}</span>
  </td>
</tr>
</table>
