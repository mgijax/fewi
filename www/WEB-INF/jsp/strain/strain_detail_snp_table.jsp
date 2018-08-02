<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.StrainSnpCell" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page import = "org.jax.mgi.fewi.config.ContextLoader" %>

<%@ page trimDirectiveWhitespaces="true" %>

				<div id="snpLeftDiv">
				<table id="snpTableHeader">
					<tr><th></th><th colspan="22" class="snpChromosomeHeader">Chromosomes<img id="snpTableHelpImage" src="${configBean.FEWI_URL}assets/images/help_icon_16.png" style="margin-bottom: -3px; margin-left: 3px; cursor: pointer;"/></th></tr>
							<div id="snpTableHelp" style="visibility: hidden;">
								<div class="hd">SNP Data Overview</div>
								<div class="bd" style="text-align: left">
									Click a column heading to sort rows by that column's value in each.  Click the same
									heading to reverse the order.  Chromosome columns sort by descending number of SNPs
									by default, while the default Strain order for the Comparison Strain column is ascending. 
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
							<td title="${cell.allCountComma} SNP<c:if test='${cell.allCount > 1}'>s</c:if>" class="cell"
								style="background-color: <%= FormatHelper.getSnpColorCode(cell.getAllCount(), maxCount) %>"
								<c:if test='${cell.allCount > 0}'>
								onClick="window.open('${configBean.FEWI_URL}snp/summary?selectedChromosome=${cell.chromosome}&coordinate=0-200&coordinateUnit=Mbp&selectedStrains=${row.comparisonStrainName}&referenceStrain=${strain.name}&searchBySameDiff=&selectedTab=1');"
								</c:if>
								></td>
						</c:forEach>
						</tr>
					</c:forEach>
				  </table>
				</div>
				</div>
				<div id="snpRightDiv">
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
	YAHOO.snp.container.snpTableHelp = new YAHOO.widget.Panel("snpTableHelp", { width:"360px", draggable:false, visible:false, constraintoviewport:true } );
	YAHOO.snp.container.snpTableHelp.render();
	YAHOO.util.Event.addListener("snpTableHelpImage", "click", YAHOO.snp.container.snpTableHelp.show, YAHOO.snp.container.snpTableHelp, true);
	</script>