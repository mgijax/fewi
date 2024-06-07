	<div class="row" id="snpRibbon">
		<div class="header <%=leftTdStyles.getNext() %>" id="snpRibbonLabel">
			SNPs
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon" id="snpRibbonDetails">
			<div id="snpToggle" title="Show More" class="toggleImage hdExpand">more</div>
			<div id="snpTeaderWrapper" class="flex">
				<div id="snpTableTeaser" class="summarySec1snp flex">
					<img id="heatmap_icon" src="${configBean.FEWI_URL}assets/images/heatmap_icon.png" onClick="$('#snpToggle').click();" />
					<div id="snpProfileLabel" class="label">SNP Profile Heat Map</div>
				</div>
				<div id="snpCounts" class="summarySec2snp">
				<ul>
					<li>
						<div class="label rightLabelSnp">SNP Calls Involving 
							<c:if test='${fn:length(strain.name) > 21}'>
								this strain
							</c:if>
							<c:if test='${fn:length(strain.name) <= 21}'>
								<fewi:super value="${strain.name}"/>
							</c:if>
						</div>
						<div id="snpCount" class="valueNarrow"><fmt:formatNumber type="number" value="${strain.snpCount}" maxFractionDigits="0" groupingUsed="true"/> from ${snpBuildNumber}</div>
					</li>
					<li>
						<div class="label rightLabelSnp">Comparison Strains</div>
						<div id="comparisonStrainCount" class="valueNarrow">${strain.countOfSnpComparisonStrains}</a></div>
					</li>
				</ul>
				</div>
			</div>
			<div id="snpContainer" class="extra closed">
				<!-- populated by Ajax -->
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
	#snpTableHeader th { font-weight: bold; white-space: nowrap; }
    #snpTableHeader td { border: 1px solid black; }
	#snpTable th { font-weight: bold; white-space: nowrap; }
    #snpTable td { border: 1px solid black; }
    .cell { min-width: 29px; max-width: 29px; width: 29px; height: 29px; }
    .snpHeaderCell { min-width: 29px; max-width: 29px; width: 29px; height: 20px; text-align: center; vertical-align: bottom; }
    #snpLeftDiv { margin-left: 20px; }
    #snpRightDiv { margin-left: 40px; }
    #snpContainer { display: flex; }
    #snpLegend { margin-top: 3px; margin-left: 30px; }
    #snpLegend td { border: 1px solid black; }
    #legendLabel { font-weight: bold; margin-left: 73px; }
    .snpLeftColumn { min-width: 215px; max-width: 215px; width: 215px; height: 20px; }
    #snpTableDiv {}
    .rlPad { padding-left: 3px; padding-right: 3px; }
    .snpChromosomeHeader { padding-bottom: 4px; text-align: center }
    .sortArrow { margin-left: 1px; }
    #sameDiffTable td { max-width: 225px; border: none; padding-right: 4px; vertical-align: top; line-height: 1.5em; }
    #sameDiffLabel { font-weight: bold; margin-left: 80px; }
    .slash { background-image: linear-gradient(to bottom right, rgb(0,0,0,0) 48%, black, rgb(0,0,0,0) 52% ); }
    .flex { display: flex; }
    #snpTableTeaser {}
    #snpCounts {}
    #heatmap_icon { height: 40px; width: 40px; margin-left: 80px; border: 1px solid #333333; }
    #snpProfileLabel { margin-left: -20px; margin-top: 17px; }
	</style>
	
