	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>" id="qtlRibbonLabel">
			QTL Mapped<br/>
			with this<br/>
			Strain
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1 ">
				<span class="indented">
		    	<span id="strainQtlCount">${fn:length(strain.qtls)}</span> associated QTL</span>
		    	<span id="qtlButton" class="searchToolButton indented hidden">Show All</span>
		    	<br/>
			    <div id="qtlSummaryDiv" style="max-height: 125px; overflow-y: auto; overflow-x: hidden; margin-top: 5px; margin-left: 15px; max-width: 95%" id="qtlDiv">
				    <table id="qtlSummaryTable">
				    <tr>
				    	<th>Allele</th>
				    	<th>Gene</th>
				    </tr>
				    <c:forEach var="qtl" items="${strain.qtls}">
				    	<tr>
				    		<td><c:if test="${not empty qtl.alleleID}">
				    			<a href="${configBean.FEWI_URL}allele/${qtl.alleleID}"><fewi:super value="${qtl.alleleSymbol}"/></a>
				    			</c:if></td>
				    		<td><c:if test="${not empty qtl.markerID}">
				    			<a href="${configBean.FEWI_URL}marker/${qtl.markerID}"><fewi:super value="${qtl.markerSymbol}"/></a>
				    			</c:if></td>
				    	</tr>
				    </c:forEach> 
					</table>
			    </div>
			</section>
		</div>
	</div>
	<script>
	// fix width of DIV containing table and adjust the header color
	$('#qtlSummaryDiv').width($('#qtlSummaryTable').width() + 20);
	$('#qtlSummaryTable th').css('background-color', $('#qtlRibbonLabel').css('background-color'));
	$('#qtlButton').click(function() {
		// section is already expanded, so contract it
		if ($('#qtlSummaryDiv').css('max-height') == 'none') {
			$('#qtlSummaryDiv').css('max-height', '125px');
			$('#qtlButton').html('Show All');
		} else {
			$('#qtlSummaryDiv').css('max-height', 'none');
			$('#qtlButton').html('Show Less');
		}
	});
	if ($('#qtlSummaryDiv').height() >= 124) {
		$('#qtlButton').removeClass('hidden');
	}
	</script>