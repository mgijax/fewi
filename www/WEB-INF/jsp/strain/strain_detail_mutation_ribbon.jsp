	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>" id="mutationRibbonLabel">
			Associated<br/>
			Mutations and<br/>
			Markers
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1 ">
				<span class="indented">
		    	${fn:length(strain.mutations)} associated
		    	<c:choose>
			    	<c:when test="${fn:length(strain.mutations) > 1}">mutations and markers</c:when>
			    	<c:otherwise>mutation and marker</c:otherwise>
		    	</c:choose>
		    	</span>
		    	<span id="mutationButton" class="searchToolButton indented hidden">Show All</span><br/>
			    <div id="mutationSummaryDiv" style="max-height: 125px; overflow-y: auto; overflow-x: hidden; margin-top: 5px; margin-left: 15px; max-width: 95%" id="mutationsDiv">
				    <table id="mutationSummaryTable">
				    <tr>
				    	<th>Mutation Carried</th>
				    	<th>Gene</th>
				    </tr>
				    <c:forEach var="mutation" items="${strain.mutations}">
				    	<tr>
				    		<td><c:if test="${not empty mutation.alleleID}">
				    			<a href="${configBean.FEWI_URL}allele/${mutation.alleleID}"><fewi:super value="${mutation.alleleSymbol}"/></a>
				    			</c:if></td>
				    		<td><c:if test="${not empty mutation.markerID}">
				    			<a href="${configBean.FEWI_URL}marker/${mutation.markerID}"><fewi:super value="${mutation.markerSymbol}"/></a>
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
	}
	</script>