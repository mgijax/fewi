<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.StrainSnpCell" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page import = "org.jax.mgi.fewi.config.ContextLoader" %>

<%@ page trimDirectiveWhitespaces="true" %>

<c:choose>
	<c:when test="${mode == 'same'}">
		<c:set var="qfMode" value="same_reference"/>
	</c:when>
	<c:when test="${mode == 'diff'}">
		<c:set var="qfMode" value="diff_reference"/>
	</c:when>
	<c:otherwise>
		<c:set var="qfMode" value=""/>
	</c:otherwise>
</c:choose>
				<div id="snpLeftDiv">
				<table id="snpTableHeader">
					<tr><th></th><th colspan="22" class="snpChromosomeHeader">Chromosomes<img id="snpTableHelpImage" src="${configBean.FEWI_URL}assets/images/help_icon_16.png" style="margin-bottom: -3px; margin-left: 3px; cursor: pointer;"/></th></tr>
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
					<% int maxCount = (int) request.getAttribute("maxSnpCount"); %>
					<c:forEach var="row" items="${snpRows}">
						<tr>
						<td class='snpLeftColumn'><a href="${configBean.FEWI_URL}strain/${row.comparisonStrainID}" target="_blank">${row.comparisonStrainName}</a></td>
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
								style="background-color: <%= FormatHelper.getSnpColorCode(cellCount, maxCount) %>"
								<c:if test='${cellCount > 0}'>
								onClick="window.open('${configBean.FEWI_URL}snp/summary?selectedChromosome=${cell.chromosome}&coordinate=0-200&coordinateUnit=Mbp&selectedStrains=${row.comparisonStrainName}&referenceStrain=${strain.name}&searchBySameDiff=${qfMode}&selectedTab=1');"
								</c:if>
								></td>
						</c:forEach>
						</tr>
					</c:forEach>
				  </table>
				</div>
				</div>
				<div id="snpRightDiv">
					<span id="sameDiffLabel">View</span><br/>
					<table id="sameDiffTable">
					<tr>
						<td><input name="mode" type="radio" id="mode1" value="all"/></td>
						<td>all SNPs</td>
					</tr>
					<tr>
						<td><input name="mode" type="radio" id="mode2" value="same"/></td>
						<td>SNPs where alleles are the same as ${strain.name}</td>
					</tr>
					<tr>
						<td><input name="mode" type="radio" id="mode3" value="diff"/></td>
						<td>SNPs where alleles are different from ${strain.name}</td>
					</tr>
					</table>
					<p/>
					<span id="legendLabel">Legend</span><br/>
					<!-- Values need to be kept in sync with fedatamodel's StrainSnpCell class. -->
					<table id="snpLegend">
						<c:set var="snpBins" value="1-9 10-99 100-999 1000-9999 10000-99999 100000-${maxSnpCount}"/>
						<c:forEach var="bin" items="${fn:split(snpBins, ' ')}">
							<c:set var="fromTo" value="${fn:split(bin, '-')}"/>
							<c:set var="reqLastStart" value="${fromTo[0]}" scope="request"/>
							<c:set var="reqBin" value="${fromTo[1]}" scope="request"/>
							<% int lastStart = (int) Integer.valueOf((String) request.getAttribute("reqLastStart")); %>
							<% int sbin = (int) Integer.valueOf((String) request.getAttribute("reqBin")); %>
							<tr><td class="cell" style="background-color: <%= FormatHelper.getSnpColorCode(lastStart, maxCount) %>"></td>
							<td class="cell" style="background-color: <%= FormatHelper.getSnpColorCode(sbin, maxCount) %>"></td>
							<td class="rlPad"><fmt:formatNumber type="number" value="${reqLastStart}" maxFractionDigits="0" groupingUsed="true"/>-<fmt:formatNumber type="number" value="${reqBin}" maxFractionDigits="0" groupingUsed="true"/> SNPs</td></tr>
						</c:forEach>
					</table>
				</div>
	<script>
	YAHOO.namespace("snp.container");
	YAHOO.snp.container.snpTableHelp = new YAHOO.widget.Panel("snpTableHelp", { width:"390px", draggable:false, visible:false, constraintoviewport:true } );
	YAHOO.snp.container.snpTableHelp.render();
	YAHOO.util.Event.addListener("snpTableHelpImage", "click", YAHOO.snp.container.snpTableHelp.show, YAHOO.snp.container.snpTableHelp, true);
	</script>