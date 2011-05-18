<!-- header table -->
<table class="summaryHeader">
<tr >
  <td class="summaryHeaderCat1">
       <span class="label">Symbol</span><br/>
       <span class="label">Name</span><br/>
       <span class="label">ID</span>
  </td>
  <td class="summaryHeaderData1">
  	<% Allele myAllele = (Allele)request.getAttribute("allele"); %>
    <a href="${configBean.FEWI_URL}marker/${allele.primaryID}" class="symbolLink">
      <%=FormatHelper.superscript(myAllele.getSymbol())%></a><br/>
    <span>${allele.name}</span><br/>
    <span>${allele.primaryID}</span>
  </td>
</tr>
</table>
