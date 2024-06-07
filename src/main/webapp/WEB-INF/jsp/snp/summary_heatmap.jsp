<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.forms.SnpQueryForm" %>
<%@ page trimDirectiveWhitespaces="true" %>

<!-- heatmap for SNP summary where one range is returned -->
<c:choose>
	<c:when test="${not empty error}">
		<script>
		console.log('Heatmap error: ${error}');
		</script>
	</c:when>
	<c:otherwise>
                <c:set var="minCount" value="999999999" scope="request"/>
                <c:set var="maxCount" value="1" scope="request"/>
                <c:forEach var="count" items="${sliceCounts}" varStatus="loop">
                        <c:if test="${count > 0 && count < minCount}">
                                <c:set var="minCount" value="${count}" scope="request"/>
                        </c:if>
                        <c:if test="${count > maxCount}">
                                <c:set var="maxCount" value="${count}" scope="request"/>
                        </c:if>
                </c:forEach>

		<% Integer minCount = Integer.parseInt("" + request.getAttribute("minCount")); %>
		<% Integer maxCount = Integer.parseInt("" + request.getAttribute("maxCount")); %>
		<div id="snpHeatmapHelp" style="visibility: hidden;">
			<div class="hd">SNP Density Heatmap Overview</div>
			<div class="bd" style="text-align: left">
				Each cell represents a ${binSize} span of the region matching your search.<p/>
				The colors represent the relative SNP density across the sub-regions.<p/>
				Hover over a cell to see the exact number of SNPs.<p/>
				Click on a cell to refine your search to that sub-region.
                                Heat map colors readjust with each view.<p/>
				<div style="text-align: center">
				<b>Legend:</b><br/>
				<table id="heatmapLegend">
				<tr>
					<c:choose>
						<c:when test="${maxCount > 1}">
							<td style="text-align: left; width: 100px;">${minCount} SNPs</td>
							<td style="text-align: right; width: 100px">${maxCount} SNPs</td>
						</c:when>
						<c:otherwise>
							<td style="text-align: left; width: 200px;">1 SNP</td>
						</c:otherwise>
					</c:choose>
				</tr>
				<tr>
					<c:choose>
						<c:when test="${maxCount > 1}">
							<td colspan="2" style="background: linear-gradient(to right,
								<%= FormatHelper.getSnpColorCode(minCount, minCount, maxCount) %>,
								<%= FormatHelper.getSnpColorCode(maxCount, minCount, maxCount) %>);"></td>
						</c:when>
						<c:otherwise>
							<td style="background: linear-gradient(to right,
								<%= FormatHelper.getSnpColorCode(minCount, minCount, maxCount) %>,
								<%= FormatHelper.getSnpColorCode(maxCount, minCount, maxCount) %>);"></td>
						</c:otherwise>
					</c:choose>
				</tr>
				</table>
				</div>
			</div>
		</div>
		<span class='ysf'>SNP Density Heatmap</span>
		<img id="snpHeatmapHelpImage" src="${configBean.FEWI_URL}assets/images/help_icon_16.png" style="margin-bottom: -3px; margin-left: 3px; cursor: pointer;"/>
		<br/>
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
							onClick="window.location='${configBean.FEWI_URL}snp/summary?sliceStartCoord=${sliceStart}&sliceEndCoord=${sliceEnd}&sliceMaxCount=${sliceMaxCount}&sliceMinCount=${sliceMinCount}&' + getFullRangeQuerystring()"></td>
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
#heatmap { margin-top: 3px; width: 400px; }
#snpHeatmapHelp_c { margin-left: 470px; }
#heatmapColorRow { border: 1px solid black; }
#heatmapInfoRow { text-align: center; }
#unzoomRow { text-align: center; }
.heatmapTD { border-right: 1px solid black; cursor: pointer; height: 1em; width: 20px; cell-padding: 0px; cell-spacing: 0px; }
#heatmapLegend { margin-left: 70px; }
#heatmapLegend tr { border: 1px solid black; }
#heatmapLegend td { height: 1.3em; }
</style>
<script>
	// set up popup for help icon in pheno grid area
	YAHOO.namespace("heatmapHelp.container");
	YAHOO.heatmapHelp.container.shHelp = new YAHOO.widget.Panel("snpHeatmapHelp", { width:"360px", draggable:false, visible:false, constraintoviewport:true } );
	YAHOO.heatmapHelp.container.shHelp.render();
	YAHOO.util.Event.addListener("snpHeatmapHelpImage", "click", YAHOO.heatmapHelp.container.shHelp.show, YAHOO.heatmapHelp.container.shHelp, true);
</script>
