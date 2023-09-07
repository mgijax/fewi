<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fe.datamodel.StrainSnpCell" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page import = "org.jax.mgi.fewi.config.ContextLoader" %>

<%@ page trimDirectiveWhitespaces="true" %>

<c:choose>
	<c:when test="${mode == 'same'}">
		<c:set var="qfMode" value="${same_reference}"/>
	</c:when>
	<c:when test="${mode == 'diff'}">
		<c:set var="qfMode" value="${diff_reference}"/>
	</c:when>
	<c:otherwise>
		<c:set var="qfMode" value=""/>
	</c:otherwise>
</c:choose>
				<div id="snpLeftDiv">
				<table id="snpTableHeader">
					<tr><th></th><th colspan="22" class="snpChromosomeHeader">Chromosomes</th></tr>
							<div id="snpTableHelp" style="visibility: hidden;">
								<div class="hd">SNP Data Overview</div>
								<div class="bd" style="text-align: left">
									Hover over a colored cell to see the number of SNPs.<p/>
									Click on a cell to see the SNPs and refine your search.<p/>
									Click on a column heading to sort. 
								</div>
							</div>
					<tr>
						<th id='comparisonStrainLabel' class='snpLeftColumn'>Comparison Strain <fewi:sortIcon column="strain" sortBy="${sortBy}" dir="${dir}"/></th>
						<c:forEach var="chrom" items="${strain.snpChromosomes}">
							<th class="snpHeaderCell">${chrom}<fewi:sortIcon column="${chrom}" sortBy="${sortBy}" dir="${dir}"/></th>
						</c:forEach>
					</tr>
				</table>
				<div id="snpTableDiv">
				  <table id="snpTable">
					<% Integer maxCount = (Integer) request.getAttribute("maxSnpCount"); %>
					<c:forEach var="row" items="${snpRows}">
						<tr>
						<td class='snpLeftColumn'><a href="${configBean.FEWI_URL}strain/${row.comparisonStrainID}" target="_blank"><fewi:super value="${row.comparisonStrainName}"/></a></td>
						<c:forEach var="cell" items="${row.cells}">
							<c:set var="reqCell" value="${cell}" scope="request"/>
							<% StrainSnpCell cell = (StrainSnpCell) request.getAttribute("reqCell"); %>

							<c:choose>
								<c:when test="${mode == 'same'}">
									<c:set var="cellCount" value="${cell.sameCount}" scope="request"/>
									<c:set var="countWithComma" value="${cell.sameCountComma}"/>
								</c:when>
								<c:when test="${mode == 'diff'}">
									<c:set var="cellCount" value="${cell.differentCount}" scope="request"/>
									<c:set var="countWithComma" value="${cell.diffCountComma}"/>
								</c:when>
								<c:otherwise>
									<c:set var="cellCount" value="${cell.allCount}" scope="request"/>
									<c:set var="countWithComma" value="${cell.allCountComma}"/>
								</c:otherwise>
							</c:choose>
							
							<c:set var="flagCell" value=""/>
							<c:choose>
								<c:when test="${cell.allCount == 0}">
									<c:set var="cellTitle" value="no data"/>
									<c:set var="flagCell" value="slash"/>
								</c:when>
								<c:when test="${cellCount == 1}">
									<c:set var="cellTitle" value="1 SNP"/>
								</c:when>
								<c:otherwise>
									<c:set var="cellTitle" value="${countWithComma} SNPs"/>
								</c:otherwise>
							</c:choose>

							<% Integer cellCount = (Integer) request.getAttribute("cellCount"); %>
							<td title="${cellTitle}" class="cell ${flagCell}"
								style="background-color: <%= FormatHelper.getSnpColorCode(cellCount, cell.getAllCount(), maxCount) %>"
								<c:if test='${cellCount > 0}'>
								onClick="window.open('${configBean.FEWI_URL}snp/summary?selectedChromosome=${cell.chromosome}&coordinate=0-${chromosomeSize.get(cell.chromosome)}&coordinateUnit=bp&selectedStrains=${row.comparisonStrainName}&referenceStrains=${strain.name}&alleleAgreementFilter=${qfMode}&selectedTab=1');"
								</c:if>
								></td>
						</c:forEach>
						</tr>
					</c:forEach>
				  </table>
				</div>
				</div>
				<div id="snpRightDiv">
					<div id="heatmapHelp"><a href="" onClick="return false;">How to use the SNP Heat Map</a>
						<img id="snpTableHelpImage" src="${configBean.FEWI_URL}assets/images/help_icon_16.png" style="margin-bottom: -3px; margin-left: 3px; cursor: pointer;"/>
					</div>
					<div id="snpViewPanel">
					<p/>
					<span id="sameDiffLabel">View</span><br/>
					<table id="sameDiffTable">
					<tr>
						<td><input name="mode" type="radio" id="mode1" value="all"/></td>
						<td>all SNPs</td>
					</tr>
					<tr>
						<td><input name="mode" type="radio" id="mode2" value="same"/></td>
						<td>SNPs where alleles are the same as <fewi:super value="${strain.name}"/></td>
					</tr>
					<tr>
						<td><input name="mode" type="radio" id="mode3" value="diff"/></td>
						<td>SNPs where alleles are different from <fewi:super value="${strain.name}"/></td>
					</tr>
					</table>
					<p/>
					<span id="legendLabel">Legend</span><br/>
					<table id="snpLegend">
						<tr><td class="cell slash" style=""></td>
							<td class="rlPad">No data</td></tr>
						<tr><td rowspan="6" class="cell" style="background: linear-gradient(
							<%= FormatHelper.getSnpColorCode(1, 1, maxCount) %>, <%= FormatHelper.getSnpColorCode(maxCount, maxCount, maxCount) %>);"></td>
							<td class="rlPad" style="height: 29px">1 SNP</td></tr>
						<c:set var="snpBins" value="100 1000 10000 100000 ${maxSnpCount}"/>
						<c:forEach var="bin" items="${fn:split(snpBins, ' ')}">
							<tr><td class="rlPad" style="height: 29px"><fmt:formatNumber type="number" value="${bin}" maxFractionDigits="0" groupingUsed="true"/> SNPs</td></tr>
						</c:forEach>
					</table>
					</div>
				</div>
	<script>
	YAHOO.namespace("snp.container");
	YAHOO.snp.container.snpTableHelp = new YAHOO.widget.Panel("snpTableHelp", { width:"390px", draggable:false, visible:false, constraintoviewport:true } );
	YAHOO.snp.container.snpTableHelp.render();
	YAHOO.util.Event.addListener("heatmapHelp", "click", YAHOO.snp.container.snpTableHelp.show, YAHOO.snp.container.snpTableHelp, true);
	YAHOO.util.Event.addListener("snpTableHelpImage", "click", YAHOO.snp.container.snpTableHelp.show, YAHOO.snp.container.snpTableHelp, true);
	</script>
	<style>
	#snpTableHelp_c { margin-left: 400px; }
	</style>
