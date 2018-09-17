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
		<span class='ysf'>SNP Density Heatmap</span><br/>
		<table id="heatmap">
			<tr id="heatmapColorRow">
				<c:forEach var="count" items="${sliceCounts}" varStatus="loop">
					<c:set var="color" value="${sliceColors.get(count)}"/>
					<c:set var="sliceStart" value="${sliceStartCoords.get(loop.index)}"/>
					<c:set var="sliceEnd" value="${sliceEndCoords.get(loop.index)}"/>

					<c:choose>
					<c:when test="${count > 0}">
						<td class="heatmapTD" style="background-color: ${color}"
							title='${count} SNPs from <fmt:formatNumber type="number" groupingUsed="true" value="${sliceStart}"/> to <fmt:formatNumber type="number" groupingUsed="true" value="${sliceEnd}"/>'
							onClick="window.location='${configBean.FEWI_URL}snp/summary?sliceStartCoord=${sliceStart}&sliceEndCoord=${sliceEnd}&sliceMaxCount=${sliceMaxCount}&' + getFullRangeQuerystring()"></td>
					</c:when>
					<c:otherwise>
						<td class="heatmapTD" style="background-color: ${color}"
							title='${count} SNPs from <fmt:formatNumber type="number" groupingUsed="true" value="${sliceStart}"/> to <fmt:formatNumber type="number" groupingUsed="true" value="${sliceEnd}"/>'></td>
					</c:otherwise>
					</c:choose>
				</c:forEach>
			</tr>
			<tr id="heatmapInfoRow">
				<td colspan="${numberOfBins}">
					<div>
						<div style="float:left">
							Chr${chromosome} from ${prettyStart} to ${prettyEnd}
						</div>
						<div style="float:right">
							(${prettyRange})
						</div>
					</div>
				</td>
			</tr>
			<c:if test="${not empty snpQueryForm.sliceMaxCount}">
				<tr id="unzoomRow">
					<td colspan="${numberOfBins}">
						<a id='unzoom' href='#' onClick="$('#unzoom')[0].href='${configBean.FEWI_URL}snp/summary?' + getFullRangeQuerystring()">Return to original search boundary</a>
					</td>
				</tr>
				<script>
					// allow extra room for the new line
					$('#hideStrainsDiv').css('padding-bottom', '32px');
				</script>
			</c:if>
		</table>
	</c:otherwise>
</c:choose>
<style>
#heatmap { margin-top: 3px; }
#heatmapColorRow { border: 1px solid black; }
#heatmapInfoRow { text-align: center; }
#unzoomRow { text-align: center; }
.heatmapTD { border-right: 1px solid black; cursor: pointer; height: 1em; width: 20px; cell-padding: 0px; cell-spacing: 0px; }
</style>