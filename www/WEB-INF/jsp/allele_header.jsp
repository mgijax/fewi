<!-- header table -->
<table class="summaryHeader">
<tr >
  <td class="summaryHeaderCat1">
       <b>Symbol</b><br>
       <b>Name</b><br>
       <b>ID</b>
  </td>
  <td class="summaryHeaderData1">
  	<% Allele myAllele = (Allele)request.getAttribute("allele"); %>
    <a style="font-size:x-large;  font-weight: bold;" 
      href="${configBean.FEWI_URL}marker/${allele.primaryID}">
      <%=FormatHelper.superscript(myAllele.getSymbol())%></a>
    <br/>
    ${allele.name}<br>
    ${allele.primaryID}</td>
</tr>
</table>
