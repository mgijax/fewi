<%-- 
	Sub template for rendering the Recombinase minihome counts
--%>

<span class="headerDate">${databaseDate}</span>

<table class="statsTable small">
	<c:forEach var="statistic" items="${statistics}">
	  <tr><td>${statistic.value}</td><td>${statistic.name}</td></tr>
	</c:forEach>
</table>
<a class="small homeLink" href="${configBean.WI_URL}homepages/stats/all_stats.shtml#allstats_cre">More...</a>