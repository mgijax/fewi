<c:if test="${not empty strain.mutations}">
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>" id="mutationRibbonLabel">
			Associated<br/>
			Mutations and<br/>
			Markers
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1 ">
			    <div id="mutationSummaryDiv" style="max-height: 125px; overflow-y: scroll; overflow-x: hidden; margin-top: 5px; margin-left: 15px; max-width: 95%" id="mutationsDiv">
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
	$('#mutationSummaryDiv').width($('#mutationSummaryTable').width() + 19);
	$('#mutationSummaryTable th').css('background-color', $('#mutationRibbonLabel').css('background-color'))
	</script>
</c:if>