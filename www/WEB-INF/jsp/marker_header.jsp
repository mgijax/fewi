<!-- header table -->
<table class="summaryHeader">
<tr>
  <td class="summaryHeaderCat1">
       <span class="label">Symbol</span><br/>
       <span class="label">Name</span><br/>
       <span class="label">ID</span>
  </td>
  <td class="summaryHeaderData1">
    <a href="${configBean.FEWI_URL}marker/${marker.primaryID}" class="symbolLink">${marker.symbol}</a> 
      	<c:if test="${marker.status == 'interim'}">(Interim)</c:if><br/>
    <span>${marker.name}</span><br/>
    <span>${marker.primaryID}</span>
  </td>
</tr>
</table>