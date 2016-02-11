<% /* assumes 'ss' is a SubSNP and formats it into one or more table rows,
    * one per population
    */
%>
<c:set var="rowspan" value=""/>
<c:set var="popCount" value="0"/>
<c:forEach var="pop" items="${ss.populations}">
  <c:if test="${fn:length(pop.alleles) > 0}">
    <c:set var="popCount" value="${popCount + 1}"/>
  </c:if> 
</c:forEach>
<c:if test="${popCount > 1}">
  <c:set var="rowspan" value=" rowspan='${popCount}'"/>
</c:if>

<c:set var="i" value="0"/>
<c:forEach var="pop" items="${ss.populations}">
  <c:if test="${fn:length(pop.alleles) > 0}">
    <c:set var="i" value="${i + 1}"/>
    <c:if test="${i == 1 }">
      <tr>
	<td<c:if test="${ss.exemplar}"> class="exemplar"</c:if>${rowspan}><a href="${fn:replace(externalUrls.SubSNP, '@@@@', ss.accid)}" target="_blank">${ss.accid}</a></td>
	<td${rowspan}><a href="${fn:replace(externalUrls.SubmitterSNP, '@@@@', ss.submitterId)}" target="_blank">${ss.submitterId}</a></td>
	<td><a href="${fn:replace(externalUrls.Submitter_Handle, '@@@@', pop.subHandleName)}" target="_blank">${pop.subHandleName}</a></td>
	<td><a href="${fn:replace(externalUrls.SubSNP_Population, '@@@@', pop.accid)}" target="_blank">${pop.populationName}</a></td>
	<td${rowspan}>${ss.orientation}</td>
	<td${rowspan}>${ss.variationClass}</td>
	<c:set var="alleles" value="${pop.alleles}"/>
	<%@ include file="SNPDetail_Assays_Alleles.jsp" %>
      </tr>
    </c:if>
    <c:if test="${i > 1 }">
      <tr>
	<td>${pop.subHandleName}</td>
	<td>${pop.populationName}</td>
	<c:set var="alleles" value="${pop.alleles}"/>
	<%@ include file="SNPDetail_Assays_Alleles.jsp" %>
      </tr>
    </c:if>
  </c:if>
</c:forEach>
