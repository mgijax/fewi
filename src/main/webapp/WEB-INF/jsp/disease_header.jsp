<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!-- header table -->
<table class="summaryHeader">
<tr>
  <td class="summaryHeaderCat1">
       <span class="label">Human Disease</span><br/>
       <span class="label">DO ID</span>
  </td>
  <td class="summaryHeaderData1">
	<span><a href="${configBean.FEWI_URL}disease/${disease.primaryID}">${disease.disease}</a></span><br/>
    <span>${disease.primaryID}</span>
  </td>
</tr>
</table>
