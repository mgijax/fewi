	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>" id="referenceRibbonLabel">
			SNPs
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
				<ul>
					<li>
						<div class="label narrow">SNPs involving ${strain.name}</div>
						<div id="snpCount" class="valueNarrow"><fmt:formatNumber type="number" value="${strain.snpCount}" maxFractionDigits="0" groupingUsed="true"/></div>
					</li>
					<li>
						<div class="label narrow">Comparison Strains</div>
						<div id="comparisonStrainCount" class="valueNarrow">${strain.countOfSnpComparisonStrains}</a></div>
					</li>
				</ul>
			<div id="snpContainer">
				<div id="snpLeftDiv">
				<table id="snpTable">
					<tr>
						<th class='snpLeftColumn'>Comparison Strain</th>
						<c:forEach var="chrom" items="${strain.snpChromosomes}">
							<th class="snpHeaderCell">${chrom}</th>
						</c:forEach>
					</tr>
					<% int maxCount = (int) request.getAttribute("maxSnpCount"); %>
					<c:forEach var="row" items="${strain.snpRows}">
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
				<div id="snpRightDiv">
					<span id="legendLabel">Legend</span><br/>
					<!-- Values need to be kept in sync with fedatamodel's StrainSnpCell class. -->
					<table id="snpLegend">
						<c:set var="snpBins" value="10 100 1000 10000 100000 ${maxSnpCount}"/>
						<c:forEach var="bin" items="${fn:split(snpBins, ' ')}">
							<c:set var="reqBin" value="${bin}" scope="request"/>
							<% int bin = (int) Integer.valueOf((String) request.getAttribute("reqBin")); %>
							<tr><td class="cell" style="background-color: <%= FormatHelper.getSnpColorCode(bin, maxCount) %>"></td>
							<td><fmt:formatNumber type="number" value="${bin}" maxFractionDigits="0" groupingUsed="true"/> SNPs</td></tr>
						</c:forEach>
					</table>
				</div>
			</div>
		</div>
	</div>
	
	<style>
	.colorBin0 { background-color: #FFFFFF; }
	.colorBin1 { background-color: #CCFFFF; }
	.colorBin2 { background-color: #66CCFF; }
	.colorBin3 { background-color: #3399FF; }
	.colorBin4 { background-color: #0066FF; }
	.colorBin5 { background-color: #0000FF; }
	.colorBin6 { background-color: #0000CC; }
	#snpTable th { font-weight: bold; white-space: nowrap; }
    #snpTable td { border: 1px solid black; }
    .cell { width: 20px; height: 20px; }
    .snpHeaderCell { width: 20px; height: 20px; text-align: center; }
    #snpLeftDiv { margin-left: 20px; }
    #snpRightDiv { margin-left: 40px; }
    #snpContainer { display: flex; }
    #snpLegend { margin-top: 3px; }
    #snpLegend td { border: 1px solid black; }
    #legendLabel { font-weight: bold; margin-left: 31px; }
    .snpLeftColumn { width: 215px; height: 20px; }
	</style>