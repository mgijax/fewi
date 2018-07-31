	<div class="row" id="snpRibbon">
		<div class="header <%=leftTdStyles.getNext() %>" id="snpRibbonLabel">
			SNPs
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon" id="snpRibbonDetails">
			<div id="snpToggle" title="Show More" class="toggleImage hdExpand">more</div>
				<ul>
					<li>
						<div class="label narrow">Involving ${strain.name}</div>
						<div id="snpCount" class="valueNarrow"><fmt:formatNumber type="number" value="${strain.snpCount}" maxFractionDigits="0" groupingUsed="true"/></div>
					</li>
					<li>
						<div class="label narrow">Comparison Strains</div>
						<div id="comparisonStrainCount" class="valueNarrow">${strain.countOfSnpComparisonStrains}</a></div>
					</li>
				</ul>
			<div id="snpContainer" class="extra closed">
				<c:set var="snpRows" value="${strain.snpRows}"/>
			    <%@ include file="strain_detail_snp_table.jsp" %>
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
    .cell { min-width: 20px; max-width: 20px; width: 20px; height: 20px; }
    .snpHeaderCell { min-width: 20px; max-width: 20px; width: 20px; height: 20px; text-align: center; }
    #snpLeftDiv { margin-left: 20px; }
    #snpRightDiv { margin-left: 40px; }
    #snpContainer { display: flex; }
    #snpLegend { margin-top: 3px; }
    #snpLegend td { border: 1px solid black; }
    #legendLabel { font-weight: bold; margin-left: 73px; }
    .snpLeftColumn { min-width: 215px; max-width: 215px; width: 215px; height: 20px; }
    #snpTableDiv {
    	max-height: 200px;
    	overflow-y: auto;
    }
    .rlPad { padding-left: 3px; padding-right: 3px; }
    .snpChromosomeHeader { padding-bottom: 4px; text-align: center }
	</style>

	<script>
	// seems silly to make a table border the same color as the background, but we need it to help the
	// header cells line up with the color cells in the scrollable table below
	var snpHeaderBorderColor = $('#snpContainer').parent().css('background-color');
	$('#snpTableHeader th').css({
		'border-left' : '1px solid ' + snpHeaderBorderColor,
		'border-right' : '1px solid ' + snpHeaderBorderColor
	});
	</script>