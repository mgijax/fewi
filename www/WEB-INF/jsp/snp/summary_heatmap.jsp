<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page trimDirectiveWhitespaces="true" %>

<!-- heatmap for SNP summary where one range is returned -->
<c:choose>
	<c:when test="${not empty error}">
		<script>
		console.log('Heatmap error: ${error}');
		</script>
	</c:when>
	<c:otherwise>
		<table id="heatmap">
			<tr id="heatmapColorRow">
				<c:forEach var="count" items="${sliceCounts}">
					<c:set var="color" value="${sliceColors.get(count)}"/>

					<td class="heatmapTD" style="background-color: ${color}" title="${count} SNPs" onClick="window.open('${configBean.FEWI_URL}snp/summary?' + getQuerystring())"></td>
				</c:forEach>
			</tr>
			<tr id="heatmapInfoRow">
				<td colspan="${numberOfBins}">
					Chr${chromosome}
					from <fmt:formatNumber type="number" groupingUsed="true" value="${startCoordinate}"/>
					to <fmt:formatNumber type="number" groupingUsed="true" value="${endCoordinate}"/>
					(<fmt:formatNumber type="number" groupingUsed="true" value="${endCoordinate - startCoordinate + 1}"/> bp)
				</td>
			</tr>
		</table>
	</c:otherwise>
</c:choose>
<style>
#heatmapColorRow { border: 1px solid black; }
#heatmapInfoRow { text-align: center; }
.heatmapTD { border: none; cursor: pointer; height: 1em; width: 20px; cell-padding: 0px; cell-spacing: 0px; }
</style>