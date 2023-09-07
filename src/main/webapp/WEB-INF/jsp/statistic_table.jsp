<%-- 
	renders tables of statistics to be retrieved via Ajax from mini home pages, all stats page, etc.
--%>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<style>
.statisticHelpIcon {
	margin-bottom: -1px;
	margin-left: 3px;
	cursor: pointer;
	width: 12px;
}
</style>
<table class="statsTable small">
	<c:forEach var="statistic" items="${statistics}">
	  <tr><td>${statistic.commaDelimitedValue}</td>
	  <td>${statistic.name}
	  <c:if test="${not empty statistic.tooltip}">
		<img src="${configBean.FEWI_URL}assets/images/help_icon_16.png" class="statisticHelpIcon" title="${statistic.tooltip}"/>
	  </c:if>
	  </td></tr>
	</c:forEach>
</table>