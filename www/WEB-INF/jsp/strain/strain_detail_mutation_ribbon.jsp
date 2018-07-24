	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>" id="mutationRibbonLabel">
			Associated<br/>
			Mutations,<br/>
			Markers,<br/>
			and QTL
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<!-- marker/allele mutation data -->
			<c:if test="${not empty strain.mutations}">
			<section class="summarySec1 msStyle" id="mutationSection">
				<span class="indented">
		    	<span id="strainMutationCount">${fn:length(strain.mutations)}</span> associated
		    	<c:choose>
			    	<c:when test="${fn:length(strain.mutations) > 1}">mutations</c:when>
			    	<c:otherwise>mutation</c:otherwise>
		    	</c:choose>
		    	</span>
		    	<span id="mutationButton" class="searchToolButton indented hidden">Show All</span><br/>
			    <div id="mutationSummaryDiv" style="max-height: 125px; overflow-y: auto; overflow-x: hidden; margin-top: 5px; margin-left: 15px; max-width: 95%; border-bottom: 1px solid black;" id="mutationsDiv">
				    <table id="mutationSummaryTable">
				    <tr>
				    	<th>Mutation Carried</th>
				    	<th>Marker</th>
				    </tr>
				    <c:forEach var="mutation" items="${strain.mutations}">
				    	<tr>
				    		<td><c:if test="${not empty mutation.alleleID}">
				    			<a href="${configBean.FEWI_URL}allele/${mutation.alleleID}"><fewi:super value="${mutation.alleleSymbol}"/></a>
				    			</c:if></td>
				    		<td><c:if test="${not empty mutation.markerID}">
				    			<a href="${configBean.FEWI_URL}marker/${mutation.markerID}" title="${mutation.markerName}"><fewi:super value="${mutation.markerSymbol}"/></a>
				    			</c:if></td>
				    	</tr>
				    </c:forEach> 
					</table>
			    </div>
			</section>
		    </c:if>

			<!-- QTL data -->
			<c:if test="${not empty strain.qtls}">
			<section class="summarySec1 qtlStyle" id="qtlSection" style="width:auto">
				<span class="indented">
		    	<span id="strainQtlCount">${fn:length(strain.qtls)}</span> associated QTL</span>
		    	<span id="qtlButton" class="searchToolButton indented hidden">Show All</span>
		    	<br/>
			    <div id="qtlSummaryDiv" style="max-height: 125px; overflow-y: auto; overflow-x: hidden; margin-top: 5px; margin-left: 15px; max-width: 95%; border-bottom: 1px solid black;" id="qtlDiv">
				    <table id="qtlSummaryTable">
				    <tr>
				    	<th>Allele</th>
				    	<th>Marker</th>
				    </tr>
				    <c:forEach var="qtl" items="${strain.qtls}">
				    	<tr>
				    		<td><c:if test="${not empty qtl.alleleID}">
				    			<a href="${configBean.FEWI_URL}allele/${qtl.alleleID}"><fewi:super value="${qtl.alleleSymbol}"/></a>
				    			</c:if></td>
				    		<td><c:if test="${not empty qtl.markerID}">
				    			<a href="${configBean.FEWI_URL}marker/${qtl.markerID}" title="${qtl.markerName}"><fewi:super value="${qtl.markerSymbol}"/></a>
				    			</c:if></td>
				    	</tr>
				    </c:forEach> 
					</table>
			    </div>
			</section>
			</c:if>
		</div>
	</div>
	<script>
	<c:if test="${not empty strain.mutations}">
	// fix width of DIV containing mutation table and adjust the header color
	$('#mutationSummaryDiv').width($('#mutationSummaryTable').width() + 20);
	$('#mutationSummaryTable th').css('background-color', $('#mutationRibbonLabel').css('background-color'))
	$('#mutationButton').click(function() {
		// section is already expanded, so contract it
		if ($('#mutationSummaryDiv').css('max-height') == 'none') {
			$('#mutationSummaryDiv').css('max-height', '125px');
			$('#mutationButton').html('Show All');
		} else {
			$('#mutationSummaryDiv').css('max-height', 'none');
			$('#mutationButton').html('Show Less');
		}
	});
	if ($('#mutationSummaryDiv').height() >= 124) {
		$('#mutationButton').removeClass('hidden');
	} else {
		// table is all showing (no scrolling), so we don't need the extra line on bottom
		$('#mutationSummaryDiv').css({ 'border-bottom' : 'none'});
	}
	</c:if>
	
	<c:if test="${not empty strain.qtls}">
	// fix width of DIV containing QTL table and adjust the header color
	$('#qtlSummaryDiv').width($('#qtlSummaryTable').width() + 20);
	$('#qtlSummaryTable th').css('background-color', $('#mutationRibbonLabel').css('background-color'));
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
	} else {
		// table is all showing (no scrolling), so we don't need the extra line on bottom
		$('#qtlSummaryDiv').css({ 'border-bottom' : 'none'});
	}
	</c:if>
	</script>

	<style>
	#body .msStyle { padding-left: 95px; min-width: 470px; width: auto; margin-bottom: 3px; }
	#body .qtlStyle { width: auto; padding-left: 95px; mar}
	</style>