<%-- 
	renders tables of statistics to be retrieved via Ajax from mini home pages, all stats page, etc.
--%>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<table class="statsTable small">
	<c:forEach var="statistic" items="${statistics}">
	  <tr><td>${statistic.commaDelimitedValue}</td>
	  <c:choose>
	  	<c:when test="${not empty statistic.tooltip}">
		  <td><span title="${statistic.tooltip}">${statistic.name}</span></td></tr>
	  	</c:when>
	  	<c:otherwise>
		  <td>${statistic.name}</td></tr>
	  	</c:otherwise>
	  </c:choose>
	</c:forEach>
</table>