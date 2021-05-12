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
	<td<c:if test="${ss.exemplar}"> class="exemplar"</c:if>${rowspan}>${ss.accid}</td>
	<td${rowspan}>${ss.submitterId}</td>
	<td>${pop.subHandleName}</td>
	<td>${pop.populationName}</td>
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
