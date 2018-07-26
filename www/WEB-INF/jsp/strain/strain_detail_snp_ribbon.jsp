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
						<th>Comparison Strain</th>
						<c:forEach var="chrom" items="${strain.snpChromosomes}">
							<th class="center">${chrom}</th>
						</c:forEach>
					</tr>
					<c:forEach var="row" items="${strain.snpRows}">
						<tr>
						<td><a href="${configBean.FEWI_URL}strain/${row.comparisonStrainID}" target="_blank">${row.comparisonStrainName}</a></td>
						<c:forEach var="cell" items="${row.cells}">
							<td title="${cell.allCountComma} SNP<c:if test='${cell.allCount > 1}'>s</c:if>" class="cell colorBin${cell.colorBin}"
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
						<tr><td class="cell colorBin0"></td><td>0 SNPs</td></tr>
						<tr><td class="cell colorBin1"></td><td>1-9 SNPs</td></tr>
						<tr><td class="cell colorBin2"></td><td>10-99 SNPs</td></tr>
						<tr><td class="cell colorBin3"></td><td>100-349 SNPs</td></tr>
						<tr><td class="cell colorBin4"></td><td>350-499 SNPs</td></tr>
						<tr><td class="cell colorBin5"></td><td>500-999 SNPs</td></tr>
						<tr><td class="cell colorBin6"></td><td>1,000+ SNPs</td></tr>
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
    .center { text-align: center; }
    #snpLeftDiv { margin-left: 20px; }
    #snpRightDiv { margin-left: 40px; }
    #snpContainer { display: flex; }
    #snpLegend { margin-top: 3px; }
    #snpLegend td { border: 1px solid black; }
    #legendLabel { font-weight: bold; margin-left: 31px; }
	</style>